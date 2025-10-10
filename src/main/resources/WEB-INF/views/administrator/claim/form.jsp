<%@page%>

<%@taglib prefix = "jstl" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix = "acme" uri = "http://acme-framework.org/"%>

<acme:form>
	<acme:input-moment code = "administrator.claim.form.label.registrationMoment" path = "registrationMoment" readonly = "true"/>	
	<acme:input-email code = "administrator.claim.form.label.passengerEmail" path = "passengerEmail"/>	
	<acme:input-textarea code = "administrator.claim.form.label.description" path = "description"/>
	<acme:input-select code = "administrator.claim.form.label.type" path = "type" choices = "${types}"/>
	<acme:input-select code = "administrator.claim.form.label.status" path = "status" choices = "${statuses}" readonly = "true"/>	
	<acme:input-select code = "administrator.claim.form.label.leg" path = "leg" choices = "${legs}"/>
	
	<jstl:choose>
		<jstl:when test = "${acme:anyOf(_command, 'show|update|delete|publish')}">
			<acme:button code = "administrator.claim.form.button.trackingLogs" action = "/administrator/tracking-log/list?masterId=${id}"/>
		</jstl:when>	
	</jstl:choose>
</acme:form>