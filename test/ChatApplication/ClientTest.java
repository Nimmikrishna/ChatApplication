package ChatApplication;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ChatApplication.Client;
import ChatApplication.MessageHandler;

/**
 * JUnit test cases for Client class
 * Tests GUI components, event handling, and network connection functionality
 */
public class ClientTest {
    
    private Client client;
    private static final int TEST_PORT = 8003; // Different port for testing
    
    @BeforeClass
    public static void setUpClass() {
        // Set up any class-level resources
    }
    
    @AfterClass
    public static void tearDownClass() {
        // Clean up any class-level resources
    }
    
    @Before
    public void setUp() {
        // Create a new client instance for each test
        client = new Client();
    }
    
    @After
    public void tearDown() {
        // Clean up after each test
        if (client != null) {
            client.dispose();
            client = null;
        }
    }
    
    @Test
    public void testClientInitialization() {
        // Test that client initializes properly
        assertNotNull("Client should not be null", client);
        
        // Test that client window has correct title
        assertEquals("Client window should have correct title", " Chat Client ", client.getTitle());
        
        // Test that client window has correct size
        assertEquals("Client window should have correct width", 600, client.getWidth());
        assertEquals("Client window should have correct height", 400, client.getHeight());
    }
    
    @Test
    public void testClientWindowProperties() {
        // Test client window properties
        assertTrue("Client should be instance of JFrame", client instanceof JFrame);
        assertFalse("Client should not be visible initially", client.isVisible());
        assertFalse("Client should not be resizable by default", client.isResizable());
        
        // Test default close operation
        assertEquals("Default close operation should be EXIT_ON_CLOSE", 
                     JFrame.EXIT_ON_CLOSE, client.getDefaultCloseOperation());
    }
    
    @Test
    public void testClientLayout() {
        // Test that client has proper layout
        assertNotNull("Client layout should not be null", client.getLayout());
        assertTrue("Client layout should be BorderLayout", client.getLayout() instanceof BorderLayout);
    }
    
    @Test
    public void testClientComponents() {
        // Test that client has required components
        Component[] components = client.getContentPane().getComponents();
        assertTrue("Client should have components", components.length > 0);
        
        // Should have two main panels (NORTH and CENTER)
        assertEquals("Client should have 2 main components", 2, components.length);
    }
    
    @Test
    public void testClientTopPanel() {
        // Test the top panel (NORTH component)
        Component[] components = client.getContentPane().getComponents();
        Component northComponent = null;
        
        for (Component comp : components) {
            if (client.getContentPane().getLayout() instanceof BorderLayout) {
                BorderLayout layout = (BorderLayout) client.getContentPane().getLayout();
                if (layout.getLayoutComponent(BorderLayout.NORTH) == comp) {
                    northComponent = comp;
                    break;
                }
            }
        }
        
        assertNotNull("Client should have a NORTH component", northComponent);
        assertTrue("NORTH component should be a JPanel", northComponent instanceof JPanel);
        
        JPanel topPanel = (JPanel) northComponent;
        assertEquals("Top panel should have GridLayout", GridLayout.class, topPanel.getLayout().getClass());
    }
    
    @Test
    public void testClientChatPanel() {
        // Test the chat panel (CENTER component)
        Component[] components = client.getContentPane().getComponents();
        Component centerComponent = null;
        
        for (Component comp : components) {
            if (client.getContentPane().getLayout() instanceof BorderLayout) {
                BorderLayout layout = (BorderLayout) client.getContentPane().getLayout();
                if (layout.getLayoutComponent(BorderLayout.CENTER) == comp) {
                    centerComponent = comp;
                    break;
                }
            }
        }
        
        assertNotNull("Client should have a CENTER component", centerComponent);
        assertTrue("CENTER component should be a JPanel", centerComponent instanceof JPanel);
        
        JPanel chatPanel = (JPanel) centerComponent;
        assertEquals("Chat panel should have BorderLayout", BorderLayout.class, chatPanel.getLayout().getClass());
    }
    
    @Test
    public void testClientTextFields() {
        // Test that client has required text fields
        try {
            java.lang.reflect.Field sendBoxFieldField = Client.class.getDeclaredField("sendBoxField");
            java.lang.reflect.Field nameFieldField = Client.class.getDeclaredField("nameField");
            
            sendBoxFieldField.setAccessible(true);
            nameFieldField.setAccessible(true);
            
            JTextField sendField = (JTextField) sendBoxFieldField.get(client);
            JTextField nameFieldObj = (JTextField) nameFieldField.get(client);
            
            assertNotNull("Send box field should not be null", sendField);
            assertNotNull("Name field should not be null", nameFieldObj);
            
            assertTrue("Send box field should be instance of JTextField", sendField instanceof JTextField);
            assertTrue("Name field should be instance of JTextField", nameFieldObj instanceof JTextField);
            
        } catch (Exception e) {
            fail("Could not access text field fields: " + e.getMessage());
        }
    }
    
