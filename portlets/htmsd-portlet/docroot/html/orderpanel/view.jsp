<%@ include file="/html/orderpanel/init.jsp"%>

<%
	String tabs1 = ParamUtil.getString(request, "tab1", "Pending");
    String tabNames = "Pending,Delivered";
%>

<portlet:renderURL var="tabsURL">
	<portlet:param name="tabs1" value="<%= tabs1 %>" />
</portlet:renderURL>

<liferay-ui:tabs names="<%= tabNames %>" refresh="true" param="tab1"
	url="<%= tabsURL.toString() %>">
	<%@ include file="/html/orderpanel/list.jsp"%>
</liferay-ui:tabs>



