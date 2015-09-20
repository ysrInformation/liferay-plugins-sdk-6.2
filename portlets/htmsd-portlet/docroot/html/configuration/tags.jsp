<%@page import="com.htmsd.slayer.model.ShoppingItem"%>
<%@page import="com.liferay.portal.util.PortalUtil"%>
<%@page import="com.liferay.portal.util.Portal"%>
<%@page import="com.liferay.portlet.asset.service.AssetTagLocalServiceUtil"%>
<%@page import="com.liferay.portlet.asset.model.AssetTag"%>
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
			
			 	List<AssetTag>  assetTags =  AssetTagLocalServiceUtil.getTags();
				total = assetTags.size();
				
				pageContext.setAttribute("results", assetTags);
				pageContext.setAttribute("total", total);
			%>
		</liferay-ui:search-container-results>			 
		
		<liferay-ui:search-container-row className="com.liferay.portlet.asset.model.AssetTag" modelVar="tag" indexVar="indexVar" keyProperty="tagId">
	
			<liferay-ui:search-container-column-text name="no.">
				<%=String.valueOf(searchContainer.getStart()+1+indexVar)%>
			</liferay-ui:search-container-column-text>
			
			<liferay-ui:search-container-column-text name="tag-name" property="name" />
			
			<liferay-ui:search-container-column-text name="createDate"  >
				<fmt:formatDate value="<%=tag.getCreateDate() %>" pattern="dd/MM/yyyy" />
			</liferay-ui:search-container-column-text>
		
		</liferay-ui:search-container-row>
		
		<liferay-ui:search-iterator  searchContainer="<%=searchContainer %>"/>
	</liferay-ui:search-container>
</aui:form>