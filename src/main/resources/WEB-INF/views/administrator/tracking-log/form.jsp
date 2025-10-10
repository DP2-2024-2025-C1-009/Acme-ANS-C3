<%@page%>

<%@taglib prefix = "jstl" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix = "acme" uri = "http://acme-framework.org/"%>

<acme:form>
	<acme:input-moment code = "administrator.tracking-log.form.label.updateMoment" path = "updateMoment" readonly = "true"/>	
	<acme:input-moment code = "administrator.tracking-log.form.label.creationMoment" path = "creationMoment" readonly = "true"/>	
	<acme:input-textbox code = "administrator.tracking-log.form.label.steps" path = "steps"/>
	<acme:input-double code = "administrator.tracking-log.form.label.resolutionPercentage" path = "resolutionPercentage" placeholder = "administrator.tracking-log.form.placeholder.resolutionPercentage"/>
	<acme:input-textarea code = "administrator.tracking-log.form.label.resolution" path = "resolution"/>
	<acme:input-select code = "administrator.tracking-log.form.label.status" path = "status" choices = "${statuses}"/>	
</acme:form>