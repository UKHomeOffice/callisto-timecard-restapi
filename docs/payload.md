<!-- Generator: Widdershins v4.0.1 -->

<h1 id="timecard">TimeCard v0.1.0</h1>

The TimeCard container exposes a number of models to clients. The TimeEntry is a key model from which most others hang

# Schemas

<h2 id="tocS_Note">Note</h2>
<!-- backwards compatibility -->
<a id="schemanote"></a>
<a id="schema_Note"></a>
<a id="tocSnote"></a>
<a id="tocsnote"></a>

```json
{
  "content": "string",
  "date": "current date and time",
  "created_at": "2019-08-24T14:15:22Z",
  "author": {
    "personId": 0,
    "firstName": [
      "string"
    ],
    "lastName": "string"
  }
}

```

A note is used to carry arbitrary textual information about a date. Notes are immutable.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|content|string|true|none|Holds the note's text|
|date|string(date)|true|none|The date that the note is associated with|
|created_at|string(date-time)|false|none|Assigned by the TimeCard container when the note is persisted|
|author|[Person](#schemaperson)|true|none|The person who wrote the note's content|

<h2 id="tocS_CodedValue">CodedValue</h2>
<!-- backwards compatibility -->
<a id="schemacodedvalue"></a>
<a id="schema_CodedValue"></a>
<a id="tocScodedvalue"></a>
<a id="tocscodedvalue"></a>

```json
{
  "code": "string",
  "namespace": "http://[tenantId].timecard.sas.digital.homeoffice.go.uk",
  "display": "the value of the code property"
}

```

A CodeValue encapsulates a code

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|true|none|The code itself|
|namespace|string(uri)|false|none|The optional namespace that the code belongs to. The namespace and code together present a unique identifier for the CodedValue in the context of the TimeCard container|
|display|string|false|none|The optional human-readable label for the code|

<h2 id="tocS_Person">Person</h2>
<!-- backwards compatibility -->
<a id="schemaperson"></a>
<a id="schema_Person"></a>
<a id="tocSperson"></a>
<a id="tocsperson"></a>

```json
{
  "personId": 0,
  "firstName": [
    "string"
  ],
  "lastName": "string"
}

```

A Person is an authorised human user who is able to create or modify TimeCard container data

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|personId|number|true|none|Assigned by the TimeCard container|
|firstName|[string]|true|none|Given names (not always 'first'). Includes middle names. Given Names appear in the correct order for presenting the name|
|lastName|string|true|none|The person's last name. Often known as 'surname'|

<h2 id="tocS_TimeEntry">TimeEntry</h2>
<!-- backwards compatibility -->
<a id="schematimeentry"></a>
<a id="schema_TimeEntry"></a>
<a id="tocStimeentry"></a>
<a id="tocstimeentry"></a>

```json
{
  "timeEntryId": 0,
  "version": 0,
  "owner": {
    "personId": 0,
    "firstName": [
      "string"
    ],
    "lastName": "string"
  },
  "actualStartTime": "2019-08-24T14:15:22Z",
  "actualEndTime": "2019-08-24T14:15:22Z",
  "timePeriodType": {
    "code": "string",
    "namespace": "http://[tenantId].timecard.sas.digital.homeoffice.go.uk",
    "display": "the value of the code property"
  },
  "activity": {
    "code": "string",
    "namespace": "http://[tenantId].timecard.sas.digital.homeoffice.go.uk",
    "display": "the value of the code property"
  }
}

```

A TimeEntry carries the time periods during which employees have performed a business activity (e.g. PCP, dog handling etc) or HR activity (e.g. leaves, training etc). TimeEntry is the actual recording of hours done by employees as per their roster. Encapsulates day and time (to the minute).

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|timeEntryId|number|false|none|Assigned by the TimeCard container|
|version|number|false|none|The version of the TimeEntry as assigned by the TimeCard container. This value changes when the resource is created, updated, or deleted.|
|owner|[Person](#schemaperson)|true|none|The Person who owns this TimeEntry i.e. the Person who has performed the activity in the given time period|
|actualStartTime|string(date-time)|true|none|The start time of the activity that was worked (to the minute)|
|actualEndTime|string(date-time)|true|none|The end time of the activity that was worked (to the minute)|
|timePeriodType|[CodedValue](#schemacodedvalue)|true|none|The type of time entry (e.g. a shift, a standard rest day)|
|activity|[CodedValue](#schemacodedvalue)|true|none|The type of work that has been carried out (e.g PCP)|

