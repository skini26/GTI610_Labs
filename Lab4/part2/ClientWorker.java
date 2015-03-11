package part2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientWorker implements Runnable {

	private Socket client;
	
	public ClientWorker(Socket client){
		this.client = client;
		System.out.println("New client connected !");
	}
	
	public void run(){
	    
		BufferedReader clientIn = null;
		PrintWriter clientOut = null;
		try {
			clientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
			clientOut = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	    try {
			String input;
			SimpleDateFormat messageDateFormat = new SimpleDateFormat("HH:mm");
			while ((input = clientIn.readLine()) != null) {
				Date now = new Date();
				String formatedDate = messageDateFormat.format(now);
				
				System.out.println("Client dit ("+formatedDate+") : " + input);
				
				clientOut.println(input.toUpperCase());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(1);
		}
	}

}
