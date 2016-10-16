<%@include file="/html/common/init.jsp" %>

<%@page import="com.liferay.portal.model.Address"%>
<%@page import="com.liferay.portal.model.Contact"%>
<%@page import="com.liferay.portal.model.ListType"%>
<%@page import="com.liferay.portal.model.ListTypeConstants"%>
<%@page import="com.liferay.portal.util.PortletKeys"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="com.liferay.portal.kernel.util.TextFormatter"%>
<%@page import="com.liferay.portal.service.ListTypeServiceUtil"%>

<%@page import="com.htmsd.util.CommonUtil"%>
<%@page import="com.htmsd.util.ShoppingBean"%>
<%@page import="com.htmsd.slayer.model.UserInfo"%>
<%@page import="com.htmsd.slayer.model.ShoppingItem"%>
<%@page import="com.htmsd.slayer.model.impl.UserInfoImpl"%>
<%@page import="com.htmsd.slayer.service.UserInfoLocalServiceUtil"%>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="javax.portlet.WindowState"%>
<%@page import="javax.portlet.PortletSession"%>

<%
	String continueShoppingURL=themeDisplay.getPortalURL() +"/web/guest/home";
%>