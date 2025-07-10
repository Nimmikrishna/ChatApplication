package ChatApplication;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.awt.*;
import javax.swing.*;
import ChatApplication.Server;
import ChatApplication.MessageHandler;

/**
 * JUnit test cases for Server class
 * Tests server initialization, client handling, and message broadcasting
 */
public class ServerTest {
    
    private Server server;
    private static final int TEST_PORT = 8002; // Different port for testing
    
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
        // Create a new server instance for each test
        server = new Server();
    }
    
    @After
    public void tearDown() {
        // Clean up after each test
        if (server != null) {
            server.dispose();
            server = null;
        }
    }
    
    @Test
    public void testServerInitialization() {
        // Test that server initializes properly
        assertNotNull("Server should not be null", server);
        
        // Test that server window is visible
        assertTrue("Server window should be visible", server.isVisible());
        
        // Test that server window has correct title
        assertEquals("Server window should have correct title", "Server ", server.getTitle());
        
        // Test that server window has correct size
        assertEquals("Server window should have correct width", 600, server.getWidth());
        assertEquals("Server window should have correct height", 400, server.getHeight());
    }
    
    @Test
    public void testServerWindowProperties() {
        // Test server window properties
        assertTrue("Server should be instance of JFrame", server instanceof JFrame);
        assertTrue("Server should be visible", server.isVisible());
        assertFalse("Server should not be resizable by default", server.isResizable());
        
        // Test default close operation
        assertEquals("Default close operation should be EXIT_ON_CLOSE", 
                     JFrame.EXIT_ON_CLOSE, server.getDefaultCloseOperation());
    }
    
    @Test
    public void testServerLayout() {
        // Test that server has proper layout
        assertNotNull("Server layout should not be null", server.getLayout());
        assertTrue("Server layout should be BorderLayout", server.getLayout() instanceof BorderLayout);
    }
    
    @Test
    public void testServerComponents() {
        // Test that server has required components
        Component[] components = server.getContentPane().getComponents();
        assertTrue("Server should have components", components.length > 0);
        
        // Find the JScrollPane component
        boolean hasScrollPane = false;
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                hasScrollPane = true;
                break;
            }
        }
        assertTrue("Server should have a JScrollPane", hasScrollPane);
    }
    
    @Test
    public void testServerTextArea() {
        // Test that server has a text area for logging
        Component[] components = server.getContentPane().getComponents();
        JScrollPane scrollPane = null;
        
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                scrollPane = (JScrollPane) comp;
                break;
            }
        }
        
        assertNotNull("Server should have a JScrollPane", scrollPane);
        
        // Get the text area from the scroll pane
        Component viewportView = scrollPane.getViewport().getView();
        assertTrue("ScrollPane should contain a JTextArea", viewportView instanceof JTextArea);
        
        JTextArea textArea = (JTextArea) viewportView;
        assertNotNull("TextArea should not be null", textArea);
        assertTrue("TextArea should be editable", textArea.isEditable());
    }
    
    @Test
    public void testServerDateFormat() {
        // Test that server has proper date format
        // This test accesses the private date field through reflection
        try {
            java.lang.reflect.Field dateField = Server.class.getDeclaredField("date");
            dateField.setAccessible(true);
            SimpleDateFormat date = (SimpleDateFormat) dateField.get(server);
            
            assertNotNull("Date format should not be null", date);
            assertEquals("Date format should be 'hh:mm:ss'", "hh:mm:ss", date.toPattern());
        } catch (Exception e) {
            fail("Could not access date field: " + e.getMessage());
        }
    }
    
    @Test
    public void testServerClientList() {
        // Test that server has a client list
        try {
            java.lang.reflect.Field clientListField = Server.class.getDeclaredField("listOfClients");
            clientListField.setAccessible(true);
            ArrayList<?> clientList = (ArrayList<?>) clientListField.get(server);
            
            assertNotNull("Client list should not be null", clientList);
            assertTrue("Client list should be empty initially", clientList.isEmpty());
        } catch (Exception e) {
            fail("Could not access client list field: " + e.getMessage());
        }
    }
    
    @Test
    public void testServerClientCounter() {
        // Test that server has a client counter
        try {
            java.lang.reflect.Field clientNoField = Server.class.getDeclaredField("clientNo");
            clientNoField.setAccessible(true);
            int clientNo = (int) clientNoField.get(server);
            
            assertEquals("Client counter should start at 0", 0, clientNo);
        } catch (Exception e) {
            fail("Could not access client counter field: " + e.getMessage());
        }
    }
    
    @Test
    public void testServerSocketCreation() {
        // Test that server can create a socket on a test port
        try (ServerSocket testSocket = new ServerSocket(TEST_PORT)) {
            assertNotNull("Test server socket should not be null", testSocket);
            assertTrue("Test server socket should be bound", testSocket.isBound());
            assertEquals("Test server socket should be on correct port", TEST_PORT, testSocket.getLocalPort());
        } catch (IOException e) {
            fail("Could not create test server socket: " + e.getMessage());
        }
    }
    
    @Test
    public void testServerSocketAcceptance() {
        // Test that server can accept connections (simulated)
        try (ServerSocket testSocket = new ServerSocket(TEST_PORT)) {
            // Start a thread to accept connections
            Thread acceptThread = new Thread(() -> {
                try {
                    Socket clientSocket = testSocket.accept();
                    assertNotNull("Accepted client socket should not be null", clientSocket);
                    clientSocket.close();
                } catch (IOException e) {
                    // Expected when test client disconnects
                }
            });
            acceptThread.start();
            
            // Simulate a client connection
            try (Socket clientSocket = new Socket("localhost", TEST_PORT)) {
                assertTrue("Client socket should be connected", clientSocket.isConnected());
                Thread.sleep(100); // Give time for server to accept
            }
            
            acceptThread.join(1000); // Wait for accept thread to finish
        } catch (Exception e) {
            fail("Socket acceptance test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testMessageHandlerCreation() {
        // Test creating MessageHandler objects for server communication
        MessageHandler normalMessage = new MessageHandler("Test message");
        MessageHandler disconnectMessage = new MessageHandler();
        
        assertNotNull("Normal message should not be null", normalMessage);
        assertNotNull("Disconnect message should not be null", disconnectMessage);
        
        assertEquals("Normal message should contain correct text", "Test message", normalMessage.getMessage());
        assertNull("Disconnect message should have null text", disconnectMessage.getMessage());
        
        assertFalse("Normal message should not be disconnect", normalMessage.diconnectClient());
        assertTrue("Disconnect message should be disconnect", disconnectMessage.diconnectClient());
    }
    
    @Test
    public void testMessageHandlerSerialization() throws IOException, ClassNotFoundException {
        // Test that MessageHandler can be serialized for network transmission
        MessageHandler originalMessage = new MessageHandler("Serialization test");
        
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
    public void testServerWindowDisposal() {
        // Test that server window can be disposed properly
        assertTrue("Server should be visible before disposal", server.isVisible());
        
        server.dispose();
        
        assertFalse("Server should not be visible after disposal", server.isVisible());
    }
    
    @Test
    public void testServerDefaultCloseOperation() {
        // Test that server has correct default close operation
        assertEquals("Server should have EXIT_ON_CLOSE as default close operation", 
                     JFrame.EXIT_ON_CLOSE, server.getDefaultCloseOperation());
    }
    
    @Test
    public void testServerWindowSize() {
        // Test server window size constraints
        Dimension size = server.getSize();
        assertNotNull("Server size should not be null", size);
        assertTrue("Server width should be 600", size.width == 600);
        assertTrue("Server height should be 400", size.height == 400);
    }
    
    @Test
    public void testServerWindowLocation() {
        // Test that server window has a valid location
        Point location = server.getLocation();
        assertNotNull("Server location should not be null", location);
        assertTrue("Server x location should be >= 0", location.x >= 0);
        assertTrue("Server y location should be >= 0", location.y >= 0);
    }
} 