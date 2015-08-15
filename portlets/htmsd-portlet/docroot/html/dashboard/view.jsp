<%@include file="/html/dashboard/init.jsp" %>

<c:choose>
	<c:when test="<%=permissionChecker.isOmniadmin() %>">
		<%@include file="/html/dashboard/admin.jsp" %>
	</c:when>
	<c:otherwise>
		<%@include file="/html/dashboard/user.jsp" %>
	</c:otherwise>
</c:choose>