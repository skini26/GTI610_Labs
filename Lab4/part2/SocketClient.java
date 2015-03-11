package part2;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

public class SocketClient {
	
	public static void main(String[] args) throws IOException {
		
		IPAddressValidator ipAddressValidator = new IPAddressValidator();
		String hote = null;
		int port = 0;
		
		boolean addressValid = false;
		String errorMsg = "";
		while(addressValid == false){
			String address = JOptionPane.showInputDialog(null, errorMsg+" Address:port");
			int index = address.indexOf(":");
			if(index == -1){
				errorMsg = "Address not valid, please retry :";
				continue;
			}
			hote = address.substring(0, index);
			System.out.println("Hote = "+hote);
			if(hote.trim().equalsIgnoreCase("localhost") || ipAddressValidator.validate(hote)){
				try{
					String portString = address.substring(address.indexOf(":")+1, address.length());
					System.out.println("Port ="+portString);
					port = Integer.parseInt(portString);
					
				}
				catch(NumberFormatException e){
					errorMsg = "Address not valid, please retry :";
					System.err.println("NumberFormatException");
					continue;
				}
				addressValid = true;
			}
			else{
				errorMsg = "Address not valid, please retry :";
				continue;
			}
		}
		 

		Socket clientSocket = new Socket(hote, port);
		PrintWriter serverOut = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader serverIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		try {
			String clientInput;
			String serverInput;
			SimpleDateFormat messageDateFormat = new SimpleDateFormat("HH:mm");
			while (true) {
				if((clientInput = stdIn.readLine()) != null){
					serverOut.println(clientInput);
				}
				if((serverInput = serverIn.readLine()) != null){
					Date now = new Date();
					String formatedDate = messageDateFormat.format(now);
					
					System.out.println("Serveur dit ("+formatedDate+") : " + serverInput);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			stdIn.close();
			serverOut.close();
			clientSocket.close();
			System.exit(1);
		}
	}
}