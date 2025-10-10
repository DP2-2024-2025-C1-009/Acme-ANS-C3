<%@page%>

<%@taglib prefix = "jstl" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix = "acme" uri = "http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code = "administrator.tracking-log.list.label.resolutionPercentage" path = "resolutionPercentage"/>
	<acme:list-column code = "administrator.tracking-log.list.label.status" path = "status"/>
	<acme:list-column code = "administrator.tracking-log.list.label.isPublished" path = "isPublished"/>
	<acme:list-payload path = "payload"/>
</acme:list>