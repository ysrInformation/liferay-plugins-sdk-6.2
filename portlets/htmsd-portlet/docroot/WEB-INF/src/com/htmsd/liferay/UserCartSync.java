package com.htmsd.liferay;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.htmsd.slayer.model.ShoppingCart;
import com.htmsd.slayer.service.ShoppingCartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.ShoppingBean;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class UserCartSync extends Action {
	/* (non-Java-doc)
	 * @see com.liferay.portal.kernel.events.SimpleAction#SimpleAction()
	 */
	public UserCartSync() {
		super();
	}

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ActionException {
		addItemsToCart(request);	
	}
	
	private void addItemsToCart(HttpServletRequest request) {
		
		System.out.println("Inside addItemsToCart...");
		HttpSession session = request.getSession();
		long userId = PortalUtil.getUserId(request);
		long companyId = PortalUtil.getCompanyId(request);
		long groupId = 0l;
		long cartId = 0l;
		
		String fullName = StringPool.BLANK;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
			User user = PortalUtil.getUser(request);
			fullName = (Validator.isNotNull(user))?user.getFullName():StringPool.BLANK;
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		List<ShoppingBean> shoppingBeanList = CommonUtil.getGuestUserList(session);
		System.out.println("List size is ::"+shoppingBeanList.size());
		ShoppingCart shoppingCart = null;
		try {
			shoppingCart = ShoppingCartLocalServiceUtil.getShoppingCartByUserId(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (Validator.isNotNull(shoppingCart)) {
			cartId = shoppingCart.getCartId();
		} else {
			shoppingCart = ShoppingCartLocalServiceUtil.insertShoppingCart(
					userId, companyId, groupId, fullName);
			cartId = shoppingCart.getCartId();
		}
		
		if (Validator.isNotNull(shoppingBeanList) && shoppingBeanList.size() > 0) {
			for (ShoppingBean shoppingBean:shoppingBeanList) {
				 ShoppingItem_CartLocalServiceUtil.insertItemsToCart(shoppingBean.getQuantity(), 
						 cartId, shoppingBean.getItemId(), shoppingBean.getTotalPrice());
			}
			session.removeAttribute("SHOPPING_ITEMS"); 
		}
	}
}