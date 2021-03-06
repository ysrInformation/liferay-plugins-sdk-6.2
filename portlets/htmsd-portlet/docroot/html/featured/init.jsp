<%@page import="javax.portlet.PortletSession"%>
<%@include file="/html/common/init.jsp" %>

<%@page import="com.liferay.portal.kernel.util.ListUtil"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.liferay.portal.kernel.dao.search.DisplayTerms"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@page import="com.liferay.portal.kernel.dao.search.RowChecker"%>
<%@page import="com.liferay.portlet.documentlibrary.model.DLFileEntry"%>
<%@page import="com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.util.StringUtil"%>
<%@page import="com.liferay.portal.kernel.events.Action"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>

<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.DecimalFormat"%>

<%@page import="javax.portlet.WindowState"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="javax.portlet.ActionRequest"%>

<%@page import="com.htmsd.util.HConstants"%>
<%@page import="com.htmsd.slayer.model.ShoppingItem"%>
<%@page import="com.htmsd.slayer.service.ShoppingItemLocalServiceUtil"%>
<%@page import="com.htmsd.shoppinglist.ShoppingListPortlet"%>
<%@page import="com.htmsd.slayer.model.Tag"%>
<%@page import="com.htmsd.slayer.service.TagLocalServiceUtil"%>
<%@page import="com.htmsd.slayer.service.CategoryLocalServiceUtil"%>
<%@page import="com.htmsd.slayer.model.Category"%>
<%@page import="com.htmsd.util.CommonUtil"%>

<%@page import="org.apache.commons.collections.ListUtils"%>
<%@page import="org.apache.commons.beanutils.BeanComparator"%>
<%
	String val = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
	long currencyId = (Validator.isNull(val)) ?  0 : Long.valueOf(val);
%>	
<aui:script>
	function formatPrice(number) {
		accounting.settings.currency.symbol = '<%=CommonUtil.getCurrencySymbol(currencyId)%> ';
		var text = '';
		text = accounting.formatMoney(Math.abs(number));
		return text;
	}
</aui:script>