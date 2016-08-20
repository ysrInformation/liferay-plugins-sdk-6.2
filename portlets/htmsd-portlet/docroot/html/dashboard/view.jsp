<%@include file="/html/dashboard/init.jsp" %>

<%
	String tabs2 = ParamUtil.getString(request, "tab2", "Add Items");
    String tabNames = "Add Items,Orders";
%>

<portlet:renderURL var="tabsURL">
	<portlet:param name="tabs2" value="<%= tabs2 %>" />
</portlet:renderURL>

<c:choose>
	<c:when test="<%=isAdmin || isStaff%>">
		<jsp:include page="/html/dashboard/admin.jsp" />
	</c:when>
	<c:otherwise>
		<liferay-ui:tabs names="<%= tabNames %>" refresh="true" param="tab2" url="<%= tabsURL.toString() %>">
			<c:if test='<%= tabs2.equalsIgnoreCase("Add Items") %>'>
				<jsp:include page="/html/dashboard/user.jsp" />
			</c:if>
			<c:if test='<%= tabs2.equalsIgnoreCase("Orders") %>'>
				<jsp:include page="/html/dashboard/orders.jsp" />
			</c:if>
		</liferay-ui:tabs>
	</c:otherwise>
</c:choose>