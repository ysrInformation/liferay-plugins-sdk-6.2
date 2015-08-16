<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@include file="/html/dashboard/init.jsp" %>

<%
	String orderByCol = ParamUtil.getString(request, "orderByCol","createDate");
 	String orderByType = ParamUtil.getString(request, "orderByType","desc");
    String tabs1 = ParamUtil.getString(request, "tab1", "New Items");
	PortletURL mainURL = renderResponse.createRenderURL();
	mainURL.setWindowState(WindowState.MAXIMIZED);
    String tabNames = "New Items,Approved Items,Rejected Items";
    PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("jspPage", "/html/dashboard/admin.jsp");
	iteratorURL.setParameter("tab1", tabs1);
%>

<portlet:renderURL  var="addItemURL">
	<portlet:param name="jspPage" value="/html/dashboard/additem.jsp"/>
	<portlet:param name="backURL" value="<%=themeDisplay.getURLCurrent() %>"/>
	<portlet:param name="tab1" value="<%=tabs1 %>"/>
</portlet:renderURL>

<portlet:actionURL  var="deletSetURL" name="deleteItemSet">
	<portlet:param name="tab1" value="<%=tabs1 %>"/>
</portlet:actionURL>
<portlet:actionURL  var="approveSetURL" name="updateItemSet">
	<portlet:param name="<%=HConstants.status %>" value="<%=String.valueOf(HConstants.APPROVE)%>"/>
	<portlet:param name="tab1" value="<%=tabs1 %>"/>
</portlet:actionURL>
<portlet:actionURL  var="rejectSetURL" name="updateItemSet">
	<portlet:param name="<%=HConstants.status %>" value="<%=String.valueOf(HConstants.REJECT)%>"/>
	<portlet:param name="tab1" value="<%=tabs1 %>"/>
</portlet:actionURL>

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
<aui:button name="deleteBtn" type="submit" value="delete" style="display:none"/>
<aui:button name="approveBtn" type="button" value="approve" onClick="changeAction('approve');" style="display:none"/>
<aui:button name="rejectBtn" type="button" value="reject" onClick="changeAction('reject');" style="display:none"/>
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
				itemDetailURL.setParameter("tab1", tabs1);
			%>
			<aui:a href="<%=itemDetailURL.toString() %>">View</aui:a>
		</liferay-ui:search-container-column-text>
		
	</liferay-ui:search-container-row>
	
	<liferay-ui:search-iterator  searchContainer="<%=searchContainer %>"/>
</liferay-ui:search-container>
</aui:form>


<aui:script>
	$(document).ready(function(){
		 var btns = $("#<portlet:namespace/>deleteBtn,#<portlet:namespace/>approveBtn,#<portlet:namespace/>rejectBtn").hide();
	 	 var checkBoxs  = $(":checkbox").on("click",function(){
			btns.toggle(checkBoxs.is(":checked"));
		});
 	});
	function changeAction(action) {
		if(action == "approve") {
			document.<portlet:namespace/>bookListForm.action = '<%= approveSetURL.toString()%>';	
		} else if (action == "reject") {
			document.<portlet:namespace/>bookListForm.action = '<%= rejectSetURL.toString()%>';
		}
		 document.<portlet:namespace/>bookListForm.submit();
	}
</aui:script>