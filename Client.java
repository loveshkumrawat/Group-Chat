import java.io.*;
import java.net.*;

class Client {

	public static void main(String args[]) throws Exception {

		// Client socket
		Socket client = new Socket("localhost", 5080);

		// Data exchange Objects
		DataOutputStream send = new DataOutputStream(client.getOutputStream());			// to send data to the server
		BufferedReader recv = new BufferedReader(new InputStreamReader(client.getInputStream()));			// to read data coming from the server
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));			// to read data from the keyboard
		
		String consoleTxt, recvTxt;
		
		while (true) {
			if ((consoleTxt = console.readLine()).equals("exit")) break;
			send.writeBytes(consoleTxt + "\n");
			if ((recvTxt = recv.readLine()).equals("exit")) break;
			System.out.println(recvTxt);
		}
		
		send.writeBytes("exit");

		// close connection.
		send.close();
		recv.close();
		console.close();
		client.close();
	}
}