<%@page import="com.liferay.portal.kernel.workflow.WorkflowConstants"%>
<%@page import="com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.htmsd.util.CommonUtil"%>
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
    String tabNames = "New Items,Approved Items,Incomplete Items,Rejected Items";
    if (isApprover) {
    	tabNames = "New Items";
    }
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
	} else if(tabs1.equals("Approved Items")) {
		status = HConstants.APPROVE;	
	} else if(tabs1.equals("Incomplete Items")) {
		status = HConstants.INCOMPLETE;
	} else {
		status = HConstants.REJECT;
	}
	
	int count=1;
	if (isApprover) {
		itemList = CommonUtil.getPendingApprovalItems(themeDisplay.getUserId(), themeDisplay.getCompanyId(), status);
	} else {
		itemList = ShoppingItemLocalServiceUtil.getByStatus(status);
	}
%>    	

<%-- <aui:form action="<%=deletSetURL.toString() %>" name="bookListForm" method="POST"> --%>
<aui:button name="deleteBtn" type="submit" value="delete" style="display:none"/>
<aui:button name="approveBtn" type="button" value="approve" onClick="changeAction('approve');" style="display:none"/>
<aui:button name="rejectBtn" type="button" value="reject" onClick="changeAction('reject');" style="display:none"/>
<c:if test="<%= !isApprover %>">
	<aui:nav-bar>
		<aui:nav>
			<aui:nav-item href="<%=addItemURL.toString() %>" iconCssClass="icon-plus" label="add-item"/>
		</aui:nav>
	</aui:nav-bar>
</c:if>
<table id="displayItems" class="table table-striped table-bordered dt-responsive nowrap" width="100%" cellspacing="0"> 
	<thead>
  		<tr>
    		<th><liferay-ui:message key="no."/></th>
    		<th><liferay-ui:message key="product-code"/></th>
    		<th><liferay-ui:message key="item-title"/></th>
    		<th><liferay-ui:message key="date"/></th>
    		<th><liferay-ui:message key="seller"/></th>
    		<th><liferay-ui:message key="seller-city"/></th>
    		<c:if test='<%=tabs1.equals("Approved Items") %>'>
    			<th><liferay-ui:message key="product-stock"/></th>
    			<th><liferay-ui:message key="total-sold"/></th>
    		</c:if>
    		<th><liferay-ui:message key="action"/></th>
  		</tr>
  </thead>
  <tbody>
  		<c:if test='<%= Validator.isNotNull(itemList) && itemList.size() > 0 %>'>
  			<% for (ShoppingItem item : itemList) { %>
  				<tr>
  					<td><%= count %></td>
  					<td><%= item.getProductCode() %></td>
  					<td>
  						<portlet:renderURL var="historyURL">
							<portlet:param name="jspPage" value="/html/dashboard/itemhistory.jsp"/>
							<portlet:param name="backURL" value="<%=redirectURL %>"/>
							<portlet:param name="<%=HConstants.ITEM_ID %>" value="<%=String.valueOf(item.getItemId())%>"/>
						</portlet:renderURL>
						<aui:a href="<%= historyURL %>"><%= item.getName() %></aui:a> 
  					</td>
  					<td>
  						<fmt:formatDate value="<%= item.getCreateDate() %>" pattern="dd/MM/yyyy" />
  					</td>
  					<td>
  						<%= item.getUserEmail() + StringPool.SPACE + StringPool.OPEN_PARENTHESIS + item.getUserName() + StringPool.CLOSE_PARENTHESIS %>
  					</td>
  					<td>
  						<% User seller = UserLocalServiceUtil.fetchUser(item.getUserId()); %>
						<%= seller.getAddresses().isEmpty() ? StringPool.BLANK : seller.getAddresses().get(0).getCity() %>
  					</td>
  					<c:if test='<%=tabs1.equals("Approved Items") %>'>
  						<td>
  							<%
								PortletURL showStockFormURL = renderResponse.createRenderURL();
								showStockFormURL.setParameter("jspPage", "/html/dashboard/stockform.jsp");
								showStockFormURL.setParameter(HConstants.ITEM_ID, String.valueOf(item.getItemId()));	
								showStockFormURL.setParameter("redirectURL", redirectURL);
								showStockFormURL.setWindowState(LiferayWindowState.POP_UP);
							%>
							<a href="#" onclick="showStockForm('<%=showStockFormURL%>');"><aui:icon image="edit"/></a> 
							<%= item.getQuantity() == -1 ? LanguageUtil.get(portletConfig,themeDisplay.getLocale(),"unlimited") : item.getQuantity() %>
  						</td>
  						<td>
  							<%
								int orderStatus = (int) ShoppingOrderLocalServiceUtil.getAssetCategoryIdByName(HConstants.DELIVERED_STATUS);
								int itemsSold = ShoppingOrderLocalServiceUtil.getTotalItemsSold(orderStatus, item.getItemId());
							%>
							<%= itemsSold %>
  						</td>
  					</c:if>
  					<td>
  						<% String itemDetailURL = CommonUtil.getWorkflowURL(item.getItemId(), themeDisplay); %>
						<a onclick="<%= itemDetailURL %>" href="javascript:void(0);">View</a>
  					</td>
  				</tr>
  			<% count++;
  			} %>
  		</c:if>
  </tbody>
</table>

 
<aui:script>
	$(document).ready(function(){
		 var btns = $("#<portlet:namespace/>deleteBtn,#<portlet:namespace/>approveBtn,#<portlet:namespace/>rejectBtn").hide();
	 	 var checkBoxs  = $(":checkbox").on("click",function(){
			btns.toggle(checkBoxs.is(":checked"));
		});
		$("#displayItems").DataTable();
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
				    cssClass: 'dashboard-popup a2zali-popup',
				    modal: true,
				    width  : 300,
				    height : 300,
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

	function showReviewPage(url) {
		AUI().use('aui-modal', function(A) {
			Liferay.Util.openWindow({
				dialog : {
					centered : true,
					cssClass: 'dashboard-popup a2zali-popup',
					modal : true,
					width : "90%",
					height : 800,
					destroyOnHide: true,
    				resizable: false
				},
				dialogIframe : {
					id : 'showstockDialog',
					uri : url
				},
				title : Liferay.Language.get('review'),
				uri : url
			});
		});
	}
</aui:script>