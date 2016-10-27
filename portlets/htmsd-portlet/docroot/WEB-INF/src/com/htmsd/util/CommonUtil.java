package com.htmsd.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.htmsd.slayer.NoSuchCategoryException;
import com.htmsd.slayer.model.Category;
import com.htmsd.slayer.model.Currency;
import com.htmsd.slayer.model.Invoice;
import com.htmsd.slayer.model.ShoppingCart;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingItem_Cart;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.model.ShoppingOrderItem;
import com.htmsd.slayer.model.UserInfo;
import com.htmsd.slayer.service.CategoryLocalServiceUtil;
import com.htmsd.slayer.service.CurrencyLocalServiceUtil;
import com.htmsd.slayer.service.InvoiceLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingCartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.htmsd.slayer.service.UserInfoLocalServiceUtil;
import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.Country;
import com.liferay.portal.model.Phone;
import com.liferay.portal.model.Region;
import com.liferay.portal.model.User;
import com.liferay.portal.service.AddressLocalServiceUtil;
import com.liferay.portal.service.CountryServiceUtil;
import com.liferay.portal.service.RegionServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.service.ClpSerializer;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskInstanceTokenLocalServiceUtil;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.model.AssetVocabulary;
import com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetVocabularyLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFileShortcut;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.portlet.expando.model.ExpandoBridge;

public class CommonUtil {
	
	private final static Log _log = LogFactoryUtil.getLog(CommonUtil.class);
	
	/**
	 * 
	 * @param fileEntryId
	 * @param themeDisplay
	 * @return
	 */
	public static String getThumbnailpath(long fileEntryId,
			long groupId, boolean isThumbnail) {

		_log.info(" get Thumbnail ");

		String thumbnail = StringPool.BLANK;
		FileEntry documentFileEntry = null;
		DLFileShortcut dlFileShortcut = null;

		try {
			documentFileEntry = DLAppLocalServiceUtil.getFileEntry(fileEntryId);
			thumbnail = "/documents/"
					+ documentFileEntry.getGroupId()
					+ StringPool.FORWARD_SLASH
					+ documentFileEntry.getFolderId()
					+ StringPool.FORWARD_SLASH
					+ documentFileEntry.getTitle()
					+ StringPool.FORWARD_SLASH 
					+ documentFileEntry.getUuid();
			if (isThumbnail) {
				//thumbnail+= "&imageThumbnail=1";
			}

		} catch (Exception e) {
			_log.error("Exception occured when getting Thumbnail" + e);
		}
		return thumbnail;
	}
	
