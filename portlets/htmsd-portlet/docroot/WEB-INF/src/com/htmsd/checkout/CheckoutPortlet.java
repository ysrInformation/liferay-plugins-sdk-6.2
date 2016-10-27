package com.htmsd.checkout;

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
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingItem_Cart;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.model.UserInfo;
import com.htmsd.slayer.model.WholeSale;
import com.htmsd.slayer.service.InvoiceLocalServiceUtil;
import com.htmsd.slayer.service.PurchaseLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingCartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.htmsd.slayer.service.UserInfoLocalServiceUtil;
import com.htmsd.slayer.service.WholeSaleLocalServiceUtil;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.htmsd.util.NotificationUtil;
import com.htmsd.util.ShoppingBean;
import com.itextpdf.text.DocumentException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
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
import com.liferay.portal.model.Address;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.ListType;
import com.liferay.portal.model.User;
import com.liferay.portal.service.AddressLocalServiceUtil;
import com.liferay.portal.service.ListTypeServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class CheckoutPortlet
 */
public class CheckoutPortlet extends MVCPortlet {

	public void confirmOrder(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
		
		_log.info("In confirmOrder Method .."); 
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		
		addShoppingOrderItems(actionRequest); 
		
		String successMessage = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "you-order-has-been-registered-you-may-get-email-shortly");
		SessionMessages.add(actionRequest, "request_processed", successMessage);
		actionResponse.setRenderParameter("jspPage", "/html/checkout/thanks.jsp");
	}
	
	/**
	 * Method for adding items in ShoppingOrderItems
	 * @param themeDisplay
	 * @param shoppingOrder
	 */
	protected void addShoppingOrderItems(ActionRequest actionRequest) {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		PortletSession portletSession = actionRequest.getPortletSession();
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		
		List<ShoppingItem_Cart> shoppingItem_Carts = CommonUtil.getUserCartItems(themeDisplay.getUserId());
		
		Address address = null;
		UserInfo _userInfo = UserInfoLocalServiceUtil.getUserInfoByUserIdAndIsDelivery(themeDisplay.getUserId(), true); 
		if (Validator.isNotNull(_userInfo)) {
			try {
				address = AddressLocalServiceUtil.fetchAddress(_userInfo.getShippingAddressId());
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		
		String paymentMode = ParamUtil.getString(actionRequest, "paymentMethod");
		String shippingFirstName = (Validator.isNotNull(_userInfo) ? _userInfo.getFirstName() :themeDisplay.getUser().getFirstName());
		String shippingLastName = (Validator.isNotNull(_userInfo) ? _userInfo.getLastName() : themeDisplay.getUser().getLastName());
		String shippingEmailAddress = (Validator.isNotNull(_userInfo) ? _userInfo.getEmail() : themeDisplay.getUser().getEmailAddress());
		String shippingMoble = (Validator.isNotNull(_userInfo) ? _userInfo.getMobileNumber() : StringPool.BLANK);
		String shippingAltMoble = (Validator.isNotNull(_userInfo) ? _userInfo.getAltNumber() : StringPool.BLANK);
		String shippingStreet = (Validator.isNotNull(address) ? address.getStreet1() : StringPool.BLANK);
		String shippingCity =  (Validator.isNotNull(address) ? address.getCity() : StringPool.BLANK);
		String shippingZip = (Validator.isNotNull(address) ? address.getZip() : StringPool.BLANK);
		String shippingCountry = (Validator.isNotNull(address) ? String.valueOf(address.getCountryId()) : StringPool.BLANK);
		String shippingState = (Validator.isNotNull(address) ? String.valueOf(address.getRegionId()) : StringPool.BLANK);
		
		if (Validator.isNotNull(shoppingItem_Carts) && shoppingItem_Carts.size() > 0) {
			for (ShoppingItem_Cart shoppingItem_Cart:shoppingItem_Carts) {
				
				ShoppingItem shoppingItem = CommonUtil.getShoppingItem(shoppingItem_Cart.getItemId()); 

				if (Validator.isNull(shoppingItem)) continue;
					
				int orderQuantity = shoppingItem_Cart.getQuantity();
				long sellerId = shoppingItem.getUserId();
				long shoppingItemId = shoppingItem.getItemId();
				double totalPrice = shoppingItem_Cart.getTotalPrice();
				String sellerName = CommonUtil.getUserFullName(sellerId);  
				
				addItemToShoppingorder(actionRequest, themeDisplay, portletSession, portletConfig, shippingFirstName, 
						shippingLastName, shippingEmailAddress, shippingMoble, shippingAltMoble, shippingStreet, shippingCity,
						shippingZip, shippingCountry, shippingState, shoppingItem_Cart, shoppingItem, HConstants.PENDING, orderQuantity, themeDisplay.getUserId(), 
						themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId(), sellerId, shoppingItemId, totalPrice, sellerName, HConstants.ORDER_COMFIRMATION, StringPool.BLANK, paymentMode);
			}
		} 
	}

	protected void addItemToShoppingorder(ActionRequest actionRequest, ThemeDisplay themeDisplay,
			PortletSession portletSession, PortletConfig portletConfig, String shippingFirstName,
			String shippingLastName, String shippingEmailAddress, String shippingMoble, String shippingAltMoble,
			String shippingStreet, String shippingCity, String shippingZip, String shippingCountry,
			String shippingState, ShoppingItem_Cart shoppingItem_Cart, ShoppingItem shoppingItem, int orderStatus,
			int orderQuantity, long userId, long groupId, long companyId, long sellerId, long shoppingItemId,
			double totalPrice, String sellerName, String articleId, String emailAddress, String paymentMode) {
		
		ShoppingOrder shoppingOrder = ShoppingOrderLocalServiceUtil.insertShoppingOrder(orderStatus, orderQuantity, 
				shoppingItemId, sellerId, userId, companyId, groupId, totalPrice, sellerName, StringPool.BLANK, shippingFirstName, 
				shippingLastName, shippingStreet, shippingCity, shippingZip, shippingEmailAddress, shippingState,
				shippingCountry, shippingMoble, shippingAltMoble);
		
		InvoiceLocalServiceUtil.insertInvoice(themeDisplay.getUserId(), shoppingOrder.getOrderId());
		
		//updating shoppingItem quantity
		if (Validator.isNotNull(shoppingOrder)) {
			try {
				long quantity = shoppingItem.getQuantity() - shoppingItem_Cart.getQuantity();
				ShoppingItem shoppingItem2 = ShoppingItemLocalServiceUtil.fetchShoppingItem(shoppingOrder.getShoppingItemId());
				shoppingItem2.setQuantity(quantity);
				ShoppingItemLocalServiceUtil.updateShoppingItem(shoppingItem2);
			} catch (Exception e) {
				_log.error(e.getMessage()); 
			}
			
			//update purchase table
			String currentCurrencyid = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
			long currencyId = (Validator.isNull(currentCurrencyid)) ?  0 : Long.valueOf(currentCurrencyid);
			try {
				PurchaseLocalServiceUtil.insertPurchaseDetails(userId, groupId, companyId, currencyId, shoppingOrder.getOrderId(), totalPrice, 
						paymentMode, HConstants.PAYMENT_STATUS);
			} catch (SystemException e) {
				_log.error(e.getMessage()); 
			}
			
			String[] tokens = ShoppingCartLocalServiceUtil.getOrderTokens();
			String[] values = ShoppingCartLocalServiceUtil.getValueTokens(shoppingOrder);
			NotificationUtil.sendNotification(themeDisplay.getScopeGroupId(), shoppingOrder.getUserName(),
					shoppingOrder.getShippingEmailAddress(), articleId, tokens, values); 
			
			String notificationContent = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "order-confirmed-notification-message");
			notificationContent = notificationContent.replace("[$USER_NAME$]", shoppingOrder.getUserName());
			notificationContent = notificationContent.replace("[$Product_details$]", CommonUtil.getShoppingItem(shoppingOrder.getShoppingItemId()).getName());
			NotificationUtil.sendUserNotification(userId, notificationContent, actionRequest);

			//send receipt to seller
			sendInvoiceToSeller(actionRequest, portletSession, groupId, companyId, sellerId, emailAddress, shoppingOrder);
		}
		
		//deleting items from shoppingItem_Cart table. 
		try {
			ShoppingItem_CartLocalServiceUtil.deleteShoppingItem_Cart(shoppingItem_Cart.getId());
		} catch (PortalException e) {
			_log.error(e.getMessage()); 
		} catch (SystemException e) {
			_log.error(e.getMessage()); 
		}
	}

	protected void sendInvoiceToSeller(ActionRequest actionRequest, PortletSession portletSession, 
			long groupId, long companyId, long sellerId, String emailAddress, ShoppingOrder shoppingOrder) {
		
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
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
			_log.info("email not found :"+e);
		}
		
		if (!emailAddress.isEmpty()) {
			ShoppingItem _shoppingItem = CommonUtil.getShoppingItem(shoppingOrder.getShoppingItemId());
			NotificationUtil.sendReceipt(groupId, emailAddress, "SEND_INVOICE", shoppingOrder.getUserName(),
					GenerateInvoice.getFilePath(fileName), fileName, new String[]{"[$USER$]"}, new String[]{shoppingOrder.getUserName()});
			String notificationContent = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "invoice-sent-notification-message");
			notificationContent = notificationContent.replace("[$USER_NAME$]", _shoppingItem.getUserName());
			notificationContent = notificationContent.replace("[$Product_details$]",_shoppingItem.getName());
			NotificationUtil.sendUserNotification(sellerId, notificationContent, actionRequest);
		} else {
			_log.info("Email of seller not found.."); 
		}
	}
	
	/***
	 * Method for saving addresses
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 * @throws PortletException
	 * @throws PortalException
	 * @throws SystemException
	 */
	public void updateUserInfo(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException, PortalException, SystemException {
		
		_log.info("In updateUserInfo method.."); 
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		ServiceContext serviceContext = getServiceContext(actionRequest);
		
		long userId = themeDisplay.getUserId();
		long userInfoId = ParamUtil.getLong(actionRequest, "userInfoId");
		long addressId = ParamUtil.getLong(actionRequest, "addressId");
		long shippingCountry = ParamUtil.getLong(actionRequest, "country");
		long shippingState = ParamUtil.getLong(actionRequest, "state");
		
		int typeId = ParamUtil.getInteger(actionRequest, "typeId");
		String firstName = ParamUtil.getString(actionRequest, "firstName");
		String lastName = ParamUtil.getString(actionRequest, "lastName");
		String email = ParamUtil.getString(actionRequest, "email");
		String shippingMoble = ParamUtil.getString(actionRequest, "mobileNumber");
		String shippingAltMoble = ParamUtil.getString(actionRequest, "altNumber");
		String shippingStreet = ParamUtil.getString(actionRequest, "street");
		String shippingCity = ParamUtil.getString(actionRequest, "city");
		String shippingZip = ParamUtil.getString(actionRequest, "zip");
		_log.info("typeId ::"+typeId); 
		
		Address address = null;
		if (addressId > 0) {
			address = AddressLocalServiceUtil.fetchAddress(addressId);
			address.setCountryId(shippingCountry); 
			address.setRegionId(shippingState);
			address.setStreet1(shippingStreet);
			address.setZip(shippingZip);
			address.setCity(shippingCity); 
			address.setTypeId(typeId); 
			AddressLocalServiceUtil.updateAddress(address);
		} else {
			//int typeId = getListTypeId(Contact.class.getName() + ListTypeConstants.ADDRESS, type);
			try {
				address = AddressLocalServiceUtil.addAddress(themeDisplay.getUserId(), Contact.class.getName(), 
						themeDisplay.getContact().getContactId(), shippingStreet, StringPool.BLANK, StringPool.BLANK,
						shippingCity, shippingZip, shippingState, shippingCountry, typeId, false, true, serviceContext);
			} catch (PortalException e) {
				_log.error(e);
			} catch (SystemException e) {
				_log.error(e);
			}
		}
		
		UserInfo userInfo = UserInfoLocalServiceUtil.addUserInfo(userInfoId, userId, themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId(), address.getAddressId(), 0l, 
				shippingMoble, shippingAltMoble, firstName, lastName, email, false);
		
		_log.info("updated users address and userinfo table successfully, the userinfoId is :"+userInfo.getUserInfoId()); 
		
		String successMessage = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "your-address-saved-successfully");
		SessionMessages.add(actionRequest, "request_processed", successMessage);
		actionResponse.setRenderParameter("jspPage", "/html/checkout/view.jsp");
		actionResponse.setRenderParameter("order_step", "step3");
	}
	
	public void setDeliveryAddress(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException, SystemException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		
		long userInfoId = ParamUtil.getLong(actionRequest, "userInfoId");
		if (userInfoId > 0) {
			List<UserInfo> userInfoList = UserInfoLocalServiceUtil.getUserInfoByUserId(themeDisplay.getUserId());
			if (Validator.isNotNull(userInfoList) && userInfoList.size() > 0) {
				for (UserInfo userInfo:userInfoList) {
					if (userInfo.getIsDeliveryAddress() == true) {
						_log.info("Inside delivery address!"); 
						userInfo.setIsDeliveryAddress(false);
						UserInfoLocalServiceUtil.updateUserInfo(userInfo);
					}
				}
			}
			
			UserInfo userInfo = UserInfoLocalServiceUtil.fetchUserInfo(userInfoId);
			userInfo.setIsDeliveryAddress(true); 
			UserInfoLocalServiceUtil.updateUserInfo(userInfo);
		}
		
		String successMessage = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "your-delivery-address-set-successfully");
		SessionMessages.add(actionRequest, "request_processed", successMessage);
		actionResponse.setRenderParameter("jspPage", "/html/checkout/view.jsp");
		actionResponse.setRenderParameter("order_step", "step5");
	}

	private ServiceContext getServiceContext(ActionRequest actionRequest) {
		ServiceContext serviceContext = null;
		try {
			serviceContext = ServiceContextFactory.getInstance(actionRequest);
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
		return serviceContext;
	}
	
	public int getListTypeId(String type, String name) throws SystemException {
		int typeId = 0;
		List<ListType> listTypes = ListTypeServiceUtil.getListTypes(type);
		if (Validator.isNotNull(listTypes)) {
			for (ListType listType : listTypes) {
				if (listType.getName().equals(name)) {
					typeId = listType.getListTypeId();
					break;
				}
			}
		}
		return typeId;
	}
	
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws IOException, PortletException {
		String cmd = ParamUtil.getString(resourceRequest, Constants.CMD);
		if (Validator.isNotNull(cmd)) {
			if (cmd.equalsIgnoreCase("remove-item")){
				removeItem(resourceRequest, resourceResponse);
			} else if (cmd.equalsIgnoreCase("total-price")) {
				updateItemPrice(resourceRequest);
			}
		}
	}
 
	/***
	 * update Item price 
	 **/
	private void updateItemPrice(ResourceRequest resourceRequest) {
		
		int quantity = ParamUtil.getInteger(resourceRequest, "quantity");
		double itemPrice = ParamUtil.getDouble(resourceRequest, "itemPrice");
		long id = ParamUtil.getLong(resourceRequest, "id");
		long itemId = ParamUtil.getLong(resourceRequest, "itemId");
		
		HttpSession session = PortalUtil.getHttpServletRequest(resourceRequest).getSession();
		List<ShoppingBean> shoppingCartList = CommonUtil.getGuestUserList(session);
		List<ShoppingBean> newShoppingCartList = new ArrayList<ShoppingBean>();
		double totalPrice = getWholeSalePrice(quantity, itemId, itemPrice);
				
		if (id > 0) {
			ShoppingItem_CartLocalServiceUtil.updateShoppingItem_Cart(id, quantity, totalPrice);
		} else {
			if (Validator.isNotNull(shoppingCartList) && shoppingCartList.size() > 0) {
				for (ShoppingBean shoppingBean:shoppingCartList) {
					if (itemId == shoppingBean.getItemId()) {
						shoppingBean.setQuantity(quantity); 
						shoppingBean.setTotalPrice(totalPrice);
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
	
	private double getWholeSalePrice(int quantity, long itemId, double itemPrice) {
		
		double totalPrice = 0.0;
		List<WholeSale> wholesaleList = WholeSaleLocalServiceUtil.getWholeSaleByQty(itemId, quantity);
		if (Validator.isNotNull(wholesaleList)) {
			totalPrice = quantity * itemPrice;
			for (WholeSale wholeSale: wholesaleList) {
				if (quantity == wholeSale.getQuantity()) {
					totalPrice = wholeSale.getPrice(); 
				} else if (quantity > wholeSale.getQuantity()) {
					totalPrice = quantity * (wholeSale.getPrice() / wholeSale.getQuantity());
				}
			}
		} else {
			totalPrice = quantity * itemPrice;
		}
		
		return totalPrice;
	}
	
	private static final Log _log = LogFactoryUtil.getLog(CheckoutPortlet.class.getName());

}
