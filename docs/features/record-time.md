
# Record time

Feature

## Data Flows
The following data flows underpin the user stories that sit under the Record time feature. 

### View non-existing timecard

**user stories** 

- [Timecard View - No Shift / Day Type Populated](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1230) (access required)

**input(s)** 

- timecardDate - the date that the timecard is associated with
- timecardOwnerId - the primary key of the person who is the owner of the timecard
- tenantId - the identifier for the tenant that holds the timecard 

**container commands**
- ReferenceData.get time period enumeration(tennantId) - used to give the user a choice as to what type of time period they wish to enter
- [TimeCard.get timecard(timecardDate, timecardOwnerId, tenantId)](../container-definition.md#get-timecard) - used to retrieve a timecard. Note that in this data flow the expectation is that this call will return a timecard now found status
- [TimeCard.create timecard(TimeCardEntitiy)](../container-definition.md#create-timecard) - used to create a brand new TimeCard entity along with a new TimeEntry entitiy that is in part populated from the time period value that the user has chosen and the subsequent time or date values that they have entered

**sequence diagrams**
![View non-existing timecard](../images/view-non-existing-timecard.png)

