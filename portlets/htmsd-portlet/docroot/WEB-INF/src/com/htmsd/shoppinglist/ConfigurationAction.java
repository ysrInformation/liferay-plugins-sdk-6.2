package com.htmsd.shoppinglist;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;

import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;

public class ConfigurationAction extends DefaultConfigurationAction {
	
	@Override
	public void processAction(PortletConfig portletConfig,
			ActionRequest actionRequest, ActionResponse actionResponse)
			throws Exception {
		super.processAction(portletConfig, actionRequest, actionResponse);

		String noOfItem = ParamUtil.getString(actionRequest, "noOfItems");
		String categoryToDisplay = ParamUtil.getString(actionRequest, "categoryToDisplay");
		
		PortletPreferences preferences = actionRequest.getPreferences();
		preferences.setValue("noOfItems", noOfItem);
		preferences.setValue("categoryToDisplay", categoryToDisplay);
		preferences.store();
	}
}
