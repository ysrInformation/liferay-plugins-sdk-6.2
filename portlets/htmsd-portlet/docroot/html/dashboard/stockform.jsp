<%@include file="/html/dashboard/init.jsp" %>
<portlet:actionURL var="updateStockURL" name="updateStock" >
	<portlet:param name="redirectURL" value='<%=ParamUtil.getString(renderRequest, "redirectURL") %>'/>
</portlet:actionURL>
<aui:form action="<%=updateStockURL %>" method="POST" target="_parent">
	<aui:input name="itemId" value="<%=ParamUtil.getString(renderRequest, HConstants.ITEM_ID) %>" type="hidden" />
	<aui:layout>
			<aui:column columnWidth="50">
				<aui:input name="<%=HConstants.QUANTITY %>">
					<aui:validator name="number" />
				</aui:input>
			</aui:column>
			
			<aui:column columnWidth="50">
				<aui:input name="<%=HConstants.UNILIMITED_QUANTITY %>" type="checkbox" label="unlimited-quantity" />
			</aui:column>
		</aui:layout>
		
	<aui:button value="save" type="submit"/>	
</aui:form>
 