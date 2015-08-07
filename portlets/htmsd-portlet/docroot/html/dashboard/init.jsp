<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.htmsd.util.HConstants"%>
<%@page import="java.util.List"%>
<%@page import="com.htmsd.slayer.model.ShoppingItem"%>
<%@page import="com.htmsd.slayer.service.ShoppingItemLocalServiceUtil"%>



<portlet:defineObjects />
<liferay-theme:defineObjects/>