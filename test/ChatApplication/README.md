# ChatApplication Test Suite

This directory contains comprehensive JUnit test cases for the ChatApplication project.

## Test Structure

### 1. MessageHandlerTest.java
Tests the `MessageHandler` class functionality:
- Message creation with various inputs (normal, empty, null, special characters)
- Disconnect message handling
- Object serialization and deserialization
- Method functionality (`getMessage()`, `diconnectClient()`)

**Test Cases:**
- `testMessageHandlerWithMessage()` - Tests normal message creation
- `testMessageHandlerWithoutMessage()` - Tests disconnect message creation
- `testMessageHandlerWithEmptyString()` - Tests empty string handling
- `testMessageHandlerWithNullMessage()` - Tests null message handling
- `testMessageHandlerWithSpecialCharacters()` - Tests special character handling
- `testMessageHandlerWithLongMessage()` - Tests long message handling
- `testSerialization()` - Tests object serialization
- `testDisconnectSerialization()` - Tests disconnect message serialization
- `testMultipleMessageHandlers()` - Tests multiple handler instances
- `testGetMessageMethod()` - Tests getMessage() method
- `testDisconnectClientMethod()` - Tests diconnectClient() method

### 2. ServerTest.java
Tests the `Server` class functionality:
- Server initialization and GUI components
- Client list management
- Socket creation and acceptance
- Message broadcasting capabilities

**Test Cases:**
- `testServerInitialization()` - Tests server setup
- `testServerWindowProperties()` - Tests GUI properties
- `testServerLayout()` - Tests layout management
- `testServerComponents()` - Tests component creation
- `testServerTextArea()` - Tests text area functionality
- `testServerDateFormat()` - Tests date formatting
- `testServerClientList()` - Tests client list management
- `testServerClientCounter()` - Tests client counting
- `testServerSocketCreation()` - Tests socket creation
- `testServerSocketAcceptance()` - Tests connection acceptance
- `testMessageHandlerCreation()` - Tests message handling
- `testMessageHandlerSerialization()` - Tests message serialization
- `testServerWindowDisposal()` - Tests window cleanup
- `testServerDefaultCloseOperation()` - Tests close behavior
- `testServerWindowSize()` - Tests window sizing
- `testServerWindowLocation()` - Tests window positioning

### 3. ClientTest.java
Tests the `Client` class functionality:
- Client GUI initialization and components
- Event handling and user interactions
- Network connection capabilities
- Message sending and receiving

**Test Cases:**
- `testClientInitialization()` - Tests client setup
- `testClientWindowProperties()` - Tests GUI properties
- `testClientLayout()` - Tests layout management
- `testClientComponents()` - Tests component creation
- `testClientTopPanel()` - Tests top panel functionality
- `testClientChatPanel()` - Tests chat panel functionality
- `testClientTextFields()` - Tests text field components
- `testClientTextArea()` - Tests text area functionality
- `testClientButtons()` - Tests button components
- `testClientPanels()` - Tests panel management
- `testClientStartClient()` - Tests client startup
- `testClientWindowDisposal()` - Tests window cleanup
- `testClientDefaultCloseOperation()` - Tests close behavior
- `testClientWindowSize()` - Tests window sizing
- `testClientWindowLocation()` - Tests window positioning
- `testClientSocketConnection()` - Tests network connection
- `testClientStreamCreation()` - Tests stream creation
- `testMessageHandlerForClient()` - Tests message handling
- `testClientMessageSerialization()` - Tests message serialization
- `testClientUsernameField()` - Tests username field
- `testClientSendBoxField()` - Tests send box field
- `testClientChatLogArea()` - Tests chat log area

### 4. IntegrationTest.java
Tests complete client-server communication:
- End-to-end message transmission
- Multiple client scenarios
- Disconnect handling
- Message broadcasting
- Error handling

