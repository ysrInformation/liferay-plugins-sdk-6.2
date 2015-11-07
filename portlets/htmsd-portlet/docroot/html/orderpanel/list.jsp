<%@ include file="/html/orderpanel/init.jsp"%>

<%
	int slno = 1;
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("jspPage", "/html/orderpanel/view.jsp");
	
	String tabName = ParamUtil.getString(request, "tab1", "Pending"); 
	String orderByCol = ParamUtil.getString(request, "orderByCol","createDate");
 	String orderByType = ParamUtil.getString(request, "orderByType","desc");
    String keyword = ParamUtil.getString(renderRequest, "keywords");
	
	if (Validator.isNotNull(tabName)) {
		iteratorURL.setParameter("tab1", tabName); 
	}
	
	int orderStatus = ShoppingOrderLocalServiceUtil.getOrderStatusByTabName(tabName);
	List<ShoppingOrderItem> shoppingOrders = new ArrayList<ShoppingOrderItem>();
	
	PortletURL searchURL = renderResponse.createActionURL();
	searchURL.setParameter(ActionRequest.ACTION_NAME, "processAction");
%>

<div class="orderlist">
	<aui:form method="POST" name="fm1" action="<%= searchURL.toString() %>"> 
		<aui:input name="tabName" type="hidden" value="<%= tabName %>"/> 

		<aui:nav-bar>
			<aui:nav-bar-search cssClass="form-search pull-right">
				<liferay-ui:input-search />
			</aui:nav-bar-search>
		</aui:nav-bar>
		
		<liferay-ui:search-container delta="10" orderByCol="<%= orderByCol %>" orderByType="<%= orderByType %>"
			 iteratorURL="<%= iteratorURL %>"  emptyResultsMessage="No items to display">
			
			<liferay-ui:search-container-results>
				<%
			 	shoppingOrders = keyword.isEmpty() ? ShoppingOrderLocalServiceUtil.getShoppingOrderByTabNames(searchContainer.getStart(), 
			 			searchContainer.getEnd(), tabName) : ShoppingOrderLocalServiceUtil.getShoppingOrderByTabNames(searchContainer.getStart(),
			 					searchContainer.getEnd(), tabName);
			 				
				List<ShoppingOrderItem> copy_itemList = new ArrayList<ShoppingOrderItem>();
				copy_itemList.addAll(shoppingOrders);
			 	Comparator<ShoppingOrderItem> beanComparator = new BeanComparator(orderByCol);
				Collections.sort(copy_itemList, beanComparator);			
				if (orderByType.equals("desc")) {
					Collections.reverse(copy_itemList);
				}
				
				results = copy_itemList; 
				total = keyword.isEmpty() ? ShoppingOrderLocalServiceUtil.getOrderItemCount(orderStatus)
										    : ShoppingOrderLocalServiceUtil.getOrderItemCount(orderStatus);													
				
				pageContext.setAttribute("results", results);
				pageContext.setAttribute("total", total);
				%>
			</liferay-ui:search-container-results>		
	        
	        <liferay-ui:search-container-row className="com.htmsd.slayer.model.ShoppingOrderItem" modelVar="shoppingOrder" indexVar="indexVar" keyProperty="orderId">
	           
	            <% 
	            ShoppingOrder shoppingOrder2 = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(shoppingOrder.getOrderId());
	            
	           	String userName = Validator.isNotNull(shoppingOrder) ? shoppingOrder2.getShippingFirstName()
	           			.concat(StringPool.SPACE).concat(shoppingOrder2.getShippingLastName()):"N/A";
	           	String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	           	String orderId = HConstants.HTMSD + currentYear.substring(2, 4) + shoppingOrder.getItemId();
	           	String status = CommonUtil.getOrderStatus(shoppingOrder.getOrderStatus());
	           	String orderedDate = HConstants.DATE_FORMAT.format(shoppingOrder.getCreateDate()); 
	            String sellerName = StringPool.BLANK;
	            try {
	            	ShoppingItem shoppingItem = CommonUtil.getShoppingItem(shoppingOrder.getShoppingItemId());
	            	User seller =  UserLocalServiceUtil.fetchUser(shoppingItem.getUserId());
	            	sellerName = (Validator.isNotNull(seller)) ? seller.getFullName() : "N/A";
	            } catch(Exception e) {
	            	System.out.println(e);
	            }
	            %>
	            
	            <liferay-ui:search-container-column-text name="no" value="<%= String.valueOf(slno++) %>"/>
	
	            <liferay-ui:search-container-column-text name="order-id" value="<%= orderId %>"/>
	            
	            <liferay-ui:search-container-column-text name="user-name" value="<%= userName %>" /> 
	            
	            <liferay-ui:search-container-column-text name="seller-name" value="<%= sellerName %>"/>
	            
	            <liferay-ui:search-container-column-text name="item-title">
	            	<portlet:renderURL var="itemDetailsURL" windowState="<%= LiferayWindowState.POP_UP.toString()  %>"> 
	            		<portlet:param name="<%= HConstants.JSP_PAGE %>" value="/html/shoppinglist/details.jsp"/> 
	            		<portlet:param name="<%= HConstants.ITEM_ID %>" value="<%= String.valueOf(shoppingOrder.getShoppingItemId()) %>"/>
	            		<portlet:param name="<%= Constants.CMD %>" value="itemsDetails"/> 
	            	</portlet:renderURL>
	            	<% String itemsDetails = "javascript:showPopupDetails('"+itemDetailsURL+"','1200','Product Details');"; %>
	            	<a href="<%= itemsDetails %>" ><%= shoppingOrder.getName() %></a>
	            </liferay-ui:search-container-column-text> 
	    
	    		<liferay-ui:search-container-column-text name="order-date" value="<%= orderedDate %>" orderable="true" orderableProperty="createDate"/>
	            
	            <liferay-ui:search-container-column-text name="status" value="<%= status %>" cssClass="<%= status %>"/>
	                    
	            <liferay-ui:search-container-column-text name="quantity">
	            	<%= shoppingOrder.getQuantity() %>
	            </liferay-ui:search-container-column-text>
	            
	            <liferay-ui:search-container-column-text name="total" >
	            	<%= CommonUtil.getPriceInNumberFormat(shoppingOrder.getTotalPrice(), HConstants.RUPEE_SYMBOL) %> 
	            </liferay-ui:search-container-column-text>
	            
	            <liferay-ui:search-container-column-jsp name="action"  path="/html/orderpanel/action.jsp"/> 
	
	        </liferay-ui:search-container-row>
	     <liferay-ui:search-iterator searchContainer="<%= searchContainer %>"/>
		</liferay-ui:search-container>
	</aui:form>
</div>

<script>
function showPopupDetails(url, width, title){
	
	AUI().use('aui-base','liferay-util-window','aui-io-plugin-deprecated',function(A){
		  
    	var popup = Liferay.Util.Window.getWindow(
                {
                    dialog: {
                        centered: true,
                        constrain2view: true,
                        modal: true,
                        resizable: false,
                        width: width
                    }
                }).plug(A.Plugin.DialogIframe,
                     {
                     autoLoad: true,
                     iframeCssClass: 'dialog-iframe',
                     uri:url
                }).render();
    		popup.show();
    		popup.titleNode.html(title);
	});
}
</script>

