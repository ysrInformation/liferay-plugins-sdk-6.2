<%@include file="/html/configuration/init.jsp"%>

<portlet:actionURL name="addTag"  var="addTagURL">
	<portlet:param name="backURL" value="<%=themeDisplay.getURLCurrent() %>"/>
</portlet:actionURL>

<%
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("jspPage", "/html/configuration/view.jsp");
	iteratorURL.setParameter("tab1", "Tags");
	if(SessionErrors.contains(renderRequest, "duplicateTagName")) {
		%>
			<liferay-ui:error key="duplicateTagName" message="tag-name-already-exist" />
		<%
	}
%>

<portlet:actionURL var="deleteSetURL" name="deleteTagSet" />

			<liferay-ui:success key="product-image-delete-success" message="product-image-delete-success" />

<aui:fieldset>
	<aui:form action="<%=addTagURL %>" method="POST">
		<aui:input name="<%=HConstants.TAG %>"  required="true"/>
		<aui:button  type="submit" value="add-tag" />
	</aui:form>
</aui:fieldset>

<aui:form action="<%=deleteSetURL %>"  method="POST">
	<aui:button type="submit" value="delete"  />
	
	<liferay-ui:search-container delta="10"   emptyResultsMessage="No Items to display" iteratorURL="<%=iteratorURL %>"  rowChecker="<%= new RowChecker(renderResponse)%>">
	
		<liferay-ui:search-container-results>
			 <%
			 
			 	List<Tag>  tags = TagLocalServiceUtil.getTags(searchContainer.getStart(),  searchContainer.getEnd());
				total = TagLocalServiceUtil.getTagsCount();
				
				pageContext.setAttribute("results", tags);
				pageContext.setAttribute("total", total);
			%>
		</liferay-ui:search-container-results>			 
		
		<liferay-ui:search-container-row className="com.htmsd.slayer.model.Tag" modelVar="tag" indexVar="indexVar" keyProperty="tagId">
	
			<liferay-ui:search-container-column-text name="no.">
				<%=String.valueOf(searchContainer.getStart()+1+indexVar)%>
			</liferay-ui:search-container-column-text>
			
			<liferay-ui:search-container-column-text name="tag-name" property="name" />
			
			<liferay-ui:search-container-column-text name="date"  >
				<fmt:formatDate value="<%=tag.getCreateDate() %>" pattern="dd/MM/yyyy" />
			</liferay-ui:search-container-column-text>
		
		</liferay-ui:search-container-row>
		
		<liferay-ui:search-iterator  searchContainer="<%=searchContainer %>"/>
	</liferay-ui:search-container>
</aui:form>