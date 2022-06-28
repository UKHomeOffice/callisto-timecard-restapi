Feature: The Timecard Health Check
  
  Scenario: I want to check the timecard service health
    Given The Timecard service is running
    When I check the health status
    Then I will get the status as "UP"