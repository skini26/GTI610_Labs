package part1;


import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
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
		Socket clientSocket = serverSocket.accept();
		BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		try {
			String input;
			SimpleDateFormat messageDateFormat = new SimpleDateFormat("HH:mm");
			while ((input = clientIn.readLine()) != null) {
				Date now = new Date();
				String formatedDate = messageDateFormat.format(now);
				
				System.out.println("Client dit ("+formatedDate+") :" + input);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			clientIn.close();
			clientSocket.close();
			serverSocket.close();
			System.exit(1);
		}

	}
}