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
import java.util.concurrent.*;
import java.awt.*;
import javax.swing.*;
import ChatApplication.Server;
import ChatApplication.Client;
import ChatApplication.MessageHandler;

/**
 * Integration test cases for ChatApplication
 * Tests complete client-server communication flow and multi-client scenarios
 */
public class IntegrationTest {
    
    private static final int TEST_PORT = 8004;
    private static final String TEST_HOST = "localhost";
    private static final int TIMEOUT = 5000; // 5 seconds timeout
    
    private Server server;
    private Client client1;
    private Client client2;
    private ExecutorService executor;
    
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
        executor = Executors.newCachedThreadPool();
    }
    
    @After
    public void tearDown() {
        // Clean up after each test
        if (client1 != null) {
            client1.dispose();
            client1 = null;
        }
        if (client2 != null) {
            client2.dispose();
            client2 = null;
        }
        if (server != null) {
            server.dispose();
            server = null;
        }
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }
    
    @Test
    public void testServerClientConnection() {
        // Test basic server-client connection
        try {
            // Start server in a separate thread
            Future<?> serverFuture = executor.submit(() -> {
                try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                    Socket clientSocket = serverSocket.accept();
                    assertNotNull("Server should accept client connection", clientSocket);
                    assertTrue("Client socket should be connected", clientSocket.isConnected());
                    clientSocket.close();
                } catch (IOException e) {
                    fail("Server error: " + e.getMessage());
                }
            });
            
            // Wait a bit for server to start
            Thread.sleep(100);
            
            // Connect client
            try (Socket clientSocket = new Socket(TEST_HOST, TEST_PORT)) {
                assertNotNull("Client socket should not be null", clientSocket);
                assertTrue("Client should be connected to server", clientSocket.isConnected());
                assertEquals("Client should be connected to correct port", TEST_PORT, clientSocket.getPort());
            }
            
            // Wait for server to complete
            serverFuture.get(2, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            fail("Server-client connection test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testMessageTransmission() {
        // Test message transmission between client and server
        try {
            // Start server in a separate thread
            Future<?> serverFuture = executor.submit(() -> {
                try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                    Socket clientSocket = serverSocket.accept();
                    
                    // Create streams
                    ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    
                    // Read username
                    String username = (String) inputStream.readObject();
                    assertEquals("Username should be 'TestUser'", "TestUser", username);
                    
                    // Read message
                    MessageHandler message = (MessageHandler) inputStream.readObject();
                    assertEquals("Message should be 'Hello Server'", "Hello Server", message.getMessage());
                    assertFalse("Message should not be disconnect", message.diconnectClient());
                    
                    // Send response
                    outputStream.writeObject("Message received: " + message.getMessage());
                    
                    clientSocket.close();
                } catch (Exception e) {
                    fail("Server error: " + e.getMessage());
                }
            });
            
            // Wait a bit for server to start
            Thread.sleep(100);
            
            // Connect client and send message
            try (Socket clientSocket = new Socket(TEST_HOST, TEST_PORT)) {
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                
                // Send username
                outputStream.writeObject("TestUser");
                
                // Send message
                MessageHandler message = new MessageHandler("Hello Server");
                outputStream.writeObject(message);
                
                // Read response
                String response = (String) inputStream.readObject();
                assertEquals("Response should match expected", "Message received: Hello Server", response);
            }
            
            // Wait for server to complete
            serverFuture.get(3, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            fail("Message transmission test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testMultipleClientConnections() {
        // Test multiple clients connecting to server
        try {
            // Start server in a separate thread
            Future<?> serverFuture = executor.submit(() -> {
                try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                    // Accept first client
                    Socket client1Socket = serverSocket.accept();
                    assertNotNull("First client socket should not be null", client1Socket);
                    
                    // Accept second client
                    Socket client2Socket = serverSocket.accept();
                    assertNotNull("Second client socket should not be null", client2Socket);
                    
                    // Verify both clients are connected
                    assertTrue("First client should be connected", client1Socket.isConnected());
                    assertTrue("Second client should be connected", client2Socket.isConnected());
                    
                    client1Socket.close();
                    client2Socket.close();
                } catch (IOException e) {
                    fail("Server error: " + e.getMessage());
                }
            });
            
            // Wait a bit for server to start
            Thread.sleep(100);
            
            // Connect first client
            try (Socket client1Socket = new Socket(TEST_HOST, TEST_PORT)) {
                assertTrue("First client should be connected", client1Socket.isConnected());
                
                // Connect second client
                try (Socket client2Socket = new Socket(TEST_HOST, TEST_PORT)) {
                    assertTrue("Second client should be connected", client2Socket.isConnected());
                }
            }
            
            // Wait for server to complete
            serverFuture.get(3, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            fail("Multiple client connections test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testClientDisconnect() {
        // Test client disconnect functionality
        try {
            // Start server in a separate thread
            Future<?> serverFuture = executor.submit(() -> {
                try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                    Socket clientSocket = serverSocket.accept();
                    
                    ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    
                    // Read username
                    String username = (String) inputStream.readObject();
                    assertEquals("Username should be 'TestUser'", "TestUser", username);
                    
                    // Read disconnect message
                    MessageHandler disconnectMessage = (MessageHandler) inputStream.readObject();
                    assertTrue("Message should be disconnect", disconnectMessage.diconnectClient());
                    assertNull("Disconnect message should have null text", disconnectMessage.getMessage());
                    
                    clientSocket.close();
                } catch (Exception e) {
                    fail("Server error: " + e.getMessage());
                }
            });
            
            // Wait a bit for server to start
            Thread.sleep(100);
            
            // Connect client and send disconnect
            try (Socket clientSocket = new Socket(TEST_HOST, TEST_PORT)) {
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                
                // Send username
                outputStream.writeObject("TestUser");
                
                // Send disconnect message
                MessageHandler disconnectMessage = new MessageHandler();
                outputStream.writeObject(disconnectMessage);
            }
            
            // Wait for server to complete
            serverFuture.get(3, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            fail("Client disconnect test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testMessageBroadcasting() {
        // Test message broadcasting to multiple clients
        try {
            // Start server in a separate thread
            Future<?> serverFuture = executor.submit(() -> {
                try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                    // Accept two clients
                    Socket client1Socket = serverSocket.accept();
                    Socket client2Socket = serverSocket.accept();
                    
                    ObjectOutputStream output1 = new ObjectOutputStream(client1Socket.getOutputStream());
                    ObjectOutputStream output2 = new ObjectOutputStream(client2Socket.getOutputStream());
                    ObjectInputStream input1 = new ObjectInputStream(client1Socket.getInputStream());
                    ObjectInputStream input2 = new ObjectInputStream(client2Socket.getInputStream());
                    
                    // Read usernames
                    String username1 = (String) input1.readObject();
                    String username2 = (String) input2.readObject();
                    
                    // Read messages from both clients
                    MessageHandler message1 = (MessageHandler) input1.readObject();
                    MessageHandler message2 = (MessageHandler) input2.readObject();
                    
                    // Broadcast message to both clients
                    String broadcastMessage = "Broadcast: " + message1.getMessage() + " and " + message2.getMessage();
                    output1.writeObject(broadcastMessage);
                    output2.writeObject(broadcastMessage);
                    
                    client1Socket.close();
                    client2Socket.close();
                } catch (Exception e) {
                    fail("Server error: " + e.getMessage());
                }
            });
            
            // Wait a bit for server to start
            Thread.sleep(100);
            
            // Connect first client
            try (Socket client1Socket = new Socket(TEST_HOST, TEST_PORT)) {
                ObjectOutputStream output1 = new ObjectOutputStream(client1Socket.getOutputStream());
                ObjectInputStream input1 = new ObjectInputStream(client1Socket.getInputStream());
                
                // Send username and message
                output1.writeObject("User1");
                output1.writeObject(new MessageHandler("Hello from User1"));
                
                // Read broadcast message
                String broadcast1 = (String) input1.readObject();
                assertTrue("Broadcast should contain User1 message", broadcast1.contains("Hello from User1"));
                
                // Connect second client
                try (Socket client2Socket = new Socket(TEST_HOST, TEST_PORT)) {
                    ObjectOutputStream output2 = new ObjectOutputStream(client2Socket.getOutputStream());
                    ObjectInputStream input2 = new ObjectInputStream(client2Socket.getInputStream());
                    
                    // Send username and message
                    output2.writeObject("User2");
                    output2.writeObject(new MessageHandler("Hello from User2"));
                    
                    // Read broadcast message
                    String broadcast2 = (String) input2.readObject();
                    assertTrue("Broadcast should contain User2 message", broadcast2.contains("Hello from User2"));
                }
            }
            
            // Wait for server to complete
            serverFuture.get(5, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            fail("Message broadcasting test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testConcurrentMessageHandling() {
        // Test handling multiple messages concurrently
        try {
            // Start server in a separate thread
            Future<?> serverFuture = executor.submit(() -> {
                try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                    Socket clientSocket = serverSocket.accept();
                    ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    
                    // Read username
                    String username = (String) inputStream.readObject();
                    
                    // Read multiple messages
                    for (int i = 0; i < 5; i++) {
                        MessageHandler message = (MessageHandler) inputStream.readObject();
                        assertEquals("Message " + i + " should be correct", 
                                   "Message " + i, message.getMessage());
                        
                        // Send acknowledgment
                        outputStream.writeObject("Ack: " + message.getMessage());
                    }
                    
                    clientSocket.close();
                } catch (Exception e) {
                    fail("Server error: " + e.getMessage());
                }
            });
            
            // Wait a bit for server to start
            Thread.sleep(100);
            
            // Connect client and send multiple messages
            try (Socket clientSocket = new Socket(TEST_HOST, TEST_PORT)) {
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                
                // Send username
                outputStream.writeObject("TestUser");
                
                // Send multiple messages
                for (int i = 0; i < 5; i++) {
                    MessageHandler message = new MessageHandler("Message " + i);
                    outputStream.writeObject(message);
                    
                    // Read acknowledgment
                    String ack = (String) inputStream.readObject();
                    assertEquals("Acknowledgment should match", "Ack: Message " + i, ack);
                }
            }
            
            // Wait for server to complete
            serverFuture.get(5, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            fail("Concurrent message handling test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testLargeMessageHandling() {
        // Test handling large messages
        try {
            // Create a large message
            StringBuilder largeMessage = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                largeMessage.append("This is a large message part ").append(i).append(". ");
            }
            String largeMessageStr = largeMessage.toString();
            
            // Start server in a separate thread
            Future<?> serverFuture = executor.submit(() -> {
                try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                    Socket clientSocket = serverSocket.accept();
                    ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    
                    // Read username
                    String username = (String) inputStream.readObject();
                    
                    // Read large message
                    MessageHandler message = (MessageHandler) inputStream.readObject();
                    assertEquals("Large message should match", largeMessageStr, message.getMessage());
                    
                    // Send confirmation
                    outputStream.writeObject("Large message received successfully");
                    
                    clientSocket.close();
                } catch (Exception e) {
                    fail("Server error: " + e.getMessage());
                }
            });
            
            // Wait a bit for server to start
            Thread.sleep(100);
            
            // Connect client and send large message
            try (Socket clientSocket = new Socket(TEST_HOST, TEST_PORT)) {
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                
                // Send username
                outputStream.writeObject("TestUser");
                
                // Send large message
                MessageHandler message = new MessageHandler(largeMessageStr);
                outputStream.writeObject(message);
                
                // Read confirmation
                String confirmation = (String) inputStream.readObject();
                assertEquals("Confirmation should match", "Large message received successfully", confirmation);
            }
            
            // Wait for server to complete
            serverFuture.get(10, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            fail("Large message handling test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testSpecialCharacterHandling() {
        // Test handling messages with special characters
        try {
            String specialMessage = "Special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?\\`~";
            
            // Start server in a separate thread
            Future<?> serverFuture = executor.submit(() -> {
                try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                    Socket clientSocket = serverSocket.accept();
                    ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    
                    // Read username
                    String username = (String) inputStream.readObject();
                    
                    // Read special character message
                    MessageHandler message = (MessageHandler) inputStream.readObject();
                    assertEquals("Special character message should match", specialMessage, message.getMessage());
                    
                    // Send confirmation
                    outputStream.writeObject("Special characters handled correctly");
                    
                    clientSocket.close();
                } catch (Exception e) {
                    fail("Server error: " + e.getMessage());
                }
            });
            
            // Wait a bit for server to start
            Thread.sleep(100);
            
            // Connect client and send special character message
            try (Socket clientSocket = new Socket(TEST_HOST, TEST_PORT)) {
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                
                // Send username
                outputStream.writeObject("TestUser");
                
                // Send special character message
                MessageHandler message = new MessageHandler(specialMessage);
                outputStream.writeObject(message);
                
                // Read confirmation
                String confirmation = (String) inputStream.readObject();
                assertEquals("Confirmation should match", "Special characters handled correctly", confirmation);
            }
            
            // Wait for server to complete
            serverFuture.get(5, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            fail("Special character handling test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testNetworkErrorHandling() {
        // Test handling network errors gracefully
        try {
            // Try to connect to a non-existent server
            try (Socket clientSocket = new Socket(TEST_HOST, 9999)) {
                fail("Should not be able to connect to non-existent server");
            } catch (ConnectException e) {
                // Expected - connection should be refused
                assertTrue("Connection should be refused", e.getMessage().contains("Connection refused"));
            }
        } catch (Exception e) {
            fail("Network error handling test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testSerializationErrorHandling() {
        // Test handling serialization errors
        try {
            // Test with null message
            MessageHandler nullMessage = new MessageHandler(null);
            assertNull("Null message should have null text", nullMessage.getMessage());
            assertFalse("Null message should not be disconnect", nullMessage.diconnectClient());
            
            // Test serialization of null message
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(nullMessage);
            oos.close();
            
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            MessageHandler deserializedNull = (MessageHandler) ois.readObject();
            ois.close();
            
            assertNull("Deserialized null message should have null text", deserializedNull.getMessage());
            
        } catch (Exception e) {
            fail("Serialization error handling test failed: " + e.getMessage());
        }
    }
} 