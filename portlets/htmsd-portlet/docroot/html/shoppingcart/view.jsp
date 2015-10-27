<%@ include file="/html/shoppingcart/init.jsp"%>

<% 
	int itemscount = CommonUtil.getItemsCount(themeDisplay.getUserId(), themeDisplay, session);
	String tabNames="my-cart,my-orders";
	String tabs1 = ParamUtil.getString(request, "tab1", "my-cart");
%>

<portlet:renderURL var="tabsURL">
	<portlet:param name="tabs1" value="<%= tabs1 %>" />
</portlet:renderURL>

<div class="mycart">
	<h2><liferay-ui:message key="my-cart"/></h2> 
</div>

<liferay-ui:tabs names="<%= tabNames %>" refresh="true" param="tab1" url="<%= tabsURL.toString() %>">
	<liferay-ui:section>
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
	</liferay-ui:section>
	<liferay-ui:section>
		<%@include file="/html/shoppingcart/myorders.jsp" %>
	</liferay-ui:section>
</liferay-ui:tabs>





