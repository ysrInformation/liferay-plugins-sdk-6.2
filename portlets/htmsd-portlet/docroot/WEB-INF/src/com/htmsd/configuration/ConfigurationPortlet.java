package com.htmsd.configuration;

import java.io.IOException;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import com.htmsd.slayer.service.CategoryLocalServiceUtil;
import com.htmsd.util.HConstants;
import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetTag;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class ConfigurationPortlet
 */
public class ConfigurationPortlet extends MVCPortlet {
 
	
	
	
	public void addCategory(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		String name = ParamUtil.getString(actionRequest, HConstants.NAME);
		String description = ParamUtil.getString(actionRequest, HConstants.DESCRIPTION);
		long parentCategoryId = ParamUtil.getLong(actionRequest, HConstants.CATEGORY_ID);
		ThemeDisplay themeDisplay=(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		CategoryLocalServiceUtil.addCategory(themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId(), themeDisplay.getUserId(), name, description, parentCategoryId);

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
		
		try {
			if(AssetTagLocalServiceUtil.hasTag(themeDisplay.getScopeGroupId(), name)) {
				SessionErrors.add(actionRequest,"duplicateTagName");
			} else{
				
				AssetTag assetTag = null;
				assetTag = AssetTagLocalServiceUtil.createAssetTag(CounterLocalServiceUtil.increment(AssetTag.class.getName()));
				
				assetTag.setCompanyId(themeDisplay.getCompanyId());
				assetTag.setGroupId(themeDisplay.getScopeGroupId());
				assetTag.setCreateDate(new Date());
				assetTag.setModifiedDate(new Date());
				assetTag.setName(name);
				assetTag.setAssetCount(0);
				assetTag.setUserId(themeDisplay.getUserId());
				assetTag.setUserName(themeDisplay.getUser().getScreenName());
				
				AssetTagLocalServiceUtil.addAssetTag(assetTag);
			}
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}

		actionResponse.setRenderParameter("tab1", "Tags");
	}
	
	public void deleteTag(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		long tagId = ParamUtil.getLong(actionRequest, HConstants.TAG_ID);

		try {
			AssetTagLocalServiceUtil.deleteTag(tagId);
		} catch (PortalException e) {
			_log.error(e);
			e.printStackTrace();
		} catch (SystemException e) {
			_log.error(e);
		}
		
		actionResponse.setRenderParameter("tab1", "Tags");
	}
	
	public void deleteTagSet(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		String [] rowIds=ParamUtil.getParameterValues(actionRequest, "rowIds");
		for(String rowId : rowIds) {
			try {
				AssetTagLocalServiceUtil.deleteTag(Long.valueOf(rowId));
			} catch (PortalException e) {
				_log.error(e);
				e.printStackTrace();
			} catch (SystemException e) {
				_log.error(e);
			}
		}
		actionResponse.setRenderParameter("tab1", "Tags");
	}	
	private Log _log = LogFactoryUtil.getLog(ConfigurationPortlet.class);
}
