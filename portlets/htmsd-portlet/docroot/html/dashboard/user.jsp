<%@page import="com.liferay.portal.model.ListTypeConstants"%>
<%@include file="/html/dashboard/init.jsp" %>

<portlet:renderURL var="addItemsURL">
	<portlet:param name="jspPage" value="/html/dashboard/additem.jsp"/>
	<portlet:param name="backURL" value="<%=themeDisplay.getURLCurrent() %>"/>
</portlet:renderURL>

<aui:a href="<%=addItemsURL %>" >Add Item</aui:a>

<%
	List<ShoppingItem>shoppingItems = ShoppingItemLocalServiceUtil.getByUserId(themeDisplay.getUserId());
	for(ShoppingItem shoppingItem  : shoppingItems) {
		%>
		<%=shoppingItem.getName() %>
		<%		
	}
%>