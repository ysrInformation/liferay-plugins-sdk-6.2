<%@include file="/html/checkout/init.jsp" %>

<portlet:actionURL var="step5URL" name="step5"/>

<aui:form id="checkout-step5" name="step5" method="post" action="${step5URL}" inlineLabels="<%= true %>"> 
	
	<div class="payment-methods">
		<div class="payment-methods-label"> 
			<h1 class="page-heading"><liferay-ui:message key="checkout-payment-methods"/></h1>
		</div>
		<div class="paymentOptions">
			<aui:input name="paymentMethod" type="radio" label="checkout-label-debitcard" value="debit" disabled="true">
				<aui:validator name="required"/>
			</aui:input>
			<aui:input name="paymentMethod" type="radio" label="checkout-label-creditcard" value="credit" disabled="true">
				<aui:validator name="required"/>
			</aui:input>
			<aui:input name="paymentMethod" type="radio" label="checkout-label-cod" value="cod">
				<aui:validator name="required"/>
			</aui:input>
		</div>
	</div>	

	<aui:button-row>
		<aui:button cssClass="pull-left" type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "continue-shopping") %>' href="<%= continueShoppingURL %>"/>
		<aui:button cssClass="pull-right" type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "step5") %>'/>
	</aui:button-row>
</aui:form>