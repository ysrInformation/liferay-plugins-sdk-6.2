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

import com.htmsd.slayer.model.Commission;
import com.htmsd.slayer.service.base.CommissionLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Validator;

/**
 * The implementation of the commission local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.CommissionLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.CommissionLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.CommissionLocalServiceUtil
 */
public class CommissionLocalServiceImpl extends CommissionLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.CommissionLocalServiceUtil} to access the commission local service.
	 */
	
	public Commission update(long commissionId, long categoryId, double percent) {
		Commission commission = null;
		if (commissionId > 0) {
			try {
				commission = commissionLocalService.fetchCommission(commissionId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		} else {
			try {
				commission = commissionLocalService.createCommission(counterLocalService.increment());
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		
		commission.setCategoryId(categoryId);
		commission.setPercent(percent);
		
		if (commissionId > 0) {
			try {
				commissionLocalService.updateCommission(commission);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		} else {
			try {
				commissionLocalService.addCommission(commission);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		return commission;
	}
	
	public double getCommissionPercentByCategory(long categoryId) {
		double percent = -1;
		Commission commission = null;
		
		try {
			commission = commissionPersistence.fetchByCategoryId(categoryId);
			if (Validator.isNotNull(commission))
				percent = commission.getPercent();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return percent;
	}
}