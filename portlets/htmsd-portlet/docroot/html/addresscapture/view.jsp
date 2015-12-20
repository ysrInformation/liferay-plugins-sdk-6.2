<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@include file="/html/common/init.jsp" %>
<c:if test="<%=!permissionChecker.isOmniadmin() %>">
	<style>
		#p_p_id_6_WAR_htmsdportlet_ {
			display: none;
		}
	</style>
</c:if>
<portlet:renderURL var="addressURL" windowState="<%=LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="jspPage" value="/html/addresscapture/addressform.jsp"/>
</portlet:renderURL>
<c:if test="<%=!permissionChecker.isOmniadmin() %>">
	<script>
	
		window.onload = findUserAddress();
		
		
		function findUserAddress() {
			
			AUI().use('aui-io-request', function(A) {
				
				A.io.request('<portlet:resourceURL />', 
					{
						type:"GET",
						sync  : true,
						on: {
						   success: function() {
							   var data = this.get('responseData');
							   if(data == "true") {
								   console.log(data);
								   takeAddress();
							   }
						   }
						 }
					});
			});
		}
		
		function takeAddress() {
			console.log("inside take address");
			AUI().use('aui-modal', function(A) {
		        Liferay.Util.openWindow({
		            dialog: {
		                centered: true,
		                modal: true,
		                width : 800,
		                height :500
		            },
		            dialogIframe: {
		                id: 'updateAddress',
		                uri: '<%=addressURL%>'
		            },
		            title: Liferay.Language.get('address'),
		            uri: '<%=addressURL%>'
		        });
		       
		    });
		}
	
	</script>
</c:if>