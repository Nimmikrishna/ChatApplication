# ChatApplication

A multi-client chat application in Java with a Swing-based GUI, socket networking, and comprehensive JUnit test coverage.

## Features
- Multi-client chat server and client
- Java Swing GUI for both server and client
- Real-time messaging using TCP sockets
- Object serialization for message transfer
- Robust, multi-threaded server
- Comprehensive JUnit test suite

## Project Structure
```
ChatApplication/
  src/
    ChatApplication/
      Client.java
      Server.java
      MessageHandler.java
  test/
    ChatApplication/
      ClientTest.java
      ServerTest.java
      MessageHandlerTest.java
      IntegrationTest.java
      PerformanceTest.java
      TestSuite.java
      README.md
  lib/
  build.xml
  README.md
  .gitignore
```

## Prerequisites
- Java 8 or higher (JDK)
- [Apache Ant](https://ant.apache.org/) (for easy build/test, optional but recommended)
- Internet connection (for first-time dependency download)

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/ChatApplication.git
cd ChatApplication
```

### 2. Download Dependencies
If using Ant, run:
```bash
ant download-deps
```
Or manually download these JARs into the `lib/` directory:
- [junit-4.12.jar](https://repo1.maven.org/maven2/junit/junit/4.12/junit-4.12.jar)
- [hamcrest-core-1.3.jar](https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar)

### 3. Build the Project
#### Using Ant (Recommended)
```bash
ant compile
```
#### Manual Compilation
```bash
javac -d build/classes src/ChatApplication/*.java
```

### 4. Run the Application
#### Start the Server
```bash
ant run-server
# or manually
java -cp build/classes ChatApplication.Server
```
#### Start a Client (in a new terminal for each client)
```bash
ant run-client
# or manually
java -cp build/classes ChatApplication.Client
```

### 5. Run the Tests
#### Using Ant
```bash
ant test
```
#### Manual JUnit Usage
```bash
javac -cp ".:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar" src/ChatApplication/*.java test/ChatApplication/*.java
java -cp ".:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:src:test" org.junit.runner.JUnitCore ChatApplication.TestSuite
```

### 6. Clean Build Artifacts
```bash
ant clean
```

## .gitignore
A recommended `.gitignore` is provided to avoid tracking build artifacts, IDE files, and dependencies that can be generated locally. See below for a sample:

```
# Compiled class files
/build/
*.class

# Logs
*.log

# Dependency JARs (can be re-downloaded)
lib/junit-*.jar
lib/hamcrest-core-*.jar

# IDE files
*.iml
.idea/
*.ipr
*.iws
*.swp
.DS_Store

# OS generated files
Thumbs.db

# Test reports
build/reports/
```

## Notes
- The test suite is comprehensive and covers unit, integration, and performance tests.
- For more details on the test suite, see `test/ChatApplication/README.md`.
- If you encounter issues with ports in use, change the port numbers in the test files.

## License
[MIT](LICENSE)
