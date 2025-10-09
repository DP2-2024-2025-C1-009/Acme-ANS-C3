<%--
- form.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<h2>
	<acme:print code="assistance-agent.dashboard.form.title.general-indicators"/>
</h2>

<table class="table table-sm">
	<tr>
		<th scope="row">
			<acme:print code="assistance-agent.dashboard.form.label.ratio-of-claims-stored-successfully"/>
		</th>
		<td>
			<acme:print value="${ratioOfClaimsStoredSuccessfully}"/>
		</td>
	</tr>

	<tr>
		<th scope="row">
			<acme:print code="assistance-agent.dashboard.form.label.ratio-of-claims-rejected"/>
		</th>
		<td>
			<acme:print value="${ratioOfClaimsRejected}"/>
		</td>
	</tr>

	<tr>
		<th scope="row">
			<acme:print code="assistance-agent.dashboard.form.label.top-three-months-highest-number-of-claims"/>
		</th>
		<td>
			<acme:print value="${topThreeMonthsHighestNumberOfClaims}"/>
		</td>
	</tr>

	<tr>
		<th scope="row">
			<acme:print code="assistance-agent.dashboard.form.label.avg-number-of-logs-claims-have"/>
		</th>
		<td>
			<acme:print value="${avgNumberOfLogsClaimsHave}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="assistance-agent.dashboard.form.label.min-number-of-logs-claims-have"/>
		</th>
		<td>
			<acme:print value="${minNumberOfLogsClaimsHave}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="assistance-agent.dashboard.form.label.max-number-of-logs-claims-have"/>
		</th>
		<td>
			<acme:print value="${maxNumberOfLogsClaimsHave}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="assistance-agent.dashboard.form.label.dev-number-of-logs-claims-have"/>
		</th>
		<td>
			<acme:print value="${devNumberOfLogsClaimsHave}"/>
		</td>
	</tr>

	<tr>
		<th scope="row">
			<acme:print code="assistance-agent.dashboard.form.label.avg-number-assisted-last-month"/>
		</th>
		<td>
			<acme:print value="${avgNumberClaimsAssistedDuringLastMonth}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="assistance-agent.dashboard.form.label.min-number-assisted-last-month"/>
		</th>
		<td>
			<acme:print value="${minNumberClaimsAssistedDuringLastMonth}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="assistance-agent.dashboard.form.label.max-number-assisted-last-month"/>
		</th>
		<td>
			<acme:print value="${maxNumberClaimsAssistedDuringLastMonth}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="assistance-agent.dashboard.form.label.dev-number-assisted-last-month"/>
		</th>
		<td>
			<acme:print value="${devNumberClaimsAssistedDuringLastMonth}"/>
		</td>
	</tr>
</table>