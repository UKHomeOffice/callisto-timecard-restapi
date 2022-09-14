# TimeCard events

## Events produced

**TimeEntryEvent** 
Indicates that a new TimeEntry has been created (covers both planned or actual time) 

**FlexChangeEvent** 
Indicates that a new FlexChange has been created 


## Events consumed

**ScheduledActivity**
Triggers the creation or update of a TimeEntry. The TimeEntry is linked to a parent TimeCard via the person id which is taken from the ScheduledActivity when creating the TimeEntry 
