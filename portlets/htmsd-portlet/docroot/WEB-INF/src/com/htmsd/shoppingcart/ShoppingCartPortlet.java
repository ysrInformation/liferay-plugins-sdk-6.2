package com.htmsd.shoppingcart;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpSession;

import com.htmsd.orderpanel.GenerateInvoice;
import com.htmsd.slayer.model.Invoice;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingItem_Cart;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.model.WholeSale;
import com.htmsd.slayer.service.InvoiceLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingCartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.htmsd.slayer.service.WholeSaleLocalServiceUtil;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.htmsd.util.NotificationUtil;
import com.htmsd.util.ShoppingBean;
import com.itextpdf.text.DocumentException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.UserNotificationEventLocalServiceUtil;
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
		
		addShoppingOrderItems(actionRequest);
		
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
	private void addShoppingOrderItems(ActionRequest actionRequest) {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		PortletSession portletSession = actionRequest.getPortletSession();
		List<ShoppingItem_Cart> shoppingItem_Carts = CommonUtil.getUserCartItems(themeDisplay.getUserId());
		
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
		
		if (Validator.isNotNull(shoppingItem_Carts) && shoppingItem_Carts.size() > 0) {
			for (ShoppingItem_Cart shoppingItem_Cart:shoppingItem_Carts) {
				
				ShoppingItem shoppingItem = CommonUtil.getShoppingItem(shoppingItem_Cart.getItemId()); 

				if (Validator.isNull(shoppingItem)) continue;
					
				int orderStatus = HConstants.PENDING;
				int orderQuantity = shoppingItem_Cart.getQuantity();
				long userId = themeDisplay.getUserId();
				long groupId = themeDisplay.getScopeGroupId();
				long companyId = themeDisplay.getCompanyId();
				long sellerId = shoppingItem.getUserId();
				long shoppingItemId = shoppingItem.getItemId();
				double totalPrice = shoppingItem_Cart.getTotalPrice();
				String sellerName = CommonUtil.getUserFullName(sellerId);  
				String articleId = "ORDER_COMFIRMATION";
				String emailAddress = StringPool.BLANK;
				
				ShoppingOrder shoppingOrder = ShoppingOrderLocalServiceUtil.insertShoppingOrder(orderStatus, orderQuantity, 
						shoppingItemId, sellerId, userId, companyId, groupId, totalPrice, sellerName, StringPool.BLANK, shippingFirstName, 
						shippingLastName, shippingStreet, shippingCity, shippingZip, shippingEmailAddress, shippingState,
						shippingCountry, shippingMoble, shippingAltMoble);
				
				Invoice invoice = InvoiceLocalServiceUtil.insertInvoice(themeDisplay.getUserId(), shoppingOrder.getOrderId());
				
				//updating shoppingItem quantity
				if (Validator.isNotNull(shoppingOrder)) {
					try {
						long quantity = shoppingItem.getQuantity() - shoppingItem_Cart.getQuantity();
						ShoppingItem shoppingItem2 = ShoppingItemLocalServiceUtil.fetchShoppingItem(shoppingOrder.getShoppingItemId());
						shoppingItem2.setQuantity(quantity);
						ShoppingItemLocalServiceUtil.updateShoppingItem(shoppingItem2);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					String[] tokens = ShoppingCartLocalServiceUtil.getOrderTokens();
					String[] values = ShoppingCartLocalServiceUtil.getValueTokens(shoppingOrder);
					NotificationUtil.sendNotification(themeDisplay.getScopeGroupId(), shoppingOrder.getUserName(),
							shoppingOrder.getShippingEmailAddress(), articleId, tokens, values); 
					
					String notificationContent = "<p><strong>Hi [$USER_NAME$],</strong></p><p>Thank you for your order!</p><p>you have ordered [$Product_details$]</p>";
					notificationContent = notificationContent.replace("[$USER_NAME$]", shoppingOrder.getUserName());
					notificationContent = notificationContent.replace("[$Product_details$]", CommonUtil.getShoppingItem(shoppingOrder.getShoppingItemId()).getName());
					sendUserNotification(userId, notificationContent, actionRequest);

					//send receipt to seller
					sendInvoiceToSeller(actionRequest, portletSession, groupId, companyId, sellerId, emailAddress, shoppingOrder);
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

	protected void sendInvoiceToSeller(ActionRequest actionRequest, PortletSession portletSession, 
			long groupId, long companyId, long sellerId, String emailAddress, ShoppingOrder shoppingOrder) {
		
		String fileName = "Reciept"+new SimpleDateFormat("ddMMyyyyhhmm").format(new Date())+".pdf";
		try {
			GenerateInvoice.generateInvoice(actionRequest, portletSession, shoppingOrder.getOrderId(), companyId, GenerateInvoice.getFilePath(fileName));
		} catch (FileNotFoundException e) {
			_log.info("File not found due to:"+e);
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			_log.info("URL not formed correctly :"+e);
		} catch (DocumentException e) {
			e.printStackTrace();
			_log.info("error occured in document :"+e); 
		}
		
		try {
			User seller = UserLocalServiceUtil.fetchUser(sellerId);
			emailAddress = (Validator.isNull(seller)) ? StringPool.BLANK : seller.getEmailAddress();
		} catch (SystemException e) {
			e.printStackTrace();
			_log.info("email not found"+e);
		}
		
		if (!emailAddress.isEmpty()) {
			NotificationUtil.sendReceipt(groupId, emailAddress, "SEND_INVOICE", shoppingOrder.getUserName(),
					GenerateInvoice.getFilePath(fileName), fileName, new String[]{"[$USER$]"}, new String[]{shoppingOrder.getUserName()});
		} else {
			_log.info("Email of seller not found.."); 
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
		itemPrice = getWholeSalePrice(quantity, itemId, itemPrice);
				
		if (id > 0) {
			double totalPrice = quantity * itemPrice;
			ShoppingItem_CartLocalServiceUtil.updateShoppingItem_Cart(id, quantity, totalPrice);
		} else {
			if (Validator.isNotNull(shoppingCartList) && shoppingCartList.size() > 0) {
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
		long orderStatus = ShoppingOrderLocalServiceUtil.getAssetCategoryIdByName(HConstants.CANCEL_ORDER_STATUS);
		String cancelReason = ParamUtil.getString(actionRequest, "cancelReason");
		String articleId = ShoppingCartLocalServiceUtil.getArticleId((int)orderStatus);
		
		ShoppingOrderLocalServiceUtil.updateShoppingOrder((int)orderStatus, orderId, cancelReason);
		
		ShoppingOrder shoppingOrder = null;
		try {
			shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (Validator.isNotNull(shoppingOrder)) {
			String[] tokens = ShoppingCartLocalServiceUtil.getOrderTokens();
			String[] values = ShoppingCartLocalServiceUtil.getValueTokens(shoppingOrder);
			NotificationUtil.sendNotification(themeDisplay.getScopeGroupId(), 
					shoppingOrder.getUserName(), shoppingOrder.getShippingEmailAddress(), articleId, tokens, values);
			
			ShoppingItem _shoppingItem = CommonUtil.getShoppingItem(shoppingOrder.getShoppingItemId());
			String notificationContent = "<p><strong>Hi [$USER_NAME$],</strong></p><p>You have recently cancel an order and the details are [$Product_details$]</p>";
			notificationContent = notificationContent.replace("[$USER_NAME$]", shoppingOrder.getUserName());
			notificationContent = notificationContent.replace("[$Product_details$]",_shoppingItem.getProductCode()+" - "+_shoppingItem.getName());
			sendUserNotification(shoppingOrder.getUserId(), notificationContent, actionRequest);
			
			try {
				ShoppingItem shoppingItem = ShoppingItemLocalServiceUtil.fetchShoppingItem(shoppingOrder.getShoppingItemId());
				shoppingItem.setQuantity(shoppingItem.getQuantity() + shoppingOrder.getQuantity());
				ShoppingItemLocalServiceUtil.updateShoppingItem(shoppingItem);
			} catch (SystemException e) {
				_log.error(e.getMessage());
			}			
		}
		
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		String successMessage = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "you-have-requested-to-cancel-the-order-successfully");
		SessionMessages.add(actionRequest, "request_processed", successMessage);
		actionResponse.setRenderParameter(HConstants.JSP_PAGE, HConstants.PAGE_SHOPPING_CART_DETAILS); 
		actionResponse.setRenderParameter("tab1", "my-orders");
	}
	
	private double getWholeSalePrice(int quantity, long itemId, double itemPrice) {
		
		List<WholeSale> wholesaleList = WholeSaleLocalServiceUtil.getWholeSaleByQty(itemId, quantity);
		
		if (Validator.isNotNull(wholesaleList)) {
			for (WholeSale wholeSale: wholesaleList) {
				if (quantity == wholeSale.getQuantity() || quantity >= wholeSale.getQuantity()) {
					itemPrice = wholeSale.getPrice(); 
				}
			}
		}
		
		return itemPrice;
	}
	
	public void sendUserNotification(long userId, String notificationContent, 
			ActionRequest actionRequest) {
		
		ServiceContext serviceContext = null;
		try {
			serviceContext = ServiceContextFactory.getInstance(actionRequest);
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		JSONObject payloadJSON = JSONFactoryUtil.createJSONObject();
		payloadJSON.put("userId", userId);
		payloadJSON.put("notificationContent", notificationContent);
		
		try {
			UserNotificationEventLocalServiceUtil.addUserNotificationEvent(
					userId, UserNotificationHandler.PORTLET_ID,
					(new Date()).getTime(), userId, payloadJSON.toString(), false, serviceContext);
		} catch (Exception e) {
			_log.error("Error while sending the notification -"+e);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(ShoppingCartPortlet.class.getName());
}
