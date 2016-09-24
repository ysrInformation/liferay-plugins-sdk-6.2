<%@include file="/html/dashboard/init.jsp" %>
<style>
	#_153_TabsBack, #_153_ctvk_null_null {
		display: none;
	}
</style>
<%
	long itemId = (Long) request.getAttribute("itemId");
%>
<liferay-portlet:renderURL var="reviewUrl" portletName="1_WAR_htmsdportlet">
	<liferay-portlet:param name="jspPage" value="/html/dashboard/itemdetails.jsp"/>
	<liferay-portlet:param name="itemId" value="<%=String.valueOf(itemId) %>"/>
</liferay-portlet:renderURL>
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
				width: "90%",
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