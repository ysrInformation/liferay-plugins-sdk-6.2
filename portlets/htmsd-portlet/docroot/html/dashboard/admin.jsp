<%@include file="/html/dashboard/init.jsp" %>

<portlet:renderURL  var="addItemURL">
	<portlet:param name="jspPage" value="/html/dashboard/additem.jsp"/>
	<portlet:param name="backURL" value="<%=themeDisplay.getURLCurrent() %>"/>
</portlet:renderURL>

<portlet:actionURL  var="deletSetURL" name="deleteItemSet"/>

<%
	String orderByCol = ParamUtil.getString(request, "orderByCol","createDate");
 	String orderByType = ParamUtil.getString(request, "orderByType","asc");
    String tabs1 = ParamUtil.getString(request, "tab1", "New Items");
	PortletURL mainURL = renderResponse.createRenderURL();
	mainURL.setWindowState(WindowState.MAXIMIZED);
    String tabNames = "New Items,Approved Items,Rejected Items";
    PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("jspPage", "/html/dashboard/admin.jsp");
	iteratorURL.setParameter("tab1", tabs1);
%>

<liferay-ui:tabs names="<%=tabNames%>"  param="tab1" url="<%=mainURL.toString() %>" />
    
  
    
<%
	List<ShoppingItem> itemList = new ArrayList<ShoppingItem>();
	int status = HConstants.NEW;
	if(tabs1.equals("New Items")) {
		status = HConstants.NEW;
	}else if(tabs1.equals("Approved Items")) {
		status = HConstants.APPROVE;	
	}else{
		status = HConstants.REJECT;
	}
%>    	
<aui:form action="<%=deletSetURL.toString() %>" name="bookListForm" method="POST">
<aui:button type="submit" value="delete"  />
<aui:button type="button" value="add-item" href="<%=addItemURL.toString() %>"/>
<liferay-ui:search-container delta="10"  orderByCol="<%=orderByCol %>" orderByType="<%=orderByType %>" emptyResultsMessage="No Items to display" iteratorURL="<%=iteratorURL %>"  rowChecker="<%= new RowChecker(renderResponse)%>">

	<liferay-ui:search-container-results>
		 <%
		 
		 	itemList = 	ShoppingItemLocalServiceUtil.getByStatus(status, searchContainer.getStart(), searchContainer.getEnd());
			List<ShoppingItem> copy_itemList = new ArrayList<ShoppingItem>();
			copy_itemList.addAll(itemList);
		 	Comparator<ShoppingItem> beanComparator = new BeanComparator(orderByCol);
			Collections.sort(copy_itemList, beanComparator);			
			if(orderByType.equals("desc")) {
				Collections.reverse(copy_itemList);
			}
			
			results = copy_itemList; 
			total = ShoppingItemLocalServiceUtil.getByStatusCount(status);
			
			pageContext.setAttribute("results", results);
			pageContext.setAttribute("total", total);
		%>
	</liferay-ui:search-container-results>			 
	<liferay-ui:search-container-row className="com.htmsd.slayer.model.ShoppingItem" modelVar="item" indexVar="indexVar" keyProperty="itemId">

		<liferay-ui:search-container-column-text name="no.">
			<%=String.valueOf(searchContainer.getStart()+1+indexVar)%>
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text name="item-title" >
			<%=item.getName() %>
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text name="date" orderable="true" orderableProperty="createDate" >
			<fmt:formatDate value="<%=item.getCreateDate() %>" pattern="dd/MM/yyyy" />
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text name="">
			<%
				PortletURL itemDetailURL = renderResponse.createRenderURL();
				itemDetailURL.setParameter("jspPage", "/html/dashboard/itemdetails.jsp");
				itemDetailURL.setParameter("itemId", String.valueOf(item.getItemId()));
				itemDetailURL.setParameter("backURL", themeDisplay.getURLCurrent());
			%>
			<aui:a href="<%=itemDetailURL.toString() %>">View</aui:a>
		</liferay-ui:search-container-column-text>
		
	</liferay-ui:search-container-row>
	
	<liferay-ui:search-iterator  searchContainer="<%=searchContainer %>"/>
</liferay-ui:search-container>
</aui:form>
