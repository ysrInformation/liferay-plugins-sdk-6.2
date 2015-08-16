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

import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.model.impl.ShoppingOrderImpl;
import com.htmsd.slayer.service.base.ShoppingOrderLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Validator;

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
	public ShoppingOrder insertShoppingOrder(int orderStatus,long userId, long companyId, long groupId,
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
	public void updateShoppingOrder(int orderStatus, long orderId) {
		
		ShoppingOrder shoppingOrder = null;
		try {
			shoppingOrder = shoppingOrderLocalService.fetchShoppingOrder(orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (Validator.isNull(shoppingOrder)) return;
		
		shoppingOrder.setOrderStatus(orderStatus);
		shoppingOrder.setModifiedDate(new Date());
		
		try {
			shoppingOrderLocalService.updateShoppingOrder(shoppingOrder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}