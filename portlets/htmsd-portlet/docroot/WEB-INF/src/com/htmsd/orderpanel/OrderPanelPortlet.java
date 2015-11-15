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
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class OrderPanelPortlet
 */
public class OrderPanelPortlet extends MVCPortlet {
	
	@Override
	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		String keywords = ParamUtil.getString(actionRequest, "keywords");
		String tabName = ParamUtil.getString(actionRequest, "tabName");
		if (keywords.isEmpty()) {
			super.processAction(actionRequest, actionResponse);
		} else {
			PortalUtil.copyRequestParameters(actionRequest, actionResponse);
		}
		actionResponse.setRenderParameter("tab1", tabName);
	}
	
	public void updateOrderStatus(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		_log.info("In updateOrderStatus ..."); 
		int orderStatus = ParamUtil.getInteger(actionRequest, "orderStatus");
		long orderId = ParamUtil.getLong(actionRequest, "orderId");
		long orderItemId = ParamUtil.getLong(actionRequest, "orderItemId");
		String tabName = ParamUtil.getString(actionRequest, "tabName");
		
		ShoppingOrderLocalServiceUtil.updateShoppingOrderItem(orderStatus, orderItemId);
		
		ShoppingOrder shoppingOrder = null;
		try {
			shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (Validator.isNotNull(shoppingOrder)) {
			NotificationUtil.sendNotification(shoppingOrder.getGroupId(), 
					shoppingOrder.getUserName(), shoppingOrder.getShippingEmailAddress(), "EMAIL_NOTIFICATION", new String[1],new String[1]);
		}
		
		actionResponse.setWindowState(LiferayWindowState.NORMAL);
		actionResponse.setRenderParameter("tab1", tabName); 
	}

	private final Log _log = LogFactoryUtil.getLog(OrderPanelPortlet.class);

}
