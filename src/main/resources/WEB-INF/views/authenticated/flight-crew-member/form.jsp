<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="authenticated.flight-crew-member.form.label.employeeCode" path="employeeCode" readonly = "${readOnly}" placeholder = "acme.placeholders.form.flightCrewMember.employeeCode"/>
	<acme:input-textbox code="authenticated.flight-crew-member.form.label.phoneNumber" path="phoneNumber" placeholder = "acme.placeholders.form.flightCrewMember.phoneNumber"/>
	<acme:input-textarea code="authenticated.flight-crew-member.form.label.languageSkills" path="languageSkills" placeholder = "acme.placeholders.form.flightCrewMember.languageSkills"/>
	<acme:input-select code="authenticated.flight-crew-member.form.label.flightCrewMemberStatus" path="flightCrewMemberStatus" choices = "${statusChoices}"/>
	<acme:input-money code="authenticated.flight-crew-member.form.label.salary" path="salary" readonly = "${readOnly}" placeholder = "acme.placeholders.form.flightCrewMember.salary"/>
	<acme:input-textbox code="authenticated.flight-crew-member.form.label.yearsOfExperience" path="yearsOfExperience" readonly = "${readOnly}" placeholder = "acme.placeholders.form.flightCrewMember.yearsOfExperience"/>
	<acme:input-select code="authenticated.flight-crew-member.form.label.airline" path="airline" choices = "${airlineChoices}" readonly = "${readOnly}"/>
	<jstl:if test="${_command == 'create'}">
		<acme:submit code="authenticated.flight-crew-member.form.button.create" action="/authenticated/flight-crew-member/create"/>
	</jstl:if>
	
	<jstl:if test="${_command == 'update'}">
		<acme:submit code="authenticated.flight-crew-member.form.button.update" action="/authenticated/flight-crew-member/update"/>
	</jstl:if>
</acme:form>