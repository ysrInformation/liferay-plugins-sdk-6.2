package com.liferay.wrapper;

import javax.portlet.ActionRequest;
import javax.portlet.filter.ActionRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

public class ExtActionRequestWrapper extends ActionRequestWrapper  {

	private Log _log = LogFactoryUtil.getLog(ExtActionRequestWrapper.class);
	private ActionRequest actionRequest;
	
	public ExtActionRequestWrapper(ActionRequest actionRequest) {
		super(actionRequest);
		this.actionRequest = actionRequest;
	}

	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		if (name.equals("login") && Validator.isEmailAddress(value)) {
			try {
				value = UserLocalServiceUtil.getUserByEmailAddress(PortalUtil.getCompanyId(actionRequest), value).getScreenName();
			} catch (PortalException e) {
				_log.error(e.getMessage());
			} catch (SystemException e) {
				_log.error(e.getMessage());
			} catch (NullPointerException e) {
				_log.error(e.getMessage()+ " No user exist with email address");
			}
		}
		return value;
	}
}