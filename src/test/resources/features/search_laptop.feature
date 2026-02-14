
@smoke
Feature: Search laptops on Croma and pick the best within budget
  As a shopper
  I want to search laptops on Croma and apply max price filter
  So that I can see the best option within my budget

  Scenario: Search laptop and validate price after applying max filter
    Given I am on Croma home page
    When I read test data from Excel
    And I search the product and apply price filter
    Then I capture the best product and verify it is within budget
