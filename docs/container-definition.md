
# TimeCard container

## Executive summary

![Callisto containers](./images/container.png)

The TimeCard container allows a person to record time that they have worked. This is sometimes known as booking time. It also allows that person to see what hours they have been scheduled to work. In addition, the container allows a manager to view and approve timecard entries for members of their team. Finally, the TimeCard container allow a person and their manager to make changes to scheduled time which are known as flexible (or flex) changes. 

In order to present a person with their scheduled hours, the TimeCard container consumes ScheduledActivityEvent events produced by the Scheduler Container.  

Internally the TimeCard container maintains a set of TimeEntry entities for a person which record scheduled and booked time. TimeEntry entities are collected under a single TimeCard entity for that person. TimeCard additionally holds the approval state of the TimeEntry entities that it owns. Additionally, the TimeCard container stores FlexChange entity against the TimeEntry which was impacted by the change. 


## What is the container for and why would you use it?
In the wider context of Callisto the TimeCard container is used to convey shift start/end times and dates for the given shift worker. 

A shift worker can use Timecard container to see what days and hours they have been scheduled to work on. It also allows them to record their actual hours worked against those scheduled time slots. 

A shift manager can also use the Timecard container to see who is scheduled to work in a given time period (**TODO** question - would this be something that the scheduler container provides and not the timecard container) 

A Timecard approver will also use the Timecard container to approve or reject the time that a given shift worker has recorded. 

## Dependencies

Person container – amongst other things (**TBC**) a person’s full time status determines whether or not they can take a meal break or how long their meal break can be. TimeCard needs to know a person’s full time status in order to present the Shift worker with the appropriate options when recording their meal break. 

Reference Data container – used to retrieve Activity enumeration which allows a Shift worker to record their time against an Activity 

Scheduler container – Timecard consumes ScheduledActivity events in order to create planned TimeEntry instances. Note that ScheduledActivity encapsulates absences (both planned and unplanned) and flex changes.

## Model

### Entity

**TimeEntry** 
Used to record both planned and actual time. Encapsulates day and time (to the minute). In addition, the time entry captures the way that the time has been spent via the activity property  

**Notes** 
At this stage it is not clear if the Notes should be associated with a TimeEntry, a TimeCard or both. In any case Notes are used to communicate arbitrary information between individuals that relates to the TimeCard/TimeEntry. Notes cannot exist on their own. 

**TimeLine** 
**TODO** – confirm that this is a separate entity 

**TimeCard** 
A TimeCard instance is for a given shift worker. It collects multiple TimeEntry instances and layers on the concept of approval.  

**FlexChange** 
Encapsulates a change to one or more planned TimeEntry instances. It references TimeEntry by using the TimeEntry.id as a foreign key. There is a set of specific reasons that planned time can be changed and it must be approved. The FlexChange records this reason along with the new planned times for the associated TimeEntry instances.   

A FlexChange can be created directly by a Shift worker on their TimeCard. A FlexChange can also come from the Scheduler container 

### Enum

**TimeCardStatus** 
Describes the state that a TimeCard can be in. Covers the concept of planning time and then booking actual time. In addition, the status encapsulates TimeCard approval 

**TimeEntryActivity** 
A set of different types of work that a Shift worker can record time against 

### Events produced

**TimeEntryEvent** 
Indicates that a new TimeEntry has been created (covers both planned or actual time) 

**FlexChangeEvent** 
Indicates that a new FlexChange has been created 

**TODO** – how to model approval 

### Events consumed

**ScheduledActivity**
Triggers the creation or update of a TimeEntry. The TimeEntry is linked to a parent TimeCard via the person id which is taken from the ScheduledActivity when creating the TimeEntry 