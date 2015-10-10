<%@ include file="/html/orderpanel/init.jsp"%>

<%
	int slno = 1;
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("jspPage", "/html/orderpanel/view.jsp");
	
	PortletURL detailsURL = renderResponse.createRenderURL();
	detailsURL.setParameter("jspPage", "/html/orderpanel/detail.jsp"); 
	
	String tabName = ParamUtil.getString(request, "tab1", "Pending"); 
	String orderByCol = ParamUtil.getString(request, "orderByCol","createDate");
 	String orderByType = ParamUtil.getString(request, "orderByType","desc");
    String keyword = ParamUtil.getString(renderRequest, "keywords");
	
	if (Validator.isNotNull(tabName)) {
		iteratorURL.setParameter("tab1", tabName); 
		detailsURL.setParameter("tab1", tabName);
	}
	
	int orderStatus = ShoppingOrderLocalServiceUtil.getOrderStatusByTabName(tabName);
	List<ShoppingOrder> shoppingOrders = new ArrayList<ShoppingOrder>();
	
	PortletURL searchURL = renderResponse.createActionURL();
	searchURL.setParameter(ActionRequest.ACTION_NAME, "processAction");
%>

<div class="orderlist">
	<aui:form method="POST" name="fm1" action="<%= searchURL.toString() %>"> 
		<aui:input name="tabName" type="hidden" value="<%= tabName %>"/> 
		<%--<div class="text-right">
			<liferay-ui:input-search />
		</div> --%>
		<liferay-ui:search-container delta="10" orderByCol="<%= orderByCol %>" orderByType="<%= orderByType %>"
			 iteratorURL="<%= iteratorURL %>"  emptyResultsMessage="No items to display">
			
			<liferay-ui:search-container-results>
				<%
			 	shoppingOrders = keyword.isEmpty() ? ShoppingOrderLocalServiceUtil.getShoppingOrderByTabNames(searchContainer.getStart(), searchContainer.getEnd(), tabName)
			 									: ShoppingOrderLocalServiceUtil.searchItems(orderStatus, keyword, searchContainer.getStart(), searchContainer.getEnd()) ;
			 				
				List<ShoppingOrder> copy_itemList = new ArrayList<ShoppingOrder>();
				copy_itemList.addAll(shoppingOrders);
			 	Comparator<ShoppingOrder> beanComparator = new BeanComparator(orderByCol);
				Collections.sort(copy_itemList, beanComparator);			
				if (orderByType.equals("desc")) {
					Collections.reverse(copy_itemList);
				}
				
				results = copy_itemList; 
				total = keyword.isEmpty() ? ShoppingOrderLocalServiceUtil.getCountByOrderStatus(orderStatus)
										    : ShoppingOrderLocalServiceUtil.searchCount(orderStatus, keyword);													
				
				pageContext.setAttribute("results", results);
				pageContext.setAttribute("total", total);
				%>
			</liferay-ui:search-container-results>		
	        
	        <liferay-ui:search-container-row className="com.htmsd.slayer.model.ShoppingOrder" modelVar="shoppingOrder" indexVar="indexVar" keyProperty="orderId">
	           
	            <% 
	           	detailsURL.setParameter("orderId", String.valueOf(shoppingOrder.getOrderId()));
	           	String userName = Validator.isNotNull(shoppingOrder) ? shoppingOrder.getShippingFirstName()
	           			.concat(StringPool.SPACE).concat(shoppingOrder.getShippingLastName()):"N/A";
	           	String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	           	String orderId = HConstants.HTMSD + currentYear.substring(2, 4) + shoppingOrder.getOrderId();
	           	String status = CommonUtil.getOrderStatus(shoppingOrder.getOrderStatus());
	           	String orderedDate = HConstants.DATE_FORMAT.format(shoppingOrder.getCreateDate()); 
	            %>
	            
	            <liferay-ui:search-container-column-text name="no" value="<%= String.valueOf(slno++) %>"/>
	
	            <liferay-ui:search-container-column-text name="order-id" value="<%= orderId %>"/>
	            
	            <liferay-ui:search-container-column-text name="user-name" value="<%= userName %>" /> 
	            
	            <liferay-ui:search-container-column-text name="order-date" value="<%= orderedDate %>" orderable="true" orderableProperty="createDate"/>
	            
	            <liferay-ui:search-container-column-text name="status" value="<%= status %>"/>
	            
	            <liferay-ui:search-container-column-text name="action" >
	            	<aui:a href="<%= detailsURL.toString() %>"><liferay-ui:message key="view"/></aui:a>
	            </liferay-ui:search-container-column-text>   
	
	        </liferay-ui:search-container-row>
	     <liferay-ui:search-iterator searchContainer="<%= searchContainer %>"/>
		</liferay-ui:search-container>
	</aui:form>
</div>