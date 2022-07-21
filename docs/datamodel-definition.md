
# TimeCard Data Model

## Overview

The TimeCard container needs to persist the TimeCard 'containing' entity which manages the ownership and status of the time card and the children entities including TimeEntries, TimeCardNotes and FlexChanges.

![Callisto containers](./images/timecard-container-data-model.jpg)

## Entity

### TimeCard

A TimeCard instance is for a given person. It is the containing Entity for all collects multiple TimeEntry instances and layers on the concept of approval.  A timecard maps to a single date.


| Key | Column Name    | Type      | Description            |
| --- | -------------- | --------- | ---------------------- |
|     |                |           |                        |
| Key | TimeCardId     | long      |                        |
|     | TimeCardStatus | varchar   | Approved, Rejected     |
|     | Date           | timestamp | Date shift started.    |
|     | PersonId       | long      |                        |
|     | SubmittedOn    | timestamp |                        |
|     | ApprovedOn     | timestamp |                        |
|     | RejectedOn     | timestamp |                        |
|     | ApprovedBy     | long      |                        |
|     | createdtadstp  | timestamp | Created timestamp      |
|     | modifiedtadstp | timestamp | Last modified timestamp|
|     | deleted        | bool      | Soft delete flag       |

### TimeEntry

Used to record both planned and actual time. Encapsulates day and time (to the minute). In addition, the time entry captures the way that the time has been spent via the activity property.

| Key | Column Name      | Type      | Description                   |
| --- | ---------------- | --------- | ----------------------------- |
|     |                  |           |                               |
| Key | TimeEntryId      | long      |                               |
|     | TimeCardId       | long      |                               |
|     | TimeEntryStatus  | enum      | Planned, Booked,Cancelled     |
|     | TimePeriodId     | int       | Shift, Absence, SRD, NWD, etc |
|     | ShiftType        | varchar   | Early, Late, Day etc          |
|     | ActivityId       | int       |                               |
|     | ActualStartTime  | timestamp |                               |
|     | ActualEndTime    | timestamp |                               |
|     | PlannedStartTime | timestamp |                               |
|     | PlannedEndTime   | timestamp |                               |
|     | IsOverridden     | bool      |                               |
|     | createdtadstp    | timestamp | Created timestamp             |
|     | modifiedtadstp   | timestamp | Last modified timestamp       |
|     | deleted          | bool      | Soft delete flag              |


### TimeCardNotes

Notes are associated with a TimeCard. Notes are used to communicate arbitrary information between individuals that relates to the TimeCard. Notes cannot exist on their own. 

| Key | Column Name    | Type      | Description             |
| --- | -------------- | --------- | ----------------------- |
|     |                |           |                         |
| Key | TimeCardNoteID | long      |                         |
|     | TimeCardId     | long      |                         |
|     | content        | varchar   |                         |
|     | createdtadstp  | timestamp | Created timestamp       |
|     | modifiedtadstp | timestamp | Last modified timestamp |
|     | deleted        | bool      | Soft delete flag        |
|     |                |           |                         |
|     |                |           |                         |

### TimeCardEventLog

| Key | Column Name      | Type      | Description                                      |
| --- | ---------------- | --------- | ------------------------------------------------ |
|     |                  |           |                                                  |
| Key | EventId          | long      |                                                  |
|     | TimeCardId       | long      | Optional. TimeCardId to which the event belongs  |
|     | TimeEntryId      | long      | Optional. TimeEntryId to which the event belongs |
|     | EventDate        | timestamp | Event timestamp                                  |
|     | EventInitiatorId | long      | Person (if applic able) that initiated event     |
|     | EventTypeId      | long      | The type of Event.                               |
|     | EventDescription | varchar   | Textual description of the event                 |
|     | createdtadstp    | timestamp | Created timestamp                                |
|     | modifiedtadstp   | timestamp | Last modified timestamp                          |
|     | deleted          | bool      | Soft delete flag                                 |


### FlexChange

A FlexChange entry records a change to one or more planned TimeEntry instances. It references TimeEntry by using the TimeEntry.id as a foreign key. There is a set of specific reasons that planned time can be changed and it must be approved. The FlexChange records this reason along with the new planned times for the associated TimeEntry instances.   

A FlexChange can be created directly by an End user Shift worker on their TimeCard entry screen. A FlexChange can also come from the Scheduler container 

| Key | Column Name      | Type      | Description                              |
| --- | ---------------- | --------- | ---------------------------------------- |
|     |                  |           |                                          |
| Key | FlexChangeId     | long      |                                          |
|     | TimeCardId       | long      |                                          |
|     | TimeEntryId      | long      | Maybe zero or blank                      |
|     | FlexChangeType   | enum      | WholesaleChange,Curtailment, ExtendShift |
|     | EffectiveDate    | timestamp |                                          |
|     | RequesterId      | int       | Who requested the flex change            |
|     | ApproverId       | int       |                                          |
|     | ApprovalDate     | timestamp |                                          |
|     | FlexChangeReason | varchar   |                                          |
|     | createdtadstp    | timestamp | Created timestamp                        |
|     | modifiedtadstp   | timestamp | Last modified timestamp                  |
|     | deleted          | bool      | Soft delete flag                         |

### FlexChangeNote

| Key | Column Name    | Type      | Description             |
| --- | -------------- | --------- | ----------------------- |
|     |                |           |                         |
| Key | FlexChangeId   | long      |                         |
|     | TimeCardId     | long      |                         |
|     | content        | varchar   |                         |
|     | createdtadstp  | timestamp | Created timestamp       |
|     | modifiedtadstp | timestamp | Last modified timestamp |
|     | deleted        | bool      | Soft delete flag        |

### Enumerations

**TimeCardStatus** 
Describes the state that a TimeCard can be in. Covers the concept of planning time and then booking actual time. In addition, the status encapsulates TimeCard approval 

**TimeEntryActivity** 
A set of different types of work that a Shift worker can record time against 

**TimePeriod**
Describes the way a person has spent their time eg a shift, a non-working day, an absence

