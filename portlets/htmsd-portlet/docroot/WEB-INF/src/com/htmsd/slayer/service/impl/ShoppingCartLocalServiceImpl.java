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

import com.htmsd.slayer.model.ShoppingCart;
import com.htmsd.slayer.service.base.ShoppingCartLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Validator;

/**
 * The implementation of the shopping cart local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.ShoppingCartLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.ShoppingCartLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.ShoppingCartLocalServiceUtil
 */
public class ShoppingCartLocalServiceImpl
	extends ShoppingCartLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.ShoppingCartLocalServiceUtil} to access the shopping cart local service.
	 */
	public ShoppingCart insertShoppingCart(long userId, long companyId, long groupId, String Username) {
		
		long cartId = 0L;
		ShoppingCart shoppingCart = null;
		try {
			shoppingCart = getShoppingCartByUserId(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}

		if(Validator.isNotNull(shoppingCart)) return shoppingCart;
		
		try {
			cartId = counterLocalService.increment();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		shoppingCart = shoppingCartLocalService.createShoppingCart(cartId);
		shoppingCart.setCompanyId(companyId);
		shoppingCart.setCreateDate(new Date());
		shoppingCart.setUserId(userId);
		shoppingCart.setGroupId(groupId);
		shoppingCart.setUserName(Username); 
		
		try {
			shoppingCart = shoppingCartLocalService.addShoppingCart(shoppingCart);
		} catch (SystemException e) {
			e.printStackTrace();
		}

		return shoppingCart;
	}
	
	public ShoppingCart getShoppingCartByUserId(long userId) throws SystemException {
		return shoppingCartPersistence.fetchByUserId(userId);
	}
}