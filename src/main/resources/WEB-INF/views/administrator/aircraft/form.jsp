<%@page %>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="acme" uri="http://acme-framework.org/" %>

<acme:form>
	<acme:input-textbox code="aircraft.form.label.model" path="model"/>
	<acme:input-textbox code="aircraft.form.label.numberRegistration" path="numberRegistration"/>
	<acme:input-textbox code="aircraft.form.label.numberPassengers" path="numberPassengers"/>
	<acme:input-textbox code="aircraft.form.label.loadWeight" path="loadWeight"/>
	<acme:input-textbox code="aircraft.form.label.isActive" path="isActive"/>
	<acme:input-textbox code="aircraft.form.label.optionalDetails" path="optionalDetails"/>
	<acme:input-select code="aircraft.form.label.airline" path="airline" choices="${airlinesChoices}"/>
	
	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:input-checkbox code="aircraft.form.label.confirmation" path="confirmation"/>
			<acme:submit code="aircraft.form.button.create" action="/administrator/aircraft/create"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update')}">
			<acme:input-checkbox code="aircraft.form.label.confirmation" path="confirmation"/>
			<acme:submit code="aircraft.form.button.update" action="/administrator/aircraft/update"/>
		</jstl:when>
	</jstl:choose>
</acme:form>