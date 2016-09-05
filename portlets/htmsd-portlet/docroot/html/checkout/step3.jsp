<%@include file="/html/checkout/init.jsp" %>

<h1 class="page-heading">Step 3</h1>

<portlet:actionURL var="step3URL" name="step3"/>

<aui:form id="checkout-step3" name="step3" method="post" action="${step3URL}"> 
	<aui:button type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "step3") %>'/>
</aui:form>