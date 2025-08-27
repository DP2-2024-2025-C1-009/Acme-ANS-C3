<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

  <jstl:when test="${_command == 'create'}">
    <acme:input-select  code="flight-crew-member.flight-assignment.list.label.duty"              path="duty"      choices="${dutyChoices}"/>
    <acme:input-moment  code="flight-crew-member.flight-assignment.list.label.lastUpdate"        path="lastUpdate" readonly="true"/>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.assignmentStatus"  path="status"    readonly="true"/>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.remarks"           path="remarks"   placeholder="acme.flightAssignment.placeholder.remarks"/>
    <acme:input-select  code="flight-crew-member.flight-assignment.list.label.legs"              path="leg"       choices="${legChoices}"/>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.availableFlightCrewMembers" path="name" readonly="true"/>

    <acme:submit code="flight-crew-member.flight-assignment.form.button.create" action="/flight-crew-member/flight-assignment/create"/>
  </jstl:when>

  <jstl:when test="${_command == 'show' && draftMode == false}">
    <acme:input-select  code="flight-crew-member.flight-assignment.list.label.duty"              path="duty"      choices="${dutyChoices}" readonly="true"/>
    <acme:input-moment  code="flight-crew-member.flight-assignment.list.label.lastUpdate"        path="lastUpdate" readonly="true"/>
    <acme:input-select  code="flight-crew-member.flight-assignment.list.label.assignmentStatus"  path="status"    choices="${statusChoices}" readonly="true"/>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.remarks"           path="remarks"   readonly="true"/>
    <acme:input-select  code="flight-crew-member.flight-assignment.list.label.legs"              path="leg"       choices="${legChoices}" readonly="true"/>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.availableFlightCrewMembers" path="name" readonly="true"/>

    <acme:button code="flight-crew-member.flight-assignment.form.button.activityLogs"
                 action="/flight-crew-member/activity-log/list?fId=${id}"/>
  </jstl:when>


  <jstl:when test="${acme:anyOf(_command, 'show|update|publish') && draftMode == true}">
    <acme:input-select  code="flight-crew-member.flight-assignment.list.label.duty"              path="duty"      choices="${dutyChoices}"/>
    <acme:input-moment  code="flight-crew-member.flight-assignment.list.label.lastUpdate"        path="lastUpdate" readonly="true"/>
    <acme:input-select  code="flight-crew-member.flight-assignment.list.label.assignmentStatus"  path="status"    choices="${statusChoices}"/>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.remarks"           path="remarks"   placeholder="acme.flightAssignment.placeholder.remarks"/>
    <acme:input-select  code="flight-crew-member.flight-assignment.list.label.legs"              path="leg"       choices="${legChoices}"/>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.availableFlightCrewMembers" path="name" readonly="true"/>

    <acme:submit code="flight-crew-member.flight-assignment.form.button.update"  action="/flight-crew-member/flight-assignment/update"/>
    <acme:submit code="flight-crew-member.flight-assignment.form.button.publish" action="/flight-crew-member/flight-assignment/publish"/>
  </jstl:when>

  <jstl:when test="${_command == 'delete'}">
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.duty"              path="duty"       readonly="true"/>
    <acme:input-moment  code="flight-crew-member.flight-assignment.list.label.lastUpdate"        path="lastUpdate" readonly="true"/>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.assignmentStatus"  path="status"     readonly="true"/>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.remarks"           path="remarks"    readonly="true"/>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.legs"              path="legLabel"   readonly="true"/>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.availableFlightCrewMembers" path="name" readonly="true"/>

    <acme:submit code="flight-crew-member.flight-assignment.form.button.delete" action="/flight-crew-member/flight-assignment/delete"/>
  </jstl:when>

</acme:form>
