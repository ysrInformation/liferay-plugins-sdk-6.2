<%@page import="com.htmsd.util.HConstants"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.htmsd.slayer.model.Commission"%>
<%@page import="com.htmsd.slayer.service.CommissionLocalServiceUtil"%>
<%@page import="com.htmsd.slayer.service.CategoryLocalServiceUtil"%>
<%@page import="com.htmsd.slayer.model.Category"%>
<%@page import="java.util.List"%>
<%
	long commissionId = ParamUtil.getLong(request, "commissionId");
	long categoryId = 0;
	double percent = 0;
	if (commissionId > 0) {
		Commission commission = CommissionLocalServiceUtil.getCommission(commissionId);
		categoryId = commission.getCategoryId();
		percent = commission.getPercent();
	}
	List<Category> parentCategories = CategoryLocalServiceUtil.getByChild(0);
	List<Commission> commissions = CommissionLocalServiceUtil.getCommissions(-1, -1);
%>

<portlet:actionURL var="saveCommisionURL" name="saveCommison"/>

<aui:form action="${saveCommisionURL}">
	<aui:input name="commissionId" type="hidden" value="<%= commissionId %>"/>
	<aui:select name="<%=HConstants.CATEGORY_ID %>"   required="true" showEmptyOption="true">
		<c:forEach items="<%=parentCategories %>" var="category">   
			<aui:option label="${category.name}"  value="${category.categoryId}"/>
		</c:forEach>
	</aui:select>
	<aui:input name="percent" required="true" suffix="%"/>
	<aui:button-row>
		<aui:button type="submit"/>
	</aui:button-row>
</aui:form>

<table id="commisionTable" class="table table-striped table-bordered dt-responsive nowrap" width="100%" cellspacing="0">
	<thead>
		<tr>
			<th>
				<liferay-ui:message key="no"/>
			</th>
			<th>
				<liferay-ui:message key="category"/>
			</th>
			<th>
				<liferay-ui:message key="percent"/>
			</th>
			<th>
				<liferay-ui:message key="actions"/>
			</th>
		</tr>
	</thead>
	<tbody>
		<%
			int count = 1;
			for(Commission commission : commissions) {
				%>
					<tr>
						<td><%= count %></td>
						<td> <%= CategoryLocalServiceUtil.getCategory(commission.getCategoryId()).getName() %> </td>
						<td> <%= commission.getPercent() + " %" %></td>
						<td>
							<portlet:renderURL var="editCommissionURL" >
								<portlet:param name="commissionId" value="<%=String.valueOf(commission.getCommissionId()) %>"/>
								<portlet:param name="jspPage" value="/html/configuration/view.jsp"/>
								<portlet:param name="tab1" value="Commission"/>
							</portlet:renderURL>
							<portlet:actionURL var="deleteCommissionURL" name="deleteCommission">
								<portlet:param name="commissionId" value="<%=String.valueOf(commission.getCommissionId()) %>"/>
							</portlet:actionURL>
							<div>
								<a href="${editCommissionURL}">
									<liferay-ui:message key="edit"/>
								</a>
							</div>
							<div>
								<a href="${deleteCommissionURL}">
									<liferay-ui:message key="delete"/>
								</a>
							</div>
						</td>
					</tr>
				<%
				count++;
			}
		%>
	</tbody>
</table>
<script>
	$(function() {
		var categoryId = <%=categoryId %>;
		var percent = <%=percent%>;
		$('#<portlet:namespace/>categoryId option[value='+categoryId+']').prop("selected", true);
		$('#<portlet:namespace/>percent').val(percent);
		$("#commisionTable").DataTable();
		$('#<portlet:namespace/>categoryId').on('change', function() {
			Liferay.Service('/htmsd-portlet.commission/get-commission-percent-by-category', {
				categoryId: $('#<portlet:namespace/>categoryId').val()
			}, function(obj) {
				if (obj > 0) {
					alert("Commission for selected category exist.");
					$('#<portlet:namespace/>categoryId option[value=""]').prop("selected", true);
				}
			});
		});			
	});
</script>		