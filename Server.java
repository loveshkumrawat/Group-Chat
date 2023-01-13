import java.io.*;
import java.net.*;

class Server {
	
	int delay = 100, memberIter = 0, grpLimit = 10, chatSize = 0, active = 0;
	Socket client[] = new Socket[grpLimit];
	PrintStream send[] = new PrintStream[grpLimit];
	BufferedReader recv[] = new BufferedReader[grpLimit];
	Boolean exit = false;
	Thread recvThread[] = new Thread[grpLimit], sendThread[] = new Thread[grpLimit];
	String senderName[] = new String[grpLimit], serverName, recvText[] = new String[grpLimit], consoleTxt, endRule = "exit";

	public void cleanup(int memberIndx) {
		client[memberIndx] = null;
		send[memberIndx] = null;
		recv[memberIndx] = null;
		recvThread[memberIndx] = null;
		recvThread[memberIndx] = null;
		senderName[memberIndx] = endRule;
		recvText[memberIndx] = null;
		active--;
	}

	public void user() throws Exception {

		// Server Socket
		ServerSocket server = new ServerSocket(3333);

		// Console Reader
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

		// Connection to Client Socket
		System.out.print("Enter your name: ");
		serverName = console.readLine();
		System.out.println("Waiting for atleast 1 member to join the chat room...");

		Thread multiClients = new Thread() {
			public void run() {
				
				while (chatSize < grpLimit) {
					try {
						client[chatSize] = server.accept();
					
						// Data exchange Objects
						send[chatSize] = new PrintStream(client[chatSize].getOutputStream());
						recv[chatSize] = new BufferedReader(new InputStreamReader(client[chatSize].getInputStream()));

						// User details exchange
						send[chatSize].println(serverName);
						senderName[chatSize] = recv[chatSize].readLine();
						System.out.println("\n\t\t" + senderName[chatSize] + " joined the chat room.\n");
						for (int indx=0; indx<chatSize; indx++) if (indx != chatSize)
							send[indx].println(senderName[chatSize] + " joined the chat room.");
						chatSize++;
						active++;
						exit = true;
					} catch (Exception e) { e.printStackTrace(); }			//! Resolve
				}
				
				//TODO: Response for over-limit Clients
			}
		};

		multiClients.start();
		
		while (!exit || active != 0) {
			
			Thread.sleep(delay * 10);
			for (memberIter=0; memberIter<chatSize; memberIter++) {

				// Minimum delay for proper execution
				Thread.sleep(delay);
				
				// Reciever port
				if (recvThread[memberIter] == null || !recvThread[memberIter].isAlive()) {
					
					int memberIndx = memberIter;
					
					recvThread[memberIndx] = new Thread() {
						public void run() {
							
							try {
								if ((recvText[memberIndx] = recv[memberIndx].readLine()).equals(endRule)) {
									System.out.println("\n\t\t" + senderName[memberIndx] + " left the chat room.\n");
									for (int indx=0; indx<chatSize; indx++) if (indx != memberIndx)
										send[indx].println("\n\t\t" + senderName[memberIndx] + " left the chat room.\n");
									cleanup(memberIndx);
								}
								else {
									System.out.println(senderName[memberIndx] + ": " + recvText[memberIndx]);
									for (int indx=0; indx<chatSize; indx++) if (indx != memberIndx)
										send[indx].println("~" + senderName[memberIndx] + ": " + recvText[memberIndx]);
								}
							} catch (Exception e) {
								e.printStackTrace();
								cleanup(memberIndx);			//TODO: Data cleanup not proper
							}
						}
					};

					recvThread[memberIter].start();
				}
				
				// Sender port
				if (sendThread[memberIter] == null || !sendThread[memberIter].isAlive()) {
					
					int memberIndx = memberIter;
					
					sendThread[memberIndx] = new Thread() {
						public void run() {
							
							try {
								if ((consoleTxt = console.readLine()).equals(endRule)) chatSize = 0;
								if (!recvText[memberIndx].equals(endRule)) for (int indx=0; indx<chatSize; indx++)
									send[indx].println(consoleTxt);
							} catch (Exception e) {
								System.out.println("Disconnected: Running Thread...");
								exit = true;
							}
						}
					};

					sendThread[memberIter].start();
				}
			}
		}
		
		// Disconnection
		console.close();
		for (int c=0; c < chatSize; c++) {
			send[c].close();
			recv[c].close();
			recv[c].close();
			client[c].close();
		}
		
		// Server shut-down
		server.close();
	}

	public static void main(String args[]) throws Exception {
		new Server().user();
	}
}