**Test Cases:**
- `testServerClientConnection()` - Tests basic connection
- `testMessageTransmission()` - Tests message sending/receiving
- `testMultipleClientConnections()` - Tests multiple clients
- `testClientDisconnect()` - Tests disconnect functionality
- `testMessageBroadcasting()` - Tests message broadcasting
- `testConcurrentMessageHandling()` - Tests concurrent processing
- `testLargeMessageHandling()` - Tests large message handling
- `testSpecialCharacterHandling()` - Tests special characters
- `testNetworkErrorHandling()` - Tests error scenarios
- `testSerializationErrorHandling()` - Tests serialization errors

### 5. PerformanceTest.java
Tests application performance under load:
- Single and multiple client performance
- Stress testing with many clients
- Memory usage analysis
- Thread usage monitoring
- Large message handling

**Test Cases:**
- `testSingleClientPerformance()` - Tests single client performance
- `testMultipleClientPerformance()` - Tests multiple client performance
- `testStressTest()` - Tests stress scenarios
- `testMemoryUsage()` - Tests memory consumption
- `testThreadUsage()` - Tests thread management
- `testConcurrentConnectionHandling()` - Tests concurrent connections
- `testLargeMessagePerformance()` - Tests large message performance

### 6. TestSuite.java
Comprehensive test suite that runs all test classes.

## Running the Tests

### Prerequisites
- Java 8 or higher
- JUnit 4.12 or higher
- The ChatApplication classes in the src/ChatApplication directory

### Command Line
```bash
# Compile the test classes
javac -cp ".:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar" src/ChatApplication/*.java test/ChatApplication/*.java

# Run all tests
java -cp ".:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore ChatApplication.TestSuite

# Run individual test classes
java -cp ".:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore ChatApplication.MessageHandlerTest
java -cp ".:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore ChatApplication.ServerTest
java -cp ".:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore ChatApplication.ClientTest
java -cp ".:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore ChatApplication.IntegrationTest
java -cp ".:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore ChatApplication.PerformanceTest
```

### IDE (Eclipse/IntelliJ)
1. Add JUnit 4 to your project classpath
2. Right-click on the test/ChatApplication directory or individual test files
3. Select "Run As" > "JUnit Test"

## Test Coverage

The test suite provides comprehensive coverage for:

### Functional Testing
- ✅ Message creation and handling
- ✅ Client-server communication
- ✅ GUI component functionality
- ✅ Event handling
- ✅ Network connectivity
- ✅ Message broadcasting
- ✅ Disconnect handling

### Non-Functional Testing
- ✅ Performance under load
- ✅ Memory usage patterns
- ✅ Thread management
- ✅ Large message handling
- ✅ Concurrent operations
- ✅ Error scenarios

### Edge Cases
- ✅ Null and empty messages
- ✅ Special characters
- ✅ Network errors
- ✅ Serialization errors
- ✅ Multiple client scenarios

## Expected Results

When all tests pass, you should see output similar to:
```
JUnit version 4.12
........................
Time: 15.234

OK (24 tests)
```

## Troubleshooting

### Common Issues

1. **ClassNotFoundException**: Ensure JUnit is in your classpath
2. **Connection refused**: Some integration tests require network access
3. **Timeout errors**: Performance tests may take longer on slower machines
4. **Memory errors**: Increase JVM heap size for performance tests

### Performance Test Notes
- Performance tests may take 30+ seconds to complete
- Memory usage tests may trigger garbage collection
- Stress tests create many network connections
- Some tests require the server to be running on specific ports

## Adding New Tests

To add new test cases:

1. Create a new test method with the `@Test` annotation
2. Follow the naming convention: `test[FeatureName]()`
3. Add appropriate assertions using JUnit's assert methods
4. Include setup/teardown if needed with `@Before`/`@After`
5. Add the test class to `TestSuite.java` if it's a new test class

## Test Data

The tests use various test data:
- Normal messages: "Hello World", "Test message"
- Special characters: "!@#$%^&*()_+-=[]{}|;':\",./<>?"
- Long messages: 1000+ character strings
- Usernames: "TestUser", "User1", "User2", etc.
- Port numbers: 8001-8005 (different ports for different test types) 