package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Page Object Model for the Car Report Page.
 */
public class CarReportPage {
    private static final Logger logger = LogManager.getLogger(CarReportPage.class);
    private final WebDriver driver;

    // Locators
    private static final By REG_NUMBER_ELEMENT = By.id("subForm");
    private static final By MAKE_ELEMENT = By.xpath("//td[text()='Make']/following-sibling::td");
    private static final By MODEL_ELEMENT = By.xpath("//td[text()='Model']/following-sibling::td");
    private static final By YEAR_ELEMENT = By.xpath("//td[text()='Year of manufacture']/following-sibling::td");

    /**
     * Constructor to initialize the CarReportPage with WebDriver.
     *
     * @param driver the WebDriver instance
     */
    public CarReportPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Retrieves the registration number from the report page.
     *
     * @return the registration number
     */
    public String getRegistrationNumber() {
        WebElement regNumberElement = driver.findElement(REG_NUMBER_ELEMENT);
        String regNumber = regNumberElement.getAttribute("value");
        logger.info("Retrieved registration number: " + regNumber);
        return regNumber;
    }

    /**
     * Retrieves the make of the car from the report page.
     *
     * @return the make of the car
     */
    public String getMake() {
        WebElement makeElement = driver.findElement(MAKE_ELEMENT);
        String make = makeElement.getText();
        logger.info("Retrieved make: " + make);
        return make;
    }

    /**
     * Retrieves the model of the car from the report page.
     *
     * @return the model of the car
     */
    public String getModel() {
        WebElement modelElement = driver.findElement(MODEL_ELEMENT);
        String model = modelElement.getText();
        logger.info("Retrieved model: " + model);
        return model;
    }

    /**
     * Retrieves the year of manufacture of the car from the report page.
     *
     * @return the year of manufacture
     */
    public String getYearOfManufacture() {
        WebElement yearElement = driver.findElement(YEAR_ELEMENT);
        String year = yearElement.getText();
        logger.info("Retrieved year of manufacture: " + year);
        return year;
    }
}