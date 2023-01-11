import java.io.*;
import java.net.*;

class Server {
	
	int delay = 100;
	Boolean exit = false;
	Thread recvThread, sendThread;
	String senderName, recvText = "", consoleTxt, endRule = "exit";

	public void user() throws Exception {

		// Server Socket
		ServerSocket server = new ServerSocket(3333);

		// Console Reader
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

		// Connection to Client Socket
		System.out.print("Enter your name: ");
		consoleTxt = console.readLine();
		System.out.println("Waiting for client...");
		Socket client = server.accept();
		
		// Data exchange Objects
		PrintStream send = new PrintStream(client.getOutputStream());
		BufferedReader recv = new BufferedReader(new InputStreamReader(client.getInputStream()));

		// User details exchange
		send.println(consoleTxt);
		senderName = recv.readLine();
		System.out.println("Connected with " + senderName + "\n");
		
		while (!exit) {

			// Minimum delay for proper execution
			Thread.sleep(delay);
			
			// Reciever port
			if (recvThread == null || !recvThread.isAlive()) {
				
				recvThread = new Thread() {
					public void run() {
						
						try {
							if ((recvText = recv.readLine()).equals(endRule)) exit = true;
							System.out.println(senderName + ": " + recvText);
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
		
		// Server shut-down
		server.close();
	}

	public static void main(String args[]) throws Exception {
		new Server().user();
	}
}