package tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.params.provider.Arguments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Provides test data for car valuation tests.
 */
public class TestDataProvider {

    private static final Logger logger = LogManager.getLogger(TestDataProvider.class);
    private static final Path INPUT_FILE = Paths.get("src/test/resources/cleaned_test_data.txt");

    /**
     * Provides car data from the cleaned input file.
     *
     * @return a stream of arguments for parameterized tests
     * @throws IOException if an I/O error occurs
     */
    public static Stream<Arguments> carDataProvider() throws IOException {
        try {
            return Files.lines(INPUT_FILE)
                    .skip(1) // Skip the header line
                    .filter(line -> !line.trim().isEmpty()) // Filter out empty lines
                    .map(line -> {
                        String[] parts = line.split(",");
                        return Arguments.of(parts[0], parts[1]);
                    });
        } catch (IOException e) {
            logger.error("Error reading input file: " + INPUT_FILE, e);
            throw e;
        }
    }
}