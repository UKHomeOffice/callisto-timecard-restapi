# Authorization
Three roles exist in the context of the TimeCard container 

- timecard-user - a person who has worked a number of hours and wishes to record their time. This same person may also want to view or amend previously recorded time. FFinally this person may want to look ahead in time to see what hours they have been planned or rostered to work
- timecard-manager - a person who looks after a team of people who have the timecard-user role. The manager can view and update the recorded and planned time of their team members
- timecard-ref-data-admin - a person who can manage reference data that the TimeCard container relies on

##### Permissions
<table>
	<thead>
		<tr>
			<th rowspan="2">Role</th>
			<th colspan="3">TimeEntry</th>
			<th colspan="3">Note</th>
			<th colspan="3">FlexChange</th>
			<th colspan="3" rowspan="1">FlexChangeNote</th>
			<th colspan="3">Team</th>
			<th colspan="3">TeamMember</th>
			<th colspan="3">TimePeriodType</th>
		</tr>
		<tr>
			<th>owner</th>
			<th>team</th>
			<th>public</th>
			<th>owner</th>
			<th>team</th>
			<th>public</th>
			<th>owner</th>
			<th>team</th>
			<th>public</th>
			<th>owner</th>
			<th>team</th>
			<th>public</th>
			<th>owner</th>
			<th>team</th>
			<th>public</th>
			<th>owner</th>
			<th>team</th>
			<th>public</th>
			<th>owner</th>
			<th>team</th>
			<th>public</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>user</td>
			<td>c r u d</td>
			<td>-</td>
			<td>-</td>
			<td>c r</td>
			<td>-</td>
			<td>-</td>
			<td>c r u d</td>
			<td>-</td>
			<td>-</td>
			<td>c r</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>r</td>
		</tr>
		<tr>
			<td>manager</td>
			<td>c r u d</td>
			<td>c r u d</td>
			<td>-</td>
			<td>c r</td>
			<td>c r u d</td>
			<td>-</td>
			<td>c r u d</td>
			<td>&nbsp;c r u d</td>
			<td>-</td>
			<td>c r&nbsp;</td>
			<td>c r&nbsp;</td>
			<td>-</td>
			<td>r</td>
			<td>r</td>
			<td>-</td>
			<td>r</td>
			<td>r</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>r</td>
		</tr>
		<tr>
			<td>ref-data-admin</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>c r u d</td>
			<td>-</td>
			<td>r</td>
		</tr>
	</tbody>
</table>
