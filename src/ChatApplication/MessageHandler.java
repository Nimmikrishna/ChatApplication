/**
 * 12th Edition
 * Chapter 33. Programming Exercise 10
 * Multiple Client Chat Application
 * MessageHandler implements Serializable interface.
 * Message can be sent and received using the object output and input streams.
 * @author :  Nimmikrishna Babu
 * @group : Group 15
 * @members : Nimmikrishna Babu
 * @date : 03 May 2022
 */


package ChatApplication;

import java.io.*;

public class MessageHandler implements Serializable {
	
	public String message = null;
    public boolean disconnect;
	
    public MessageHandler(String msg)
    {
    	message = msg;
        this.disconnect = false;
	}
        
    public MessageHandler()
    {
        this.disconnect = true;
    }
        
    public boolean diconnectClient()
    {
    	return disconnect;
    }
	
	public String getMessage(){
		return message;
	}

}
