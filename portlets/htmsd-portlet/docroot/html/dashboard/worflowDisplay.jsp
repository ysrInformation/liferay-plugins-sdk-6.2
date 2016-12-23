<%@page import="com.liferay.portal.security.permission.PermissionThreadLocal"%>
<%@include file="/html/dashboard/init.jsp" %>
<style>
	#_153_TabsBack, #_153_ctvk_null_null {
		display: none;
	}
</style>
<%
	long itemId = (Long) request.getAttribute("itemId");
	String reviewUrl = themeDisplay.getURLPortal() + "/group/guest/system-admin?p_p_id=1_WAR_htmsdportlet&p_p_lifecycle=0&p_p_state=pop_up&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_1_WAR_htmsdportlet_jspPage=%2Fhtml%2Fdashboard%2Fitemdetails.jsp&_1_WAR_htmsdportlet_itemId="+itemId;
%>
<aui:button value="Review Item" onClick="javascript:showNewDetailPage();"/>
<script>
function showNewDetailPage() {
	var url = '<%=reviewUrl%>';
	url += "&controlPanelCategory=my";
	AUI().use('liferay-util-window', function(A) {
		Liferay.Util.Window.getWindow({
			dialog: {
				centered: true,
				modal: true,
				cssClass: 'dashboard-popup a2zali-popup',
				width: "95%",
				height: 800,
				destroyOnHide: true			
			},
			id: 'detailPopup',
			title: 'Details',
			uri: url
		});        
	});
}
</script> 