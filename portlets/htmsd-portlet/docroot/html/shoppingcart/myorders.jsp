<%@ include file="/html/shoppingcart/init.jsp"%>

<h3><liferay-ui:message key="my-orders"/></h3>

<%
	int slNo = 1;
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter(HConstants.JSP_PAGE, "/html/shoppingcart/myorders.jsp");
	iteratorURL.setParameter("tab1", "my-orders");
	
	PortletURL orderDetailsURL = renderResponse.createRenderURL();
	orderDetailsURL.setParameter(HConstants.JSP_PAGE, "/html/shoppingcart/order-details.jsp");
	orderDetailsURL.setParameter("tab1", "my-orders");
	orderDetailsURL.setParameter("backURL", themeDisplay.getURLCurrent());
	
	List<ShoppingOrder> myOrderList = CommonUtil.getMyOrders(themeDisplay.getUserId());
%>

<div>
	<liferay-ui:search-container delta="10" iteratorURL="<%= iteratorURL %>" 
		emptyResultsMessage="No items to display">
    	 
    	 <liferay-ui:search-container-results
            results="<%= ListUtil.subList(myOrderList, searchContainer.getStart(), searchContainer.getEnd()) %>"
            total="<%= myOrderList.size() %>"
         />
        
        <liferay-ui:search-container-row className="com.htmsd.slayer.model.ShoppingOrder" 
        	modelVar="shoppingOrder">
           
            <% 
            orderDetailsURL.setParameter("orderId", String.valueOf(shoppingOrder.getOrderId())); 
           	String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
           	String orderId = HConstants.HTMSD + currentYear.substring(2, 4) + shoppingOrder.getOrderId();
           	String orderedDate = HConstants.DATE_FORMAT.format(shoppingOrder.getCreateDate()); 
           	String status = CommonUtil.getOrderStatus(shoppingOrder.getOrderStatus());
            %>
            
            <liferay-ui:search-container-column-text name="no" value="<%= String.valueOf(slNo++) %>"/>

            <liferay-ui:search-container-column-text name="order-id" value="<%= orderId %>"/>
            
            <liferay-ui:search-container-column-text name="user-name" value="<%= themeDisplay.getUser().getFullName() %>" /> 
            
            <liferay-ui:search-container-column-text name="order-date" value="<%= orderedDate %>"/>
            
            <liferay-ui:search-container-column-text name="status" value="<%= status %>" cssClass="<%= status %>"/> 
            
            <liferay-ui:search-container-column-text name="action">
            	<div>
            		<aui:a href="<%= orderDetailsURL.toString() %>"><liferay-ui:message key="view"/></aui:a>
            	</div>
            	<c:if test="<%= shoppingOrder.getOrderStatus() == HConstants.PENDING %>">
            		<portlet:actionURL var="cancelOrderURL" name="CancelOrder">
            			<portlet:param name="orderId" value="<%= String.valueOf(shoppingOrder.getOrderId()) %>"/>
            		</portlet:actionURL>
            		<div>
            		    <aui:a href="javascript:void(0);" onClick="javascript:cancelOrder('<%= cancelOrderURL %>');">
            				<liferay-ui:message key="cancel-order"/>
            			</aui:a>
            		</div>
            	</c:if>
            </liferay-ui:search-container-column-text> 

        </liferay-ui:search-container-row>
     <liferay-ui:search-iterator searchContainer="<%= searchContainer %>"/>
	</liferay-ui:search-container>
</div>

<aui:script>
function cancelOrder(url) {
	
	var message = '<%= UnicodeLanguageUtil.get(pageContext, "are-you-sure-you-want-to-cancel-the-order?")%>';
	
	if (confirm(message)) {
		window.location.href = url;
	}
}
</aui:script>