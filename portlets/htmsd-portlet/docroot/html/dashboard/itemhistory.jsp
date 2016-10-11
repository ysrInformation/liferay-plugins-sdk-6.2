<%@page import="com.htmsd.slayer.service.ItemHistoryLocalServiceUtil"%>
<%@page import="com.htmsd.slayer.model.ItemHistory"%>
<%@include file="/html/dashboard/init.jsp"%>

<%
	long itemId = ParamUtil.getLong(renderRequest, HConstants.ITEM_ID);
	List<ItemHistory> itemHistoryList = new ArrayList<ItemHistory>();
	String orderByCol = ParamUtil.getString(request, "orderByCol", "createDate");
	String orderByType = ParamUtil.getString(request, "orderByType","desc");
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("jspPage", "/html/dashboard/itemhistory.jsp");
%>
<liferay-ui:header title="item-history" backLabel="go-back" backURL='<%=ParamUtil.getString(renderRequest, "backURL") %>'/>

<liferay-ui:search-container delta="10"   orderByCol="<%=orderByCol %>" orderByType="<%=orderByType %>" emptyResultsMessage="No Item History to Display" iteratorURL="<%=iteratorURL %>">

	<liferay-ui:search-container-results>
		 <%
		 
		 	itemHistoryList = 	ItemHistoryLocalServiceUtil.getItemHistoryByItemId(itemId, searchContainer.getStart(), searchContainer.getEnd());
			List<ItemHistory> copy_itemHistoryList = new ArrayList<ItemHistory>();
			copy_itemHistoryList.addAll(itemHistoryList);
		 	Comparator<ItemHistory> beanComparator = new BeanComparator(orderByCol);
			Collections.sort(copy_itemHistoryList, beanComparator);			
			if(orderByType.equals("desc")) {
				Collections.reverse(copy_itemHistoryList);
			}
			
		 	results = copy_itemHistoryList; 
		 	total = ItemHistoryLocalServiceUtil.getItemIdCount(itemId);
			pageContext.setAttribute("results", results);
			pageContext.setAttribute("total", total);
		%>
	</liferay-ui:search-container-results>			 
	<liferay-ui:search-container-row className="com.htmsd.slayer.model.ItemHistory" modelVar="itemHistory" indexVar="indexVar" keyProperty="historyId">

		<liferay-ui:search-container-column-text name="no.">
			<%=String.valueOf(searchContainer.getStart()+1+indexVar)%>
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text name="last-update-by" >
			<%=itemHistory.getUserEmail() + StringPool.OPEN_PARENTHESIS + itemHistory.getUserName() + StringPool.CLOSE_PARENTHESIS %>
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text name="date" orderable="true" orderableProperty="createDate" >
			<fmt:formatDate value="<%=itemHistory.getCreateDate() %>" pattern="dd/MM/yyyy" />
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text name="action" >
			<%=LanguageUtil.get(portletConfig,themeDisplay.getLocale(), itemHistory.getAction() == HConstants.ITEM_ADDED ? "item-added" : "item-updated" ) %>
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text name="Remarks" property="remark" />
		
	</liferay-ui:search-container-row>
	
	<liferay-ui:search-iterator  searchContainer="<%=searchContainer %>"/>
</liferay-ui:search-container>

