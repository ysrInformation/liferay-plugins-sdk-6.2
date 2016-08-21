package com.liferay.listener;

import java.util.Date;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.BaseModelListener;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.Phone;
import com.liferay.portal.model.User;
import com.liferay.portal.service.PhoneLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;

public class UserListener extends BaseModelListener<User> {
	
	private Log _log = LogFactoryUtil.getLog(UserListener.class);
	
	public static String emailAddress;
	public static String phoneNumber;
	
	@Override
	public void onAfterCreate(User user) throws ModelListenerException {
		// TODO Auto-generated method stub
		super.onAfterCreate(user);
		_log.info("Adding Number "+phoneNumber+" to user "+user.getEmailAddress());
		if (Validator.isNotNull(emailAddress) && user.getEmailAddress().equals(emailAddress)) {
			addPhone(user);
			_log.info("Number "+phoneNumber+" Added Successfuly to user "+user.getEmailAddress());
		}
	}
	
	private void addPhone(User user) {
		int ltypeId = 11008; //MobilePhone;
		Phone phone = null;
		try {
			phone = PhoneLocalServiceUtil.createPhone(CounterLocalServiceUtil.increment());
		} catch (SystemException e) {
			e.printStackTrace();
		}
		phone.setUserId(user.getUserId());
		phone.setUserName(user.getFullName());
		phone.setCompanyId(user.getCompanyId());
		phone.setClassName(Contact.class.getName());
		phone.setClassPK(user.getContactId());
		phone.setNumber(phoneNumber);
		phone.setTypeId(ltypeId);
		phone.setPrimary(true);
		phone.setCreateDate(new Date());
		phone.setModifiedDate(new Date());
		try {
			PhoneLocalServiceUtil.addPhone(phone);
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
}