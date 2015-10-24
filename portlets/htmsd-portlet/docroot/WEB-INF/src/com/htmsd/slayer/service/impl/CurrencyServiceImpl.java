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

import javax.servlet.http.HttpServletRequest;

import com.htmsd.slayer.model.Currency;
import com.htmsd.slayer.service.CurrencyLocalServiceUtil;
import com.htmsd.slayer.service.base.CurrencyServiceBaseImpl;
import com.htmsd.util.HConstants;
import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.PortalPreferences;
import com.liferay.portal.model.PortletPreferences;
import com.liferay.portal.security.ac.AccessControlled;
import com.liferay.portal.service.PortalPreferencesLocalServiceUtil;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;

/**
 * The implementation of the currency remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.CurrencyService} interface.
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have security checks based on the propagated JAAS credentials because this service can be accessed remotely.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.CurrencyServiceBaseImpl
 * @see com.htmsd.slayer.service.CurrencyServiceUtil
 */
public class CurrencyServiceImpl extends CurrencyServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.CurrencyServiceUtil} to access the currency remote service.
	 */
	@AccessControlled(guestAccessEnabled =  true)
	public List<Currency> getCurrencies() throws SystemException {
		return CurrencyLocalServiceUtil.getCurrencies(-1, -1);
	}
}