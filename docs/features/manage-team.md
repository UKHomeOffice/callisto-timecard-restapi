# Manage Team data
The TimeCard container keeps its own representation of a team. Team data is not mastered in TimeCard container but nonetheless the TimeCard needs to maintain an up to date and accurate copy of Callisto team structures to support the actions that the manager of a team is allowed to perform.

In the case of both creation and updating `Team` and `TeamMember` resources the `Team Events Producer` is the master feed of events that detail changes. The TimeCard container subscribes to those events and uses them to keep its internal representation up to date.