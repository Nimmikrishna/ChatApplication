

/**
 * 12th Edition
 * Chapter 33. Programming Exercise 10
 * Multiple Client Chat Application
 * Server side of the application
 * Server class that serves multiple clients simultaneously.
 * @author :  Nimmikrishna Babu
 * @group : Group 15
 * @members : Nimmikrishna Babu
 * @date : 03 May 2022
 */

package ChatApplication;

import java.util.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*; 




public class Server extends JFrame {
	
	//Message box 
	private SimpleDateFormat date;
	private JTextArea textBox;
	private int clientNo; //client number
	private ArrayList<HandleAClient> listOfClients;
	
	public Server() {
		
		date = new SimpleDateFormat("hh:mm:ss");
		textBox = new JTextArea();
		textBox.append("Waiting for connections..........\n");
		clientNo = 0; 
		listOfClients = new ArrayList<HandleAClient>();
		//sever window 
        setLayout(new BorderLayout());
        add(new JScrollPane(textBox), BorderLayout.CENTER);
        setSize(600, 400);
        setTitle("Server ");	        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
	}
	
	
	//constructor sets up the server window
	public void startServer() {
		  
	        try {
	        	// Create a server socket
	            ServerSocket serverSocket = new ServerSocket(8001);
	            while (true) {
	            	// Socket Listen for connection request
	                Socket socket = serverSocket.accept();
	                textBox.append("New Client connected..........\n");
	                HandleAClient client = new HandleAClient(socket);
	                listOfClients.add(client);
	                client.start();
	            }
	        } catch (IOException ex) {
	            System.err.println("Server error : " + ex);
	        }
	    }
	
	  
	    private synchronized void transfer(String message) {
	        String transferMessage = date.format(new Date()) + " " + message + "\n";
	        textBox.append(transferMessage);
	        for (int i = listOfClients.size(); --i >= 0;) {
	            HandleAClient clienti = listOfClients.get(i);
	            clienti.sendMessage(transferMessage);
	        }
	    }
	    class HandleAClient extends Thread {

	        Socket socket;
	        ObjectInputStream input;
	        ObjectOutputStream output;
	        String username;
	        int numberOfClients;
	        MessageHandler message;

	        HandleAClient(Socket socket) {
	            this.socket = socket;
	            numberOfClients = clientNo++;
	            try {
	                output = new ObjectOutputStream(socket.getOutputStream());
	                input = new ObjectInputStream(socket.getInputStream());
	                username = (String) input.readObject();
	                textBox.append(new Date() + " " + username + " connected\n");
	            } catch (IOException | ClassNotFoundException e) {
	                System.err.println(e);
	            }
	        }

	        public void run() {
	            while (true) {
	                try {
	                    message = (MessageHandler) input.readObject();
	                } catch (IOException | ClassNotFoundException error) {
	                    System.err.println("No client found" + error);
	                }
	                if (!message.diconnectClient()) {
	                	transfer(username + ": " + message.getMessage());
	                } else {
	                    break;
	                }
	            }
	            transfer(username + " disconnected.");
	        }

	        private void sendMessage(String msg) {
	            try {
	                output.writeObject(msg);
	            } catch (IOException error) {
	                System.err.println("Closed Connection : " + error);
	            }
	        }
	    }//close Handle class

	
	public static void main(String[] args) {
		
        Server S = new Server();
        S.startServer();
       
    } //close main
	
}//Close Server class
