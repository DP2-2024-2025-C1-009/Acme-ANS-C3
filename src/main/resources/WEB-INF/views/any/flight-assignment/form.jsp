<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

	<acme:input-textbox code="flight-crew-member.flight-assignment.list.label.duty" path="duty" readonly ="true"/>
	<acme:input-moment code="flight-crew-member.flight-assignment.list.label.lastUpdate" path="lastUpdate" readonly ="true"/>
	<acme:input-textbox code="flight-crew-member.flight-assignment.list.label.status" path="status"  readonly ="true"/>
	<acme:input-textbox code="flight-crew-member.flight-assignment.list.label.remarks" path="remarks" readonly ="true"/>
	<acme:input-textbox code="flight-crew-member.flight-assignment.list.label.leg" path="legCode" readonly ="true"/>
	<acme:input-textbox code="flight-crew-member.flight-assignment.list.label.crewMember" path="crewMemberName" readonly ="true"/>

</acme:form>