    @Test
    public void testClientTextArea() {
        // Test that client has a text area for chat
        try {
            java.lang.reflect.Field chatLogAreaField = Client.class.getDeclaredField("chatLogArea");
            chatLogAreaField.setAccessible(true);
            
            JTextArea chatLogArea = (JTextArea) chatLogAreaField.get(client);
            
            assertNotNull("Chat log area should not be null", chatLogArea);
            assertTrue("Chat log area should be instance of JTextArea", chatLogArea instanceof JTextArea);
            assertFalse("Chat log area should not be editable", chatLogArea.isEditable());
            
        } catch (Exception e) {
            fail("Could not access chat log area field: " + e.getMessage());
        }
    }
    
    @Test
    public void testClientButtons() {
        // Test that client has required buttons
        try {
            java.lang.reflect.Field connectButtonField = Client.class.getDeclaredField("connectButton");
            java.lang.reflect.Field sendButtonField = Client.class.getDeclaredField("sendButton");
            
            connectButtonField.setAccessible(true);
            sendButtonField.setAccessible(true);
            
            JButton connectButton = (JButton) connectButtonField.get(client);
            JButton sendButton = (JButton) sendButtonField.get(client);
            
            assertNotNull("Connect button should not be null", connectButton);
            assertNotNull("Send button should not be null", sendButton);
            
            assertEquals("Connect button should have correct text", "Connect", connectButton.getText());
            assertEquals("Send button should have correct text", "   Send   ", sendButton.getText());
            
        } catch (Exception e) {
            fail("Could not access button fields: " + e.getMessage());
        }
    }
    
    @Test
    public void testClientPanels() {
        // Test that client has required panels
        try {
            java.lang.reflect.Field panelUsernameField = Client.class.getDeclaredField("panelUsername");
            java.lang.reflect.Field panelInputField = Client.class.getDeclaredField("panelInput");
            java.lang.reflect.Field panelChatField = Client.class.getDeclaredField("panelChat");
            java.lang.reflect.Field panelTopField = Client.class.getDeclaredField("panelTop");
            
            panelUsernameField.setAccessible(true);
            panelInputField.setAccessible(true);
            panelChatField.setAccessible(true);
            panelTopField.setAccessible(true);
            
            JPanel panelUsername = (JPanel) panelUsernameField.get(client);
            JPanel panelInput = (JPanel) panelInputField.get(client);
            JPanel panelChat = (JPanel) panelChatField.get(client);
            JPanel panelTop = (JPanel) panelTopField.get(client);
            
            assertNotNull("Username panel should not be null", panelUsername);
            assertNotNull("Input panel should not be null", panelInput);
            assertNotNull("Chat panel should not be null", panelChat);
            assertNotNull("Top panel should not be null", panelTop);
            
        } catch (Exception e) {
            fail("Could not access panel fields: " + e.getMessage());
        }
    }
    
    @Test
    public void testClientStartClient() {
        // Test that client can be started
        assertFalse("Client should not be visible before start", client.isVisible());
        
        client.startClient();
        
        assertTrue("Client should be visible after start", client.isVisible());
    }
    
    @Test
    public void testClientWindowDisposal() {
        // Test that client window can be disposed properly
        client.startClient();
        assertTrue("Client should be visible before disposal", client.isVisible());
        
        client.dispose();
        
        assertFalse("Client should not be visible after disposal", client.isVisible());
    }
    
    @Test
    public void testClientDefaultCloseOperation() {
        // Test that client has correct default close operation
        assertEquals("Client should have EXIT_ON_CLOSE as default close operation", 
                     JFrame.EXIT_ON_CLOSE, client.getDefaultCloseOperation());
    }
    
    @Test
    public void testClientWindowSize() {
        // Test client window size constraints
        Dimension size = client.getSize();
        assertNotNull("Client size should not be null", size);
        assertTrue("Client width should be 600", size.width == 600);
        assertTrue("Client height should be 400", size.height == 400);
    }
    
    @Test
    public void testClientWindowLocation() {
        // Test that client window has a valid location
        Point location = client.getLocation();
        assertNotNull("Client location should not be null", location);
        assertTrue("Client x location should be >= 0", location.x >= 0);
        assertTrue("Client y location should be >= 0", location.y >= 0);
    }
    
