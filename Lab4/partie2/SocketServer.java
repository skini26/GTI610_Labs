package partie2;


import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;

import javax.swing.JOptionPane;

public class SocketServer {
	
	
	public static void main(String[] args) throws IOException {
		
		// Port specifique
		int port = 8080;
		int nbClient = 1;
		
		ServerSocket serverSocket 	= null;
		Socket clientSocket 		= null;
		PrintWriter serverOut 		= null;
		
		try{
			// Creation de ServerSocket
			serverSocket = new ServerSocket(port);
			System.out.println("Serveur "+ InetAddress.getLocalHost().getHostName() +" en marche");
		} catch (Exception e) {
			System.out.println(e);
		}

		// Thread
		ExecutorService threadPool = Executors.newFixedThreadPool(8);
		
		while (true) {
			
		    ClientWorker w;
		    
		    try{

				// Confirme connexion
				clientSocket = serverSocket.accept();
				serverOut = new PrintWriter(clientSocket.getOutputStream(), true);
				serverOut.println("Bienvenu!");
				
				w = new ClientWorker(clientSocket, nbClient);
				nbClient ++;
				threadPool.execute(w);
				
		    } 
		    catch (Exception e) {
				System.out.println(e);
		    }
		}
	}
}