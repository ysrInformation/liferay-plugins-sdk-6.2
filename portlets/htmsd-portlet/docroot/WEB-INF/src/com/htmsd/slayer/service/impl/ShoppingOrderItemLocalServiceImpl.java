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
import java.util.Date;
import java.util.List;

import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.model.ShoppingOrderItem;
import com.htmsd.slayer.model.impl.ShoppingOrderItemImpl;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.htmsd.slayer.service.base.ShoppingOrderItemLocalServiceBaseImpl;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * The implementation of the shopping order item local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.ShoppingOrderItemLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.ShoppingOrderItemLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.ShoppingOrderItemLocalServiceUtil
 */
public class ShoppingOrderItemLocalServiceImpl
	extends ShoppingOrderItemLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.ShoppingOrderItemLocalServiceUtil} to access the shopping order item local service.
	 */
	
	public ShoppingOrderItem insertShoppingOrderItem(int quantity, int orderStatus, double totalPrice, long userId, long companyId, 
			long groupId, long orderId, long shoppingItemId, String productName, String description, String productCode) {
		
		ShoppingOrderItem shoppingOrderItem = new ShoppingOrderItemImpl();
		
		long itemId = 0L;
		
		try {
			itemId = counterLocalService.increment();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		shoppingOrderItem = shoppingOrderItemLocalService.createShoppingOrderItem(itemId);
		shoppingOrderItem.setUserId(userId);
		shoppingOrderItem.setCompanyId(companyId);
		shoppingOrderItem.setGroupId(groupId);
		shoppingOrderItem.setName(productName);
		shoppingOrderItem.setOrderStatus(orderStatus); 
		shoppingOrderItem.setProductCode(productCode);
		shoppingOrderItem.setCreateDate(new Date());
		shoppingOrderItem.setOrderId(orderId);
		shoppingOrderItem.setDescription(description);
		shoppingOrderItem.setTotalPrice(totalPrice);
		shoppingOrderItem.setQuantity(quantity);
		shoppingOrderItem.setShoppingItemId(shoppingItemId); 
		
		try {
			shoppingOrderItem = shoppingOrderItemLocalService.addShoppingOrderItem(shoppingOrderItem);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return shoppingOrderItem;
	}
	
	public List<ShoppingOrderItem> getShoppingOrderItemsByOrderId(long orderId) {
		
		List<ShoppingOrderItem> shoppingOrderItems = null;
		try {
			shoppingOrderItems = shoppingOrderItemPersistence.findByOrderId(orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shoppingOrderItems;
	}
	
	public String getUserOrders(long userId) {
		
		String orderIdsArray = "0";
		List<Long> orderIds = new ArrayList<Long>();
		List<ShoppingOrder> shoppingOrders = ShoppingOrderLocalServiceUtil.getShoppingOrderByUserId(userId);
		
		if (Validator.isNull(shoppingOrders) && shoppingOrders.isEmpty()) return orderIdsArray;
		
		for (ShoppingOrder shoppingOrder : shoppingOrders) {
			orderIds.add(shoppingOrder.getOrderId());
		}
		
		return StringUtil.merge(orderIds); 
	}
	
	public List<ShoppingOrderItem> getShoppingOrderItems(int start, int end, long userId) {
		
		List<ShoppingOrderItem> shoppingOrderItems = new ArrayList<ShoppingOrderItem>();
		shoppingOrderItems = shoppingItemFinder.getOrderItemsByUserId(start, end, userId, getUserOrders(userId), true); 
		return shoppingOrderItems;
	}
	
	public int getOrderItemsCount(long userId) {
		List<ShoppingOrderItem> shoppingOrderItems = shoppingItemFinder.getOrderItemsByUserId(0, 0, userId, getUserOrders(userId), false);
		return shoppingOrderItems.size();
	}
	
	public List<ShoppingOrderItem> getShoppingOrderItems(long userId) {
		List<ShoppingOrderItem> shoppingOrderItems = new ArrayList<ShoppingOrderItem>();
		shoppingOrderItems = shoppingItemFinder.getOrderItemsByUserId(0, 0, userId, getUserOrders(userId), false); 
		return shoppingOrderItems;
	}
}