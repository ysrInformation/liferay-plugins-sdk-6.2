<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@include file="/html/dashboard/init.jsp" %>



<%
	long userId = themeDisplay.getUserId();
	String redirectURL = themeDisplay.getURLCurrent();
	List<ShoppingItem> itemList = new ArrayList<ShoppingItem>();
	String orderByCol = ParamUtil.getString(request, "orderByCol", "createDate");
	String orderByType = ParamUtil.getString(request, "orderByType","desc");
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("jspPage", "/html/dashboard/user.jsp");
%>

<portlet:renderURL var="addItemsURL">
	<portlet:param name="jspPage" value="/html/dashboard/additem.jsp"/>
	<portlet:param name="backURL" value="<%=redirectURL %>"/>
</portlet:renderURL>


<aui:button  href="<%=addItemsURL.toString() %>" value="add-item"/>

<liferay-ui:search-container delta="10"   orderByCol="<%=orderByCol %>" orderByType="<%=orderByType %>" emptyResultsMessage="No Items to Display" iteratorURL="<%=iteratorURL %>">

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
			<%=LanguageUtil.get(portletConfig,themeDisplay.getLocale(),String.valueOf(item.getStatus())) %>
		</liferay-ui:search-container-column-text>
		
		<c:if test='<%=item.getStatus() == HConstants.APPROVE %>'>
			
			<liferay-ui:search-container-column-text name="stock">
				<%=item.getQuantity() == -1 ? LanguageUtil.get(portletConfig,themeDisplay.getLocale(),"unlimited") : item.getQuantity() %>
				
				<%
					PortletURL showStockFormURL = renderResponse.createRenderURL();
					showStockFormURL.setParameter("jspPage", "/html/dashboard/stockform.jsp");
					showStockFormURL.setParameter(HConstants.ITEM_ID, String.valueOf(item.getItemId()));	
					showStockFormURL.setWindowState(LiferayWindowState.POP_UP);
					showStockFormURL.setParameter("redirectURL", redirectURL);
				%>
				<a href="#" onclick="showStockForm('<%=showStockFormURL%>');"><aui:icon image="edit" /></a> 
			
			</liferay-ui:search-container-column-text>
		</c:if>
		
		<liferay-ui:search-container-column-text name="">
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


<script>
function showStockForm(url) {
	
	AUI().use('aui-modal', function(A) {
			Liferay.Util.openWindow({
				dialog: {
				    centered: true,
				    modal: true,
				    width : 300
				},
				dialogIframe: {
					id: 'showstockDialog',
					uri: url
				},
				title: Liferay.Language.get('stock'),
				uri: url
			});
			
		}); 
}
</script>
