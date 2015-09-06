<%@ include file="/html/orderpanel/init.jsp"%>

<%
	int slno = 1;
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("jspPage", "/html/orderpanel/view.jsp");
	
	PortletURL detailsURL = renderResponse.createRenderURL();
	detailsURL.setParameter("jspPage", "/html/orderpanel/detail.jsp"); 
	
	String tabName = ParamUtil.getString(request, "tab1", "Pending");  
	
	if (Validator.isNotNull(tabName)) {
		iteratorURL.setParameter("tab1", tabName); 
		detailsURL.setParameter("tab1", tabName);
	}
	
	List<ShoppingOrder> shoppingOrders = CommonUtil.getShoppingOrderByTabNames(themeDisplay.getScopeGroupId(), tabName);
%>

<div class="orderlist">
	<liferay-ui:search-container delta="10" iteratorURL="<%= iteratorURL %>" 
		emptyResultsMessage="No items to display">
    	 
    	 <liferay-ui:search-container-results
            results="<%= ListUtil.subList(shoppingOrders, searchContainer.getStart(), searchContainer.getEnd()) %>"
            total="<%= shoppingOrders.size() %>"
         />
        
        <liferay-ui:search-container-row className="com.htmsd.slayer.model.ShoppingOrder" 
        	modelVar="shoppingOrder">
           
            <% 
           	detailsURL.setParameter("orderId", String.valueOf(shoppingOrder.getOrderId()));
           	User _user = UserLocalServiceUtil.fetchUser(shoppingOrder.getUserId());
           	String userName = (shoppingOrder.getUserId()>0l)?_user.getFullName():"N/A";
           	String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
           	String orderId = HConstants.HTMSD + currentYear.substring(2, 4) + shoppingOrder.getOrderId();
           	String status = tabName.equalsIgnoreCase("Pending") ? HConstants.PENDING_STATUS:HConstants.DELIVERED_STATUS;
           	String orderedDate = HConstants.DATE_FORMAT.format(shoppingOrder.getCreateDate()); 
            %>
            
            <liferay-ui:search-container-column-text name="no" value="<%= String.valueOf(slno++) %>"/>

            <liferay-ui:search-container-column-text name="order-id" value="<%= orderId %>"/>
            
            <liferay-ui:search-container-column-text name="user-name" value="<%= userName %>" /> 
            
            <liferay-ui:search-container-column-text name="seller-name" value="<%= userName %>" /> 
            
            <liferay-ui:search-container-column-text name="order-date" value="<%= orderedDate %>"/>
            
            <liferay-ui:search-container-column-text name="status" value="<%= status %>"/>
            
            <liferay-ui:search-container-column-text name="action" >
            	<aui:a href="<%= detailsURL.toString() %>"><liferay-ui:message key="view"/></aui:a>
            </liferay-ui:search-container-column-text>   

        </liferay-ui:search-container-row>
     <liferay-ui:search-iterator searchContainer="<%= searchContainer %>"/>
	</liferay-ui:search-container>
</div>