<%@include file="/html/checkout/init.jsp" %>

<%
	String myOrders = themeDisplay.getPortalURL()+"/web/guest/myorders";
%>

<div class="new-checkout">
	<div class="checkout-thanks-message checkout-details-container">
		<h2><liferay-ui:message key="thank-you-for-ordering-an-item-from-a2zali" /></h2>
		<aui:button-row>
			<aui:button type="button" cssClass="btn-primary" value='<%= LanguageUtil.get(portletConfig, locale, "view-order-details") %>' href="<%= myOrders %>"/> 
		</aui:button-row>
	</div>
</div>
