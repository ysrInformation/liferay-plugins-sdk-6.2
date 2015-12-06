/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.htmsd.slayer.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.model.ShoppingOrderItem;
import com.htmsd.slayer.model.impl.ShoppingOrderImpl;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.htmsd.slayer.service.base.ShoppingOrderLocalServiceBaseImpl;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Junction;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil;

/**
 * The implementation of the shopping order local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.ShoppingOrderLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.ShoppingOrderLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil
 */
public class ShoppingOrderLocalServiceImpl
	extends ShoppingOrderLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil} to access the shopping order local service.
	 */
	public ShoppingOrder insertShoppingOrder(int orderStatus, int quantity, long shoppingItemId, long sellerId, 
			long userId, long companyId, long groupId, double totalPrice, String sellerName, String cancelReason,
			String shippingFirstName, String shippingLastName, String shippingStreet, String shippingCity,
			String shippingZip, String shippingEmailAddress, String shippingState, String shippingCountry,
			String shippingMoble, String shippingAltMoble){
		
		ShoppingOrder shoppingOrder = new ShoppingOrderImpl();
		long orderId = 0L;
		
		try {
			orderId = counterLocalService.increment();
		} catch(Exception e){
			e.printStackTrace();
		}
		shoppingOrder = shoppingOrderLocalService.createShoppingOrder(orderId);
		
		shoppingOrder.setUserId(userId);
		shoppingOrder.setCompanyId(companyId);
		shoppingOrder.setGroupId(groupId);
		shoppingOrder.setCreateDate(new Date());
		shoppingOrder.setOrderStatus(orderStatus);
		shoppingOrder.setShippingFirstName(shippingFirstName);
		shoppingOrder.setShippingLastName(shippingLastName);
		shoppingOrder.setShippingStreet(shippingStreet);
		shoppingOrder.setShippingCity(shippingCity);
		shoppingOrder.setShippingZip(shippingZip);
		shoppingOrder.setShippingEmailAddress(shippingEmailAddress);
		shoppingOrder.setShippingState(shippingState);
		shoppingOrder.setShippingCountry(shippingCountry);
		shoppingOrder.setShippingMoble(shippingMoble);
		shoppingOrder.setShippingAltMoble(shippingAltMoble);
		
		shoppingOrder.setShoppingItemId(shoppingItemId);
		shoppingOrder.setCancelReason(cancelReason);
		shoppingOrder.setQuantity(quantity);
		shoppingOrder.setTotalPrice(totalPrice);
		shoppingOrder.setSellerId(sellerId);
		shoppingOrder.setSellerName(sellerName);
		shoppingOrder.setUserName(shippingFirstName+StringPool.SPACE+shippingLastName); 
		
		try {
			shoppingOrder = shoppingOrderLocalService.addShoppingOrder(shoppingOrder);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return shoppingOrder;
	}
	
	/**
	 * Method for updating order status
	 * @param orderStatus
	 * @param orderId
	 */
	public void updateShoppingOrder(int orderStatus, long orderId, String cancelReason) {
		
		ShoppingOrder shoppingOrder = null;
		try {
			shoppingOrder = shoppingOrderLocalService.fetchShoppingOrder(orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (Validator.isNull(shoppingOrder)) return;
		
		shoppingOrder.setOrderStatus(orderStatus);
		shoppingOrder.setModifiedDate(new Date());
		shoppingOrder.setCancelReason(cancelReason);
		
		try {
			shoppingOrderLocalService.updateShoppingOrder(shoppingOrder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateShoppingOrderItem(int orderStatus, long orderItemId) {
		
		ShoppingOrderItem shoppingOrderItem = null;
		try {
			shoppingOrderItem = shoppingOrderItemLocalService.fetchShoppingOrderItem(orderItemId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (Validator.isNull(shoppingOrderItem)) return;
		
		shoppingOrderItem.setOrderStatus(orderStatus); 
		shoppingOrderItem.setModifiedDate(new Date()); 
		
		try {
			shoppingOrderItemLocalService.updateShoppingOrderItem(shoppingOrderItem);
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	
	public List<ShoppingOrder> getShoppingOrderByUserId(long userId){
		List<ShoppingOrder> shoppingOrders = null;
		try {
			shoppingOrders = shoppingOrderPersistence.findByUserId(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shoppingOrders;
	}
	
	public List<ShoppingOrder> getShoppingOrderByTabNames(int start, int end, String tabName) {
		
		List<ShoppingOrder> shoppingOrders = new ArrayList<ShoppingOrder>();
		boolean isPendingOrderTab = tabName.equalsIgnoreCase("Pending");
		boolean isShippingOrderTab = tabName.equalsIgnoreCase("Shipping");
		boolean isOrderCancelledTab = tabName.equalsIgnoreCase("Order Cancelled");
		
		if (isPendingOrderTab) {
			shoppingOrders = getShoppingOrders(HConstants.PENDING, start, end);
		} else if (isShippingOrderTab) {
			shoppingOrders = getShoppingOrders(getOrderStatusByTabName("Shipping"), start, end); 
		} else if(isOrderCancelledTab) { 
			shoppingOrders = getShoppingOrders(getOrderStatusByTabName("Order Cancelled"), start, end);
		} else {
			shoppingOrders = getShoppingOrders(getOrderStatusByTabName("Delivered"), start, end);
		}
		
		return shoppingOrders;
	}
	
	public List<ShoppingOrder> getShoppingOrders(int orderStatus, int start, int end) {
		
		List<ShoppingOrder> pendingOrderList = new ArrayList<ShoppingOrder>();

		try {
			pendingOrderList = shoppingOrderPersistence.findByOrderStatus(orderStatus, start, end);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return pendingOrderList;
	}
	
	public int getCountByOrderStatus(int orderStatus) {
		
		int count = 0;
		try {
			shoppingOrderPersistence.countByOrderStatus(orderStatus);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public  List<ShoppingOrder> searchItems(String keyword, String tabName, int start, int end) {
		
		System.out.println("Searched Method  >>>>>>>>>> Keyword is ::"+keyword); 
		String likeKeyword = StringUtil.quote(keyword, StringPool.PERCENT);
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(ShoppingOrder.class);
		Junction junctionOR = RestrictionsFactoryUtil.disjunction();
		
		Property property = PropertyFactoryUtil.forName("sellerName");
		junctionOR.add(property.like(likeKeyword));
		
		property = PropertyFactoryUtil.forName("userName");
		junctionOR.add(property.like(likeKeyword));
		
		if (CommonUtil.isNumeric(keyword)){
			property = PropertyFactoryUtil.forName("quantity");
			junctionOR.add(property.eq(Integer.valueOf(keyword))); 
		}
		
		if (keyword.startsWith("HT")) {
			String orderId = keyword.substring(7, keyword.length());
			System.out.println("Order ItemId is ==>"+Long.valueOf(orderId)); 
			junctionOR.add(RestrictionsFactoryUtil.eq("orderId", Long.valueOf(orderId)));
		}
		
		if (keyword.contains("-")) {
			Criterion criterion = PropertyFactoryUtil.forName("createDate").between(
					getFromDateAndToDate(0, 0, 0, keyword), 
					getFromDateAndToDate(23, 59, 59, keyword));
			junctionOR.add(criterion);
		}
		
		dynamicQuery.add(junctionOR);
		
		dynamicQuery.add(RestrictionsFactoryUtil.eq("orderStatus", getOrderStatusByTabName(tabName)));
		
		List<ShoppingOrder> searchList = new ArrayList<ShoppingOrder>();
		try {
			searchList =  ShoppingOrderLocalServiceUtil.dynamicQuery(dynamicQuery, start, end);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return searchList;
	}
	
	public int searchCount(String tabName, String keyword) {
		
		System.out.println("searchCount Method <:::> Keyword is ::"+keyword); 
		String likeKeyword = StringUtil.quote(keyword, StringPool.PERCENT);
		
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(ShoppingOrder.class);
		Junction junctionOR = RestrictionsFactoryUtil.disjunction();
		
		Property property = PropertyFactoryUtil.forName("sellerName");
		junctionOR.add(property.like(likeKeyword));
		
		property = PropertyFactoryUtil.forName("userName");
		junctionOR.add(property.like(likeKeyword));
		
		if (CommonUtil.isNumeric(keyword)){
			property = PropertyFactoryUtil.forName("quantity");
			junctionOR.add(property.eq(Integer.valueOf(keyword)));  
		}
		
		if (keyword.startsWith("HT")) {
			String orderId = keyword.substring(7, keyword.length());
			junctionOR.add(RestrictionsFactoryUtil.eq("orderId", Long.valueOf(orderId)));
		}
		
		if (keyword.contains("-")) {
			Criterion criterion = PropertyFactoryUtil.forName("createDate").between(
					getFromDateAndToDate(0, 0, 0, keyword), 
					getFromDateAndToDate(23, 59, 59, keyword));
			junctionOR.add(criterion);
		}
		
		dynamicQuery.add(junctionOR);
		
		dynamicQuery.add(RestrictionsFactoryUtil.eq("orderStatus", getOrderStatusByTabName(tabName)));

		int searchCount = 0;
		try {
			searchCount = (int) ShoppingOrderLocalServiceUtil.dynamicQueryCount(dynamicQuery);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return searchCount;
	}
	
	public int getOrderStatusByTabName(String tabName) {
		
		int orderStatus = 0;
		if (Validator.isNotNull(tabName)) {
			if (tabName.equalsIgnoreCase("Pending")) {
				orderStatus = HConstants.PENDING;
			} else if (tabName.equalsIgnoreCase("Shipping")) {
				orderStatus = (int) getAssetCategoryIdByName(HConstants.SHIPPING_STATUS);
			} else if (tabName.equalsIgnoreCase("Order Cancelled")) {
				orderStatus = (int) getAssetCategoryIdByName(HConstants.CANCEL_ORDER_STATUS);
			} else {
				orderStatus = (int) getAssetCategoryIdByName(HConstants.DELIVERED_STATUS);
			}
		}
		return orderStatus;
	}
	
	public List<ShoppingOrderItem> getOrderItems(int orderStatus, int start, int end) {
		
		List<ShoppingOrderItem> shoppingOrderItems = null;
		try {
			shoppingOrderItems = shoppingOrderItemPersistence.findByOrderStatus(orderStatus, start, end);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return shoppingOrderItems;
	}
	
	public int getOrderItemCount(int orderStatus) {
		
		int count = 0;
		try {
			count = shoppingOrderItemLocalService.getShoppingOrderItemsCount();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public long getAssetCategoryIdByName(String name) {
		
		long categoryId = 0;
		List<AssetCategory> assetCategories = null;
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(AssetCategory.class);
		dynamicQuery.add(PropertyFactoryUtil.forName("name").eq(name));
		try {
			assetCategories = AssetCategoryLocalServiceUtil.dynamicQuery(dynamicQuery);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (Validator.isNotNull(assetCategories) && assetCategories.size() > 0) {
			categoryId = assetCategories.get(0).getCategoryId();
		}
		
		return categoryId;
	}
	
	private static Date getFromDateAndToDate(int hours, int minutes, int seconds, String date) {
		
		Date returnDate = null; 
		String[] dateArray = date.split("-");
		if (Validator.isNotNull(date)) {
			int day = Integer.valueOf(dateArray[0]);
			int month = Integer.valueOf(dateArray[1])-1;
			int year = Integer.valueOf(dateArray[2]);
			Calendar cal = Calendar.getInstance();
			cal.set(year, month, day, hours, minutes, seconds); 
			returnDate = cal.getTime();
		}

		return returnDate;
	}
	
	public List<ShoppingOrder> getItemsByItemId_OrderStatus(int orderStatus, long itemId) throws SystemException{
		return shoppingOrderPersistence.findByItemId_OrderStatus(itemId, orderStatus);
	}
	
	public int getTotalItemsSold(int orderStatus, long itemId) {
		
		List<ShoppingOrder> shoppingOrders = new ArrayList<ShoppingOrder>();
		try {
			shoppingOrders = getItemsByItemId_OrderStatus(orderStatus, itemId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return (Validator.isNotNull(shoppingOrders) ? shoppingOrders.size():0); 
	}
	
	public List<ShoppingOrder> getShoppingOrderByUserId(int start, int end, long userId) {
		List<ShoppingOrder> shoppingOrders = new ArrayList<ShoppingOrder>();
		try {
			shoppingOrders = shoppingOrderPersistence.findByUserId(userId, start, end);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return shoppingOrders;
	}
	
	public int getItemsCountByUserId(long userId) {
		int count = 0;
		try {
			count = shoppingOrderPersistence.countByUserId(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return (Validator.isNotNull(count)? count : 0); 
	}
}