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

import com.htmsd.slayer.model.Purchase;
import com.htmsd.slayer.model.impl.PurchaseImpl;
import com.htmsd.slayer.service.base.PurchaseLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * The implementation of the purchase local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.PurchaseLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.PurchaseLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.PurchaseLocalServiceUtil
 */
public class PurchaseLocalServiceImpl extends PurchaseLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.PurchaseLocalServiceUtil} to access the purchase local service.
	 */
	
	public Purchase insertPurchaseDetails(long userId, long groupId, long companyId, long currencyId, 
			long orderId, double totalAmount, String paymentMode, String paymentStatus) throws SystemException {
		
		Purchase purchase = new PurchaseImpl();
		purchase = purchaseLocalService.createPurchase(counterLocalService.increment()); 
		purchase.setUserId(userId);
		purchase.setCompanyId(companyId);
		purchase.setCurrencyId(currencyId);
		purchase.setOrderId(orderId);
		purchase.setGroupId(groupId);
		purchase.setPaymentMode(paymentMode);
		purchase.setPaymentStatus(paymentStatus);
		purchase.setPurchaseDate(new Date());
		purchase.setTotalAmount(totalAmount);
		purchase = purchaseLocalService.addPurchase(purchase);
		
		return purchase;
	}
}