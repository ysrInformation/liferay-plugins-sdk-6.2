<%@include file="/html/dashboard/init.jsp" %>

<%
	String tabs2 = ParamUtil.getString(request, "tab2", "Add Items");
    String tabNames = "Add Items,Orders";
%>

<portlet:renderURL var="tabsURL">
	<portlet:param name="tabs2" value="<%= tabs2 %>" />
</portlet:renderURL>
<c:choose>
	<c:when test="<%= !isSeller && !isApprover && !isAdmin %>">
		<%@include file="/html/dashboard/businessinfo.jsp" %>
	</c:when>
	<c:otherwise>
		<c:if test='<%= isSeller %>'>
			<portlet:renderURL var='updateCompnayDetailURL'> 
				<portlet:param name="jspPage" value="/html/dashboard/mycompany.jsp"/> 
			</portlet:renderURL>
			<aui:button-row cssClass="text-right"> 
				<aui:button cssClass="btn btn-primary" href="<%= updateCompnayDetailURL %>" value="My Company"/>
			</aui:button-row>
		</c:if> 
		<c:choose>
			<c:when test="<%=isAdmin || isStaff || isApprover%>">
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
	</c:otherwise>
</c:choose>