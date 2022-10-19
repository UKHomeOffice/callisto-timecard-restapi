Feature: Assertions

    When a request is issued assertions will be made against the returned
  response to ensure the correct result is returned.

  Several steps are provided for basic checking of the returned response
  but the most interesting steps are the ones that allow for several
  expectations to be assert for a specified object.

  The object to assert expectations against can be specified for
  resource and business endpoints alike and back referencing
  to previous requests in the same scenario is supported.

  Once the object to assert against is specified a table can be provided
  to specify the field to assert against, it's type and an expectation.

  For more details on expectations and how they can be specified and
  customised see the documentation for AssertJ
  https://assertj.github.io/doc/#assertj-core-assertions-guide

  Background:
    Given the tester is a user
    And the admin is a user

  Scenario: Object should contain fields
    When the admin retrieves time-entries from the timecard service
    Then the last of the time-entries in the last response from the timecard service should contain the fields
      | tenantId          |
      | shiftType         |
      | actualStartTime   |
      | timePeriodTypeId  |

  Scenario: Object should contain fields
    When the admin retrieves time-period-types from the timecard service
    Then the last of the time-period-types in the last response from the timecard service should contain the fields
      | id        |
      | tenantId  |
      | name      |
