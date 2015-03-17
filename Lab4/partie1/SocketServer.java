package partie1;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;

import javax.swing.JOptionPane;

public class SocketServer {
	
	public static void main(String[] args) throws IOException {

		int port = 8080;
		System.out.println("Serveur en marche");

		// Creation de ServerSocket
		ServerSocket serverSocket = new ServerSocket(port);
		Socket clientSocket = serverSocket.accept();
		
		// Lecture client
		BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		PrintWriter serverOut = new PrintWriter(clientSocket.getOutputStream(), true);
		
		try {

			// Confirme connexion
			serverOut.println("Serveur dit: Bienvenu\n");
			
			String input;
			SimpleDateFormat messageDateFormat = new SimpleDateFormat("HH:mm");
			
			while (true) {

				// Date
				Date now = new Date();
				String formatedDate = messageDateFormat.format(now);
				
				// Lecture ligne
				input = clientIn.readLine();
				System.out.println("Client dit ("+formatedDate+"): " + input);
				
				// Quitter
				if(input.equals("END"))
				{
					System.out.println("Fermeture serveur");
					break;
				}
				
			}
			
		} catch (Exception e) {
			System.out.println(e);
		} 
		finally {
			// Fermeture
			clientIn.close();
			clientSocket.close();
			serverSocket.close();
			System.exit(1);
		}
	}
}