	public static List<ShoppingItem> getAllPendingItems(int status) {
		
		List<ShoppingItem> shoppingItems = Collections.emptyList();
		
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(ShoppingItem.class, PortletClassLoaderUtil.getClassLoader());
		dynamicQuery.add(RestrictionsFactoryUtil.eq("status", status));
		try {
			shoppingItems = ShoppingItemLocalServiceUtil.dynamicQuery(dynamicQuery);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return shoppingItems;
	}
	
	public static String getPriceFormat(double price) {
		
		String priceInString = StringPool.BLANK;
		DecimalFormat df = new DecimalFormat("0.00");
		priceInString = HConstants.RUPPEE_SYMBOL+StringPool.PERIOD+ StringPool.SPACE + df.format(price);
		return priceInString;
	}
	 
	public static boolean hasShoppingCart(long userId) {
		
		ShoppingCart shoppingCart = null;
		try {
			shoppingCart = ShoppingCartLocalServiceUtil.getShoppingCartByUserId(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return (Validator.isNotNull(shoppingCart));
	}

	public static ShoppingCart getShoppingCartByUserId(long userId) {
		
		ShoppingCart shoppingCart = null;
		try {
			shoppingCart = ShoppingCartLocalServiceUtil.getShoppingCartByUserId(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return shoppingCart;
	}
	
	public static List<ShoppingItem_Cart> getUserCartItems(long userId) {
		
		ShoppingCart shoppingCart = null;
		try {
			shoppingCart = ShoppingCartLocalServiceUtil.getShoppingCartByUserId(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		List<ShoppingItem_Cart> shoppingItem_Carts = Collections.emptyList();
		if (Validator.isNotNull(shoppingCart)) {
			try {
				shoppingItem_Carts = ShoppingItem_CartLocalServiceUtil.findByCartId(shoppingCart.getCartId());
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		
		return shoppingItem_Carts;
	}
	
	public static ShoppingItem getShoppingItem(long itemId) {
		
		ShoppingItem shoppingItem = null;
		if (itemId > 0) {
			try {
				shoppingItem = ShoppingItemLocalServiceUtil.fetchShoppingItem(itemId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		return shoppingItem;
	}
	
	public static String[] getUserAddress(long userId) {
		
		String[] userAddress = new String[6];

		User user = null;
		Phone phone = null;
		Address address = null;
		List<Phone> phoneList = new ArrayList<Phone>();
		List<Address> addresses = new ArrayList<Address>(); 
		try {
			user = UserLocalServiceUtil.fetchUser(userId);
			if (Validator.isNotNull(user)) 
				addresses = user.getAddresses();
				phoneList = user.getPhones();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (addresses.size() > 0) {
			address = addresses.get(0);
		}
		
		if (phoneList.size() > 0) {
			phone = phoneList.get(0);			
		}
		
		userAddress[0] = (Validator.isNotNull(address))?address.getStreet1():StringPool.BLANK;
		userAddress[1] = (Validator.isNotNull(address))?address.getCity():StringPool.BLANK;
		userAddress[2] = (Validator.isNotNull(address))?address.getZip():StringPool.BLANK;
		userAddress[3] = (Validator.isNotNull(address))?String.valueOf(address.getCountryId()):"0";
		userAddress[4] = (Validator.isNotNull(address))?String.valueOf(address.getRegionId()):"0";
		userAddress[5] = (Validator.isNotNull(phone))? phone.getNumber():StringPool.BLANK;

		return userAddress;
	}
	
	@SuppressWarnings("unchecked")
	public static List<ShoppingOrder> getShoppingOrders(int orderStatus, long groupId) {
		
		List<ShoppingOrder> pendingOrderList = Collections.emptyList();
		DynamicQuery orderQuery = DynamicQueryFactoryUtil.forClass(ShoppingOrder.class);
		orderQuery.add(RestrictionsFactoryUtil.eq("orderStatus", orderStatus));
		orderQuery.add(RestrictionsFactoryUtil.eq("groupId", groupId));
		try {
			pendingOrderList = ShoppingOrderLocalServiceUtil.dynamicQuery(orderQuery);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return pendingOrderList;
	}
	
	public static List<ShoppingOrder> getShoppingOrderByTabNames(long groupId, String tabName) {
		
		List<ShoppingOrder> shoppingOrders = Collections.emptyList();
		boolean isPendingOrderTab = tabName.equalsIgnoreCase("Pending");
		boolean isShippingOrderTab = tabName.equalsIgnoreCase("Shipping");
		boolean isOrderCancelledTab = tabName.equalsIgnoreCase("Order Cancelled");
		
		if (isPendingOrderTab) {
			shoppingOrders = getShoppingOrders(HConstants.PENDING, groupId);
		} else if (isShippingOrderTab) {
			shoppingOrders = getShoppingOrders(HConstants.SHIPPING, groupId);
		} else if(isOrderCancelledTab) { 
			shoppingOrders = getShoppingOrders(HConstants.CANCEL_ORDER, groupId);
		} else {
			shoppingOrders = getShoppingOrders(HConstants.DELIVERED, groupId);
		}
		
		return shoppingOrders;
	}
	
	public static String getCountry(long countryId) {
		
		String countryName = StringPool.BLANK;
		if (countryId >0l) {
			Country country = null;
			try {
				country = CountryServiceUtil.fetchCountry(countryId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			countryName = (Validator.isNotNull(country))?
				TextFormatter.format(country.getName(), TextFormatter.J):StringPool.BLANK;
		}
		
		return countryName;
	}
	
	public static String getState(long regionId) {
		
		String state = StringPool.BLANK;
		if (regionId >0l) {
			Region region = null;
			try {
				region = RegionServiceUtil.getRegion(regionId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			state = (Validator.isNotNull(region))?region.getName():StringPool.BLANK;
		}
		
		return state;
	}
	
	public static List<ShoppingOrder> getMyOrders(long userId) {
		return (ShoppingOrderLocalServiceUtil.getShoppingOrderByUserId(userId));
	}
	
	public static int getItemsCount(long userId, ThemeDisplay themeDisplay, HttpSession session) {
		
		int itemsCount = 0;
		if (themeDisplay.isSignedIn()) {
			try {
				itemsCount = ShoppingItem_CartLocalServiceUtil.getItemsCountByCartId(userId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			itemsCount = (!getGuestUserList(session).isEmpty()) ? getGuestUserList(session).size() : 0;
		}
		return itemsCount;
	}
	
	public static List<ShoppingOrderItem> getShoppingOrderItems(long orderId) {
		
		List<ShoppingOrderItem> shoppingOrderItems = Collections.emptyList();
		try {
			shoppingOrderItems = ShoppingOrderItemLocalServiceUtil.getShoppingOrderItemsByOrderId(orderId);
		} catch (Exception e) {
			_log.error("No order exist  ::"+e);
		}
		return shoppingOrderItems;
	}
	
	public static String getBillNumber(long orderId) {
		
		String billNumber = StringPool.BLANK;
		Invoice invoice = null;
		try {
			invoice = InvoiceLocalServiceUtil.getInvoiceByOrderId(orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		billNumber = (Validator.isNotNull(invoice))?String.valueOf(invoice.getInvoiceId()):"NA";
		return billNumber;
	}
	
	public static String getOrderStatus(int orderStatus) {
		
		String orderStatusString = "N/A";
		
		if (orderStatus == HConstants.PENDING) {
			orderStatusString = HConstants.PROCESSING_STATUS;
		} else  {
			AssetCategory category = getAssetCategoryById(orderStatus);
			if (Validator.isNull(category)) return orderStatusString;
			
			if (category.getName().equals(HConstants.CANCEL_ORDER_STATUS)) {
				orderStatusString = HConstants.ORDER_CANCELLED_STATUS;
			} else {
				orderStatusString = category.getName();
			}
		}
		return orderStatusString;
	}
	
	public static int[] getItemsQuantity(long itemId) {
		
		int remainingQnty = 0;
		long totalStock = 0l;
		int[] quantityArray = null;
		ShoppingItem shoppingItem = CommonUtil.getShoppingItem(itemId);
		
		if (Validator.isNotNull(shoppingItem)) {
			totalStock = shoppingItem.getQuantity();
		}
		
		if (totalStock == -1 ){
			remainingQnty = HConstants.MAX_QUANTITY;
		} else {
			remainingQnty = (int) totalStock;
		}
		
		quantityArray = new int[remainingQnty];
		if (remainingQnty > 0) {
			for (int i=1;i<=(remainingQnty);i++) {
				quantityArray[i-1] = i;
			}
		}
		
		return quantityArray;
	}
	
	public static String getPriceInNumberFormat(double totalPrice, String priceSymbol) {
		
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMinimumFractionDigits(2);
		String formattedPrice = priceSymbol+StringPool.SPACE+numberFormat.format(totalPrice);
		return formattedPrice;
	}
	
	public  static Category getShoppingItemParentCategory(long itemId) {
		Category category = ShoppingItemLocalServiceUtil.getShoppingItemCategory(itemId);
		long parentCategoryId = category.getParentCategoryId();
		Category parenCategory = null;
		try {
			parenCategory = CategoryLocalServiceUtil.fetchCategory(parentCategoryId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return parenCategory;
	}
	
	public static String getCurrencySymbol(long currencyId) {
		Currency currencyRates = getCurrency(currencyId);
		if (Validator.isNull(currencyRates)) return HConstants.RUPEE_SYMBOL;
        java.util.Currency currency = java.util.Currency.getInstance(currencyRates.getCurrencyCode());
        
        return currency.getSymbol();
	}

	private static Currency getCurrency(long currencyId) {
		
		Currency currencyRates = null;
		try {
			currencyRates = CurrencyLocalServiceUtil.fetchCurrency(currencyId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return currencyRates;
	}
	
	public static double getCurrentRate(long currencyId) {
		Currency currencyRates = getCurrency(currencyId);
		if (Validator.isNull(currencyRates)) return 0;
		return currencyRates.getConversion();
	}
	
	public static String getPriceFormat(double price, long currencyId) {
		String priceInString = StringPool.BLANK;
		NumberFormat df = NumberFormat.getInstance();
		df.setMinimumFractionDigits(2); 
		String symbol = getCurrencySymbol(currencyId);
		try {
			java.util.Currency currency = java.util.Currency.getInstance(symbol);
			priceInString = currency.getSymbol() +" "+ df.format(price);
		} catch (IllegalArgumentException e) {
			priceInString = symbol + " " + df.format(price);
		}
		return priceInString;
	}
	
	public static List<ShoppingBean> getSignedInUserItems(long userId){
		
		_log.info("Inside getSignedInUserItems ..."); 
		List<ShoppingBean> shoppingBeans = new ArrayList<ShoppingBean>();
		
		for (ShoppingItem_Cart shIt_cart: getUserCartItems(userId)) {
			ShoppingBean shoppingBean = new ShoppingBean();
			ShoppingItem shoppingItem = getShoppingItem(shIt_cart.getItemId());
			shoppingBean.setCartId(shIt_cart.getCartId());
			shoppingBean.setQuantity(shIt_cart.getQuantity());
			shoppingBean.setTotalPrice(shIt_cart.getTotalPrice()); 
			shoppingBean.setItemId(shIt_cart.getItemId()); 
			shoppingBean.setUnitPrice(shoppingItem.getTotalPrice()); 
			shoppingBean.setProductName(shoppingItem.getName());
			shoppingBean.setImageId(Validator.isNotNull(shoppingItem)? shoppingItem.getSmallImage():0);
			shoppingBean.setCartItemId(shIt_cart.getId());
			shoppingBean.setDescription((Validator.isNotNull(shoppingItem))?shoppingItem.getDescription():StringPool.BLANK); 
			shoppingBean.setProductCode(shoppingItem.getProductCode()); 
			shoppingBeans.add(shoppingBean);
		}
		
		return shoppingBeans;
	}
	
	@SuppressWarnings("unchecked")
	public static void getGuestUserItems(long itemId, HttpSession session) {
		
		_log.info("Inside guestUserItem ...."); 
		
		List<ShoppingBean> newShoppingCartList = new ArrayList<ShoppingBean>();
		List<ShoppingBean> shoppingCartList = getGuestUserList(session);
		
		ShoppingBean shoppingBean = new ShoppingBean();
		ShoppingItem shoppingItem = getShoppingItem(itemId);
		double totalPrice = shoppingItem.getTotalPrice() * HConstants.INITIAL_QUANTITY;
		shoppingBean.setItemId(itemId);
		shoppingBean.setProductCode(shoppingItem.getProductCode());
		shoppingBean.setProductName(shoppingItem.getName());
		shoppingBean.setQuantity(HConstants.INITIAL_QUANTITY);
		shoppingBean.setUnitPrice(shoppingItem.getTotalPrice()); 
		shoppingBean.setTotalPrice(totalPrice);
		shoppingBean.setDescription((Validator.isNotNull(shoppingItem))?shoppingItem.getDescription():StringPool.BLANK); 
		shoppingBean.setImageId(Validator.isNotNull(shoppingItem)? shoppingItem.getSmallImage():0);
		
		if (Validator.isNotNull(shoppingCartList) && shoppingCartList.size() > 0) {
			for (ShoppingBean shoppingcartItem: shoppingCartList) {
				newShoppingCartList.add(shoppingcartItem);
			} 
		}
		newShoppingCartList.add(shoppingBean);
		
		session.removeAttribute("SHOPPING_ITEMS"); 
		session.setAttribute("SHOPPING_ITEMS", newShoppingCartList);
	}
	
	@SuppressWarnings("unchecked")
	public static List<ShoppingBean> getGuestUserList(HttpSession httpsession) {
		
		_log.info("Inside getGuestUserList ..."); 
		List<ShoppingBean> shoppingBeanList = new ArrayList<ShoppingBean>();
		if (Validator.isNotNull(httpsession.getAttribute("SHOPPING_ITEMS"))) {
			shoppingBeanList = (List<ShoppingBean>) httpsession.getAttribute("SHOPPING_ITEMS");
		}
		return shoppingBeanList;
	}
	
	public static List<AssetCategory> getAssetCategoryList(long groupId, String vocabularyName) {
		
		AssetVocabulary assetVocabulary = null;
		List<AssetCategory> assetCategories = new ArrayList<AssetCategory>();
		try {
			assetVocabulary = AssetVocabularyLocalServiceUtil.getGroupVocabulary(groupId, vocabularyName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (Validator.isNull(assetVocabulary)) return assetCategories;
		
		assetCategories = getAssetCategory(assetVocabulary.getVocabularyId());
		
		return assetCategories;
	}
	
	public static List<AssetCategory> getAssetCategory(long vocabularyId) {
		
		_log.info("Inside Asset Category....");
		List<AssetCategory> assetCategories = new ArrayList<AssetCategory>();
		try {
			assetCategories = AssetCategoryLocalServiceUtil.getVocabularyCategories(vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return assetCategories;
	}
	
	public static AssetCategory getAssetCategoryById(long categoryId) {
		AssetCategory category = null;
		try {
			category = AssetCategoryLocalServiceUtil.fetchAssetCategory(categoryId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return category;
	}
	
	public static double calculateVat(double totalPrice, double vatPercentage) {
		double vat = 0.0;
		vat = (totalPrice * vatPercentage) / (100 + vatPercentage);
		return vat;
	}
	
	public static int getGuestUserQuantity(long itemId, HttpSession httpSession) {
		
		int quantity = 0;
		List<ShoppingBean> shoppingItems = getGuestUserList(httpSession);
		if (Validator.isNotNull(shoppingItems) && shoppingItems.size() > 0) {
			for (ShoppingBean shoppingBean:shoppingItems) {
				if (itemId == shoppingBean.getItemId()) {
					quantity += shoppingBean.getQuantity();
				}
			}
		}
		return quantity;
	}
	
	public static boolean isNumeric(String str) {
	    return str.matches("[+-]?\\d*(\\.\\d+)?");
	}
	
	public static String getSellerCompanyDetails(long userId, String attributeName) {
		User user = null;
		try {
			user = UserLocalServiceUtil.fetchUser(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		ExpandoBridge expandoBridge = user.getExpandoBridge();
		String expandoValues = (Validator.isNotNull(expandoBridge)) ? 
				(String) expandoBridge.getAttribute(attributeName) : "N/A";
		return expandoValues;
	}
	
	public static String getUserFullName(long userId) {
		User user = null;
		try {
			user = UserLocalServiceUtil.fetchUser(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return (Validator.isNotNull(user) ? user.getFullName() : StringPool.BLANK);
	}
	
	public static String getAddressBuilder(long regionId, long countryId, String street, String city, String zip) {
		
		StringBuilder sb = new StringBuilder();
		sb.append(street);
		sb.append(StringPool.COMMA_AND_SPACE);
		sb.append(city);
		sb.append(StringPool.COMMA_AND_SPACE);
		sb.append(CommonUtil.getState(regionId)); 
		sb.append(StringPool.SPACE);
		sb.append(CommonUtil.getCountry(countryId)); 
		sb.append(StringPool.COMMA_AND_SPACE);
		sb.append(zip);
		return sb.toString();
	}
	
	public static String toJavaScriptArray(String[] arr){
	    StringBuffer sb = new StringBuffer();
	    sb.append("[");
	    for(int i=0; i<arr.length; i++){
	        sb.append("\"").append(arr[i]).append("\"");
	        if(i+1 < arr.length){
	            sb.append(",");
	        }
	    }
	    sb.append("]");
	    return sb.toString();
	}
	
	public static JSONObject invoker(String URL) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		HttpClient client = new HttpClient();
	    GetMethod method = new GetMethod(URL);
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));
		try {
			int statusCode = client.executeMethod(method);
			byte[] responseBody = method.getResponseBody();
			if (statusCode == HttpStatus.SC_OK) {
				jsonObject = JSONFactoryUtil.createJSONObject(new String(responseBody));
			}
			System.out.println(new String(responseBody));
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		return jsonObject;
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	public static KaleoTaskInstanceToken getKaleoTaskId(long itemId) {
		KaleoTaskInstanceToken kaleoTaskInstanceToken = null;
		try {
			ClassLoader classLoader = (ClassLoader) PortletBeanLocatorUtil.locate(ClpSerializer.getServletContextName(),"portletClassLoader");
			DynamicQuery kaleoQuery = DynamicQueryFactoryUtil.forClass(KaleoTaskInstanceToken.class, classLoader);
			kaleoQuery.add(RestrictionsFactoryUtil.eq("className", ShoppingItem.class.getName()));
			kaleoQuery.add(RestrictionsFactoryUtil.eq("classPK", itemId));
			kaleoQuery.addOrder(new OrderFactoryUtil().desc("createDate"));
			
			List<KaleoTaskInstanceToken> kaleoTaskInstanceTokens = null;
			try {
				kaleoTaskInstanceTokens = KaleoTaskInstanceTokenLocalServiceUtil.dynamicQuery(kaleoQuery);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			
			if (Validator.isNotNull(kaleoTaskInstanceTokens) && kaleoTaskInstanceTokens.size() > 0) 
				kaleoTaskInstanceToken = kaleoTaskInstanceTokens.get(0);
		} catch (RuntimeException e) {
			_log.error(e.getMessage());
		}
		return kaleoTaskInstanceToken;
	}
	
	public static String getWorkflowURL(long ItemId, ThemeDisplay themeDisplay) {
		KaleoTaskInstanceToken kaleoTaskInstanceToken = CommonUtil.getKaleoTaskId(ItemId);
		String itemDetailURL = StringPool.BLANK;
		if (Validator.isNotNull(kaleoTaskInstanceToken)) {
			itemDetailURL = "showReviewPage('"+themeDisplay.getPortalURL() +"/group/control_panel/manage/-/my_workflow_tasks/view/"
				+ kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId()  +"?_153_struts_action=%2Fmy_workflow_tasks%2Fedit_workflow_task"
				+ "&_153_itemId="+ItemId
				+ "&_153_workflowTaskId="+kaleoTaskInstanceToken.getKaleoTaskId()
				+ "&controlPanelCategory=my"
				+ "&_153_redirect=" + URLEncoder.encode(themeDisplay.getURLCurrent()) +"');";
		}
		return itemDetailURL;
	}
	
	public static String getCategoryName(long categoryId) {
		String categoryName = "All Items";
		Category category = null;
		try {
			category = CategoryLocalServiceUtil.fetchCategory(categoryId);
			if (Validator.isNotNull(category)) categoryName = category.getName().toUpperCase();
		} catch (Exception e) {
			_log.error(e.getMessage());
		}
		return categoryName;
	}
	
	public static boolean isUserHasAddress(long userId) throws SystemException {
		boolean isUserAddressNotEmpty = false;
		User user = null;
		try {
			user = UserLocalServiceUtil.getUser(userId);
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		if (Validator.isNotNull(user)) {
			List<Address> addressList = user.getAddresses(); 
			isUserAddressNotEmpty = (Validator.isNotNull(addressList) && addressList.size() > 0);
		}
		return isUserAddressNotEmpty;
	}
	
	public static Address getUsersAddress(long userId) throws SystemException {
		Address address = null;
		List<Address> addressList = new ArrayList<Address>();
		User user = null;
		try {
			user = UserLocalServiceUtil.getUser(userId);
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		if (Validator.isNull(user)) return address;
		addressList = user.getAddresses();
		if (Validator.isNotNull(addressList) && addressList.size() > 0) {
			address = addressList.get(0);
		}
		return address;
	}
	
	public static long getAddressId(long userId) {
		Address address = null;
		try {
			address = getUsersAddress(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return (Validator.isNotNull(address) ? address.getAddressId() : 0);
	}
	
	public static UserInfo findUserInfoByUserId(long userId) {
		return UserInfoLocalServiceUtil.findUserInfoByUserId(userId);
	}
	
	public static String getUserExpandoValue(User user, String attribute) {
		String value = StringPool.BLANK;
		ExpandoBridge expandoBridge = user.getExpandoBridge();
		if (Validator.isNotNull(expandoBridge)) {
			value = (String) expandoBridge.getAttribute(attribute);
		}
		return value;
	}
	
	public static Address getAddressByAddressId(long addressId) {
		Address address = null;
		try {
			address = AddressLocalServiceUtil.fetchAddress(addressId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return address;
	}
	
	public static Map<String, Long> getDeliveryAddresses(long userId, long groupId, long companyId) {
		Map<String, Long> addressmap = new LinkedHashMap<String, Long>();
		List<UserInfo> userInfoList = UserInfoLocalServiceUtil.getUserAddressFromUserInfo(userId, groupId, companyId);
		if (Validator.isNotNull(userInfoList) && userInfoList.size() > 0) {
			for (int i=0;i<userInfoList.size();i++) {
				String key = HConstants.MY_ADDRESS + StringPool.SPACE +(i+1);
				addressmap.put(key, userInfoList.get(i).getUserInfoId());
			}
		}
		return addressmap;
	}
}