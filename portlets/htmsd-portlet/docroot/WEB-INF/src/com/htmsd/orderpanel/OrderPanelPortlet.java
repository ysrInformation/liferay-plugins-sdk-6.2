package com.htmsd.orderpanel;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class OrderPanelPortlet
 */
public class OrderPanelPortlet extends MVCPortlet {
	
	public void updateOrderStatus(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		_log.info("In updateOrderStatus ..."); 
		int orderStatus = ParamUtil.getInteger(actionRequest, "orderStatus");
		long orderId = ParamUtil.getLong(actionRequest, "orderId");
		
		ShoppingOrderLocalServiceUtil.updateShoppingOrder(orderStatus, orderId);
		
	}

	private final Log _log = LogFactoryUtil.getLog(OrderPanelPortlet.class);

}
