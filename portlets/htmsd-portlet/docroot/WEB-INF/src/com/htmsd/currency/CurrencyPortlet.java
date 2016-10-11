package com.htmsd.currency;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class CurrencyPortlet
 */
public class CurrencyPortlet extends MVCPortlet {

	public void serveResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws IOException,
			PortletException {
		if (resourceRequest.getResourceID().equalsIgnoreCase("currencyId")) {
			String currencyId = ParamUtil.getString(resourceRequest, "currencyId");
			System.out.println(currencyId);
			PortletSession portletSession = resourceRequest.getPortletSession();
			portletSession.setAttribute("currentCurrencyId", String.valueOf(currencyId), PortletSession.APPLICATION_SCOPE);
		}
	}
}