<%@ include file="/html/orderpanel/init.jsp"%>
<%@page import="com.liferay.portal.kernel.json.JSONObject"%>
<%@page import="com.htmsd.slayer.service.ShoppingItemLocalServiceUtil"%>

<%
	int slno = 1;
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter(HConstants.JSP_PAGE, "/html/dashboard/orders.jsp");
	
	long sellerId = themeDisplay.getUserId();
	String tabName = ParamUtil.getString(request, "tab2", "Orders"); 
	String orderByCol = ParamUtil.getString(request, "orderByCol","createDate");
 	String orderByType = ParamUtil.getString(request, "orderByType","desc");
    String keyword = ParamUtil.getString(renderRequest, "keywords");

	PortletURL searchURL = renderResponse.createActionURL();
	searchURL.setParameter(ActionRequest.ACTION_NAME, "processAction");

	if (Validator.isNotNull(tabName)) {
		iteratorURL.setParameter("tab2", tabName); 
		searchURL.setParameter("tab2", tabName); 
	}
	
	List<ShoppingOrder> shoppingOrders = new ArrayList<ShoppingOrder>();

	String val2 = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
	long currencyId1 = (Validator.isNull(val2)) ?  0 : Long.valueOf(val2);

	double currentRate = CommonUtil.getCurrentRate(Long.valueOf(currencyId1));
	JSONObject autoCompleteJSON = ShoppingOrderLocalServiceUtil.getOrderAutoCompleteList(HConstants.ORDER_REVIEW_STATUS);
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
			 	shoppingOrders = keyword.isEmpty() ? ShoppingOrderLocalServiceUtil.getShoppingOrderListBySellerId(searchContainer.getStart(), 
			 			searchContainer.getEnd(), sellerId) : 
			 				ShoppingOrderLocalServiceUtil.searchSellerItems(searchContainer.getStart(), searchContainer.getEnd(), sellerId, keyword);
			 				
				List<ShoppingOrder> copy_itemList = new ArrayList<ShoppingOrder>();
				copy_itemList.addAll(shoppingOrders);
			 	Comparator<ShoppingOrder> beanComparator = new BeanComparator(orderByCol);
				Collections.sort(copy_itemList, beanComparator);			
				if (orderByType.equals("desc")) {
					Collections.reverse(copy_itemList);
				}
				
				results = copy_itemList; 
				total = keyword.isEmpty() ? ShoppingOrderLocalServiceUtil.getCountBySellerId(sellerId)
										    : ShoppingOrderLocalServiceUtil.sellerSearchCount(sellerId, keyword);											
				
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
	           	String orderId = HConstants.HTMSD + currentYear.substring(2, 4) + shoppingOrder.getOrderId();
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
	            	<portlet:renderURL var="itemDetailsURL" windowState="<%= LiferayWindowState.POP_UP.toString()  %>"> 
	            		<portlet:param name="<%= HConstants.JSP_PAGE %>" value="/html/shoppinglist/details.jsp"/> 
	            		<portlet:param name="<%= HConstants.ITEM_ID %>" value="<%= String.valueOf(shoppingOrder.getShoppingItemId()) %>"/>
	            		<portlet:param name="<%= Constants.CMD %>" value="itemsDetails"/> 
	            	</portlet:renderURL>
	            	<% String itemsDetails = "javascript:showPopupDetails('"+itemDetailsURL+"','1200','Product Details');"; %>
	            	<a href="<%= itemsDetails %>" ><%= itemName %></a>
	            </liferay-ui:search-container-column-text> 
	    
	    		<liferay-ui:search-container-column-text name="order-date" value="<%= orderedDate %>" orderable="true" orderableProperty="createDate"/>
	            
	            <liferay-ui:search-container-column-text name="status" cssClass="<%= status %>">
	            	<c:choose>
	            		<c:when test='<%= status.equalsIgnoreCase(HConstants.ORDER_REVIEW_STATUS) %>'>
	            			<portlet:resourceURL var="changeOrderStatusURL" id="changeOrderStatus"/>  
	            			<% String updateOrderStatus = "javascript:updateOrderStatus('"+changeOrderStatusURL+"','"+String.valueOf(shoppingOrder.getOrderId())+"');"; %> 
	            			<a href="<%= updateOrderStatus %>" class="<%= status %>"><%= status %></a>  
	            		</c:when>
	            		<c:otherwise><%= status %></c:otherwise>
	            	</c:choose>
	            </liferay-ui:search-container-column-text>
	                    
	            <liferay-ui:search-container-column-text name="quantity">
	            	<%= shoppingOrder.getQuantity() %>
	            </liferay-ui:search-container-column-text>
	            
	            <liferay-ui:search-container-column-text name="total" >
	            	<%= CommonUtil.getPriceFormat(totalPrice, currencyId1) %> 
	            </liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text name="action">
					<portlet:renderURL var="viewReceiptURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="<%= HConstants.JSP_PAGE %>" value="/html/orderpanel/reciept.jsp"/>
						<portlet:param name="orderId" value="<%= String.valueOf(shoppingOrder.getOrderId()) %>"/> 
					</portlet:renderURL>
					<% String viewReciept = "javascript:showPopupDetails('"+viewReceiptURL.toString()+"','"+"1200','"+"View Receipt');"; %>  
	            	<a href="<%= viewReciept %>"><liferay-ui:message key="view-reciept"/></a> 
	            </liferay-ui:search-container-column-text>
	
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

function updateOrderStatus(url, orderid) {
	var msg = "<liferay-ui:message key='update-order-status-confirm-msg'/>";
	if(confirm(msg)) {
		$.ajax({
			url : url,
			type : "GET",
			data : {
				<portlet:namespace/>orderId:orderid
			},
			success : function(data) {
				location.reload();
			},
			error : function() {
				alert("Something went wrong !!Please try again..");
			}
		});
	}
}

$(function(){
	var jsonData = <%= autoCompleteJSON %>;
	$("#<portlet:namespace/>keywords").autocomplete({
	    source: jsonData.autocompleteData
    });
});
</script>