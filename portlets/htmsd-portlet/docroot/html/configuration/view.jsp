<%@page import="javax.portlet.ActionRequest"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="com.htmsd.slayer.model.Category"%>
<%@page import="java.util.List"%>
<%@page import="com.htmsd.slayer.service.CategoryLocalServiceUtil"%>
<%@page import="com.htmsd.util.HConstants"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib uri="http://alloy.liferay.com/tld/aui"  prefix="aui"%>
<%@taglib uri="http://liferay.com/tld/theme"  prefix="liferay-theme"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<portlet:defineObjects />
<liferay-theme:defineObjects/>

<portlet:actionURL name="addCategory"  var="addCategoryURL">
	<portlet:param name="backURL" value="<%=themeDisplay.getURLCurrent() %>"/>
</portlet:actionURL>


<aui:fieldset>
	<aui:form action="<%=addCategoryURL %>" method="POST">
		<aui:input name="<%=HConstants.NAME %>"  required="true"/>
		<aui:input name="<%=HConstants.DESCRIPTION %>" required="true" type="textarea" />
		<aui:button  type="submit" value="add-category" />
	</aui:form>
</aui:fieldset>
<%
	List<Category> categories =  CategoryLocalServiceUtil.getCategories(-1, -1);
%>

<aui:fieldset>
	<table border="2">
		<tr>
			<td>No.</td>
			<td>Category Name</td>
			<td>Category Description</td>
			<td>Delete</td>
		</tr>
		<%
			int i = 1;
			for(Category category : categories) {
				%>
				<tr>
					<td><%=i++ %></td>
					<td><%=category.getName() %></td>
					<td><%=category.getDescription() %></td>
					<td>
						<%
							PortletURL deleteURL = renderResponse.createActionURL();
							deleteURL.setParameter(ActionRequest.ACTION_NAME, "deleteCategory");
							deleteURL.setParameter("categoryId", String.valueOf(category.getCategoryId()));
 						%>
 						<aui:a href="<%=deleteURL.toString() %>">Delete</aui:a>
					</td>
				</tr>
				<%
			}
		%>   
				
	</table>
</aui:fieldset>