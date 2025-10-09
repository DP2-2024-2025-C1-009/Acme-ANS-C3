<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.airport.label.name" path="airportName" width="25%"/>
	<acme:list-column code="administrator.airport.label.iata" path="iataCode"  width="25%"/>
	<acme:list-column code="administrator.airport.label.city" path="city" width="25%"/>
	<acme:list-column code="administrator.airport.label.operationalScope" path="operationalScope"  width="25%"/>
	<acme:list-payload path="payload"/>	
</acme:list>	


	<acme:button code="administrator.airport.button.create" action="/administrator/airport/create"/>

