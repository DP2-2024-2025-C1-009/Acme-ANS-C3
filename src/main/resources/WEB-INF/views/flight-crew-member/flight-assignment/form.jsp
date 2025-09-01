<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-moment code="flight-crew-member.flight-assignment.form.label.moment" path="moment" readonly="true" />
	<acme:input-textbox code="flight-crew-member.flight-assignment.form.label.crewMember" path="crewMember" readonly="true"/>
	<acme:input-select code="flight-crew-member.flight-assignment.form.label.duty" path="duty" choices="${dutyChoices}" readonly="draftMode"/>
	<acme:input-select code="flight-crew-member.flight-assignment.form.label.leg" path="leg" choices= "${legChoices}" readonly="draftMode"/>
		
		
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish|delete')}">
		<hr/>
		<h2>
			<acme:print code="crew-member.flight-assignment.form.title.leg"/>
		</h2>
		<acme:input-textbox code="flight-crew-member.flight-assignment.form.label.flightNumber" path="leg.flightNumber" readonly="true"/>
		<acme:input-textbox code="flight-crew-member.flight-assignment.form.label.status" path="leg.status" readonly="true"/>
		<acme:input-moment code="flight-crew-member.flight-assignment.form.label.scheduledDeparture" path="leg.scheduledDeparture" readonly="true"/>
		<acme:input-moment code="flight-crew-member.flight-assignment.form.label.scheduledArrival" path="leg.scheduledArrival" readonly="true"/>
		<acme:input-textbox code="flight-crew-member.flight-assignment.form.label.departureAirport" path="leg.departureAirport" readonly="true"/>
		<acme:input-textbox code="flight-crew-member.flight-assignment.form.label.arrivalAirport" path="leg.arrivalAirport" readonly="true"/>
		<acme:input-textbox code="flight-crew-member.flight-assignment.form.label.aircraft" path="leg.aircraft" readonly="true"/>
		<acme:input-textbox code="flight-crew-member.flight-assignment.form.label.flight" path="leg.flight" readonly="true"/>
	
	</jstl:when>
	</jstl:choose>
	
	
	<hr/>
	<acme:input-select code="flight-crew-member.flight-assignment.form.label.status" path="status" choices= "${statusChoices}" readonly="draftMode"/>
	<acme:input-textarea code="flight-crew-member.flight-assignment.form.label.remarks" path="remarks" readonly="draftMode"/>
	
	
	<jstl:choose>
		<jstl:when test="${_command == 'show' && canShowActivityLogs}">
			<acme:button code="flight-crew-member.flight-assignment.form.button.activity-log" action="/flight-crew-member/activity-log/list?assignmentId=${id}"/>					
		</jstl:when> 
	</jstl:choose>
	
	<jstl:if test="${_command == 'show' && !canShowActivityLogs}">
		<hr/>
		<div>
			<small><em><acme:print code="flight-crew-member.flight-assignment.form.message.publishToCreateLog"/></em></small>
		</div>
	</jstl:if>
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish')  && draftMode == true}">
			<acme:submit code="flight-crew-member.flight-assignment.form.button.update" action="/flight-crew-member/flight-assignment/update"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.delete" action="/flight-crew-member/flight-assignment/delete"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.publish" action="/flight-crew-member/flight-assignment/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="flight-crew-member.flight-assignment.form.button.create" action="/flight-crew-member/flight-assignment/create"/>
		</jstl:when>		
	</jstl:choose>	
</acme:form>

		