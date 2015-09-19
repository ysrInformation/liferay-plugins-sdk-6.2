<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@include file="/html/shoppinglist/init.jsp" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />
<%
	String noOfItems  = portletPreferences.getValue("noOfItems", "");
	String categoryToDisplay  = portletPreferences.getValue("categoryToDisplay", "");
%>
<aui:form action="<%= configurationURL %>" method="post" name="fm">	
	<aui:input name="preferences--noOfItems--"  value="<%=noOfItems %>">
		<aui:validator name="required"/> 
		<aui:validator name="number"/> 
	</aui:input>
	<aui:input name="<%=Constants.CMD %>" type="hidden" value="<%=Constants.UPDATE %>"/>
	<aui:button type="submit" value="Save"/>
</aui:form>