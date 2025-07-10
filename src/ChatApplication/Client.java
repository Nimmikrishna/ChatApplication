

/**
 * 12th Edition
 * Chapter 33. Programming Exercise 10
 * Multiple Client Chat Application
 * Client side of the application
 * Client class creates a client instance on each run of the program.
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
import java.awt.event.*;




public class Client extends JFrame {
	
	private String username;
	private ObjectOutputStream outputStreamToServer; 
	private ObjectInputStream inputStreamFromServer;
	private Socket socket;
	private JTextField sendBoxField;
	private JTextField nameField;	 
	private JTextArea chatLogArea;
	private JPanel panelUsername;
	private JPanel panelInput;
	private JPanel panelChat;
	private JPanel panelTop;
	private JButton connectButton;
	private JButton sendButton;
	
	
	
	public Client() {
		
		//GUI for chat client
		
		sendBoxField = new JTextField();
		nameField = new JTextField();
		chatLogArea = new JTextArea();
		panelUsername = new JPanel();
		panelInput = new JPanel();
		panelChat = new JPanel();
		panelTop = new JPanel();
		connectButton = new JButton("Connect");
		sendButton = new JButton("   Send   ");
		sendBoxField.setHorizontalAlignment(JTextField.LEFT);		
		nameField.setHorizontalAlignment(JTextField.LEFT);		
		chatLogArea.setEditable(false);		
		panelUsername.setLayout(new BorderLayout());
		panelUsername.add(new JLabel(" Username "), BorderLayout.WEST);
		panelUsername.add(nameField, BorderLayout.CENTER);
		panelUsername.add(connectButton, BorderLayout.EAST);		
		panelInput.setLayout(new BorderLayout());
		panelInput.add(new JLabel(" Enter Text "), BorderLayout.WEST);
		panelInput.add(sendBoxField, BorderLayout.CENTER);
		panelInput.add(sendButton, BorderLayout.EAST);		
		panelChat.setLayout(new BorderLayout());		
		panelChat.add(new JScrollPane(chatLogArea), BorderLayout.CENTER);		
		panelTop.setLayout(new GridLayout(2,1));
		panelTop.add(panelUsername);
		panelTop.add(panelInput);		
		setLayout(new BorderLayout());
		add(panelTop, BorderLayout.NORTH);
		add(panelChat, BorderLayout.CENTER);		
		setTitle(" Chat Client ");
		setSize(600,400);
		  
	}
	
	public void startClient() {
		
		sendBoxField.addActionListener(new sendListener());
		sendButton.addActionListener(new sendListener());
		nameField.addActionListener(new UsernameListener());
		connectButton.addActionListener(new UsernameListener());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
        this.addWindowListener(ExitListener);
		   
  
	}
	
	//Chat Username Listener
	private class UsernameListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent eventp){
				username = nameField.getText();
				setTitle((String)username);
				nameField.setText(username);
				nameField.setEditable(false);
				startConnectionWithServer();
			}
	}
		
	//Chat ActionListener
	private class sendListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent eventp){
				try {
					outputStreamToServer.writeObject(new MessageHandler(sendBoxField.getText()));
					sendBoxField.setText("");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	
	
	public void startConnectionWithServer(){
		try {
			socket = new Socket("localhost", 8001);
			outputStreamToServer = new ObjectOutputStream(socket.getOutputStream());
			inputStreamFromServer = new ObjectInputStream(socket.getInputStream());
			
		}
		catch (IOException ex)
        {
			System.err.println(ex);
            System.exit(1);
		}
		chatLogArea.append("Connected............. \nYou can start sending messages.");
	
		new HandleServerClient().start();
		
		try
        {
			outputStreamToServer.writeObject(username);
		}
		catch (IOException ex){
			System.err.println(ex);
		}
	}
              
	
        WindowListener ExitListener = new WindowAdapter()
        {
              @Override
              public void windowClosing(WindowEvent event)
              {
                try
                {
                    System.out.println("Exited");
                    outputStreamToServer.writeObject(new MessageHandler());
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                System.exit(0);
              }
        };
	
	//Thread to handle server
	class HandleServerClient extends Thread{
		public void run(){
			while(true){
				try{
					String msg = (String) inputStreamFromServer.readObject();
					chatLogArea.append(msg);
				}
				catch (IOException | ClassNotFoundException e){
					System.err.println(e);
				}
			}
		}
	}

	public static void main(String[] args) {
		
		Client c = new Client();
		c.startClient();

	}//close main

}//close client class
