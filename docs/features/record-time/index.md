
# Record time

This high-level design is intended to cover the Record Time Feature which includes the user stories listed below. It seeks to:

1.  Guide the reader through the relevant parts of the container definitions which will be used to satisfy this feature.
2.  Where required illustrate the high-level container orchestration.
3.  Detail and explain the relevant parts of the data model impacting this feature.

[Feature definition](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-925) (access required)


## User Stories


- [Manager Input Start and Finish Time (No Existing Entries)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1751) (access required)
- [Manager Add On-call Period to Timecard (Existing Non On-Call Entry)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1788) (access required)
- [Add On-call Period to Timecard (Existing On-Call Entry) - Validation Fail](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1762) (access required)
- [Add Time Worked During On-call Period to Timecard](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1796) (access required)
- [Input Finish Time - Day After Start Time Date (No Existing End Time)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-589) (access required)
- [Input Start Time (No Existing Entries)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-513) (access required)
- [Input Finish Time Same Day (No Existing Finish Time)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-521) (access required)
- [Record Shift in Timecard (No Existing Entries)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1347) (access required)
- [Record On-call Period in Timecard (Blank Timecard)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1350) (access required)
- [Input Start and Finish Time (No Existing Entries)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1402) (access required)
- [Add On-call Period to Timecard (Existing Non On-Call Entry)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1471) (access required)
- [Add On-call Period to Timecard (Existing On-Call Entry)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1761) (access required)
- [User Record an Ends night shift then starts a night shift in same day - System will identify them as separate shifts.](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-580) (access required)
- [Manager Record On-Call Period in Timecard (Blank Timecard)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1685) (access required)
- [Manager Record Shift in Timecard (No Existing Entries)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1686) (access required)
- [Manager Input Finish Time Same Day (No Existing Finish Time)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1749) (access required)

## Overview

To understand the proposed high-level design, it is instructive to consider both the definition of the containers used to perform TimeCard actions and the appropriate parts of the data model.
[TimeCard Container Definition](https://github.com/UKHomeOffice/callisto-timecard-restapi/container-definition.md)

It is also useful to understand the terms used; in particular TimeCard and TimEntry. This is part is a repetition of the TimeCard Data Model but it bears repeating.

[TimeCard Data Model](https://github.com/UKHomeOffice/callisto-timecard-restapi/datamodel-definition.md)


## TimeCard

A TimeCard instance is for a given person. It is the containing Entity for multiple TimeEntry instances as well as TimeCardNotes, FlexChanges as shown below

<img src="https://github.com/UKHomeOffice/callisto-timecard-restapi/blob/main/docs/images/timecard-container-data-model.jpg" width=500>

Note that a TimeCard maps to a single date but if a continuous working TimeEntry spans two dates (e.q shift started at 10pm and ended at 5am) then the date in the TimeCard is the date the shift started.

## Time Entry

A TimeEntry belongs to a TimeCard with an identical TimeCardId. When creating a TimeEntry it must be associated with a TimeCard, it cannot
exist on its own.

### Get TimeCard

When an end user wants to record a time entry the starting point will be to choose a date and check whether there is a TimeCard already created
for that date and to return the payload which will contain, amongst other things the TimeEntry records already created if any exist as illustrated below at a high level.

![Callisto containers](https://github.com/UKHomeOffice/callisto-timecard-restapi/blob/main/docs/images/record-timeentry-high-level-sequence.jpg)

So a call to GetTimeCard will return the entire TimeCard instance including TimeEntry records, FlexChanges, TimeCardNotes and TimeCardEvents for display.

#### input(s)
- timecardDate - mandatory - the date that the timecard is associated with
- timecardOwnerId - mandatory - the primary key of the person who is the owner of the timecard
- tenantId - mandatory the identifier for the tenant that holds the timecard 

#### output(s)
TimeCard or a TimeCard does not exist status message 

#### container command(s)
- [TimeCard.get timecard(timecardDate, timecardOwnerId, tenantId)](../../container-definition.md#gettimecard) - used to retrieve a timecard. 


### Create or Modify a Time Entry

Once the payload has been returned or no TimeCard is returned the system
is in a position to either display the returned payload and allow the
end user to choose to modify an existing TimeCard or add a new TimeEntry
but essentially all the options at this point boil down to one of:

1.  Creating a Time Entry when there is no corresponding TimeCard (or
    Entry) for that date.

2.  Creating a new Time Entry when a TimeCard exists.

3.  Modifying a previously entered TimeEntry.

When creating the first TimeEntry for a given day several entities will
be created as follows:

-   TimeCard -- containing entity

-   TimeEntry -- detailing the time period to be recorded

-   TimeCardEventLog -- A create timecard / time entry record

When a timecard for that date already exists the addition of a new timecard entry will trigger an update to that existing timecard and its component parts.

### Modify TimeEntry

- [Update TimeCard](../../container-definition.md#update-timecard) - used to update an existing timecard. 


### Remove TimeEntry

- [Remove TimeEntry](../../container-definition.md#update-timecard) - used to remove an existing timeentry within an exisiting TimeCard. It will not delete the TimeCard


### Store TimeEntry

- [Add TimeEntry](../../container-definition.md#update-timecard) - used to store a new timeentry.

## Considerations

1.  Locking. Whilst the TimeCard and its component parts are being viewed none of the records are locked. Therefore it is important that the client returns are token of some kind that would allow the service to check that the database record matches the one being updated. The modifiedtadstp is suggested.
2.  The type of information collected to record a time entry varies according to the TimePeriodType (Shift, Standard Rest Day etc) selected by the user. For example for a SRD only a date is required but for a shift a start time and end time is collected.



