<%@page%>

<%@taglib prefix = "jstl" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix = "acme" uri = "http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code = "administrator.claim.list.label.status" path = "status"/>
	<acme:list-column code = "administrator.claim.list.label.type" path = "type"/>
	<acme:list-column code = "administrator.claim.list.label.registrationMoment" path = "registrationMoment"/>
	<acme:list-column code = "administrator.claim.list.label.cIsAccepted" path = "cIsAccepted"/>
	<acme:list-payload path = "payload"/>
</acme:list>