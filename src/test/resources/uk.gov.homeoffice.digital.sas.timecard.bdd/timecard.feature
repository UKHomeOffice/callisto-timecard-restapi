Feature: Timecard

  Background:
    Given Shift time period type

  Scenario: Create Time Entry

    Given Trevor is a user
    And the valid time-entries are
      """
      {
        "ownerId": "00000000-0000-0000-0000-000000000001",
        "timePeriodTypeId": "#{sharedVariables.get('timePeriodTypeId')}",
        "actualStartTime": "2022-11-16T08:00:00Z",
        "actualEndTime": null
      }
      """
    When Trevor creates the valid time-entries in the timecard service
    Then the last response should have a status code of 200
    Then the 1st of the time-entries in the last response should contain
      | field           | type    | expectation                                       |
      | id              | String  | isNotNull()                                       |
      | ownerId         | String  | isEqualTo("00000000-0000-0000-0000-000000000001") |
      | actualStartTime | Instant | isEqualTo("2022-11-16T08:00:00Z")                 |
      | actualEndTime   | Instant | isNull()                                          |
