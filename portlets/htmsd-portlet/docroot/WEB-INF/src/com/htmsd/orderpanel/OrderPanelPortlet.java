package com.htmsd.orderpanel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.WindowStateException;

import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.service.ShoppingCartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.NotificationUtil;
import com.itextpdf.text.DocumentException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
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
		String tabName = ParamUtil.getString(actionRequest, "tabName");
		String cancelReason = ParamUtil.getString(actionRequest, "cancelReason");
		String articleId = ShoppingCartLocalServiceUtil.getArticleId((int)orderStatus);
		
		ShoppingOrderLocalServiceUtil.updateShoppingOrder(orderStatus, orderId, cancelReason); 
		
		ShoppingOrder shoppingOrder = null;
		try {
			shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (Validator.isNotNull(shoppingOrder) && !articleId.isEmpty()) {
			String[] tokens = ShoppingCartLocalServiceUtil.getOrderTokens();
			String[] values = ShoppingCartLocalServiceUtil.getValueTokens(shoppingOrder);
			NotificationUtil.sendNotification(shoppingOrder.getGroupId(), 
					shoppingOrder.getUserName(), shoppingOrder.getShippingEmailAddress(), articleId, tokens, values);
			
			//updating shoppingitem if order is cancelled.
			int cancelStatusId = ShoppingOrderLocalServiceUtil.getOrderStatusByTabName("Order Cancelled");
			if (orderStatus == cancelStatusId) {
				try {
					ShoppingItem shoppingItem = ShoppingItemLocalServiceUtil.fetchShoppingItem(shoppingOrder.getShoppingItemId());
					shoppingItem.setQuantity(shoppingItem.getQuantity() + shoppingOrder.getQuantity());
					ShoppingItemLocalServiceUtil.updateShoppingItem(shoppingItem);
				} catch (SystemException e) {
					_log.error(e.getMessage());
				}			
			}
		}
		
		actionResponse.setWindowState(LiferayWindowState.NORMAL);
		actionResponse.setRenderParameter("tab1", tabName); 
	}
	
	/**
	 * Method for generating Invoice
	 * @param actionRequest
	 * @param actionResponse
	 * @throws MalformedURLException
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 * @throws SystemException
	 * @throws WindowStateException 
	 */
	public void generateInvoice(ActionRequest actionRequest, ActionResponse actionResponse) 
				throws MalformedURLException, FileNotFoundException, DocumentException, SystemException, WindowStateException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		PortletSession portletSession = actionRequest.getPortletSession();
		
		long orderId = ParamUtil.getLong(actionRequest, "orderId");
		String tabName = ParamUtil.getString(actionRequest, "tabName");
		String isSeller = ParamUtil.getString(actionRequest, "isSeller");
		
		String timeStamp = new SimpleDateFormat("ddMMyyyyhhmm").format(new Date());
		String tempFileName = "Reciept"+timeStamp+".pdf";
		String filePath = GenerateInvoice.getFilePath(tempFileName);
		
		GenerateInvoice.generateInvoice(actionRequest, portletSession, orderId, themeDisplay.getCompanyId(), filePath);
		
		String articleId = "SEND_INVOICE";
		ShoppingOrder shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);
		
		if (Validator.isNotNull(shoppingOrder)) {
			String[] placeHolders = new String[]{"[$USER$]"};
			String[] values = new String[] {shoppingOrder.getUserName()};
			User seller = UserLocalServiceUtil.fetchUser(shoppingOrder.getSellerId());
			String emailAddress = (isSeller.equals("true") ? seller.getEmailAddress() : shoppingOrder.getShippingEmailAddress());
			NotificationUtil.sendReceipt(themeDisplay.getScopeGroupId(), emailAddress,
					articleId, shoppingOrder.getUserName(), filePath, tempFileName, placeHolders, values);
			
			ShoppingItem _shoppingItem = CommonUtil.getShoppingItem(shoppingOrder.getShoppingItemId());
			if (Validator.isNotNull(_shoppingItem)) {
				long userId = (isSeller.equals("true")) ? shoppingOrder.getSellerId() : shoppingOrder.getUserId();
				String userName = (isSeller.equals("true")) ? _shoppingItem.getUserName() : shoppingOrder.getUserName();
				String notificationContent = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "invoice-sent-notification-message");
				notificationContent = notificationContent.replace("[$USER_NAME$]", userName);
				notificationContent = notificationContent.replace("[$Product_details$]",_shoppingItem.getName());
				NotificationUtil.sendUserNotification(userId, notificationContent, actionRequest);
			}
		}
		
		String successMessage = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "reciept-has-been-sent-to-the-user");
		SessionMessages.add(actionRequest, "request_processed", successMessage);
		actionResponse.setWindowState(LiferayWindowState.NORMAL);
		actionResponse.setRenderParameter("tab1", tabName);  
	}

	private final Log _log = LogFactoryUtil.getLog(OrderPanelPortlet.class);
}
