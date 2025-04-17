package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.CarCheckingPage;
import pages.CarReportPage;

import java.io.IOException;
import java.time.Duration;

import static net.serenitybdd.core.Serenity.getDriver;
import static org.assertj.core.api.Assertions.assertThat;

public class CarSteps {

    private WebDriver driver = getDriver();
    private CarCheckingPage carCheckingPage;
    private CarReportPage carReportPage;
    private static final String OUTPUT_FILE_PATH = "output/error_alerts.csv";

    @Given("I am on the car checking page")
    public void iAmOnTheCarCheckingPage() {
        carCheckingPage = new CarCheckingPage(driver);
        carCheckingPage.open();
    }

    @When("I enter a valid registration number {string}")
    public void iEnterAValidRegistrationNumber(String registrationNumber) {
        carCheckingPage.enterRegistrationNumber(registrationNumber);
    }

    private String invalidRegistrationNumber; // Class-level variable to store the invalid registration number

    @When("I enter an invalid registration number {string}")
    public void iEnterAnInvalidRegistrationNumber(String registrationNumber) {
        this.invalidRegistrationNumber = registrationNumber; // Store the invalid registration number
        carCheckingPage.enterRegistrationNumber(registrationNumber);
    }

    @When("I submit the form")
    public void iSubmitTheForm() {
        carCheckingPage.submitForm();
    }

    @Then("I should see the car report page")
    public void iShouldSeeTheCarReportPage() {
        carReportPage = new CarReportPage(driver);
        assertThat(carReportPage.getRegistrationNumber()).isNotNull();
    }

    @Then("the registration number should be {string}")
    public void theRegistrationNumberShouldBe(String expectedRegNumber) {
        assertThat(carReportPage.getRegistrationNumber()).isEqualTo(expectedRegNumber);
    }

    @Then("I should see an error message {string}")
    public void iShouldSeeAnErrorMessage(String expectedErrorMessage) throws IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        boolean alertFound = checkForErrorAlert(wait, invalidRegistrationNumber); // Use the stored invalid registration number
        assertThat(alertFound).as(expectedErrorMessage).isTrue();

//        String actualErrorMessage = carCheckingPage.getErrorMessage();
//        assertThat(actualErrorMessage)
//                .as("The actual error message does not match the expected one.")
//                .isEqualTo(expectedErrorMessage);
    }

    private boolean checkForErrorAlert(WebDriverWait wait, String registrationNumber) throws IOException {
        WebElement errorAlert = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert.alert-danger")));
        if (errorAlert != null && errorAlert.isDisplayed()) {
            String alertMessage = errorAlert.getText();
            System.out.println("Entered Registration Number: " + registrationNumber);
            System.out.println("Alert Message: " + alertMessage);
            return true;
        }
        return false;
    }
}