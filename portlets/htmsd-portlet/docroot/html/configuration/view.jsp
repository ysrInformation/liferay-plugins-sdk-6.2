<%@include file="/html/configuration/init.jsp"%>

<%
	String tabs1 = ParamUtil.getString(request, "tab1", "Category");
	PortletURL mainURL = renderResponse.createRenderURL();
	mainURL.setWindowState(WindowState.MAXIMIZED);
    String tabNames = "Category,Head Category,Item Types,Profit";
%>

<liferay-ui:tabs names="<%=tabNames%>"  param="tab1" url="<%=mainURL.toString() %>" />
<c:choose>
	<c:when test='<%=tabs1.equals("Category") %>'>
		<%@include file="/html/configuration/categories.jsp" %>
	</c:when>
	<c:when test='<%=tabs1.equals("Head Category") %>'>
		<%@include file="/html/configuration/categories.jsp" %>
	</c:when>
	<c:when test='<%=tabs1.equals("Item Types") %>'>
		<%@include file="/html/configuration/itemType.jsp" %>
	</c:when>
	<c:when test='<%=tabs1.equals("Profit") %>'>
		<%@include file="/html/configuration/commission.jsp" %>
	</c:when>
</c:choose>
