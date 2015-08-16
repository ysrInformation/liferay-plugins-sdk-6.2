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

import java.util.List;

import com.htmsd.slayer.NoSuchShoppingItem_CartException;
import com.htmsd.slayer.model.ShoppingItem_Cart;
import com.htmsd.slayer.service.base.ShoppingItem_CartLocalServiceBaseImpl;
import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * The implementation of the shopping item_ cart local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.ShoppingItem_CartLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.ShoppingItem_CartLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil
 */
public class ShoppingItem_CartLocalServiceImpl
	extends ShoppingItem_CartLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil} to access the shopping item_ cart local service.
	 */
	
	/**
	 * 
	 * @param cartId
	 * @param itemId
	 * @return
	 */
	public ShoppingItem_Cart insertItemsToCart(long cartId, long itemId) {
		ShoppingItem_Cart shoppingItem_Cart = null;
		
		try {
			shoppingItem_Cart = createShoppingItem_Cart(CounterLocalServiceUtil.increment());
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		shoppingItem_Cart.setCartId(cartId);
		shoppingItem_Cart.setItemId(itemId);
		
		try {
			addShoppingItem_Cart(shoppingItem_Cart);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return shoppingItem_Cart;
	}
	
	/**
	 * 
	 * @param itemId
	 * @return
	 * @throws SystemException
	 * @throws NoSuchShoppingItem_CartException
	 */
	public ShoppingItem_Cart findByItemId(long itemId) throws SystemException, NoSuchShoppingItem_CartException {
		return shoppingItem_CartPersistence.findByItemId(itemId);
	}
	
	/**
	 * 
	 * @param cartId
	 * @return
	 * @throws SystemException
	 */
	public List<ShoppingItem_Cart> findByCartId(long cartId) throws SystemException {
		return shoppingItem_CartPersistence.findByCartId(cartId);
	}
}