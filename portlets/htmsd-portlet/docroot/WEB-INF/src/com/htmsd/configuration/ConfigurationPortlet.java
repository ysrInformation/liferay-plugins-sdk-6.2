package com.htmsd.configuration;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import com.htmsd.slayer.model.Tag;
import com.htmsd.slayer.service.CategoryLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.htmsd.slayer.service.TagLocalServiceUtil;
import com.htmsd.util.HConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class ConfigurationPortlet
 */
public class ConfigurationPortlet extends MVCPortlet {
 
	public void addCategory(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		String name = ParamUtil.getString(actionRequest, HConstants.NAME);
		String description = ParamUtil.getString(actionRequest, HConstants.DESCRIPTION);
		
		ThemeDisplay themeDisplay=(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		CategoryLocalServiceUtil.addCategory(themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId(), themeDisplay.getUserId(), name, description);

		actionResponse.setRenderParameter("tab1", "Category");
	}
	
	public void deleteCategory(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		long categoryId = ParamUtil.getLong(actionRequest, HConstants.CATEGORY_ID);

		CategoryLocalServiceUtil.removeCategory(categoryId);
		
		actionResponse.setRenderParameter("tab1", "Category");
	}
	
	public void deleteCategorySet(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		String [] rowIds=ParamUtil.getParameterValues(actionRequest, "rowIds");
		for(String rowId : rowIds) {
			CategoryLocalServiceUtil.removeCategory(Long.valueOf(rowId));
		}
	}
	
	public void addTag(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		String name = ParamUtil.getString(actionRequest, HConstants.TAG);
		
		ThemeDisplay themeDisplay=(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		List<Tag> tags =  TagLocalServiceUtil.getTagByName(name);
		if(tags.size() == 0 ) {
			TagLocalServiceUtil.addTag(themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId(), themeDisplay.getUserId(), name);	
		}else{
			SessionErrors.add(actionRequest, "duplicateTagName");
		}
		
		actionResponse.setRenderParameter("tab1", "Tags");
	}
	
	public void deleteTag(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		long tagId = ParamUtil.getLong(actionRequest, HConstants.TAG_ID);

		TagLocalServiceUtil.removeTag(tagId);
		
		actionResponse.setRenderParameter("tab1", "Tags");
	}
	
	public void deleteTagSet(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		String [] rowIds=ParamUtil.getParameterValues(actionRequest, "rowIds");
		for(String rowId : rowIds) {
			TagLocalServiceUtil.removeTag(Long.valueOf(rowId));
		}
		actionResponse.setRenderParameter("tab1", "Tags");
	}	
	private Log _log = LogFactoryUtil.getLog(ConfigurationPortlet.class);
}