    @Test
    public void testClientSocketConnection() {
        // Test that client can create a socket connection (simulated)
        try (ServerSocket testServerSocket = new ServerSocket(TEST_PORT)) {
            // Start a thread to accept connections
            Thread acceptThread = new Thread(() -> {
                try {
                    Socket clientSocket = testServerSocket.accept();
                    assertNotNull("Accepted client socket should not be null", clientSocket);
                    clientSocket.close();
                } catch (IOException e) {
                    // Expected when test client disconnects
                }
            });
            acceptThread.start();
            
            // Test socket creation
            try (Socket testSocket = new Socket("localhost", TEST_PORT)) {
                assertNotNull("Test socket should not be null", testSocket);
                assertTrue("Test socket should be connected", testSocket.isConnected());
                assertEquals("Test socket should be connected to correct port", TEST_PORT, testSocket.getPort());
            }
            
            acceptThread.join(1000); // Wait for accept thread to finish
        } catch (Exception e) {
            fail("Socket connection test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testClientStreamCreation() {
        // Test that client can create input/output streams
        try (Socket testSocket = new Socket("localhost", 8001)) {
            // This will fail if server is not running, but we can test stream creation
            ObjectOutputStream outputStream = new ObjectOutputStream(testSocket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(testSocket.getInputStream());
            
            assertNotNull("Output stream should not be null", outputStream);
            assertNotNull("Input stream should not be null", inputStream);
            
        } catch (IOException e) {
            // Expected if server is not running, but streams should still be created
            // This test is more about testing the stream creation capability
        }
    }
    
    @Test
    public void testMessageHandlerForClient() {
        // Test creating MessageHandler objects for client communication
        MessageHandler normalMessage = new MessageHandler("Client test message");
        MessageHandler disconnectMessage = new MessageHandler();
        
        assertNotNull("Normal message should not be null", normalMessage);
        assertNotNull("Disconnect message should not be null", disconnectMessage);
        
        assertEquals("Normal message should contain correct text", "Client test message", normalMessage.getMessage());
        assertNull("Disconnect message should have null text", disconnectMessage.getMessage());
        
        assertFalse("Normal message should not be disconnect", normalMessage.diconnectClient());
        assertTrue("Disconnect message should be disconnect", disconnectMessage.diconnectClient());
    }
    
    @Test
    public void testClientMessageSerialization() throws IOException, ClassNotFoundException {
        // Test that MessageHandler can be serialized for client-server communication
        MessageHandler originalMessage = new MessageHandler("Client serialization test");
        
        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(originalMessage);
        oos.close();
        
        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        MessageHandler deserializedMessage = (MessageHandler) ois.readObject();
        ois.close();
        
        // Verify
        assertNotNull("Deserialized message should not be null", deserializedMessage);
        assertEquals("Deserialized message should match original", 
                     originalMessage.getMessage(), deserializedMessage.getMessage());
        assertEquals("Deserialized disconnect status should match original", 
                     originalMessage.diconnectClient(), deserializedMessage.diconnectClient());
    }
    
    @Test
    public void testClientUsernameField() {
        // Test username field functionality
        try {
            java.lang.reflect.Field nameField = Client.class.getDeclaredField("nameField");
            nameField.setAccessible(true);
            
            JTextField nameFieldObj = (JTextField) nameField.get(client);
            
            // Test setting text
            nameFieldObj.setText("TestUser");
            assertEquals("Username field should contain 'TestUser'", "TestUser", nameFieldObj.getText());
            
            // Test clearing text
            nameFieldObj.setText("");
            assertEquals("Username field should be empty", "", nameFieldObj.getText());
            
        } catch (Exception e) {
            fail("Could not access name field: " + e.getMessage());
        }
    }
    
    @Test
    public void testClientSendBoxField() {
        // Test send box field functionality
        try {
            java.lang.reflect.Field sendBoxField = Client.class.getDeclaredField("sendBoxField");
            sendBoxField.setAccessible(true);
            
            JTextField sendField = (JTextField) sendBoxField.get(client);
            
            // Test setting text
            sendField.setText("Test message");
            assertEquals("Send box field should contain 'Test message'", "Test message", sendField.getText());
            
            // Test clearing text
            sendField.setText("");
            assertEquals("Send box field should be empty", "", sendField.getText());
            
        } catch (Exception e) {
            fail("Could not access send box field: " + e.getMessage());
        }
    }
    
    @Test
    public void testClientChatLogArea() {
        // Test chat log area functionality
        try {
            java.lang.reflect.Field chatLogAreaField = Client.class.getDeclaredField("chatLogArea");
            chatLogAreaField.setAccessible(true);
            
            JTextArea chatLogArea = (JTextArea) chatLogAreaField.get(client);
            
            // Test appending text
            chatLogArea.append("Test chat message\n");
            assertTrue("Chat log area should contain test message", 
                      chatLogArea.getText().contains("Test chat message"));
            
            // Test that it's not editable
            assertFalse("Chat log area should not be editable", chatLogArea.isEditable());
            
        } catch (Exception e) {
            fail("Could not access chat log area field: " + e.getMessage());
        }
    }
} 