<%@page import="com.htmsd.slayer.model.impl.SellerImpl"%>
<%@page import="com.htmsd.slayer.model.Seller"%>
<%@include file="/html/dashboard/init.jsp" %>

<portlet:actionURL name="updateSellerDetails" var="updateSellerURL"/>
<portlet:renderURL var="backURL">
	<portlet:param name="jspPage" value="/html/dashboard/view.jsp"/> 
</portlet:renderURL>

<%
	Seller _seller = SellerLocalServiceUtil.getSellerByUserId(themeDisplay.getUserId());
	if (Validator.isNull(_seller)) {
		_seller = new SellerImpl();
	}
%>

<aui:form id="businessForm" name="businessForm" action="${updateSellerURL}" >
	<aui:fieldset>
		<aui:col width="<%= 50 %>">
			<aui:input name="sellerId" type="hidden" value="<%= _seller.getSellerId() %>"/>
			<aui:input name="bankName" required="true" value="<%= _seller.getBankName() %>" />
			<aui:input name="bankAccountNumber" required="true"  value="<%= _seller.getBankAccountNumber() %>"/>
			<aui:input name="companyName" required="true" value="<%= _seller.getName() %>"/>
			<aui:input name="ifsc-code" required="true" value="<%= _seller.getIfscCode() %>"/>
			<aui:input id="tin"  type="text" name="tin" value="<%= _seller.getTIN() %>"/>
			<aui:input id="cst"  type="text" name="cst" value="<%= _seller.getCST() %>" /> 
		</aui:col>
	</aui:fieldset>
	<aui:button-row>
		<aui:button type="submit" value="Update"/>
		<aui:button type="button" value="Cancel" href="<%= backURL %>"/> 
	</aui:button-row>	
</aui:form>

