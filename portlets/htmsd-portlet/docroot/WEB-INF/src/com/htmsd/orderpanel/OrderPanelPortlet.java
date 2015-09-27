package com.htmsd.orderpanel;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.htmsd.util.NotificationUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
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
		
		ShoppingOrder shoppingOrder = null;
		try {
			shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (Validator.isNotNull(shoppingOrder)) {
			NotificationUtil.sendNotification(shoppingOrder.getGroupId(), 
					shoppingOrder.getUserName(), shoppingOrder.getShippingEmailAddress(), "EMAIL_NOTIFICATION");
		}
		
	}

	private final Log _log = LogFactoryUtil.getLog(OrderPanelPortlet.class);

}
