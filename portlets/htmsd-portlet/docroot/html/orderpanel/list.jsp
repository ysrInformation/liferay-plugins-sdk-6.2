<%@page import="com.liferay.portal.kernel.json.JSONObject"%>
<%@page import="com.htmsd.slayer.service.ShoppingItemLocalServiceUtil"%>
<%@ include file="/html/orderpanel/init.jsp"%>

<%
	int slno = 1;
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter(HConstants.JSP_PAGE, "/html/orderpanel/list.jsp");
	
	String tabName = ParamUtil.getString(request, "tab1", "Pending"); 
	String orderByCol = ParamUtil.getString(request, "orderByCol","createDate");
 	String orderByType = ParamUtil.getString(request, "orderByType","desc");
    String keyword = ParamUtil.getString(renderRequest, "keywords");
	
	if (Validator.isNotNull(tabName)) {
		iteratorURL.setParameter("tab1", tabName); 
	}
	
	int orderStatus = ShoppingOrderLocalServiceUtil.getOrderStatusByTabName(tabName);
	List<ShoppingOrder> shoppingOrders = new ArrayList<ShoppingOrder>();
	
	PortletURL searchURL = renderResponse.createActionURL();
	searchURL.setParameter(ActionRequest.ACTION_NAME, "processAction");

	String val2 = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
	long currencyId1 = (Validator.isNull(val2)) ?  0 : Long.valueOf(val2);
	long detailPlid = CommonUtil.getDetailPageLayoutId(themeDisplay.getScopeGroupId(), layout.getPlid());
	double currentRate = CommonUtil.getCurrentRate(Long.valueOf(currencyId1));
	String backURL = themeDisplay.getURLCurrent();
	JSONObject autoCompleteJSON = ShoppingOrderLocalServiceUtil.getOrderAutoCompleteList(tabName);
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
				keyword = keyword.trim();
			 	shoppingOrders = keyword.isEmpty() ? ShoppingOrderLocalServiceUtil.getShoppingOrderByTabNames(searchContainer.getStart(), 
			 			searchContainer.getEnd(), tabName) : ShoppingOrderLocalServiceUtil.searchItems(keyword, tabName, searchContainer.getStart(),
			 					searchContainer.getEnd());
			 				
				List<ShoppingOrder> copy_itemList = new ArrayList<ShoppingOrder>();
				copy_itemList.addAll(shoppingOrders);
			 	Comparator<ShoppingOrder> beanComparator = new BeanComparator(orderByCol);
				Collections.sort(copy_itemList, beanComparator);			
				if (orderByType.equals("desc")) {
					Collections.reverse(copy_itemList);
				}
				
				results = copy_itemList; 
				total = keyword.isEmpty() ? ShoppingOrderLocalServiceUtil.getOrderItemCount(orderStatus)
										    : ShoppingOrderLocalServiceUtil.searchCount(tabName, keyword);												
				
				pageContext.setAttribute("results", results);
				pageContext.setAttribute("total", total);
				%>
			</liferay-ui:search-container-results>		
	        
	        <liferay-ui:search-container-row className="com.htmsd.slayer.model.ShoppingOrder" modelVar="shoppingOrder" 
	        	indexVar="indexVar" keyProperty="orderId">
	           
	            <% 
	            double totalPrice = (currentRate == 0) ? shoppingOrder.getTotalPrice() : shoppingOrder.getTotalPrice() / currentRate;
	           	String userName = shoppingOrder.getUserName();
	           	String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	           	String orderId = HConstants.A2ZALI + currentYear.substring(2, 4) + shoppingOrder.getOrderId();
	           	String status = CommonUtil.getOrderStatus(shoppingOrder.getOrderStatus());
	           	String orderedDate = HConstants.DATE_FORMAT.format(shoppingOrder.getCreateDate()); 
	            String sellerName = shoppingOrder.getSellerName();
	            String itemName = StringPool.BLANK;
	            try {
	            	ShoppingItem shoppingItem = CommonUtil.getShoppingItem(shoppingOrder.getShoppingItemId());
	            	itemName = Validator.isNotNull(shoppingItem) ?shoppingItem.getName() : "N/A"; 
	            } catch(Exception e) {
	            	System.out.println(e);
	            }
	            %>
	            
	            <liferay-ui:search-container-column-text name="no" value="<%= String.valueOf(slno++) %>"/>
	
	            <liferay-ui:search-container-column-text name="order-id" value="<%= orderId %>"/>
	            
	            <liferay-ui:search-container-column-text name="user-name" value="<%= userName %>" /> 
	            
	            <liferay-ui:search-container-column-text name="seller-name" value="<%= sellerName %>"/>
	            
	            <liferay-ui:search-container-column-text name="item-title">
	            	<% 
	            	String showItemDetails = "javascript:showDetailsPage('"+backURL+"','"+shoppingOrder.getShoppingItemId()+"','"+detailPlid+"');";
	            	%>
	            	<a onclick="<%= showItemDetails %>" href="javascript:void(0);"><%= itemName %></a> 
	            </liferay-ui:search-container-column-text> 
	    
	    		<liferay-ui:search-container-column-text name="order-date" value="<%= orderedDate %>" orderable="true" orderableProperty="createDate"/>
	            
	            <liferay-ui:search-container-column-text name="status" value="<%= status %>" cssClass="<%= status %>"/>
	                    
	            <liferay-ui:search-container-column-text name="quantity">
	            	<%= shoppingOrder.getQuantity() %>
	            </liferay-ui:search-container-column-text>
	            
	            <liferay-ui:search-container-column-text name="total" >
	            	<%= CommonUtil.getPriceFormat(totalPrice, currencyId1)%> 
	            </liferay-ui:search-container-column-text>
	            <%
	            	if (tabName.equalsIgnoreCase("Order Cancelled")) {
	            		%>
				            <liferay-ui:search-container-column-text name="cancel-reason" >
				            	<%= shoppingOrder.getCancelReason() %>
				            </liferay-ui:search-container-column-text>
	            		<%
	            	} 
	            %>
	            <liferay-ui:search-container-column-jsp name="action"  path="/html/orderpanel/action.jsp"/> 
	
	        </liferay-ui:search-container-row>
	     <liferay-ui:search-iterator searchContainer="<%= searchContainer %>"/>
		</liferay-ui:search-container>
	</aui:form>
</div>

<script>
$(function(){
	var jsonData = <%= autoCompleteJSON %>;
	
	$("#<portlet:namespace/>keywords").autocomplete({
	    source: jsonData.autocompleteData
    });
});
</script>