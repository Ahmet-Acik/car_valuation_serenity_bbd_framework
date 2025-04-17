package tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Extracts vehicle registration numbers from input files and categorizes them as valid or invalid.
 */
public class VehicleRegistrationExtractor {

    private static final Logger logger = LogManager.getLogger(VehicleRegistrationExtractor.class);
    private static final Path INPUT_DIR = Paths.get("src/test/resources");
    private static final String VALID_REGEX = "\\b[A-Z]{2}[0-9]{2} [A-Z]{3}\\b";
    private static final String INVALID_REGEX = "\\b(?![A-Z]{2}[0-9]{2} [A-Z]{3}\\b)[A-Z0-9]{1,7}\\b";
    private static final Pattern VALID_PATTERN = Pattern.compile(VALID_REGEX);
    private static final Pattern INVALID_PATTERN = Pattern.compile(INVALID_REGEX);
    private static final Path OUTPUT_FILE = Paths.get("src/test/resources/cleaned_test_data.txt");

    /**
     * Extracts and writes registration numbers to the output file.
     */
    public static void extractAndWriteRegistrationNumbers() {
        try {
            List<String> validRegistrationNumbers = extractRegistrationNumbers(VALID_PATTERN);
            List<String> invalidRegistrationNumbers = extractRegistrationNumbers(INVALID_PATTERN)
                    .stream()
                    .filter(reg -> !validRegistrationNumbers.contains(reg))
                    .collect(Collectors.toList());

            List<String> allRegistrationNumbers = Stream.concat(
                    Stream.of("VARIANT_REG,STATUS"), // Add the header
                    Stream.concat(
                            validRegistrationNumbers.stream().map(reg -> reg + ",VALID"),
                            invalidRegistrationNumbers.stream().map(reg -> reg + ",The license plate number is not recognised")
                    )
            ).collect(Collectors.toList());

            Files.write(OUTPUT_FILE, allRegistrationNumbers);
            logger.info("Successfully wrote registration numbers to output file");
        } catch (IOException e) {
            logger.error("Error processing files", e);
        }
    }

    /**
     * Extracts registration numbers from input files based on the provided pattern.
     *
     * @param pattern the pattern to match registration numbers
     * @return a list of matched registration numbers
     * @throws IOException if an I/O error occurs
     */
    private static List<String> extractRegistrationNumbers(Pattern pattern) throws IOException {
        return Files.list(INPUT_DIR)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".txt"))
                .filter(path -> path.getFileName().toString().contains("_input")) // Filter for files with "_input" in the name
                .flatMap(VehicleRegistrationExtractor::linesFromFile)
                .flatMap(line -> {
                    Matcher matcher = pattern.matcher(line);
                    Stream.Builder<String> matches = Stream.builder();
                    while (matcher.find()) {
                        matches.add(matcher.group());
                    }
                    return matches.build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Reads lines from a file.
     *
     * @param path the path to the file
     * @return a stream of lines from the file
     */
    private static Stream<String> linesFromFile(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            logger.error("Error reading file: " + path, e);
            throw new RuntimeException("Error reading file: " + path, e);
        }
    }
}