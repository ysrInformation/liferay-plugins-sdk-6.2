package com.htmsd.addresscapture;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.ListTypeConstants;
import com.liferay.portal.service.AddressLocalServiceUtil;
import com.liferay.portal.service.ListTypeServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
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
			typeId = ListTypeServiceUtil.getListTypes(Contact.class.getName() + ListTypeConstants.ADDRESS).get(0).getListTypeId();
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
	
		SessionMessages.add(actionRequest, "request_processed","Your Address Save Thank you!!");
		actionResponse.setWindowState(LiferayWindowState.NORMAL);
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
		
		System.out.println("isnide serve resource");
		String returnData = "false";
		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		try {
			if(themeDisplay.isSignedIn() && themeDisplay.getUser().getAddresses().isEmpty()) {
				
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
