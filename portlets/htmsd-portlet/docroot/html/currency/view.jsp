<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="javax.portlet.PortletSession"%>
<%@include file="/html/common/init.jsp" %>
<style>
#p_p_id_7_WAR_htmsdportlet_, #p_p_id_7_WAR_htmsdportlet_ .portlet-borderless-container
	{
	background: transparent none repeat scroll 0 0;
	height: 38px;
	margin-top: 5px;
}

#portlet_7_WAR_htmsdportlet{
	background: transparent none repeat scroll 0 0;
	margin-top: -10px;
	padding: 0;
}

#p_p_id_7_WAR_htmsdportlet_ .portlet-body {
	height: 30px;
}

.currency label {
	color: white;
}

.currency select {
	width: 48%;
}
</style>
<%
	String currencyId = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
	if (Validator.isNull(currencyId)) {
		currencyId = "0";
	}
%>

<portlet:resourceURL var="setCurrencyURL" id="currencyId"/>

<aui:select name="Currency" inlineField="true" inlineLabel="true"  onChange="javascript:changeCurrency(this);" cssClass="currency">
	<aui:option label="INR" value="0" selected="true"/>
</aui:select>
<aui:script use="aui-base">

	var ajaxURI2 = '<%=themeDisplay.getPortalURL()%>/api/jsonws/htmsd-portlet.currency/get-currencies';
	AUI().io.request(ajaxURI2, {
		sync: false,
		dateType:'json',
		method: 'get',
		data : {
			p_auth: Liferay.authToken
		},
		on: {
			success: function() {
				currencies = JSON.parse(this.get('responseData'));
				var selectCurr = $('#<portlet:namespace/>Currency');
				for(var index in currencies) {
		    		var name = (currencies[index].currencyCode);
		    		var currencyId = (currencies[index].currencyId);
		    		selectCurr.append(new Option(name,currencyId));
	    		}
				
				var currencyId = '<%=currencyId%>';
				$('#<portlet:namespace/>Currency option[value="'+currencyId+'"]').attr('selected', 'selected');
			}
		}
	});	
	
	 changeCurrency = function(obj) {
		AUI().io.request('<%=setCurrencyURL%>', {
			sync: true,
			data : {
				p_auth: Liferay.authToken,
				<portlet:namespace/>currencyId : obj.value 				
			},
			on: {
				success: function() {
					window.location.href = '<%=themeDisplay.getURLCurrent()%>';
				}
			}
		});	
	}
</aui:script>