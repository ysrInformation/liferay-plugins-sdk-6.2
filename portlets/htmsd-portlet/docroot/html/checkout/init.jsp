<%@include file="/html/common/init.jsp" %>

<%@page import="com.liferay.portal.util.PortletKeys"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>

<%@page import="com.htmsd.util.ShoppingBean"%>
<%@page import="com.htmsd.util.CommonUtil"%>
<%@page import="com.htmsd.slayer.model.ShoppingItem"%>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="javax.portlet.WindowState"%>
<%@page import="javax.portlet.PortletSession"%>

<%
	String continueShoppingURL=themeDisplay.getPortalURL() +"/web/"+themeDisplay.getLayout().getFriendlyURL()+"/home";
%>