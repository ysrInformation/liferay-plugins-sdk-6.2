<%@include file="/html/dashboard/init.jsp" %>

<portlet:actionURL var="updateStockURL" name="updateStock" >
	<portlet:param name="redirectURL" value='<%=ParamUtil.getString(renderRequest, "redirectURL") %>'/>
</portlet:actionURL>
<aui:form action="<%=updateStockURL %>" method="POST" target="_parent">
	<aui:input name="itemId" value="<%=ParamUtil.getString(renderRequest, HConstants.ITEM_ID) %>" type="hidden"  />
				<aui:input name="<%=HConstants.QUANTITY %>">
					<aui:validator name="digits" />
				</aui:input>
	<aui:input name="<%=HConstants.UNILIMITED_QUANTITY %>" type="checkbox" label="unlimited-quantity" />
	<aui:button value="save" type="submit"/>	
</aui:form>
 