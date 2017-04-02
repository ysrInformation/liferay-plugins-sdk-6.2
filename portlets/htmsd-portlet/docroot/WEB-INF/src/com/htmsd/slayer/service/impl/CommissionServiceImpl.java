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

import com.htmsd.slayer.service.base.CommissionServiceBaseImpl;

/**
 * The implementation of the commission remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.CommissionService} interface.
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have security checks based on the propagated JAAS credentials because this service can be accessed remotely.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.CommissionServiceBaseImpl
 * @see com.htmsd.slayer.service.CommissionServiceUtil
 */
public class CommissionServiceImpl extends CommissionServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.CommissionServiceUtil} to access the commission remote service.
	 */
	
	public double getCommissionPercentByCategory(long categoryId) {
		double percent = commissionLocalService.getCommissionPercentByCategory(categoryId);
		return percent;
	}
	
	public double getTaxByCategory(long categoryId) {
		double tax = commissionLocalService.getTaxByCategory(categoryId);
		return tax;
	}
	
	public double getSellerEarningPriceByCategory(double price, long categoryId) {
		double earnedPrice = commissionLocalService.getSellerEarningPriceByCategory(price, categoryId);
		return earnedPrice;
	}
}