<%@include file="/html/checkout/init.jsp" %>

<h1 class="page-heading">Step 4</h1>

<portlet:actionURL var="step4URL" name="step4"/>

<aui:form id="checkout-step4" name="step4" method="post" action="${step4URL}"> 
	<aui:button type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "step4") %>'/>
</aui:form>