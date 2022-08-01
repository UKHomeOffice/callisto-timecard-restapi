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
- date - mandatory -  the search term
- TenantId    - mandatory -  the tenant whose `TimeEntry` instances will be searched

### output
 - success - see [standard command output](TODO)(**TODO**)  for how to report success output
 - business failure - see [Record Time](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-925) (access required) for business failure scenarios. Also see [standard command output](TODO) (**TODO**)  for how to report business failures
 - technical failure - see [standard command output](TODO) (**TODO**) for how to report technical failures