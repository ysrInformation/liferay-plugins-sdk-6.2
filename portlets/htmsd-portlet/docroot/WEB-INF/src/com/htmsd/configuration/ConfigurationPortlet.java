package com.htmsd.configuration;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import com.htmsd.slayer.service.CategoryLocalServiceUtil;
import com.htmsd.util.HConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
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

	}
	
	public void deleteCategory(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		long categoryId = ParamUtil.getLong(actionRequest, HConstants.CATEGORY_ID);

		try {
			CategoryLocalServiceUtil.deleteCategory(categoryId);
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
	}

	private Log _log = LogFactoryUtil.getLog(ConfigurationPortlet.class);
}
