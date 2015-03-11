package part2;


import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;

import javax.swing.JOptionPane;

public class SocketServer {
	
	
	public static void main(String[] args) throws IOException {

		int port = 0;
		
		boolean portValid = false;
		String errorMsg = "";
		while(portValid == false){
			String portString = JOptionPane.showInputDialog(null, errorMsg+" Port : ");
			System.out.println("Port ="+portString);
			try{
				port = Integer.parseInt(portString);
			}
			catch(NumberFormatException e){
				errorMsg = "Port not valid :";
				continue;
			}
			portValid = true;
			
		}

		ServerSocket serverSocket = new ServerSocket(port);	
		ExecutorService threadPool = Executors.newFixedThreadPool(8);
		
		while(true){
		    ClientWorker w;
		    try{
		      Socket clientSocket = serverSocket.accept();
		      w = new ClientWorker(clientSocket);
		      threadPool.execute(w);
		      //Thread clientThread = new Thread(w);
		      //clientThread.start();
		    } 
		    catch (IOException e) {
		      System.exit(-1);
		    }
		}

	}
}