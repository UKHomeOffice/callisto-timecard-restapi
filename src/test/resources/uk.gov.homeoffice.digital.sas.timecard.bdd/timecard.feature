Feature: Timecard

  Scenario: Create Time Entry

    Given Trevor is a user
    And the valid time-entries are
      """
      {
        "tenantId": "b7e813a2-bb28-11ec-8422-0242ac120002"
        "ownerId": "00000000-0000-0000-0000-000000000001",
        "timePeriodTypeId": "#{resourceHelper.getResourceId('Trevor', 'timecard','time-period-types','name=="Shift"')}",
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

#  Scenario: Create Time Entry with overlapping entries
#
#    Given Trevor is a user
#    Given Trevor has previously created the valid time-entries in the timecard service
#      """
#      {
#        "ownerId": "00000000-0000-0000-0000-000000000001",
#        "timePeriodTypeId": "#{resourceHelper.getResourceId('Trevor', 'timecard','time-period-types','name=="Shift"')}",
#        "actualStartTime": "2022-11-16T08:00:00Z",
#        "actualEndTime": "2022-11-16T10:00:00Z"
#      }
#      """
#    And the valid time-entries are
#      """
#      {
#        "ownerId": "00000000-0000-0000-0000-000000000001",
#        "timePeriodTypeId": "#{resourceHelper.getResourceId('Trevor', 'timecard','time-period-types','name=="Shift"')}",
#        "actualStartTime": "2022-11-16T08:30:00Z",
#        "actualEndTime": "2022-11-16T10:00:00Z"
#      }
#      """
#    When Trevor creates the valid time-entries in the timecard service
#    Then the last response should have a status code of 200
#    Then the 1st of the time-entries in the last response should contain
#      | field           | type    | expectation                                       |
#      | id              | String  | isNotNull()                                       |
#      | ownerId         | String  | isEqualTo("00000000-0000-0000-0000-000000000001") |
#      | actualStartTime | Instant | isEqualTo("2022-11-16T08:00:00Z")                 |
#      | actualEndTime   | Instant | isNull()                                          |


