<%@ include file="/html/shoppingcart/init.jsp"%>

<%
	long orderId = ParamUtil.getLong(request, "orderId");
%>

<portlet:actionURL var="cancelOrderURL" name="CancelOrder"/>

<liferay-ui:header title="please-specify-the-reason" showBackURL="false"/> 

<div class="update-status">
	<aui:form method="post" action="<%= cancelOrderURL %>" name="fm" id="fm" inlineLabels="true" target="_parent">  

		<aui:input name="orderId" type="hidden" value="<%= orderId %>"/>
		
		<aui:input name="cancelReason" type="textarea" cols="30" rows="5" label="please-specify-the-reason">
			<aui:validator name="required" errorMessage="please-enter-the-reason-why-you-want-to-cancel-this-order"/> 
		</aui:input>
	
		<aui:button-row>
			<aui:button type="submit" cssClass="btn-primary" value="submit"/> 
		</aui:button-row>
	</aui:form>
</div>