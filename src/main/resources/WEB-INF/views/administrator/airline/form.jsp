<%@page %>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="acme" uri="http://acme-framework.org/" %>

<acme:form>
	<acme:input-textbox code="airline.form.label.name" path="name" />
	<acme:input-textbox code="airline.form.label.iata-code" path="iataCode" />
	<acme:input-url code="airline.form.label.website" path="website" />
	<acme:input-select code="airline.form.label.type" path="type" choices="${types}" />
	<acme:input-moment code="airline.form.label.foundation-moment" path="foundationMoment" />
	<acme:input-email code="airline.form.label.email" path="email" />
	<acme:input-textbox code="airline.form.label.phone-number" path="phoneNumber" />

	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:input-checkbox code="airline.form.label.confirmation" path="confirmation" />
			<acme:submit code="airline.form.button.create" action="/administrator/airline/create" />
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update')}">
			<acme:input-checkbox code="airline.form.label.confirmation" path="confirmation" />
			<acme:submit code="airline.form.button.update" action="/administrator/airline/update" />
		</jstl:when>
	</jstl:choose>
</acme:form>
