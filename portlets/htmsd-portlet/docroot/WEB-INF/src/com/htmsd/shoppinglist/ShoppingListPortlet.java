package com.htmsd.shoppinglist;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import com.htmsd.slayer.service.ShoppingCartLocalServiceUtil;
import com.htmsd.util.HConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class ShoppingListPortlet
 */
public class ShoppingListPortlet extends MVCPortlet {

	private final static Log _log = LogFactoryUtil.getLog(ShoppingListPortlet.class);
	
	public void addItemToCart(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		_log.info("Adding Item to Cart");
		
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY); 
		
		long itemId = ParamUtil.getLong(actionRequest, HConstants.ITEM_ID);
		
		ShoppingCartLocalServiceUtil.insertShoppingCart(
				themeDisplay.getUserId(), themeDisplay.getCompanyId(), 
				themeDisplay.getScopeGroupId(), themeDisplay.getUser().getFullName(), String.valueOf(itemId));
		actionResponse.setRenderParameter(HConstants.JSP_PAGE, HConstants.PAGE_SHOPPING_LIST_DETAILS);
		actionResponse.setRenderParameter(HConstants.ITEM_ID, String.valueOf(itemId));
	}
}
