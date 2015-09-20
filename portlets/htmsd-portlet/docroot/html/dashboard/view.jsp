<%@include file="/html/dashboard/init.jsp" %>

<c:choose>
	<c:when test="<%=isAdmin || isStaff%>">
		<jsp:include page="/html/dashboard/admin.jsp" />
	</c:when>
	<c:otherwise>
		<jsp:include page="/html/dashboard/user.jsp" />
	</c:otherwise>
</c:choose>