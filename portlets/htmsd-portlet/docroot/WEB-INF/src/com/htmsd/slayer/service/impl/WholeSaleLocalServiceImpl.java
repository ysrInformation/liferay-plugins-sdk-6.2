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
import java.util.List;

import com.htmsd.slayer.model.WholeSale;
import com.htmsd.slayer.service.base.WholeSaleLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * The implementation of the whole sale local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.WholeSaleLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.WholeSaleLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.WholeSaleLocalServiceUtil
 */
public class WholeSaleLocalServiceImpl extends WholeSaleLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.WholeSaleLocalServiceUtil} to access the whole sale local service.
	 */
	
	public WholeSale putWholeSale(long itemId, long quantity, double price) {
		
		WholeSale wholeSale = null;
		try {
		    wholeSale = createWholeSale(counterLocalService.increment(WholeSale.class.getName()));
			wholeSale.setItemId(itemId);
			wholeSale.setQuantity(quantity);
			wholeSale.setPrice(price);
			addWholeSale(wholeSale);
		} catch (SystemException e) {
			_log.error(e);
		}
		return wholeSale;
	}
	
	public List<WholeSale> getWholeSaleItem(long itemId) {
		
		List<WholeSale> wholeSales = new ArrayList<WholeSale>();
		try {
			wholeSales = wholeSalePersistence.findByItem(itemId);
		} catch (SystemException e) {
			_log.error(e);
		}
		return wholeSales;
	}
	
	public List<WholeSale> getWholeSaleByQty(long itemId, long quantity) {
		
		List<WholeSale> wholeSales = new ArrayList<WholeSale>();
		try {
			wholeSales = wholeSalePersistence.findByQuantity(itemId, quantity);
		} catch (SystemException e) {
			_log.error(e);
		}
		return wholeSales;
	}
	
	public void deleteWholeSaleByItem(long itemId) {
		
		List<WholeSale> oldWholeSales = getWholeSaleItem(itemId);
		for(WholeSale wholeSale : oldWholeSales) {
			try {
				deleteWholeSale(wholeSale.getWholseSaleId());
			} catch (PortalException e) {
				_log.error(e);
			} catch (SystemException e) {
				_log.error(e);
			}
		}
	}
	
	private static  Log _log = LogFactoryUtil.getLog(WholeSaleLocalServiceImpl.class); 
}