# TimeCard Commands

## add TimeEntry
This command causes a new `TimeEntry` to be created 

### inputs 
- TenantId    - mandatory -  the tenant that holds the `TimeEntry`
- TimeEntry - mandatory - the [TimeEntry](./payload.md#timeentry) that is to be created

### output
 - success - see [standard command output](TODO)(**TODO**)  for how to report success output
 - business failure - see [Record Time](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-925) (access required) for business failure scenarios. Also see [standard command output](TODO) (**TODO**)  for how to report business failures
 - technical failure - see [standard command output](TODO) (**TODO**) for how to report technical failures

 
## modify TimeEntry
This command causes an existing `TimeEntry` entity to be modified.

### inputs 
- TimeEntry - mandatory -  the updated version of the `TimeEntry`
- TenantId    - mandatory -  the tenant that holds the `TimeEntry`

### output
 - success - see [standard command output](TODO)(**TODO**)  for how to report success output
 - business failure - see [Record Time](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-925) (access required) for business failure scenarios. Also see [standard command output](TODO) (**TODO**)  for how to report business failures
 - technical failure - see [standard command output](TODO) (**TODO**) for how to report technical failures

## remove TimeEntry
This command causes an existing `TimeEntry` entity to be removed. Note that the TimeEntryId should not be recycled and used again for a different `TimeEntry`

### inputs 
- TimeEntryId - mandatory -  the identifier of the `TimeEntry` to modify
- TenantId    - mandatory -  the tenant that holds the `TimeEntry`

### output
 - success - see [standard command output](TODO)(**TODO**)  for how to report success output
 - business failure - see [Record Time](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-925) (access required) for business failure scenarios. Also see [standard command output](TODO) (**TODO**)  for how to report business failures
 - technical failure - see [standard command output](TODO) (**TODO**) for how to report technical failures

## find TimeEntry by date
This command retrieves a set of `TimeEntry` instances whose time period (time covered by `actualStartTime` and `actualEndTime`) contains the given date.

### inputs
- ownerId - mandatory - the identifier for the person who owns the TimeEntry
- date - mandatory -  the search term
- TenantId    - mandatory -  the tenant whose `TimeEntry` instances will be searched

### output
 - success - see [standard command output](TODO)(**TODO**)  for how to report success output
 - business failure - see [Record Time](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-925) (access required) for business failure scenarios. Also see [standard command output](TODO) (**TODO**)  for how to report business failures
 - technical failure - see [standard command output](TODO) (**TODO**) for how to report technical failures

## Get TimePeriodType
This command will ultimately retrieve a filtered list of TimeEntryTypes appropriate for the Person for whom the TimeEntry is being entered. The requirements for this are evolving but some of the filters identified so far are below:

1. Time Period Type should be dependent on profile of individual user.
2. Part Time workers will be the only users who have NWD (particularly so full time AHA users don't confuse it with SRD
3. Only those whose Person Profile indicates they can work on call will see on-call.
4. TimePeriodTypes have an Effective Start Date and and Effective End Date
5. Different organisations or even Areas within a Tenant may have different sets of Time Period Types.

### inputs 
- TenantId    - mandatory -  the tenant whose `TimeEntry` instances will be searched
- PersonId      - mandatory -  the Person for whom the TimeEntryTypes will be returned

### output
 - success - see [standard command output](TODO)(**TODO**)  for how to report success output
 - business failure - see [standard command output](TODO) (**TODO**) for how to report technical failures
 - response payload - upon a successful call to get TimePeriodType a payload containing a filtered list of all relevant TimeEntryTypes must be returned.
 
 ### Implementation Notes
 
 As the filtering logic and the Person profile to support such logic is not yet fully defined (as of 04 Aug 2022) it is suggested that this initial implementaton returns a hard code list containing the following data:
 
| TimePeriodId | Time Period Type   | ValueType |
| ------------ | ------------------ | --------- |
| 1            | Shift              | DateTime  |
| 2            | Scheduled Rest Day | Date      |
| 3            | Non-Working Day    | Date      |
| 4            | On-Call            | DateTime  |
| 5            | Absence            | Date      |
| 6            | Training           | DateTime  |
| 7            | Overtime           | DateTime  |
 