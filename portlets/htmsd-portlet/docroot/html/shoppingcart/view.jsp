<%@ include file="/html/shoppingcart/init.jsp"%>

<% 
	PortletURL saveItemsURL = renderResponse.createActionURL();
	saveItemsURL.setParameter(ActionRequest.ACTION_NAME, "saveShoppingItems");
	
	ShoppingCart shoppingCart = CommonUtil.getShoppingCartByUserId(themeDisplay.getUserId());
	String[] userItemsArray = shoppingCart.getItemIds().split(StringPool.COMMA); 
	int itemscount = userItemsArray.length;
%>

<c:choose>
	<c:when test="<%= itemscount > 0 %>">
		<%@ include file="/html/shoppingcart/details.jsp" %> 
	</c:when>
	<c:otherwise>
		<liferay-ui:message key="no-items-in-cart"/>
	</c:otherwise>
</c:choose>

