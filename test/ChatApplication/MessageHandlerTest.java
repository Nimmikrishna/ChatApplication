package ChatApplication;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.io.*;
import ChatApplication.MessageHandler;

/**
 * JUnit test cases for MessageHandler class
 * Tests message creation, serialization, and disconnect functionality
 */
public class MessageHandlerTest {
    
    private MessageHandler messageHandler;
    private MessageHandler disconnectHandler;
    
    @Before
    public void setUp() {
        messageHandler = new MessageHandler("Hello World");
        disconnectHandler = new MessageHandler();
    }
    
    @After
    public void tearDown() {
        messageHandler = null;
        disconnectHandler = null;
    }
    
    @Test
    public void testMessageHandlerWithMessage() {
        // Test constructor with message
        assertNotNull("MessageHandler should not be null", messageHandler);
        assertEquals("Message should be 'Hello World'", "Hello World", messageHandler.getMessage());
        assertFalse("Should not be disconnect message", messageHandler.diconnectClient());
    }
    
    @Test
    public void testMessageHandlerWithoutMessage() {
        // Test constructor without message (disconnect)
        assertNotNull("Disconnect MessageHandler should not be null", disconnectHandler);
        assertNull("Message should be null for disconnect", disconnectHandler.getMessage());
        assertTrue("Should be disconnect message", disconnectHandler.diconnectClient());
    }
    
    @Test
    public void testMessageHandlerWithEmptyString() {
        // Test constructor with empty string
        MessageHandler emptyMessageHandler = new MessageHandler("");
        assertNotNull("Empty message handler should not be null", emptyMessageHandler);
        assertEquals("Message should be empty string", "", emptyMessageHandler.getMessage());
        assertFalse("Should not be disconnect message", emptyMessageHandler.diconnectClient());
    }
    
    @Test
    public void testMessageHandlerWithNullMessage() {
        // Test constructor with null message
        MessageHandler nullMessageHandler = new MessageHandler(null);
        assertNotNull("Null message handler should not be null", nullMessageHandler);
        assertNull("Message should be null", nullMessageHandler.getMessage());
        assertFalse("Should not be disconnect message", nullMessageHandler.diconnectClient());
    }
    
    @Test
    public void testMessageHandlerWithSpecialCharacters() {
        // Test constructor with special characters
        String specialMessage = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        MessageHandler specialMessageHandler = new MessageHandler(specialMessage);
        assertNotNull("Special message handler should not be null", specialMessageHandler);
        assertEquals("Message should contain special characters", specialMessage, specialMessageHandler.getMessage());
        assertFalse("Should not be disconnect message", specialMessageHandler.diconnectClient());
    }
    
    @Test
    public void testMessageHandlerWithLongMessage() {
        // Test constructor with long message
        String longMessage = "This is a very long message that contains many characters to test the MessageHandler class with a substantial amount of text that should be handled properly by the system.";
        MessageHandler longMessageHandler = new MessageHandler(longMessage);
        assertNotNull("Long message handler should not be null", longMessageHandler);
        assertEquals("Message should be the long message", longMessage, longMessageHandler.getMessage());
        assertFalse("Should not be disconnect message", longMessageHandler.diconnectClient());
    }
    
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        // Test that MessageHandler can be serialized and deserialized
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        
        // Serialize the message handler
        oos.writeObject(messageHandler);
        oos.close();
        
        // Deserialize the message handler
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        MessageHandler deserializedHandler = (MessageHandler) ois.readObject();
        ois.close();
        
        // Verify the deserialized object
        assertNotNull("Deserialized MessageHandler should not be null", deserializedHandler);
        assertEquals("Deserialized message should match original", messageHandler.getMessage(), deserializedHandler.getMessage());
        assertEquals("Deserialized disconnect status should match original", messageHandler.diconnectClient(), deserializedHandler.diconnectClient());
    }
    
    @Test
    public void testDisconnectSerialization() throws IOException, ClassNotFoundException {
        // Test that disconnect MessageHandler can be serialized and deserialized
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        
        // Serialize the disconnect handler
        oos.writeObject(disconnectHandler);
        oos.close();
        
        // Deserialize the disconnect handler
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        MessageHandler deserializedDisconnectHandler = (MessageHandler) ois.readObject();
        ois.close();
        
        // Verify the deserialized object
        assertNotNull("Deserialized disconnect MessageHandler should not be null", deserializedDisconnectHandler);
        assertEquals("Deserialized message should match original", disconnectHandler.getMessage(), deserializedDisconnectHandler.getMessage());
        assertEquals("Deserialized disconnect status should match original", disconnectHandler.diconnectClient(), deserializedDisconnectHandler.diconnectClient());
        assertTrue("Deserialized handler should be disconnect message", deserializedDisconnectHandler.diconnectClient());
    }
    
    @Test
    public void testMultipleMessageHandlers() {
        // Test creating multiple message handlers
        MessageHandler handler1 = new MessageHandler("Message 1");
        MessageHandler handler2 = new MessageHandler("Message 2");
        MessageHandler handler3 = new MessageHandler();
        
        assertNotNull("Handler 1 should not be null", handler1);
        assertNotNull("Handler 2 should not be null", handler2);
        assertNotNull("Handler 3 should not be null", handler3);
        
        assertEquals("Handler 1 message should be 'Message 1'", "Message 1", handler1.getMessage());
        assertEquals("Handler 2 message should be 'Message 2'", "Message 2", handler2.getMessage());
        assertNull("Handler 3 message should be null", handler3.getMessage());
        
        assertFalse("Handler 1 should not be disconnect", handler1.diconnectClient());
        assertFalse("Handler 2 should not be disconnect", handler2.diconnectClient());
        assertTrue("Handler 3 should be disconnect", handler3.diconnectClient());
    }
    
    @Test
    public void testGetMessageMethod() {
        // Test getMessage method with various inputs
        String testMessage = "Test message for getMessage method";
        MessageHandler testHandler = new MessageHandler(testMessage);
        
        assertEquals("getMessage should return the correct message", testMessage, testHandler.getMessage());
        
        // Test with empty string
        MessageHandler emptyHandler = new MessageHandler("");
        assertEquals("getMessage should return empty string", "", emptyHandler.getMessage());
        
        // Test with null
        MessageHandler nullHandler = new MessageHandler(null);
        assertNull("getMessage should return null", nullHandler.getMessage());
    }
    
    @Test
    public void testDisconnectClientMethod() {
        // Test diconnectClient method
        MessageHandler normalHandler = new MessageHandler("Normal message");
        MessageHandler disconnectHandler = new MessageHandler();
        
        assertFalse("Normal handler should return false", normalHandler.diconnectClient());
        assertTrue("Disconnect handler should return true", disconnectHandler.diconnectClient());
    }
} 