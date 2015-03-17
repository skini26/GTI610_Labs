package partie2;

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
	private int numClient;
	
	// Creation nouveau client
	public ClientWorker(Socket client, int nbClient){
		this.client = client;
		numClient = nbClient;
		System.out.println("Client "+ numClient +" est connecte!");
	}
	
	public void run(){
	    
		BufferedReader clientIn = null;
		PrintWriter clientOut 	= null;
		
		try {
			
			// Connexion
			clientIn 	= new BufferedReader(new InputStreamReader(client.getInputStream()));
			clientOut 	= new PrintWriter(client.getOutputStream(), true);

		} catch (IOException e) {
			System.out.println(e);
		}

	    try {
	    	
			String clientInput;
			SimpleDateFormat messageDateFormat = new SimpleDateFormat("HH:mm");
			
			while (true) {

				// Date
				Date now = new Date();
				String formatedDate = messageDateFormat.format(now);
				
				// Lecture ligne
				clientInput = clientIn.readLine();
				
				if(clientInput != null)
				{
					// Renvoie au client
					System.out.println("Client " + client.getInetAddress().getHostAddress() + ":8080 dit ("+ formatedDate +"): " + clientInput);
					clientOut.println(clientInput.toUpperCase());
				}
				
			}
			
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				System.out.println(e);
			}
			System.exit(1);
		}
	}

}
