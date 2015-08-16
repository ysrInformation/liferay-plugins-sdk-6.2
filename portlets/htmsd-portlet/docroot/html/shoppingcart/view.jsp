<%@ include file="/html/shoppingcart/init.jsp"%>

<% 
	int itemscount = ShoppingItem_CartLocalServiceUtil.getItemsCountByCartId(themeDisplay.getUserId());
%>

<div class="mycart">
	<h2><liferay-ui:message key="my-cart"/></h2> 
</div>

<c:choose>
	<c:when test="<%= itemscount > 0 %>">
		<%@ include file="/html/shoppingcart/details.jsp" %> 
	</c:when>
	<c:otherwise>
		<div class="mycart">
			<img src="<%=request.getContextPath()%>/images/cart-tray.png" style="width: 100px; height: auto"/>
		</div>
		<p class="mycart"><liferay-ui:message key="no-items-in-cart"/></p> 
	</c:otherwise>
</c:choose>

