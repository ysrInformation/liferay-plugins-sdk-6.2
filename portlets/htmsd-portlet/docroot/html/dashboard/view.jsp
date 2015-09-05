<%@page import="com.htmsd.util.HConstants"%>
<%@page import="com.liferay.portal.service.RoleLocalServiceUtil"%>
<%@page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@page import="com.liferay.portal.model.RoleConstants"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<liferay-theme:defineObjects/>
<%
	
%>
<c:choose>
	<c:when test="<%=permissionChecker.isOmniadmin() %>">
		<jsp:include page="/html/dashboard/admin.jsp" />
	</c:when>
	<c:when test="<%=RoleLocalServiceUtil.hasUserRole(user.getUserId(), themeDisplay.getCompanyId(), HConstants.STAFF_ROLE, false)%>">
		<jsp:include page="/html/dashboard/staff.jsp" />
	</c:when>
	
	<c:otherwise>
		<jsp:include page="/html/dashboard/user.jsp" />
	</c:otherwise>
</c:choose>