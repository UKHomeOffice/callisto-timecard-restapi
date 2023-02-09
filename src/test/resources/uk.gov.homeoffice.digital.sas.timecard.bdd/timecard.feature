Feature: Timecard

  Scenario: Create time entry with no end time

    Given Trevor is a user
    And the valid time-entries are
      """
      {
        "ownerId":  "00000000-0000-0000-0000-000000000001",
        "timePeriodTypeId": "00000000-0000-0000-0000-000000000001",
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


  Scenario: Create time entry with end date before start date

    Given Trevor is a user
    And the initial time-entries are
      """
      {
        "ownerId":  "00000000-0000-0000-0000-000000000001",
        "timePeriodTypeId": "00000000-0000-0000-0000-000000000001",
        "actualStartTime": "2022-11-16T10:00:00Z",
        "actualEndTime": "2022-11-16T08:00:00Z"
      }
      """
    When Trevor creates the initial time-entries in the timecard service
    Then the last response should have a status code of 400
    Then the last response body should contain
      | field       | type    | expectation                                    |
      | [0].field   | String  | isEqualTo("endTime")                           |
      | [0].message | String  | isEqualTo("End time must be after start time") |
      | [0].data    | String  | isNull()                                       |


  Scenario: Create time entry with invalid date format

    Given Trevor is a user
    And the initial time-entries are
      """
      {
        "ownerId":  "00000000-0000-0000-0000-000000000001",
        "timePeriodTypeId": "00000000-0000-0000-0000-000000000001",
        "actualStartTime": "foobar",
      }
      """
    When Trevor creates the initial time-entries in the timecard service
    Then the last response should have a status code of 400
    Then the last response body should contain
      | field   | type    | expectation                                                                            |
      | message | String  | startsWith("Cannot deserialize value of type `java.util.Date` from String ""foobar""") |


  Scenario: Create time entry with overlapping entries

    Given Trevor is a user
    And the initial time-entries are
      """
      {
        "ownerId": "00000000-0000-0000-0000-000000000001",
        "timePeriodTypeId": "00000000-0000-0000-0000-000000000001",
        "actualStartTime": "2022-11-16T08:00:00Z",
        "actualEndTime": "2022-11-16T10:00:00Z"
      }
      """
    And Trevor creates the initial time-entries in the timecard service
    And the new time-entries are
      """
      {
        "ownerId": "00000000-0000-0000-0000-000000000001",
        "timePeriodTypeId": "00000000-0000-0000-0000-000000000001",
        "actualStartTime": "2022-11-16T08:30:00Z",
        "actualEndTime": "2022-11-16T10:00:00Z"
      }
      """
    When Trevor creates the new time-entries in the timecard service
    Then the last response should have a status code of 400
    Then the last response body should contain
      | field                          | type   | expectation                                                         |
      | [0].field                      | String | isEqualTo("startAndEndTime")                                        |
      | [0].message                    | String | isEqualTo("Time periods must not overlap with another time period") |
      | [0].data[0].startTime          | String | isEqualTo("2022-11-16T08:00:00.000+00:00")                          |
      | [0].data[0].endTime            | String | isEqualTo("2022-11-16T10:00:00.000+00:00")                          |
      | [0].data[0].timePeriodTypeId   | String | isEqualTo("00000000-0000-0000-0000-000000000001")                   |


