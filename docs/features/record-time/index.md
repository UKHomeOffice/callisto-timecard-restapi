# Record time

This high-level design is intended to cover the Record Time Feature which includes the user stories listed below. It seeks to:

1.  Guide the reader through the relevant parts of the container definitions which will be used to satisfy this feature.
2.  Where required illustrate the high-level container orchestration.
3.  Detail and explain the relevant parts of the data model impacting this feature.

The Record Time [feature definition](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-925) (access required) in Jira details the user stories that make up the feature. The user stories contain more detailed requirement around business rules and validation logic. The intention is that this document should be used as a guide when designing and implementing and testing against a given story from the record time feature

To understand the proposed high-level design, it is instructive to consider both the definition of the [containers](../../container-definition.md) used to perform the key actions and the appropriate parts of the [payload model](../../payload.md) specifically [TimeEntry](../../payload.md#timeentry).

## Key data models

This section describes which parts of the TimeCard container's payload model are relevant when an end user wants to record their time.
- A TimeEntry is used to record time worked

![](../../images/payload-model.png)

## Key command sequences
This section describes which commands need to be invoked and in what order so that an end user is able to record their time or remove any previously recorded time.

### Get the TimeEntry instances for a given date
When an end user wants to record time worked then the starting point will be to choose a date and check whether there are any existing `TimeEntry` instances that should be updated to record the time that the user wants to enter.

A call to `find TimeEntry by date` will return all `TimeEntry` instances where the recorded time overlaps with the given date

##### container command(s)
- [TimeCard.find TimeEntry by date(timeentryDate, timeentryOwnerId, tenantId)](../../container-definition.md#get-timeentry-by-date) - used to retrieve `TimeEntry` instances. 

### Record time
The user wants to record time. Once the response has been returned from the call to `find TimeEntry by date` the client has a choice to make depending upon whether or not any `TimeEntry` instances were found.

#### TimeEntry instances found
 If the response code indicates [success](https://github.com/UKHomeOffice/callisto-docs/blob/main/blueprints/restful-endpoint.md#handle-success-consistently) and the payload contains one or more `TimeEntry` instances then the client can display them and allow the
end user to choose to modify an existing `TimeEntry` or create a new `TimeEntry`

##### container command(s)
- [TimeCard.modify timeentry(timeEntry, tenantId)](../../container-definition.md#modify-timeentry) - used to modify an existing `TimeEntry`
- [TimeCard.create timeentry(timeEntry, tenantId)](../../container-definition.md#create-timeentry) - used to create a new `TimeEntry`


#### No TimeEntry instances found
If the response code indicates that `TimeEntry` resources were [not found](https://github.com/UKHomeOffice/callisto-docs/blob/main/blueprints/restful-endpoint.md#handle-errors-gracefully-and-return-standard-error-codes) then effectively the end user is creating the first `TimeEntry` for a given date. 

##### container command(s)
- [TimeCard.create timeentry(timeEntry, tenantId)](../../container-definition.md#create-timeentry) - used to create a new `TimeEntry`

### Remove time
The user wants to remove previously recorded time. Once the response has been returned from the call to `find TimeEntry by date` the client has a choice to make depending upon whether or any `TimeEntry` instances were found.

#### TimeEntry instances found
 If the response code indicates [success](https://github.com/UKHomeOffice/callisto-docs/blob/main/blueprints/restful-endpoint.md#handle-success-consistently) and the payload contains one or more `TimeEntry` instances then the client can display them and allow the
end user to choose to remove one or more existing `TimeEntry` instances

##### container command(s)
- [Remove TimeEntry(timeEntryId, tenantId](../../container-definition.md#remove-timeentry) - used to remove an existing timeentry

#### No TimeEntry instances found
If the response code indicates that `TimeEntry` resources were [not found](https://github.com/UKHomeOffice/callisto-docs/blob/main/blueprints/restful-endpoint.md#handle-errors-gracefully-and-return-standard-error-codes) then effectively the end user is trying to delete something that does not exist therefore it is up to the client about how best to inform the user that their requested action cannot be completed

##### container command(s)
- None

## Considerations

1.  When updating the TimeCard container's data the system must prevent unintentional overwrites sometimes known as a [lost update](https://www.w3.org/1999/04/Editing/#3.1).  See [RESTful endpoint blueprint](https://github.com/UKHomeOffice/callisto-docs/blob/main/blueprints/restful-endpoint.md#managing-resource-contention) for guidance on dealing with locking in a RESTful context for more details
2.  The type of information collected to record a time entry varies according to the TimePeriodType (Shift, Standard Rest Day etc) selected by the user. For example for a SRD only a date is required but for a shift a start time and end time is collected. **WW - this needs more thought - we should add direction in this document for how to go about presenting this to the end user**
3. Unique ID + tenant ID
4. Versioning - the `TimeEntry` is a versioned resource. More guidance can be found (here)[https://github.com/UKHomeOffice/callisto-docs/blob/main/blueprints/entity-versioning.md]
5. Storage - 
6. Person data - a `TimeEntry` is associated with a `Person`. Person data is mastered in the Person container (TBD). As per our [decision](https://github.com/UKHomeOffice/callisto-docs/blob/main/decisions/service-to-service-communication.md) around container to container communication the Person container will publish events related to the lifecycle of a Person. The TimeCard container must subscribe to those events in order to create its own internal representation of a [Person](../../payload.md#person). Note that at the time of writing (02 Aug 2022 more work is required to define the Person container, the events it produces and how the TimeCard container should respond to them)
7. Reference data - There are a number of pieces of reference data that are used in the recording of time. Note that at the time of writing (02 Aug 2022 more work is required to define how reference data is maintained and accessed)



