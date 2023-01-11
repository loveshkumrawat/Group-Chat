import java.io.*;
import java.net.*;

class Server {

	public static void main(String args[]) throws Exception {

		// Server Socket
		ServerSocket server = new ServerSocket(5080);

		// Connection to Client Socket
		System.out.println("Waiting for client...");
		Socket client = server.accept();
		System.out.println("Connection established");

		// Data exchange Objects
		PrintStream send = new PrintStream(client.getOutputStream());			// to send data to the client
		BufferedReader recv = new BufferedReader(new InputStreamReader(client.getInputStream()));			// to read data coming from the client
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));			// to read data from the keyboard
		
		String recvText, consoleTxt;
		
		while (true) {
			if ((recvText = recv.readLine()).equals("exit")) break;
			System.out.println(recvText);
			if ((consoleTxt = console.readLine()).equals("exit")) break;
			send.println(consoleTxt);
		}

		send.println("exit");
		
		// close connection
		send.close();
		recv.close();
		console.close();
		server.close();
		client.close();
	}
}