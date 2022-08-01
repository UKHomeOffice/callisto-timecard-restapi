
# TimeCard Commands

## add TimeEntry
This command causes a new TimeEntry entity to be created 

### inputs 
- TenantId    - mandatory -  the tenant that holds the TimeEntry
- TimeEntry - mandatory - the [TimeEntry](./payload.md#timeentry) that is to be created

### output
 - success - see [standard command output](TODO)(**TODO**)  for how to report success output
 - business failure - see [Record Time](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-925) (access required) for business failure scenarios. Also see [standard command output](TODO) (**TODO**)  for how to report business failures
 - technical failure - see [standard command output](TODO) (**TODO**) for how to report technical failures
 
## modify TimeEntry
This command causes an existing TimeEntry entity to be modified. Note that if the TimeCard does not already exist then the command invocation should fail.

### inputs 
- TimeEntryId - mandatory -  the identifier of the TimeEntry to modify
- TenantId    - mandatory -  the tenant that holds the timecard

### output
 - success - see [standard command output](TODO)(**TODO**)  for how to report success output
 - business failure - see [Record Time](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-925) (access required) for business failure scenarios. Also see [standard command output](TODO) (**TODO**)  for how to report business failures
 - technical failure - see [standard command output](TODO) (**TODO**) for how to report technical failures
 
## Data storage
When considering how best to represent the payloads in a data store consider the [data storage design](./storage.md)