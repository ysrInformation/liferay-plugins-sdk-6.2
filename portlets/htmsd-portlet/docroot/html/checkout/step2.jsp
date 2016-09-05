<%@include file="/html/checkout/init.jsp" %>

<h1 class="page-heading">Step 2</h1>

<portlet:actionURL var="step2URL" name="step2"/>

<aui:form id="checkout-step2" name="step2" method="post" action="${step2URL}"> 
	<aui:button type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "step2") %>'/>
</aui:form>