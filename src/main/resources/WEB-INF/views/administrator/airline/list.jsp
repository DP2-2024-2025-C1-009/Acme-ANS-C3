<%@page %>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="acme" uri="http://acme-framework.org/" %>

<acme:list>
	<acme:list-column code="airline.list.label.name" path="name" width="10%" />
	<acme:list-column code="airline.list.label.iata-code" path="iataCode" width="20%" />
	<acme:list-column code="airline.list.label.type" path="type" width="10%" />

	<acme:list-payload path="payload" />
</acme:list>

<acme:button code="airline.list.button.new" action="/administrator/airline/create" />
