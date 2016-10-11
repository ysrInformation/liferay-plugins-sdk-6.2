<%@include file="/html/shoppingcart/init.jsp" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />
<%
	String noOfItems  = portletPreferences.getValue("noOfItems", "");
%>

<aui:form action="<%= configurationURL %>" method="post" name="fm">	
	<aui:input name="<%=Constants.CMD %>" type="hidden" value="<%=Constants.UPDATE %>"/>
	<aui:input name="preferences--noOfItems--"  value="<%= noOfItems %>">
		<aui:validator name="required"/> 
		<aui:validator name="number"/> 
	</aui:input>
	<aui:button type="submit" value="Save"/>
</aui:form>