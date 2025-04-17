//package tests;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import pages.CarCheckingPage;
//import pages.CarReportPage;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.time.Duration;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Stream;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//
//@TestMethodOrder(OrderAnnotation.class)
//public class CarValuationTest {
//
//    private static final Logger logger = LogManager.getLogger(CarValuationTest.class);
//    private static final String OUTPUT_FILE_PATH = "src/test/resources/car_output - V6.txt";
//    private static final String EXPECTED_OUTPUT_FILE_PATH = "src/test/resources/expected_output.txt";
//    private static final String CAR_CHECKING_URL = "https://car-checking.com/";
//    private static final String CAR_REPORT_URL = "https://car-checking.com/report";
//    private static WebDriver driver;
//
//    private void navigateToCarCheckingPage(String registrationNumber) {
//        driver.get(CAR_CHECKING_URL);
//        CarCheckingPage carCheckingPage = new CarCheckingPage(driver);
//        carCheckingPage.enterRegistrationNumber(registrationNumber);
//        carCheckingPage.submitForm();
//    }
//
//    private boolean checkForErrorAlert(WebDriverWait wait, String registrationNumber) throws IOException {
//        WebElement errorAlert = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert.alert-danger")));
//        if (errorAlert != null && errorAlert.isDisplayed()) {
//            String alertMessage = errorAlert.getText();
//            logger.info("Entered Registration Number: " + registrationNumber);
//            logger.info("Alert Message: " + alertMessage);
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH, true))) {
//                writer.write(String.format("%s,%s%n", registrationNumber, alertMessage));
//            }
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkForDataElements(WebDriverWait wait, String registrationNumber) {
//        try {
//            CarReportPage carReportPage = new CarReportPage(driver);
//            String regNumber = carReportPage.getRegistrationNumber();
//            return regNumber != null && !regNumber.isEmpty();
//        } catch (org.openqa.selenium.TimeoutException e) {
//            return false;
//        }
//    }
//
//    private void extractAndWriteCarDetails() throws IOException {
//        CarReportPage carReportPage = new CarReportPage(driver);
//        String regNumber = carReportPage.getRegistrationNumber();
//        String make = carReportPage.getMake();
//        String model = carReportPage.getModel();
//        String year = carReportPage.getYearOfManufacture();
//
//        logger.info("Extracted details - RegNumber: " + regNumber + ", Make: " + make + ", Model: " + model + ", Year: " + year);
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH, true))) {
//            if (regNumber != null && !regNumber.isEmpty() && make != null && !make.isEmpty() && model != null && !model.isEmpty() && year != null && !year.isEmpty()) {
//                writer.write(String.format("%s,%s,%s,%s%n", regNumber, make, model, year));
//                logger.info("Written valid details to file.");
//            } else {
//                writer.write(String.format("%s,The license plate number is not recognised%n", regNumber != null ? regNumber : ""));
//                logger.info("Written invalid details to file.");
//            }
//        }
//    }
//
//    @Order(1)
//    @ParameterizedTest
//    @MethodSource("validRegistrationNumbersProvider")
//    public void testValidRegistrationNumber(String validRegistrationNumber) throws IOException {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
//        try {
//            navigateToCarCheckingPage(validRegistrationNumber);
//            if (checkForDataElements(wait, validRegistrationNumber)) {
//                extractAndWriteCarDetails();
//            }
//        } finally {
//            // driver.quit();
//        }
//    }
//
//    static Stream<String> validRegistrationNumbersProvider() throws IOException {
//        BufferedReader reader = Files.newBufferedReader(Paths.get("src/test/resources/cleaned_test_data.txt"));
//        return reader.lines()
//                .skip(1)
//                .map(line -> line.split(","))
//                .filter(fields -> fields.length == 2 && "VALID".equals(fields[1]))
//                .map(fields -> fields[0])
//                .onClose(() -> {
//                    try {
//                        reader.close();
//                    } catch (IOException e) {
//                        throw new UncheckedIOException(e);
//                    }
//                });
//    }
//
//    @Order(2)
//    @ParameterizedTest
//    @MethodSource("invalidRegistrationNumbersProvider")
//    public void testInvalidRegistrationNumberDataDriven(String invalidRegistrationNumber) throws IOException {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
//        try {
//            navigateToCarCheckingPage(invalidRegistrationNumber);
//            checkForErrorAlert(wait, invalidRegistrationNumber);
//        } finally {
//            // driver.quit();
//        }
//    }
//
//    static Stream<String> invalidRegistrationNumbersProvider() throws IOException {
//        BufferedReader reader = Files.newBufferedReader(Paths.get("src/test/resources/cleaned_test_data.txt"));
//        return reader.lines()
//                .skip(1)
//                .map(line -> line.split(","))
//                .filter(fields -> fields.length == 2 && "The license plate number is not recognised".equals(fields[1]))
//                .map(fields -> fields[0])
//                .onClose(() -> {
//                    try {
//                        reader.close();
//                    } catch (IOException e) {
//                        throw new UncheckedIOException(e);
//                    }
//                });
//    }
//
//    @Order(3)
//    @ParameterizedTest
//    @MethodSource("hardcodedInvalidRegistrationNumbersProvider")
//    public void testHardcodedInvalidRegistrationNumber(String registrationNumber, String expectedMessage) throws IOException {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
//        try {
//            navigateToCarCheckingPage(registrationNumber);
//            boolean alertFound = checkForErrorAlert(wait, registrationNumber);
//            assertEquals(expectedMessage, alertFound ? "The license plate number is not recognised" : "");
//        } finally {
//            // driver.quit();
//        }
//    }
//
//    static Stream<Arguments> hardcodedInvalidRegistrationNumbersProvider() {
//        return Stream.of(
//                Arguments.of("INVALID123", "The license plate number is not recognised"),
//                Arguments.of("1", "The license plate number is not recognised"),
//                Arguments.of("A", "The license plate number is not recognised"),
//                Arguments.of("A1A", "The license plate number is not recognised"),
//                Arguments.of("AAA", "The license plate number is not recognised"),
//                Arguments.of("AA00", "The license plate number is not recognised"),
//                Arguments.of("AA00 XXX", "The license plate number is not recognised")
//        );
//    }
//
//    @Order(4)
//    @ParameterizedTest
//    @MethodSource("outputFilesProvider")
//    public void compareOutputWithExpected(String actualOutputFilePath, String expectedOutputFilePath) throws IOException {
//        List<String> actualOutput = Files.readAllLines(Paths.get(actualOutputFilePath));
//        List<String> expectedOutput = Files.readAllLines(Paths.get(expectedOutputFilePath));
//        boolean hasDiscrepancies = false;
//
//        int[][] ranges = {
//            {2, 11, 1},
//            {12, 21, 2},
//            {22, 31, 3}
//        };
//
//        for (int[] range : ranges) {
//            int actualStart = range[0] - 1;
//            int actualEnd = range[1];
//            int expectedStart = range[2] - 1;
//
//            for (int i = 0; i < (actualEnd - actualStart); i++) {
//                String[] expectedFields = expectedOutput.get(expectedStart + i).split(",");
//                String[] actualFields = actualOutput.get(actualStart + i).split(",");
//
//                for (int j = 0; j < expectedFields.length; j++) {
//                    if (j < actualFields.length) {
//                        if (!expectedFields[j].equals(actualFields[j])) {
//                            hasDiscrepancies = true;
//                        }
//                    } else {
//                        hasDiscrepancies = true;
//                    }
//                }
//            }
//        }
//
//        if (hasDiscrepancies) {
//            assertFalse(hasDiscrepancies, "There are discrepancies between the actual and expected output.");
//        }
//    }
//
//    static Stream<Arguments> outputFilesProvider() {
//        return Stream.of(
//                Arguments.of("src/test/resources/car_output - V6.txt", "src/test/resources/expected_output.txt"),
//                Arguments.of("src/test/resources/mismatched_test_output.txt", "src/test/resources/expected_output.txt")
//        );
//    }
//
//    @Order(5)
//    @Test
//    public void testWebsiteDown() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
//        try {
//            driver.get("https://car-checking.com/nonexistentpage");
//            boolean is404 = wait.until(d -> {
//                String pageSource = driver.getPageSource();
//                return pageSource.contains("404") || pageSource.contains("Not Found");
//            });
//            assertEquals(true, is404, "Expected 404 Not Found error was not found.");
//        } finally {
//            // driver.quit();
//        }
//    }
//}