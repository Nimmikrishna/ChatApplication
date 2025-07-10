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
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import ChatApplication.Server;
import ChatApplication.Client;
import ChatApplication.MessageHandler;

/**
 * Performance test cases for ChatApplication
 * Tests application performance under load, stress testing, and memory usage
 */
public class PerformanceTest {
    
    private static final int TEST_PORT = 8005;
    private static final String TEST_HOST = "localhost";
    private static final int STRESS_TEST_CLIENTS = 10;
    private static final int STRESS_TEST_MESSAGES = 100;
    private static final int TIMEOUT = 30000; // 30 seconds timeout
    
    private Server server;
    private List<Client> clients;
    private ExecutorService executor;
    private MemoryMXBean memoryBean;
    private ThreadMXBean threadBean;
    
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
        clients = new ArrayList<>();
        memoryBean = ManagementFactory.getMemoryMXBean();
        threadBean = ManagementFactory.getThreadMXBean();
    }
    
    @After
    public void tearDown() {
        // Clean up after each test
        if (clients != null) {
            for (Client client : clients) {
                if (client != null) {
                    client.dispose();
                }
            }
            clients.clear();
        }
        if (server != null) {
            server.dispose();
            server = null;
        }
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }
    
    @Test
    public void testSingleClientPerformance() {
        // Test performance with single client
        long startTime = System.currentTimeMillis();
        long startMemory = memoryBean.getHeapMemoryUsage().getUsed();
        
        try {
            // Start server
            Future<?> serverFuture = executor.submit(() -> {
                try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                    Socket clientSocket = serverSocket.accept();
                    ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    
                    // Read username
                    String username = (String) inputStream.readObject();
                    
                    // Process messages
                    for (int i = 0; i < 100; i++) {
                        MessageHandler message = (MessageHandler) inputStream.readObject();
                        outputStream.writeObject("Processed: " + message.getMessage());
                    }
                    
                    clientSocket.close();
                } catch (Exception e) {
                    fail("Server error: " + e.getMessage());
                }
            });
            
            // Wait for server to start
            Thread.sleep(100);
            
            // Connect client and send messages
            try (Socket clientSocket = new Socket(TEST_HOST, TEST_PORT)) {
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                
                outputStream.writeObject("TestUser");
                
                for (int i = 0; i < 100; i++) {
                    MessageHandler message = new MessageHandler("Message " + i);
                    outputStream.writeObject(message);
                    
                    String response = (String) inputStream.readObject();
                    assertEquals("Response should match", "Processed: Message " + i, response);
                }
            }
            
            serverFuture.get(10, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            fail("Single client performance test failed: " + e.getMessage());
        }
        
        long endTime = System.currentTimeMillis();
        long endMemory = memoryBean.getHeapMemoryUsage().getUsed();
        
        long duration = endTime - startTime;
        long memoryUsed = endMemory - startMemory;
        
        System.out.println("Single Client Performance Test:");
        System.out.println("Duration: " + duration + "ms");
        System.out.println("Memory used: " + memoryUsed + " bytes");
        System.out.println("Messages per second: " + (100.0 / (duration / 1000.0)));
        
        // Performance assertions
        assertTrue("Test should complete within reasonable time", duration < 10000);
        assertTrue("Memory usage should be reasonable", memoryUsed < 10 * 1024 * 1024); // 10MB
    }
    
    @Test
    public void testMultipleClientPerformance() {
        // Test performance with multiple clients
        long startTime = System.currentTimeMillis();
        long startMemory = memoryBean.getHeapMemoryUsage().getUsed();
        
        try {
            // Start server
            Future<?> serverFuture = executor.submit(() -> {
                try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                    List<Socket> clientSockets = new ArrayList<>();
                    List<ObjectOutputStream> outputs = new ArrayList<>();
                    List<ObjectInputStream> inputs = new ArrayList<>();
                    
                    // Accept multiple clients
                    for (int i = 0; i < 5; i++) {
                        Socket clientSocket = serverSocket.accept();
                        clientSockets.add(clientSocket);
                        outputs.add(new ObjectOutputStream(clientSocket.getOutputStream()));
                        inputs.add(new ObjectInputStream(clientSocket.getInputStream()));
                        
                        // Read username
                        String username = (String) inputs.get(i).readObject();
                    }
                    
                    // Process messages from all clients
                    for (int msg = 0; msg < 20; msg++) {
                        for (int client = 0; client < 5; client++) {
                            MessageHandler message = (MessageHandler) inputs.get(client).readObject();
                            outputs.get(client).writeObject("Processed: " + message.getMessage());
                        }
                    }
                    
                    // Close all connections
                    for (Socket socket : clientSockets) {
                        socket.close();
                    }
                } catch (Exception e) {
                    fail("Server error: " + e.getMessage());
                }
            });
            
            // Wait for server to start
            Thread.sleep(100);
            
            // Connect multiple clients
            List<Future<?>> clientFutures = new ArrayList<>();
            
            for (int clientId = 0; clientId < 5; clientId++) {
                final int id = clientId;
                Future<?> clientFuture = executor.submit(() -> {
                    try (Socket clientSocket = new Socket(TEST_HOST, TEST_PORT)) {
                        ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                        ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                        
                        outputStream.writeObject("User" + id);
                        
                        for (int i = 0; i < 20; i++) {
                            MessageHandler message = new MessageHandler("Message " + i + " from User" + id);
                            outputStream.writeObject(message);
                            
                            String response = (String) inputStream.readObject();
                            assertEquals("Response should match", "Processed: Message " + i + " from User" + id, response);
                        }
                    } catch (Exception e) {
                        fail("Client " + id + " error: " + e.getMessage());
                    }
                });
                clientFutures.add(clientFuture);
            }
            
            // Wait for all clients to complete
            for (Future<?> future : clientFutures) {
                future.get(15, TimeUnit.SECONDS);
            }
            
            serverFuture.get(20, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            fail("Multiple client performance test failed: " + e.getMessage());
        }
        
        long endTime = System.currentTimeMillis();
        long endMemory = memoryBean.getHeapMemoryUsage().getUsed();
        
        long duration = endTime - startTime;
        long memoryUsed = endMemory - startMemory;
        
        System.out.println("Multiple Client Performance Test:");
        System.out.println("Duration: " + duration + "ms");
        System.out.println("Memory used: " + memoryUsed + " bytes");
        System.out.println("Total messages: " + (5 * 20));
        System.out.println("Messages per second: " + (100.0 / (duration / 1000.0)));
        
        // Performance assertions
        assertTrue("Test should complete within reasonable time", duration < 20000);
        assertTrue("Memory usage should be reasonable", memoryUsed < 20 * 1024 * 1024); // 20MB
    }
    
    @Test
    public void testStressTest() {
        // Stress test with many clients and messages
        long startTime = System.currentTimeMillis();
        long startMemory = memoryBean.getHeapMemoryUsage().getUsed();
        
        try {
            // Start server
            Future<?> serverFuture = executor.submit(() -> {
                try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                    List<Socket> clientSockets = new ArrayList<>();
                    List<ObjectOutputStream> outputs = new ArrayList<>();
                    List<ObjectInputStream> inputs = new ArrayList<>();
                    
                    // Accept many clients
                    for (int i = 0; i < STRESS_TEST_CLIENTS; i++) {
                        Socket clientSocket = serverSocket.accept();
                        clientSockets.add(clientSocket);
                        outputs.add(new ObjectOutputStream(clientSocket.getOutputStream()));
                        inputs.add(new ObjectInputStream(clientSocket.getInputStream()));
                        
                        // Read username
                        String username = (String) inputs.get(i).readObject();
                    }
                    
                    // Process many messages from all clients
                    for (int msg = 0; msg < STRESS_TEST_MESSAGES; msg++) {
                        for (int client = 0; client < STRESS_TEST_CLIENTS; client++) {
                            MessageHandler message = (MessageHandler) inputs.get(client).readObject();
                            outputs.get(client).writeObject("Processed: " + message.getMessage());
                        }
                    }
                    
                    // Close all connections
                    for (Socket socket : clientSockets) {
                        socket.close();
                    }
                } catch (Exception e) {
                    fail("Server error: " + e.getMessage());
                }
            });
            
            // Wait for server to start
            Thread.sleep(100);
            
            // Connect many clients
            List<Future<?>> clientFutures = new ArrayList<>();
            
            for (int clientId = 0; clientId < STRESS_TEST_CLIENTS; clientId++) {
                final int id = clientId;
                Future<?> clientFuture = executor.submit(() -> {
                    try (Socket clientSocket = new Socket(TEST_HOST, TEST_PORT)) {
                        ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                        ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                        
                        outputStream.writeObject("StressUser" + id);
                        
                        for (int i = 0; i < STRESS_TEST_MESSAGES; i++) {
                            MessageHandler message = new MessageHandler("Stress message " + i + " from User" + id);
                            outputStream.writeObject(message);
                            
                            String response = (String) inputStream.readObject();
                            assertEquals("Response should match", "Processed: Stress message " + i + " from User" + id, response);
                        }
                    } catch (Exception e) {
                        fail("Stress client " + id + " error: " + e.getMessage());
                    }
                });
                clientFutures.add(clientFuture);
            }
            
            // Wait for all clients to complete
            for (Future<?> future : clientFutures) {
                future.get(TIMEOUT, TimeUnit.MILLISECONDS);
            }
            
            serverFuture.get(TIMEOUT, TimeUnit.MILLISECONDS);
            
        } catch (Exception e) {
            fail("Stress test failed: " + e.getMessage());
        }
        
        long endTime = System.currentTimeMillis();
        long endMemory = memoryBean.getHeapMemoryUsage().getUsed();
        
        long duration = endTime - startTime;
        long memoryUsed = endMemory - startMemory;
        int totalMessages = STRESS_TEST_CLIENTS * STRESS_TEST_MESSAGES;
        
        System.out.println("Stress Test Results:");
        System.out.println("Clients: " + STRESS_TEST_CLIENTS);
        System.out.println("Messages per client: " + STRESS_TEST_MESSAGES);
        System.out.println("Total messages: " + totalMessages);
        System.out.println("Duration: " + duration + "ms");
        System.out.println("Memory used: " + memoryUsed + " bytes");
        System.out.println("Messages per second: " + (totalMessages / (duration / 1000.0)));
        
        // Stress test assertions
        assertTrue("Stress test should complete within timeout", duration < TIMEOUT);
        assertTrue("Memory usage should be reasonable under stress", memoryUsed < 50 * 1024 * 1024); // 50MB
    }
    
    @Test
    public void testMemoryUsage() {
        // Test memory usage patterns
        long initialMemory = memoryBean.getHeapMemoryUsage().getUsed();
        
        try {
            // Create many MessageHandler objects
            List<MessageHandler> messages = new ArrayList<>();
            
            for (int i = 0; i < 1000; i++) {
                messages.add(new MessageHandler("Test message " + i));
            }
            
            long afterCreationMemory = memoryBean.getHeapMemoryUsage().getUsed();
            long creationMemoryUsed = afterCreationMemory - initialMemory;
            
            // Test serialization memory usage
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            
            for (MessageHandler message : messages) {
                oos.writeObject(message);
            }
            oos.close();
            
            long afterSerializationMemory = memoryBean.getHeapMemoryUsage().getUsed();
            long serializationMemoryUsed = afterSerializationMemory - afterCreationMemory;
            
            // Clear references
            messages.clear();
            baos = null;
            oos = null;
            
            // Force garbage collection
            System.gc();
            Thread.sleep(100);
            
            long finalMemory = memoryBean.getHeapMemoryUsage().getUsed();
            long finalMemoryUsed = finalMemory - initialMemory;
            
            System.out.println("Memory Usage Test:");
            System.out.println("Initial memory: " + initialMemory + " bytes");
            System.out.println("After creation: " + afterCreationMemory + " bytes");
            System.out.println("After serialization: " + afterSerializationMemory + " bytes");
            System.out.println("Final memory: " + finalMemory + " bytes");
            System.out.println("Creation memory used: " + creationMemoryUsed + " bytes");
            System.out.println("Serialization memory used: " + serializationMemoryUsed + " bytes");
            System.out.println("Final memory used: " + finalMemoryUsed + " bytes");
            
            // Memory usage assertions
            assertTrue("Creation should use reasonable memory", creationMemoryUsed < 1024 * 1024); // 1MB
            assertTrue("Serialization should use reasonable memory", serializationMemoryUsed < 1024 * 1024); // 1MB
            assertTrue("Final memory usage should be low", finalMemoryUsed < 512 * 1024); // 512KB
            
        } catch (Exception e) {
            fail("Memory usage test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testThreadUsage() {
        // Test thread usage patterns
        long initialThreadCount = threadBean.getThreadCount();
        
        try {
            // Create many threads
            List<Thread> threads = new ArrayList<>();
            
            for (int i = 0; i < 10; i++) {
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                threads.add(thread);
                thread.start();
            }
            
            // Wait for threads to complete
            for (Thread thread : threads) {
                thread.join();
            }
            
            long finalThreadCount = threadBean.getThreadCount();
            long threadIncrease = finalThreadCount - initialThreadCount;
            
            System.out.println("Thread Usage Test:");
            System.out.println("Initial thread count: " + initialThreadCount);
            System.out.println("Final thread count: " + finalThreadCount);
            System.out.println("Thread increase: " + threadIncrease);
            
            // Thread usage assertions
            assertTrue("Thread count should be reasonable", finalThreadCount < 100);
            assertTrue("Thread increase should be minimal", threadIncrease < 20);
            
        } catch (Exception e) {
            fail("Thread usage test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testConcurrentConnectionHandling() {
        // Test handling many concurrent connections
        long startTime = System.currentTimeMillis();
        
        try {
            // Start server
            Future<?> serverFuture = executor.submit(() -> {
                try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                    List<Socket> clientSockets = new ArrayList<>();
                    
                    // Accept many concurrent connections
                    for (int i = 0; i < 20; i++) {
                        Socket clientSocket = serverSocket.accept();
                        clientSockets.add(clientSocket);
                        
                        // Read username
                        ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                        String username = (String) input.readObject();
                        
                        // Send acknowledgment
                        ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                        output.writeObject("Connected: " + username);
                    }
                    
                    // Close all connections
                    for (Socket socket : clientSockets) {
                        socket.close();
                    }
                } catch (Exception e) {
                    fail("Server error: " + e.getMessage());
                }
            });
            
            // Wait for server to start
            Thread.sleep(100);
            
            // Connect many clients concurrently
            List<Future<?>> clientFutures = new ArrayList<>();
            
            for (int clientId = 0; clientId < 20; clientId++) {
                final int id = clientId;
                Future<?> clientFuture = executor.submit(() -> {
                    try (Socket clientSocket = new Socket(TEST_HOST, TEST_PORT)) {
                        ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                        ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                        
                        outputStream.writeObject("ConcurrentUser" + id);
                        
                        String response = (String) inputStream.readObject();
                        assertEquals("Response should match", "Connected: ConcurrentUser" + id, response);
                    } catch (Exception e) {
                        fail("Concurrent client " + id + " error: " + e.getMessage());
                    }
                });
                clientFutures.add(clientFuture);
            }
            
            // Wait for all clients to complete
            for (Future<?> future : clientFutures) {
                future.get(10, TimeUnit.SECONDS);
            }
            
            serverFuture.get(15, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            fail("Concurrent connection handling test failed: " + e.getMessage());
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Concurrent Connection Handling Test:");
        System.out.println("Connections: 20");
        System.out.println("Duration: " + duration + "ms");
        System.out.println("Connections per second: " + (20.0 / (duration / 1000.0)));
        
        // Performance assertions
        assertTrue("Concurrent connections should be handled efficiently", duration < 10000);
    }
    
    @Test
    public void testLargeMessagePerformance() {
        // Test performance with large messages
        long startTime = System.currentTimeMillis();
        
        // Create large message
        StringBuilder largeMessage = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeMessage.append("Large message content ").append(i).append(". ");
        }
        String largeMessageStr = largeMessage.toString();
        
        try {
            // Start server
            Future<?> serverFuture = executor.submit(() -> {
                try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                    Socket clientSocket = serverSocket.accept();
                    ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    
                    // Read username
                    String username = (String) inputStream.readObject();
                    
                    // Process large messages
                    for (int i = 0; i < 10; i++) {
                        MessageHandler message = (MessageHandler) inputStream.readObject();
                        assertEquals("Large message should match", largeMessageStr, message.getMessage());
                        outputStream.writeObject("Large message " + i + " processed");
                    }
                    
                    clientSocket.close();
                } catch (Exception e) {
                    fail("Server error: " + e.getMessage());
                }
            });
            
            // Wait for server to start
            Thread.sleep(100);
            
            // Connect client and send large messages
            try (Socket clientSocket = new Socket(TEST_HOST, TEST_PORT)) {
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                
                outputStream.writeObject("LargeMessageUser");
                
                for (int i = 0; i < 10; i++) {
                    MessageHandler message = new MessageHandler(largeMessageStr);
                    outputStream.writeObject(message);
                    
                    String response = (String) inputStream.readObject();
                    assertEquals("Response should match", "Large message " + i + " processed", response);
                }
            }
            
            serverFuture.get(30, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            fail("Large message performance test failed: " + e.getMessage());
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Large Message Performance Test:");
        System.out.println("Message size: " + largeMessageStr.length() + " characters");
        System.out.println("Messages sent: 10");
        System.out.println("Duration: " + duration + "ms");
        System.out.println("Data transfer rate: " + (largeMessageStr.length() * 10 / (duration / 1000.0)) + " chars/sec");
        
        // Performance assertions
        assertTrue("Large messages should be handled within reasonable time", duration < 30000);
    }
} 