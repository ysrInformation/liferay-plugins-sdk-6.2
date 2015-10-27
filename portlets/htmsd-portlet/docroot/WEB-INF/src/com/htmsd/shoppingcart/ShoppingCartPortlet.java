package com.htmsd.shoppingcart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpSession;

import com.htmsd.slayer.model.Invoice;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingItem_Cart;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.model.ShoppingOrderItem;
import com.htmsd.slayer.service.InvoiceLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.htmsd.util.NotificationUtil;
import com.htmsd.util.ShoppingBean;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class ShoppingCartPortlet
 */
public class ShoppingCartPortlet extends MVCPortlet {
	
	/**
	 * Method is for adding details of order made by the user
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 * @throws PortletException
	 */
	public void checkout(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		_log.info("In checkout method ..."); 
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		String shippingFirstName = ParamUtil.getString(actionRequest, "firstName");
		String shippingLastName = ParamUtil.getString(actionRequest, "lastName");
		String shippingEmailAddress = ParamUtil.getString(actionRequest, "email");
		String shippingMoble = ParamUtil.getString(actionRequest, "mobileNumber");
		String shippingAltMoble = ParamUtil.getString(actionRequest, "altNumber");
		String shippingStreet = ParamUtil.getString(actionRequest, "street");
		String shippingCity = ParamUtil.getString(actionRequest, "city");
		String shippingZip = ParamUtil.getString(actionRequest, "zip");
		String shippingCountry = ParamUtil.getString(actionRequest, "country");
		String shippingState = ParamUtil.getString(actionRequest, "state");
		
		ShoppingOrder shoppingOrder = ShoppingOrderLocalServiceUtil.insertShoppingOrder(HConstants.PENDING, 
				themeDisplay.getUserId(),themeDisplay.getCompanyId(),themeDisplay.getScopeGroupId(),
				shippingFirstName, shippingLastName, shippingStreet, shippingCity, shippingZip, 
				shippingEmailAddress, shippingState, shippingCountry, shippingMoble, shippingAltMoble);
		
		_log.info("orderId is ==>"+shoppingOrder.getOrderId()); 
		Invoice invoice = InvoiceLocalServiceUtil.insertInvoice(themeDisplay.getUserId(), shoppingOrder.getOrderId());
		addShoppingOrderItem(actionRequest, themeDisplay, shoppingOrder);
		
		NotificationUtil.sendNotification(themeDisplay.getScopeGroupId(), 
				themeDisplay.getUser().getFullName(), themeDisplay.getUser().getEmailAddress(), "EMAIL_NOTIFICATION");
		
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		String successMessage = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "you-order-has-been-registered-you-may-get-email-shortly");
		SessionMessages.add(actionRequest, "request_processed", successMessage);
		actionResponse.setRenderParameter(HConstants.JSP_PAGE, HConstants.PAGE_SHOPPING_CART_DETAILS); 
		actionResponse.setRenderParameter("tab1", "my-orders"); 
	}

	/**
	 * Method for adding items in ShoppingOrderItems
	 * @param themeDisplay
	 * @param shoppingOrder
	 */
	private void addShoppingOrderItem(ActionRequest actionRequest, ThemeDisplay themeDisplay, ShoppingOrder shoppingOrder) {
		
		double totalPrice = 0;
		List<ShoppingItem_Cart> shoppingItem_Carts = CommonUtil.getUserCartItems(themeDisplay.getUserId());
		
		if (Validator.isNotNull(shoppingItem_Carts) && shoppingItem_Carts.size() > 0) {
			for (ShoppingItem_Cart shoppingItem_Cart:shoppingItem_Carts) {
				
				ShoppingItem shoppingItem = CommonUtil.getShoppingItem(shoppingItem_Cart.getItemId()); 
				long quantity = shoppingItem.getQuantity() - shoppingItem_Cart.getQuantity();
				if (Validator.isNotNull(shoppingItem)) {
					totalPrice = shoppingItem_Cart.getTotalPrice();
					ShoppingOrderItem shoppingOrderItem = ShoppingOrderItemLocalServiceUtil
						.insertShoppingOrderItem(shoppingItem_Cart.getQuantity(), totalPrice, themeDisplay.getUserId(), 
						themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(), shoppingOrder.getOrderId(), shoppingItem.getItemId(),
						shoppingItem.getName(), shoppingItem.getDescription(), shoppingItem.getProductCode());
					
					//updating shoppingItem quantity
					if (Validator.isNotNull(shoppingOrderItem)) {
						try {
							ShoppingItem shoppingItem2 = ShoppingItemLocalServiceUtil.fetchShoppingItem(shoppingOrderItem.getShoppingItemId());
							shoppingItem2.setQuantity(quantity);
							ShoppingItemLocalServiceUtil.updateShoppingItem(shoppingItem2);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					//deleting items from shoppingItem_Cart table. 
					try {
						ShoppingItem_CartLocalServiceUtil.deleteShoppingItem_Cart(shoppingItem_Cart.getId());
					} catch (PortalException e) {
						e.printStackTrace();
					} catch (SystemException e) {
						e.printStackTrace();
					}
				}
			}
		} 
	}
	
	public void serveResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws IOException,
			PortletException {
		
		String cmd = ParamUtil.getString(resourceRequest, Constants.CMD);
		if (Validator.isNotNull(cmd)) {
			if (cmd.equalsIgnoreCase("remove-item")){
				removeItem(resourceRequest, resourceResponse);
			} else if (cmd.equalsIgnoreCase("total-price")) {
				updateItemPrice(resourceRequest);
			}
		}
	}

	private void updateItemPrice(ResourceRequest resourceRequest) {
	
		int quantity = ParamUtil.getInteger(resourceRequest, "quantity");
		double itemPrice = ParamUtil.getDouble(resourceRequest, "itemPrice");
		long id = ParamUtil.getLong(resourceRequest, "id");
		long itemId = ParamUtil.getLong(resourceRequest, "itemId");
		
		HttpSession session = PortalUtil.getHttpServletRequest(resourceRequest).getSession();
		List<ShoppingBean> shoppingCartList = CommonUtil.getGuestUserList(session);
		List<ShoppingBean> newShoppingCartList = new ArrayList<ShoppingBean>();
		
		if (id > 0) {
			double totalPrice = quantity * itemPrice;
			try {
				ShoppingItem_Cart shoppingItem_Cart =  ShoppingItem_CartLocalServiceUtil.fetchShoppingItem_Cart(id);
				shoppingItem_Cart.setQuantity(quantity);
				shoppingItem_Cart.setTotalPrice(totalPrice);
				ShoppingItem_CartLocalServiceUtil.updateShoppingItem_Cart(shoppingItem_Cart);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		} else {
			if (Validator.isNotNull(shoppingCartList) && shoppingCartList.size() > 0){
				for (ShoppingBean shoppingBean:shoppingCartList) {
					if (itemId == shoppingBean.getItemId()) {
						double total = quantity * itemPrice;
						shoppingBean.setQuantity(quantity); 
						shoppingBean.setTotalPrice(total);
						newShoppingCartList.add(shoppingBean);
					} else {
						newShoppingCartList.add(shoppingBean);
					}
				}
			}
			session.setAttribute("SHOPPING_ITEMS", newShoppingCartList);
		}
	}

	/**
	 * Method for removing item from cart
	 * @param resourceRequest
	 * @param resourceResponse
	 */
	private void removeItem(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		
		_log.info("Inside removeItem ..."); 
		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		HttpSession session = PortalUtil.getHttpServletRequest(resourceRequest).getSession();
		long itemId = ParamUtil.getLong(resourceRequest, HConstants.ITEM_ID);
		
		List<ShoppingBean> newCartList = new ArrayList<ShoppingBean>();
		List<ShoppingItem_Cart> shoppingItem_Carts = CommonUtil.getUserCartItems(themeDisplay.getUserId());
		List<ShoppingBean> shoppingbeanList = CommonUtil.getGuestUserList(session);
		
		if (themeDisplay.isSignedIn()) {
			if (Validator.isNotNull(shoppingItem_Carts)) {
				for (ShoppingItem_Cart shoppingItem_Cart:shoppingItem_Carts) {
					if (itemId == shoppingItem_Cart.getItemId()) {
						try {
							ShoppingItem_CartLocalServiceUtil.deleteShoppingItem_Cart(shoppingItem_Cart.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} else {
			//delete from guest
			if (Validator.isNotNull(shoppingbeanList) && shoppingbeanList.size() > 0) {
				for (ShoppingBean shoppingBean:shoppingbeanList) {
					newCartList.add(shoppingBean);
					if (itemId == shoppingBean.getItemId()) {
						newCartList.remove(shoppingBean);
					}
				}
			}
			session.setAttribute("SHOPPING_ITEMS", newCartList); 
		}
	}
	
	/**
	 * Method for canceling the order
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 * @throws PortletException
	 */
	public void CancelOrder(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
	
		_log.info("Inside CancelOrder method ..."); 
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		long orderId = ParamUtil.getLong(actionRequest, "orderId");

		ShoppingOrderLocalServiceUtil.updateShoppingOrder(HConstants.CANCEL_ORDER, orderId);
		
		NotificationUtil.sendNotification(themeDisplay.getScopeGroupId(), 
				themeDisplay.getUser().getFullName(), themeDisplay.getUser().getEmailAddress(), "EMAIL_NOTIFICATION");
		
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		String successMessage = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "you-have-requested-to-cancel-the-order-successfully");
		SessionMessages.add(actionRequest, "request_processed", successMessage);
		actionResponse.setRenderParameter(HConstants.JSP_PAGE, HConstants.PAGE_SHOPPING_CART_DETAILS); 
		actionResponse.setRenderParameter("tab1", "my-orders");
	}

	private static final Log _log = LogFactoryUtil.getLog(ShoppingCartPortlet.class.getName());
}
