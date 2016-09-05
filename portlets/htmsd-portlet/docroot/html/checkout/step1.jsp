<%@include file="/html/checkout/init.jsp" %>

<h1 class="page-heading">Step 1</h1>

<portlet:actionURL var="step1URL" name="step1"/>
<% 
	int itemscount = CommonUtil.getItemsCount(themeDisplay.getUserId(), themeDisplay, session);
%>

<c:choose>
	<c:when test='<%= itemscount > 0 %>'>
		<aui:form id="checkout-step1" name="step1" method="post" action="${step1URL}"> 
			<aui:button type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "step1") %>'/>
		</aui:form>
	</c:when>
	<c:otherwise>
		<p class="newcart-alert newcartalert-warning"><liferay-ui:message key="your-shopping-cart-is-empty"/></p>
	</c:otherwise>
</c:choose>