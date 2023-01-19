# EAHW-1249 Annual Target Hours

This high-level design is intended to cover the elements of the Annual Target Hours feature that affect TimeCard. It seeks to:

1.  Guide the reader through the relevant parts of the container definitions which will be used to satisfy this feature.
2.  Where required illustrate the high-level container orchestration.
3.  Detail and explain the relevant parts of the payload model impacting this feature.

The Annual Target Hours feature [feature definition](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1249) (access required) in Jira details the user stories that make up the feature. The user stories contain more detailed requirement around business rules and validation logic. The intention is that this document should be used as a guide when designing and implementing and testing against a given story from the Record Flexible Change In Timecard feature

This design builds upon the design for [Record Time](./record-time.md) and introduces some changes to that design (see below)

The Annual Target Hours feature primarily impacts the Accruals container however the process detailed in that feature is triggered by events published by the TimeCard container.

## Considerations
When a `TimeEntry` is successfully recorded by the TimeCard container it triggers the publication of an event that encapsulates that `TimeEntry` resource along with the type of action that was performed on the resource. As an event producer the TimeCard container should follow the blueprints below - 

### Topic
Based on the guideance in the [topic creation](https://github.com/UKHomeOffice/callisto-docs/blob/main/blueprints/topic-creation.md) blueprint the following are suggested - 

- name - callisto-timecard-timeentry
- partition key - `TimeEntry.tenantId`-`TimeEntry.ownerId`

### Event
Based on the guideance in the [event publication (schema & trigger points)](https://github.com/UKHomeOffice/callisto-docs/blob/main/blueprints/event-publishing-and-consuming.md) blueprint the following are suggested - 

- events to capture - create, update & delete
- use ResourceReference as `resource.content` for a delete event

## Notable changes
There are some changes to the resources exposed by the TimeCard container that have been made to support this feature

### `TimeEntry`
- `plannedStartTime` - dropped (no longer needed)
- `plannedEndTime` - dropped (no longer needed)
- `actualStartTime` - renamed to startTime (clearer name after `plannedStartTime` was dropped)
- `actualEndTime` - renamed to endTime (clearer name after `plannedEndTime` was dropped)
- `mealBreakAllowance` - added (needed by Accruals for balance calculations)
- `mealBreakTaken` - added (needed by Accruals for balance calculations)



