package ChatApplication;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Comprehensive test suite for ChatApplication
 * Runs all test cases and provides complete coverage
 */
@RunWith(Suite.class)
@SuiteClasses({
    MessageHandlerTest.class,
    ServerTest.class,
    ClientTest.class,
    IntegrationTest.class,
    PerformanceTest.class
})
public class TestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
    // which tell JUnit to run all the specified test classes
} 