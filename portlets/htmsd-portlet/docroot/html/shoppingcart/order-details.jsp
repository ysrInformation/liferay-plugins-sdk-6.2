<%@ include file="/html/shoppingcart/init.jsp"%>

<%
	int no = 1;
	String backURL = ParamUtil.getString(request, "backURL");
	long orderId = ParamUtil.getLong(request, "orderId");
	
	List<ShoppingOrderItem> shoppingOrderItems = Collections.emptyList();
	if (Validator.isNotNull(orderId)) {
		shoppingOrderItems = CommonUtil.getShoppingOrderItems(orderId);
	}
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter(HConstants.JSP_PAGE, "/html/shoppingcart/order-details.jsp");
	iteratorURL.setParameter("tab1", "my-orders");
%>

<liferay-ui:header title="order-details" backURL="<%= backURL %>"/>

<liferay-ui:search-container delta="10" iteratorURL="<%= iteratorURL %>" 
		emptyResultsMessage="No items to display">
    	 
    	 <liferay-ui:search-container-results
            results="<%= ListUtil.subList(shoppingOrderItems, searchContainer.getStart(), searchContainer.getEnd()) %>"
            total="<%= shoppingOrderItems.size() %>"
         />
        
        <liferay-ui:search-container-row className="com.htmsd.slayer.model.ShoppingOrderItem" 
        	modelVar="orderItem">
           
            <% 
            String productCode = (Validator.isNotNull(orderItem.getProductCode())) ? orderItem.getProductCode():"N/A";
            String description = orderItem.getDescription();
            int descLength = description.length();
            if (descLength > 200) {
            	descLength = 200;
            }
            String orderDescription = description.substring(0, descLength)+" .."; 
            %>
            
            <liferay-ui:search-container-column-text name="no" value="<%= String.valueOf(no++) %>"/>

            <liferay-ui:search-container-column-text name="product-code" value="<%= productCode %>"/>
            
            <liferay-ui:search-container-column-text name="name" value="<%= orderItem.getName() %>"/>
            
            <liferay-ui:search-container-column-text name="description" value="<%= orderDescription %>"/>
            
            <liferay-ui:search-container-column-text name="quantity" value="<%= String.valueOf(orderItem.getQuantity()) %>"/>
            
            <liferay-ui:search-container-column-text name="total">
            	<%= orderItem.getTotalPrice() %>
            </liferay-ui:search-container-column-text>
		
        </liferay-ui:search-container-row>
     <liferay-ui:search-iterator searchContainer="<%= searchContainer %>"/>
</liferay-ui:search-container>

