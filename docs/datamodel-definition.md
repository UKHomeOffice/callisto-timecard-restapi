
# Model

## Entity

### TimeEntry

**TimeEntry** 
Used to record both planned and actual time. Encapsulates day and time (to the minute). In addition, the time entry captures the way that the time has been spent via the activity property
|Field|Type|Cardinality|Description|
|--|--|--|--|
| TimePeriod | 1..1 | Enumeration |Describes how the person spent their time. Note that depending on which value is selected other data might become associated with the TimeEntry |
| Activity| 1..1 | Enumeration |This may not be required as it is detailed by Scheduler and so would be a duplication|
| StartTime| 1..1 | DateTime | The start of the time period |
| EndTime | 1..1 | DateTime | The end of the time period |
  

### TimeCardNotes

**Notes** 
Notes are associated with a TimeCard. Notes are used to communicate arbitrary information between individuals that relates to the TimeCard. Notes cannot exist on their own. 

|Field|Type|Cardinality|Description|
|--|--|--|--|
| Note| 1..1 | text (256)| The note itself |
| Note| 1..1 | text (256)| The note itself |

### TimeLine

**TimeLine** 
**TODO** – confirm that this is a separate entity 

### TimeCard

**TimeCard** 
A TimeCard instance is for a given shift worker. It collects multiple TimeEntry instances and layers on the concept of approval.  A timecard maps to a single date.

|Field|Type|Cardinality|Description|
|--|--|--|--|
| TimeCardDate| 1..1 | date| The date that this timecard represents|
| TimeCardStatus| 1..1 | Enumeration | Describes the approval status of the TimeCard |
| Approver| 0..1 | foreign key| The person who approved this time card |
| ApprovalDate| 0..1 | DateTime | When this timecard was approved |
| Person| 1..1 | foreign key |The person who has spent the time that this TimeCard encapsulates|
| TimeEntries| 0..* | set of foreign keys| Time Entries |
| Notes| 0..* | set of foreign keys| Notes |

### FlexChange

**FlexChange** 
Encapsulates a change to one or more planned TimeEntry instances. It references TimeEntry by using the TimeEntry.id as a foreign key. There is a set of specific reasons that planned time can be changed and it must be approved. The FlexChange records this reason along with the new planned times for the associated TimeEntry instances.   

A FlexChange can be created directly by a Shift worker on their TimeCard. A FlexChange can also come from the Scheduler container 

### Enum

**TimeCardStatus** 
Describes the state that a TimeCard can be in. Covers the concept of planning time and then booking actual time. In addition, the status encapsulates TimeCard approval 

**TimeEntryActivity** 
A set of different types of work that a Shift worker can record time against 

**TimePeriod**
Describes the way a person has spent their time eg a shift, a non-working day, an absence

