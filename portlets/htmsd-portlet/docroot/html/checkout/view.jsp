<%@include file="/html/checkout/init.jsp" %>

<%
	String orderStep = ParamUtil.getString(renderRequest, "order_step", "step1");
	String continueShoppingURL=themeDisplay.getPortalURL() +"/web/"+themeDisplay.getLayout().getFriendlyURL()+"/home";
%>

<div class="new-checkout">
	<h1 id="cart-title" class="page-heading"><liferay-ui:message key="shopping-cart-summary" /></h1>
	<ul id="order_steps" class="step clearfix">
		<li class="step_current step1"><span><liferay-ui:message key="checkout-label-summary" /></span></li>
		<li class="step_todo step2"><span><liferay-ui:message key="checkout-label-signin" /></span></li>
		<li class="step_todo step3"><span><liferay-ui:message key="checkout-label-address" /></span></li>
		<li class="step_todo step4"><span><liferay-ui:message key="checkout-label-shipping" /></span></li>
		<li id="step-end" class="step_todo step5"><span><liferay-ui:message key="checkout-label-payment" /></span></li>
	</ul>
	
	<c:if test='<%= Validator.isNotNull(orderStep) && orderStep.equalsIgnoreCase("step1") %>'>
		<jsp:include page="/html/checkout/step1.jsp"/>
	</c:if>
	<c:if test='<%= Validator.isNotNull(orderStep) && orderStep.equalsIgnoreCase("step2") %>'>
		<jsp:include page="/html/checkout/step2.jsp"/>
	</c:if>
	<c:if test='<%= Validator.isNotNull(orderStep) && orderStep.equalsIgnoreCase("step3") %>'>
		<jsp:include page="/html/checkout/step3.jsp"/>
	</c:if>
	<c:if test='<%= Validator.isNotNull(orderStep) && orderStep.equalsIgnoreCase("step4") %>'>
		<jsp:include page="/html/checkout/step4.jsp"/>
	</c:if>
	<c:if test='<%= Validator.isNotNull(orderStep) && orderStep.equalsIgnoreCase("step5") %>'>
		<jsp:include page="/html/checkout/step5.jsp"/>
	</c:if>
</div>

<script>
	$(function(){
		var currentOrderStep = '<%= orderStep %>';
		if(currentOrderStep != null) {
			$("#order_steps li").removeClass("step_current").addClass("step_done_last"); 
			$("#order_steps ."+currentOrderStep).removeClass("step_todo").addClass("step_current");	
		}
	});
</script>