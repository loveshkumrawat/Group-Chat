import java.io.*;
import java.net.*;

class Client {
	
	int delay = 100;
	Boolean exit = false;
	Thread recvThread, sendThread;
	String senderName, recvText = "", consoleTxt, endRule = "exit";

	public void user() throws Exception {

		// Client socket
		Socket client;
		String serverHost = System.getenv("SERVER_HOST");
		
		System.out.println("Wait session starting...");
		while (true) {
			try {
				client = new Socket((serverHost != null) ? serverHost : "localhost", 3333);
				break;
			} catch (Exception e) {}
		}

		// Console Reader
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

		// Data exchange Objects
		PrintStream send = new PrintStream(client.getOutputStream());
		BufferedReader recv = new BufferedReader(new InputStreamReader(client.getInputStream()));

		// User details exchange
		senderName = recv.readLine();
		System.out.println("Connected with " + senderName);
		System.out.print("Enter your name: ");
		consoleTxt = console.readLine();
		System.out.println();
		send.println(consoleTxt);
		
		while (!exit) {

			// Minimum delay for proper execution
			Thread.sleep(delay);
			
			// Reciever port
			if (recvThread == null || !recvThread.isAlive()) {
				
				recvThread = new Thread() {
					public void run() {
						
						try {
							if ((recvText = recv.readLine()).equals(endRule)) {
								exit = true;
								System.out.println("\t\t\tAdmin closed the chat room.\n");
							}
							else if (recvText.startsWith("~")) System.out.println(recvText.substring(1));
							else if (recvText.endsWith("chat room.")) System.out.println("\n\t\t" + recvText + "\n");
							else System.out.println(senderName + ": " + recvText);
						} catch (Exception e) {
							System.out.println("Disconnected: Running Thread...");
							exit = true;
						}
					}
				};

				recvThread.start();
			}
			
			// Sender port
			if (sendThread == null || !sendThread.isAlive()) {
				
				sendThread = new Thread() {
					public void run() {
						
						try {
							if ((consoleTxt = console.readLine()).equals(endRule)) exit = true;
							if (!recvText.equals(endRule)) send.println(consoleTxt);
						} catch (Exception e) {
							System.out.println("Disconnected: Running Thread...");
							exit = true;
						}
					}
				};

				sendThread.start();
			}
		}
		
		// Disconnection
		send.close();
		recv.close();
		console.close();
		client.close();
	}

	public static void main(String args[]) throws Exception {
		new Client().user();
	}
}