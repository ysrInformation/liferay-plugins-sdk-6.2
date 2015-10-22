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

import com.htmsd.slayer.NoSuchCurrencyException;
import com.htmsd.slayer.model.Currency;
import com.htmsd.slayer.service.base.CurrencyLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * The implementation of the currency local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.CurrencyLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.CurrencyLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.CurrencyLocalServiceUtil
 */
public class CurrencyLocalServiceImpl extends CurrencyLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.CurrencyLocalServiceUtil} to access the currency local service.
	 */
	
	public Currency getCurrency(String currencyCode) throws NoSuchCurrencyException, SystemException {
		return currencyPersistence.findByCurrencyCode(currencyCode);
	}
}