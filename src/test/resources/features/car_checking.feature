Feature: Car Checking Page

  Scenario: Valid car registration number
    Given I am on the car checking page
    When I enter a valid registration number "AD58 VNF"
    And I submit the form
    Then I should see the car report page
    And the registration number should be "AD58VNF"

  Scenario: Invalid car registration number
    Given I am on the car checking page
    When I enter an invalid registration number "INVALID123"
    And I submit the form
    Then I should see an error message "The license plate number is not recognised"