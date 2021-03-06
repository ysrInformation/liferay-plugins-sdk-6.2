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

import com.htmsd.slayer.model.Invoice;
import com.htmsd.slayer.model.impl.InvoiceImpl;
import com.htmsd.slayer.service.base.InvoiceLocalServiceBaseImpl;

/**
 * The implementation of the invoice local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.InvoiceLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.InvoiceLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.InvoiceLocalServiceUtil
 */
public class InvoiceLocalServiceImpl extends InvoiceLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.InvoiceLocalServiceUtil} to access the invoice local service.
	 */
	
	public Invoice insertInvoice(long userId, long orderId) {
		
		Invoice invoice = new InvoiceImpl();
		long invoiceId = 0l;
		try {
			invoiceId = counterLocalService.increment(InvoiceLocalServiceImpl.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		invoice = createInvoice(invoiceId);
		invoice.setOrderId(orderId);
		invoice.setUserId(userId);
		invoice.setCreateDate(new Date());
		
		try {
			invoice = addInvoice(invoice);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return invoice;
	}
	
	public Invoice getInvoiceByOrderId(long orderId) {
		
		Invoice invoice = null;
		try {
			invoice = invoicePersistence.fetchByOrderId(orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return invoice;
	}
}