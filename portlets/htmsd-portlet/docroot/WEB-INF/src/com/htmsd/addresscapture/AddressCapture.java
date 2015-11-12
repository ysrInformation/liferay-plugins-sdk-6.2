package com.htmsd.addresscapture;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.htmsd.util.HConstants;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.ListTypeConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.AddressLocalServiceUtil;
import com.liferay.portal.service.ListTypeServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.expando.model.ExpandoBridge;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class AddressCapture
 */
public class AddressCapture extends MVCPortlet {
 
	Log _log = LogFactoryUtil.getLog(AddressCapture.class);
	
	
	/**	
	 * Method saveAddress save the current user address
	 *@param actionRequest
	`*@param actionresponse
	 **/
	public void saveAddress(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
	
		String street1 = ParamUtil.getString(actionRequest, "street1");
		String street2 = ParamUtil.getString(actionRequest, "street2");
		String street3 = ParamUtil.getString(actionRequest, "street3");
		String city = ParamUtil.getString(actionRequest, "city");
		long countryId = ParamUtil.getLong(actionRequest, "countryId");
		long regionId = ParamUtil.getLong(actionRequest, "regionId");
		String zip = ParamUtil.getString(actionRequest, "zip");
		String tin = ParamUtil.getString(actionRequest, "tin");
		String companyName = ParamUtil.getString(actionRequest, "companyName");
		
		
		ThemeDisplay themeDisplay  = (ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		ServiceContext serviceContext = null;
		try {
			serviceContext = ServiceContextFactory.getInstance(actionRequest);
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
		
		int typeId = 0;

		try {
			typeId = ListTypeServiceUtil.getListTypes(Contact.class.getName() + ListTypeConstants.ADDRESS).get(tin.isEmpty() ? 2 : 0).getListTypeId();
		} catch (SystemException e) {
			_log.error(e);
		}
		
		try {
			AddressLocalServiceUtil.addAddress(themeDisplay.getUserId(), Contact.class.getName(), themeDisplay.getContact().getContactId(), street1, street2, street3, city, zip, regionId, countryId, typeId, false, true, serviceContext);
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
		
		
		updateCompanyInfo(companyName, tin, themeDisplay.getUser());
		SessionMessages.add(actionRequest, "request_processed","Your Address Save Thank you!!");
		actionResponse.setWindowState(LiferayWindowState.NORMAL);
	}
	
	
	
	/**	
	 * Method updateCompanyInfo save the current user address
	 *@param actionRequest
	`*@param actionresponse
	 **/
	private void updateCompanyInfo(String companyName, String tin, User user) {
		
		ExpandoBridge expandoBridge = user.getExpandoBridge();
		expandoBridge.setAttribute(HConstants.COMPANY_NAME, companyName);
		expandoBridge.setAttribute(HConstants.TIN, tin);
	}


	/**	
	 * Method serveResource check for current user address exist
	 *@param resourceRequest
	`*@param resourceResponse
	 **/
	@Override
	public void serveResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws IOException,
			PortletException {
		
		String returnData = "false";
		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		int typeId = 0;
		try {
			typeId = ListTypeServiceUtil.getListTypes(Contact.class.getName() + ListTypeConstants.ADDRESS).get(2).getListTypeId();
		} catch (SystemException e) {
			_log.error(e);
		}
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Address.class);
		dynamicQuery.add(RestrictionsFactoryUtil.eq("userId", themeDisplay.getUserId()));
		dynamicQuery.add(RestrictionsFactoryUtil.eq("typeId", typeId));
		try {
			long count = AddressLocalServiceUtil.dynamicQueryCount(dynamicQuery);
			if(themeDisplay.isSignedIn() && count == 0) {
				
				returnData = "true";
			}else{
				returnData = "false";
			}
		} catch (SystemException e) {
			_log.error(e);
			returnData = "true";
		}
		
		PrintWriter printWriter = resourceResponse.getWriter();
		printWriter.write(returnData);
		printWriter.flush();
	}

}
