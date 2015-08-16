<%@ include file="/html/shoppingcart/init.jsp"%>

<% 
	int itemscount = ShoppingItem_CartLocalServiceUtil.getItemsCountByCartId(themeDisplay.getUserId());
%>

<c:choose>
	<c:when test="<%= itemscount > 0 %>">
		<%@ include file="/html/shoppingcart/details.jsp" %> 
	</c:when>
	<c:otherwise>
		<liferay-ui:message key="no-items-in-cart"/>
	</c:otherwise>
</c:choose>

