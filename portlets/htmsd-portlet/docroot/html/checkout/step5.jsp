<%@include file="/html/checkout/init.jsp" %>

<h1 class="page-heading">Step 5</h1>

<portlet:actionURL var="step5URL" name="step5"/>

<aui:form id="checkout-step5" name="step5" method="post" action="${step5URL}"> 
	<aui:button type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "step5") %>'/>
</aui:form>