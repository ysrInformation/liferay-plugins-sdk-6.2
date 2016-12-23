<%@include file="/html/dashboard/init.jsp" %>

<%
	long userId = themeDisplay.getUserId();
	String redirectURL = themeDisplay.getURLCurrent();
	String tabs1 = ParamUtil.getString(request, "tab2", "Add Items");
	List<ShoppingItem> itemList = new ArrayList<ShoppingItem>();
	String orderByCol = ParamUtil.getString(request, "orderByCol", "createDate");
	String orderByType = ParamUtil.getString(request, "orderByType","desc");
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("jspPage", "/html/dashboard/user.jsp");
	iteratorURL.setParameter("tab2", tabs1);
	
	int count = 1, start = 0, end = ShoppingItemLocalServiceUtil.getByUserIdCount(userId);
	itemList = ShoppingItemLocalServiceUtil.getByUserId(userId, start, end);
%>

<portlet:renderURL var="addItemsURL">
	<portlet:param name="jspPage" value="/html/dashboard/additem.jsp"/>
	<portlet:param name="backURL" value="<%=redirectURL %>"/>
</portlet:renderURL>

<%-- <aui:button  href="<%=addItemsURL.toString() %>" value="add-item"/> --%>
<aui:nav-bar>
	<aui:nav>
		<aui:nav-item href="<%= addItemsURL.toString() %>" iconCssClass="icon-plus" label="add-item"/>
	</aui:nav>
	<%-- 
		<aui:nav-bar-search cssClass="form-search pull-right">
			<liferay-ui:input-search />
		</aui:nav-bar-search> 
	--%>
</aui:nav-bar>

<%-- 
<liferay-ui:search-container delta="10" orderByCol="<%=orderByCol %>" orderByType="<%=orderByType %>" emptyResultsMessage="No Items to Display" iteratorURL="<%=iteratorURL %>">

	<liferay-ui:search-container-results>
		 <%
		 
		 	itemList = 	ShoppingItemLocalServiceUtil.getByUserId(userId, searchContainer.getStart(), searchContainer.getEnd());
			List<ShoppingItem> copy_itemList = new ArrayList<ShoppingItem>();
			copy_itemList.addAll(itemList);
		 	Comparator<ShoppingItem> beanComparator = new BeanComparator(orderByCol);
			Collections.sort(copy_itemList, beanComparator);			
			if(orderByType.equals("desc")) {
				Collections.reverse(copy_itemList);
			}
			
		 	results =copy_itemList; 
			total = ShoppingItemLocalServiceUtil.getByUserIdCount(userId);
			
			pageContext.setAttribute("results", results);
			pageContext.setAttribute("total", total);
		%>
	</liferay-ui:search-container-results>			 
	<liferay-ui:search-container-row className="com.htmsd.slayer.model.ShoppingItem" modelVar="item" indexVar="indexVar" keyProperty="itemId">

		<liferay-ui:search-container-column-text name="no.">
			<%=String.valueOf(searchContainer.getStart()+1+indexVar)%>
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text name="item-title">
			<%=item.getName() %>
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text name="date" orderable="true" orderableProperty="createDate" >
			<fmt:formatDate value="<%=item.getCreateDate() %>" pattern="dd/MM/yyyy" />
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text name="status" >
			<%
				int status = item.getStatus();
				String disStatus = "New";
				if (status == HConstants.APPROVE) {
					disStatus = "Listed"; 
				} else if (status == HConstants.INCOMPLETE) {
					disStatus = "Incomplete";	
				} else if (status == HConstants.REJECT) {
					disStatus = "Rejected";
				}
			%>
			<%=disStatus%>
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text name="product-stock">
			<c:if test='<%=item.getStatus() == HConstants.APPROVE %>'>
			<%=item.getQuantity() == -1 ? LanguageUtil.get(portletConfig,themeDisplay.getLocale(),"unlimited") : item.getQuantity() %>
			
			<%
				PortletURL showStockFormURL = renderResponse.createRenderURL();
				showStockFormURL.setParameter("jspPage", "/html/dashboard/stockform.jsp");
				showStockFormURL.setParameter(HConstants.ITEM_ID, String.valueOf(item.getItemId()));	
				showStockFormURL.setWindowState(LiferayWindowState.POP_UP);
				showStockFormURL.setParameter("redirectURL", redirectURL);
			%>
			<a href="#" onclick="showStockForm('<%=showStockFormURL%>');"><aui:icon image="edit" /></a> 
			</c:if>
		</liferay-ui:search-container-column-text>
	
		
		<liferay-ui:search-container-column-text name="action">
			<%
				PortletURL itemDetailURL = renderResponse.createRenderURL();
				itemDetailURL.setParameter("jspPage", "/html/dashboard/itemdetails.jsp");
				itemDetailURL.setParameter("itemId", String.valueOf(item.getItemId()));
				itemDetailURL.setParameter("backURL", redirectURL);
			%>
			<aui:a href="<%=itemDetailURL.toString() %>">View</aui:a>
		</liferay-ui:search-container-column-text>
		
	</liferay-ui:search-container-row>
	
	<liferay-ui:search-iterator  searchContainer="<%=searchContainer %>"/>
