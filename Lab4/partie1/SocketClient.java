package partie1;

import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class SocketClient {
	
	public static void main(String[] args) throws IOException {
		
		// Init client
		int port = 8080;
		Socket clientSocket = null;
		PrintWriter serverOut = null;
		String clientInput = null;
		BufferedReader stdIn = null;
		BufferedReader clientIn = null;
		
		try {
			
			// Connexion au serveur
			clientSocket = new Socket(InetAddress.getLocalHost(), port);
			System.out.println("Client se connecte a "+ clientSocket);
			serverOut = new PrintWriter(clientSocket.getOutputStream(), true);
			clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			// Confirme connexion
			System.out.println(clientIn.readLine());
			
			while (true) {
				
				// Ecriture
				System.out.println("Ecrivez un message (ou tapez END pour quitter):");
				stdIn = new BufferedReader(new InputStreamReader(System.in));
				
				clientInput = stdIn.readLine();
				serverOut.println(clientInput);
				
				// Quitter
				if(clientInput.equals("END"))
				{
					System.out.println("Fermeture client");
					break;
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		try
		{
			// Fermeture
			stdIn.close();
			serverOut.close();
			clientSocket.close();
			System.exit(1);
		} catch (Exception e) {
			System.out.println(e);
		} 
		
	}
}