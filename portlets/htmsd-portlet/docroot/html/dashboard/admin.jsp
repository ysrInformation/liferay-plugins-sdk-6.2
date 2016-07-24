<%@page import="com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil"%>
<%@page import="com.liferay.portal.model.User"%>
<%@page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@page import="com.liferay.portal.service.UserLocalService"%>
<%@page import="com.htmsd.dashboard.DashboardPortlet"%>
<%@include file="/html/dashboard/init.jsp" %>

<%
	String redirectURL = themeDisplay.getURLCurrent();
	String orderByCol = ParamUtil.getString(request, "orderByCol","createDate");
 	String orderByType = ParamUtil.getString(request, "orderByType","desc");
    String tabs1 = ParamUtil.getString(request, "tab1", "New Items");
    String keyword = ParamUtil.getString(renderRequest, "keywords");
	PortletURL mainURL = renderResponse.createRenderURL();
	mainURL.setWindowState(WindowState.MAXIMIZED);
    String tabNames = "New Items,Approved Items,Rejected Items";
    PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("jspPage", "/html/dashboard/admin.jsp");
	iteratorURL.setParameter("tab1", tabs1);
%>

<portlet:renderURL  var="addItemURL">
	<portlet:param name="jspPage" value="/html/dashboard/additem.jsp"/>
	<portlet:param name="backURL" value="<%=redirectURL %>"/>
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
<aui:nav-bar>
	<aui:nav>
		<aui:nav-item href="<%=addItemURL.toString() %>" iconCssClass="icon-plus" label="add-item"/>
	</aui:nav>
	<aui:nav-bar-search cssClass="form-search pull-right">
		<liferay-ui:input-search />
	</aui:nav-bar-search>
</aui:nav-bar>

<liferay-ui:search-container delta="10"  orderByCol="<%=orderByCol %>" orderByType="<%=orderByType %>" emptyResultsMessage="No Items to display" iteratorURL="<%=iteratorURL %>"  rowChecker="<%= new RowChecker(renderResponse)%>">

	<liferay-ui:search-container-results>
		 <%

		 	itemList = 	keyword.isEmpty() ? ShoppingItemLocalServiceUtil.getByStatus(status, searchContainer.getStart(), searchContainer.getEnd())
		 									:ShoppingItemLocalServiceUtil.searchItems(status, keyword, searchContainer.getStart(), searchContainer.getEnd()) ;
		 				
			List<ShoppingItem> copy_itemList = new ArrayList<ShoppingItem>();
			copy_itemList.addAll(itemList);
		 	Comparator<ShoppingItem> beanComparator = new BeanComparator(orderByCol);
			Collections.sort(copy_itemList, beanComparator);			
			if(orderByType.equals("desc")) {
				Collections.reverse(copy_itemList);
			}
			
			results = copy_itemList; 
			total = keyword.isEmpty() ? ShoppingItemLocalServiceUtil.getByStatusCount(status)
									    :ShoppingItemLocalServiceUtil.searchCount(status, keyword);													;
			
			pageContext.setAttribute("results", results);
			pageContext.setAttribute("total", total);
		%>
	</liferay-ui:search-container-results>			 
	<liferay-ui:search-container-row className="com.htmsd.slayer.model.ShoppingItem" modelVar="item" indexVar="indexVar" keyProperty="itemId">

		<liferay-ui:search-container-column-text name="no.">
			<%=String.valueOf(searchContainer.getStart()+1+indexVar)%>
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text  name="product-code"  property="productCode"/>
		 
		<liferay-ui:search-container-column-text name="item-title" >
			<portlet:renderURL var="historyURL">
				<portlet:param name="jspPage" value="/html/dashboard/itemhistory.jsp"/>
				<portlet:param name="backURL" value="<%=redirectURL %>"/>
				<portlet:param name="<%=HConstants.ITEM_ID %>" value="<%=String.valueOf(item.getItemId())%>"/>
			</portlet:renderURL>
			<aui:a href="<%=historyURL %>"><%=item.getName() %></aui:a> 
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text name="date" orderable="true" orderableProperty="createDate" >
			<fmt:formatDate value="<%=item.getCreateDate() %>" pattern="dd/MM/yyyy" />
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text name="seller" >
			<%=item.getUserEmail() + StringPool.OPEN_PARENTHESIS + item.getUserName() + StringPool.CLOSE_PARENTHESIS %>
		</liferay-ui:search-container-column-text>
		
		
		<liferay-ui:search-container-column-text name="seller-city" >
			<%
				User seller = UserLocalServiceUtil.fetchUser(item.getUserId());
			%>
			<%=seller.getAddresses().isEmpty() ? StringPool.BLANK : seller.getAddresses().get(0).getCity() %>		
		</liferay-ui:search-container-column-text>
		
		<c:if test='<%=tabs1.equals("Approved Items") %>'>
			<liferay-ui:search-container-column-text name="stock">
				<%
					PortletURL showStockFormURL = renderResponse.createRenderURL();
					showStockFormURL.setParameter("jspPage", "/html/dashboard/stockform.jsp");
					showStockFormURL.setParameter(HConstants.ITEM_ID, String.valueOf(item.getItemId()));	
					showStockFormURL.setParameter("redirectURL", redirectURL);
					showStockFormURL.setWindowState(LiferayWindowState.POP_UP);
				%>
				<a href="#" onclick="showStockForm('<%=showStockFormURL%>');"><aui:icon image="edit"/></a> 
				<%=item.getQuantity() == -1 ? LanguageUtil.get(portletConfig,themeDisplay.getLocale(),"unlimited") : item.getQuantity() %>
			</liferay-ui:search-container-column-text>
			
		<liferay-ui:search-container-column-text name="total-sold" >
			<%
				int orderStatus = (int) ShoppingOrderLocalServiceUtil.getAssetCategoryIdByName(HConstants.DELIVERED_STATUS);
				int itemsSold = ShoppingOrderLocalServiceUtil.getTotalItemsSold(orderStatus, item.getItemId());
			%>
			<%= itemsSold %>
		</liferay-ui:search-container-column-text>
		</c:if>
		
		
		
		<liferay-ui:search-container-column-text name="action">
			<%
				PortletURL itemDetailURL = renderResponse.createRenderURL();
				itemDetailURL.setParameter("jspPage", "/html/dashboard/itemdetails.jsp");
				itemDetailURL.setParameter("itemId", String.valueOf(item.getItemId()));
				itemDetailURL.setParameter("backURL", redirectURL);
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
	
	function showStockForm(url) {
		
		AUI().use('aui-modal', function(A) {
				Liferay.Util.openWindow({
					dialog: {
					    centered: true,
					    modal: true,
					    width  : 300,
					    height : 300
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
</aui:script>