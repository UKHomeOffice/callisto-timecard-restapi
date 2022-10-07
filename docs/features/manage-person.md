# Manage person
The TimeCard container keeps its own representation of a person. It is not exposed to clients (e.g. the Single-Page Application). Instead the data it holds is used to support other use cases (e.g. Record time). The person data held within TimeCard is effectively read-only from a TimeCard perspective. However there needs to be a mechanism to keep that data current.

##### Creation
The `Person Onboarding Service` is responsible for managing the assembly of all data required to form a valid Callisto `Person`. Until all of that data is sourced and combined a given Person cannot exist on Callisto. However once the data is present then the process of creating the TimeCard container's `Person` can begin - 

 1. The `Person Onboarding Service` publishes the `Person` via the `Person Events Publisher`
 2. The TimeCard container receives the `Person` event via it's topic subscription 
 3. The TimeCard container uses the data in the `Person` event to create a `Person` entity in it's internal Database

##### Update
A person can be updated via one of two routes - User initiated or Manager initiated. In either case the update is performed outside of the TimeCard container. 

 1. The updated `Person` data is published via the the `Person Events Publisher`
 2. The TimeCard container receives the `Person` event via it's topic subscription 
 3. The TimeCard container uses the data in the `Person` event to update an existing `Person` entity in it's internal Database