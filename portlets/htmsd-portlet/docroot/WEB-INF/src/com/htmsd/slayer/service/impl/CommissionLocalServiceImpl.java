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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.htmsd.slayer.model.Commission;
import com.htmsd.slayer.service.base.CommissionLocalServiceBaseImpl;
import com.htmsd.util.CommonUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
	
	private static Log _log = LogFactoryUtil.getLog(CommissionLocalServiceImpl.class);
	private static int SCALE = 2;
	
	public Commission update(long commissionId, long categoryId, double percent, double tax, double deliveryCharges) {
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
		commission.setTax(tax);
		commission.setDeliveryCharges(deliveryCharges); 
		
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
	
	public double getTaxByCategory(long categoryId) {
		double tax = -1;
		Commission commission = null;
		try {
			commission = commissionPersistence.fetchByCategoryId(categoryId);
			if (Validator.isNotNull(commission))
				tax = commission.getTax();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return tax;
	}
	
	public double getSellerEarningPriceByCategory(double basePrice, long categoryId) {
		
		double earningPrice = 0, commissionPercent = 0, tax = 0; 
		float taxcalc = 0, commissioncalc = 0;
		
		Commission commission = null;
		try {
			commission = commissionPersistence.fetchByCategoryId(categoryId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (Validator.isNotNull(commission)) {
			commissionPercent = commission.getPercent();
			tax = commission.getTax();
		}
		
		System.out.println("Base price ::"+basePrice);
		System.out.println("Tax % ::"+tax);
		System.out.println("Commission % ::"+commissionPercent);
		
		taxcalc = (float) CommonUtil.calculateVat(basePrice, tax);
		commissioncalc = getAmountPercentage((float) basePrice, (float) commissionPercent); 
		float amountAfterTaxDeduction = (float) basePrice - taxcalc;
		float amountAfterCommissionDeduction = amountAfterTaxDeduction - commissioncalc;
		float finaltaxCalc = getAmountPercentage(amountAfterCommissionDeduction, (float) tax);
		earningPrice = amountAfterCommissionDeduction + finaltaxCalc;
		BigDecimal finalEarningPrice = new BigDecimal(earningPrice).setScale(SCALE, RoundingMode.HALF_UP);
		
		System.out.println("Earning Price before roundOff ::"+earningPrice);  
		System.out.println("Final Earning Price after roundOff ::"+finalEarningPrice+"\n ==================="); 
		
		return finalEarningPrice.doubleValue();
	}
	
	private float getAmountPercentage(float price, float tax) {
		float calcPrice = 0;
		if (price > 0 && tax > 0) {
			calcPrice = (price * tax) / 100;
		}
		return calcPrice;
	}
}