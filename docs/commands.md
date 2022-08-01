# TimeCard Commands

## create timecard
This command causes a new TimeCard entity to be created and stored. At a minimum one TimeEntry entity must be associated with the TimeCard for the creation to be successful. If no TimeEntry is passed with the TimeCard then the command invocation should fail. Ideally the creation of a TimeEntry and a brand new TimeCard should be atomic i.e. if the TimeCard creation fails then the command invocation should fail.

### inputs 
- timeCard - mandatory - the TimeCard entity

### output
 - success - see [standard command output](TODO)(**TODO**)  for how to report success output
 - business failure - see [Record Time](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-925) (access required) for business failure scenarios. Also see [standard command output](TODO) (**TODO**)  for how to report business failures
 - technical failure - see [standard command output](TODO) (**TODO**) for how to report technical failures

## update timecard
This command causes an existing TimeCard entity to be updated and stored. 

### inputs 
- timeCard - mandatory - the TimeCard entity
- tenantId - mandatory - the tenant that holds the timecard

### output
 - success - see [standard command output](TODO)(**TODO**)  for how to report success output
 - business failure - see [Record Time](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-925) (access required) for business failure scenarios. Also see [standard command output](TODO) (**TODO**)  for how to report business failures
 - technical failure - see [standard command output](TODO) (**TODO**) for how to report technical failures **TODO** ETag for out of stale copy of timecard to update?

## get timecard
This command retrieves a single timecard that matches the query parameters. Note that it is possible that no timecard can be found that matches the parameters however it should not be possible for multiple timecards to match the same set of parameters.

### inputs 
timecardDate - mandatory - the date that the timecard is associated with
timecardOwnerId  - mandatory - the person who owns the timecard
tenantId - mandatory - the tenant that holds the timecard

### output
- success - return [TimeCard](../src/main/avro/uk/gov/homeoffice/digital/sas/timecard/time_card.avsc). See [RESTFul endpoint blueprint](https://github.com/UKHomeOffice/callisto-docs/blob/main/blueprints/restful-endpoint.md#handle-success-consistently)
- business failure - see [Record Time](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-925) (access required) for business failure scenarios. Also see [RESTFul endpoint blueprint](https://github.com/UKHomeOffice/callisto-docs/blob/main/blueprints/restful-endpoint.md#handle-errors-gracefully-and-return-standard-error-codes)  for how to report business failures
 - technical failure - see [RESTFul endpoint blueprint](https://github.com/UKHomeOffice/callisto-docs/blob/main/blueprints/restful-endpoint.md#handle-errors-gracefully-and-return-standard-error-codes) for how to report technical failures

## add timeentry
This command causes a new TimeEntry entity to be created and associated with an existing TimeCard entity. Note that if the TimeCard does not already exist then the command invocation should fail.

### inputs 
- TimeCardId - mandatory -  the identifier of the  [TimeCard](../src/main/avro/uk/gov/homeoffice/digital/sas/timecard/time_card.avsc) that the new  TimeEntry will be associated with
- TenantId    - mandatory -  the tenant that holds the TimeEntry (and associated TimeCard)
- TimeEntry - mandatory - the [TimeEntry](../src/main/avro/uk/gov/homeoffice/digital/sas/timecard/timeentry.avsc) that is to be created

### output
 - success - see [standard command output](TODO)(**TODO**)  for how to report success output
 - business failure - see [Record Time](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-925) (access required) for business failure scenarios. Also see [standard command output](TODO) (**TODO**)  for how to report business failures
 - technical failure - see [standard command output](TODO) (**TODO**) for how to report technical failures
 

## modify timeentry
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
 
