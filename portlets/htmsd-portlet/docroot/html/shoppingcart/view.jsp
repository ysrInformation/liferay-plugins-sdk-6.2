<%@ include file="/html/shoppingcart/init.jsp"%>

<portlet:renderURL var="viewProductURL">
	<portlet:param name="jspPage" value="/html/shoppingcart/list.jsp"/>
</portlet:renderURL>

<portlet:renderURL var="viewDetailsURL">
	<portlet:param name="jspPage" value="/html/shoppingcart/details.jsp"/>
</portlet:renderURL>

<% 
	PortletURL saveItemsURL = renderResponse.createActionURL();
	saveItemsURL.setParameter(ActionRequest.ACTION_NAME, "saveShoppingItems");
	
	ShoppingCart shoppingCart = CommonUtil.getShoppingCartByUserId(themeDisplay.getUserId());
	String[] userItemsArray = shoppingCart.getItemIds().split(StringPool.COMMA); 
	int itemscount = userItemsArray.length;
%>

<c:choose>
	<c:when test="">
		<%@ include file="/html/shoppingcart/details.jsp" %> 
	</c:when>
	<c:otherwise>
		<liferay-ui:message key="no-itams-in-cart"/>
	</c:otherwise>
</c:choose>
<aui:a href="<%= viewDetailsURL %>">You have <%= itemscount %> items in your cart.</aui:a>

