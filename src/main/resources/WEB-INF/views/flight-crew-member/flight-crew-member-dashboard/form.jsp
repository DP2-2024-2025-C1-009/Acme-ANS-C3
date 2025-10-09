<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.lastFiveDestinations" path="lastFiveDestinations" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.numberOfLegsLowIncident" path="numberOfLegsLowIncident" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.numberOfLegsMediumIncident" path="numberOfLegsMediumIncident" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.numberOfLegsHighIncident" path="numberOfLegsHighIncident" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.crewMembersLastLeg" path="crewMembersLastLeg" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.confirmedFlightAssignments" path="confirmedFlightAssignments" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.pendingFlightAssignments" path="pendingFlightAssignments" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.cancelledFlightAssignments" path="cancelledFlightAssignments" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.average" path="average" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.minimum" path="minimum" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.maximum" path="maximum" readonly="true"/>
	<acme:input-textbox code="flightCrewMember.dashboard.form.label.standard" path="standard" readonly="true"/>
</acme:form>