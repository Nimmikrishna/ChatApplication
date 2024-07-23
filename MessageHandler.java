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
	
	String message = null;
    boolean disconnect;
	
    MessageHandler(String msg)
    {
    	message = msg;
        this.disconnect = false;
	}
        
    MessageHandler()
    {
        this.disconnect = true;
    }
        
    boolean diconnectClient()
    {
    	return disconnect;
    }
	
	String getMessage(){
		return message;
	}

}