</liferay-ui:search-container> 
--%>

<table id="sellerDisplayItems" class="table table-striped table-bordered dt-responsive nowrap" width="100%" cellspacing="0"> 
	<thead>
  		<tr>
    		<th><liferay-ui:message key="no."/></th>
    		<th><liferay-ui:message key="item-title"/></th>
    		<th><liferay-ui:message key="date"/></th>
    		<th><liferay-ui:message key="status"/></th>
    		<th><liferay-ui:message key="product-stock"/></th>
    		<th><liferay-ui:message key="action"/></th>
  		</tr>
  </thead>
  <tbody>
  		<c:if test='<%= Validator.isNotNull(itemList) && itemList.size() > 0 %>'>
  			<% for (ShoppingItem item : itemList) { %>
  				<tr>
  					<td><%= count %></td>
  					<td>
  						<%= item.getName() %>
  					</td>
  					<td>
  						<fmt:formatDate value="<%= item.getCreateDate() %>" pattern="dd/MM/yyyy" />
  					</td>
  					<td>
  						<%
							int status = item.getStatus();
							String disStatus = "New";
							if (status == HConstants.APPROVE) {
								disStatus = "Listed"; 
							} else if (status == HConstants.INCOMPLETE) {
								disStatus = "Incomplete";	
							} else if (status == HConstants.REJECT) {
								disStatus = "Rejected";
							}
						%>
						<%=disStatus%>
  					</td>
  					<td>
  						<c:if test='<%=item.getStatus() == HConstants.APPROVE %>'>
							<%=item.getQuantity() == -1 ? LanguageUtil.get(portletConfig,themeDisplay.getLocale(),"unlimited") : item.getQuantity() %>
							
							<%
								PortletURL showStockFormURL = renderResponse.createRenderURL();
								showStockFormURL.setParameter("jspPage", "/html/dashboard/stockform.jsp");
								showStockFormURL.setParameter(HConstants.ITEM_ID, String.valueOf(item.getItemId()));	
								showStockFormURL.setWindowState(LiferayWindowState.POP_UP);
								showStockFormURL.setParameter("redirectURL", redirectURL);
							%>
							<a href="#" onclick="showStockForm('<%=showStockFormURL%>');"><aui:icon image="edit" /></a> 
						</c:if>
  					</td>
  					<td>
						<%
							PortletURL itemDetailURL = renderResponse.createRenderURL();
							itemDetailURL.setParameter("jspPage", "/html/dashboard/itemdetails.jsp");
							itemDetailURL.setParameter("itemId", String.valueOf(item.getItemId()));
							itemDetailURL.setParameter("backURL", redirectURL);
						%>
						<aui:a href="<%=itemDetailURL.toString() %>">View</aui:a>
  					</td>
  				</tr>
  			<% count++;
  			} %>
  		</c:if>
  </tbody>
</table>


<script>
$(function(){
	$("#sellerDisplayItems").DataTable();
});

function showStockForm(url) {
	
	AUI().use('aui-modal', function(A) {
			Liferay.Util.openWindow({
				dialog: {
				    centered: true,
				    modal: true,
				    cssClass: 'dashboard-popup a2zali-popup',
				    width : 300,
				    height : 300
				},
				dialogIframe: {
					id: 'showstockDialog',
					uri: url
				},
				title: Liferay.Language.get('product-stock'),
				uri: url
			});
			
		}); 
}
</script>
