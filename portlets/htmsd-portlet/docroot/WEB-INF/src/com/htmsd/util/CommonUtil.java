package com.htmsd.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.text.NumberFormatter;

import com.htmsd.slayer.NoSuchShoppingItem_CartException;
import com.htmsd.slayer.model.Category;
import com.htmsd.slayer.model.Invoice;
import com.htmsd.slayer.model.ShoppingCart;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingItem_Cart;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.model.ShoppingOrderItem;
import com.htmsd.slayer.service.CategoryLocalServiceUtil;
import com.htmsd.slayer.service.InvoiceLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingCartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.Country;
import com.liferay.portal.model.Phone;
import com.liferay.portal.model.Region;
import com.liferay.portal.model.User;
import com.liferay.portal.service.CountryServiceUtil;
import com.liferay.portal.service.RegionServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFileShortcut;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;

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
		FileEntry fileEntry = null;
		DLFileShortcut dlFileShortcut = null;

		try {
			fileEntry = DLAppLocalServiceUtil.getFileEntry(fileEntryId);
			thumbnail = "/documents/"
					+ groupId
					+ StringPool.SLASH
					+ fileEntry .getFolderId()
					+ StringPool.SLASH
					+ HttpUtil.encodeURL(HtmlUtil.unescape(String.valueOf(fileEntry .getTitle())))
					+ StringPool.SLASH
					+ fileEntry.getUuid()
					+ "?version=" + fileEntry .getVersion()
					+ "&t=" + fileEntry.getFileVersion().getModifiedDate().getTime();
			if (isThumbnail) {
				thumbnail+= "&imageThumbnail=1";
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
		List<Phone> phoneList = Collections.emptyList();
		List<Address> addresses = Collections.emptyList();
		try {
			user = UserLocalServiceUtil.fetchUser(userId);
			if (Validator.isNotNull(user)) 
				addresses = user.getAddresses();
				phoneList = user.getPhones();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (addresses.size() > 0 && phoneList.size() > 0) {
			phone = phoneList.get(0);
			address = addresses.get(0);
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
	
	public static int getItemsCount(long userId) {
		
		int itemsCount = 0;
		try {
			itemsCount = ShoppingItem_CartLocalServiceUtil.getItemsCountByCartId(userId);
		} catch (Exception e) {
			e.printStackTrace();
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
		
		String orderStatusString = StringPool.BLANK;
		switch(orderStatus) {
			case HConstants.PENDING : orderStatusString = HConstants.PROCESSING_STATUS;
										break;
			case HConstants.SHIPPING : orderStatusString = HConstants.SHIPPING_STATUS;
										break;
			case HConstants.DELIVERED : orderStatusString = HConstants.DELIVERED_STATUS;
										break;
			case HConstants.CANCEL_ORDER : orderStatusString = HConstants.ORDER_CANCELLED_STATUS;
										break;
			default : orderStatusString = "N/A";
		}
		return orderStatusString;
	}
	
	public static int[] getItemsQuantity(long itemId, long cartId) {
		
		int allUserQuantity = 0;
		int remainingQnty = 0;
		int userQnty = 0;
		long totalStock = 0l;
		int[] quantityArray = null;
		ShoppingItem_Cart shoppingItem_Cart = null;
		ShoppingItem shoppingItem = CommonUtil.getShoppingItem(itemId);
		List<ShoppingItem_Cart> shoppingItem_Carts = new ArrayList<ShoppingItem_Cart>();
		
		if (Validator.isNotNull(shoppingItem)) {
			totalStock = shoppingItem.getQuantity();
			try {
				shoppingItem_Carts = ShoppingItem_CartLocalServiceUtil.getShoppingItemByItemId(itemId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			try {
				shoppingItem_Cart = ShoppingItem_CartLocalServiceUtil.findByCartAndItemId(cartId, itemId);
			} catch (NoSuchShoppingItem_CartException e) {
				_log.error("No such Item exist with the cartId"+e); 
			} catch (SystemException e) {
				e.printStackTrace();
			}
			
			if (Validator.isNotNull(shoppingItem_Carts) && shoppingItem_Carts.size() > 0) {
				for (ShoppingItem_Cart shoppingItem_cart : shoppingItem_Carts) {
					allUserQuantity += shoppingItem_cart.getQuantity();
				}
			}
			userQnty = (Validator.isNotNull(shoppingItem_Cart) && shoppingItem_Cart.getQuantity() > 0) ? 
					shoppingItem_Cart.getQuantity()  : 0;
		}
		
		if ((allUserQuantity) < totalStock) {
			remainingQnty = ((int) totalStock - allUserQuantity) + userQnty;
		} else if(totalStock == -1) {
			remainingQnty = HConstants.MAX_QUANTITY + userQnty;
		} else {
			remainingQnty = userQnty;
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
}