import java.io.*;
import java.net.*;

class Server {
	
	int delay = 100, memberIter = 0, grpLimit = 10, chatSize = 0;
	Socket client[] = new Socket[grpLimit];
	PrintStream send[] = new PrintStream[grpLimit];
	BufferedReader recv[] = new BufferedReader[grpLimit];
	Boolean exit = false;
	Thread recvThread[] = new Thread[grpLimit], sendThread[] = new Thread[grpLimit];
	String senderName[] = new String[grpLimit], serverName, recvText[] = new String[grpLimit], consoleTxt, endRule = "exit";

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
						// System.out.println("Waiting for " + chatSize);
						client[chatSize] = server.accept();
						// System.out.println("Waiting for " + chatSize);
					
						// Data exchange Objects
						send[chatSize] = new PrintStream(client[chatSize].getOutputStream());
						recv[chatSize] = new BufferedReader(new InputStreamReader(client[chatSize].getInputStream()));

						// User details exchange
						send[chatSize].println(serverName);
						senderName[chatSize] = recv[chatSize].readLine();
						System.out.println("\n\t\t" + senderName[chatSize] + " joined the chat room.\n");
						// System.out.println("|||||||||" + chatSize);
						chatSize++;
						exit = true;
						// System.out.println("|||||||||" + chatSize);
					} catch (Exception e) { e.printStackTrace(); }			//! Resolve
				}
				
				//TODO: Response for over-limit Clients
			}
		};

		multiClients.start();
		// System.out.println("-----------start: " + chatSize);
		
		while (!exit || chatSize != 0) {
			
			Thread.sleep(delay * 10);
			// System.out.println("------------------=======" + chatSize + "===" + exit);
			for (memberIter=0; memberIter<chatSize; memberIter++) {

				// Minimum delay for proper execution
				Thread.sleep(delay);
				// System.out.println("------------------=======" + chatSize + "===" + memberIter);
				
				// Reciever port
				if (recvThread[memberIter] == null || !recvThread[memberIter].isAlive()) {
					
					int memberIndx = memberIter;
					
					recvThread[memberIndx] = new Thread() {
						public void run() {
							
							// System.out.println("-----------recv-");
							// System.out.println("------------------=======" + chatSize + "===" + memberIter);
							try {
								// System.out.println("|" + recv[memberIndx] + "|" + memberIndx + "|");
								if ((recvText[memberIndx] = recv[memberIndx].readLine()).equals(endRule)) {
									System.out.println("\n\t\t" + senderName[memberIndx] + " left the chat room.\n");
									chatSize--;
								}
								else {
									System.out.println(senderName[memberIndx] + ": " + recvText[memberIndx]);
									for (int indx=0; indx<chatSize; indx++) if (indx != memberIndx)
										send[indx].println("~" + senderName[memberIndx] + ": " + recvText[memberIndx]);
								}
							} catch (Exception e) {
								e.printStackTrace();
								chatSize--;			//TODO: Data cleanup
							}
						}
					};

					recvThread[memberIter].start();
				}
				
				// Sender port
				if (sendThread[memberIter] == null || !sendThread[memberIter].isAlive()) {
					
					sendThread[memberIter] = new Thread() {
						public void run() {
							
							// System.out.println("-----------send-");
							int memberIndx = memberIter;
							try {
								if ((consoleTxt = console.readLine()).equals(endRule)) exit = true;
								if (!recvText[memberIndx].equals(endRule)) send[memberIndx].println(consoleTxt);
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
		// System.out.println("------------------=======Out");
		
		// Disconnection
		console.close();
		// System.out.println("------------------=======Out2");
		for (int c=0; c < chatSize; c++) {
			send[c].close();
			// System.out.println("------------------=======" + chatSize + "===||" + c);
			recv[c].close();
			recv[c].close();
			client[c].close();
		}
		// System.out.println("------------------=======Out3");
		
		// Server shut-down
		server.close();
		// System.out.println("------------------=======Out4");
	}

	public static void main(String args[]) throws Exception {
		new Server().user();
	}
}