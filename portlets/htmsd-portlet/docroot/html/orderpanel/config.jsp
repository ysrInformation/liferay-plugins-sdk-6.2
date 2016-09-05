<%@include file="/html/shoppingcart/init.jsp" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />
<%
	String receiptInfo  = portletPreferences.getValue("receiptInfo", "");
%>

<aui:form action="<%= configurationURL %>" method="post" name="fm">	
	<aui:input name="<%=Constants.CMD %>" type="hidden" value="<%=Constants.UPDATE %>"/>
	<aui:input name="preferences--receiptInfo--" type="textarea" value="<%= receiptInfo %>">
		<aui:validator name="required"/> 
	</aui:input>
	
	<aui:button type="submit" value="Save"/>
</aui:form>