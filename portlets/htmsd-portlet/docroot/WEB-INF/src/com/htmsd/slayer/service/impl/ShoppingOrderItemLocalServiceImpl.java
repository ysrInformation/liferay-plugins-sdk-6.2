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

import java.util.Date;

import com.htmsd.slayer.model.ShoppingOrderItem;
import com.htmsd.slayer.model.impl.ShoppingOrderItemImpl;
import com.htmsd.slayer.service.base.ShoppingOrderItemLocalServiceBaseImpl;

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
	
	
	public ShoppingOrderItem insertShoppingOrderItem(double totalPrice, long userId, long companyId, 
			long groupId, long orderId, String productName, String description, String productCode) {
		
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
		shoppingOrderItem.setProductCode(productCode);
		shoppingOrderItem.setCreateDate(new Date());
		shoppingOrderItem.setOrderId(orderId);
		shoppingOrderItem.setDescription(description);
		shoppingOrderItem.setTotalPrice(totalPrice);
		
		try {
			shoppingOrderItem = shoppingOrderItemLocalService.addShoppingOrderItem(shoppingOrderItem);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return shoppingOrderItem;
	}

}