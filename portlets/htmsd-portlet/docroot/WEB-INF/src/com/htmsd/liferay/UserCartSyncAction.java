package com.htmsd.liferay;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.htmsd.slayer.NoSuchShoppingItem_CartException;
import com.htmsd.slayer.model.ShoppingCart;
import com.htmsd.slayer.model.ShoppingItem_Cart;
import com.htmsd.slayer.model.WholeSale;
import com.htmsd.slayer.service.ShoppingCartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil;
import com.htmsd.slayer.service.WholeSaleLocalServiceUtil;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.ShoppingBean;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class UserCartSyncAction extends Action {
	/* (non-Java-doc)
	 * @see com.liferay.portal.kernel.events.Action#Action()
	 */
	public UserCartSyncAction() {
		super();
	}

	/* (non-Java-doc)
	 * @see com.liferay.portal.kernel.events.Action#run(HttpServletRequest arg0, HttpServletResponse arg1)
	 */
	public void run(HttpServletRequest request, HttpServletResponse response) throws ActionException {
		_log.info("In Login Post Action class (run method) @!!!"); 
		addItemsToCart(request);
	}
	
	protected void addItemsToCart(HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		
		long groupId = 0l, cartId = 0l;
		long userId = PortalUtil.getUserId(request);
		long companyId = PortalUtil.getCompanyId(request);
		
		String fullName = StringPool.BLANK;
		List<ShoppingBean> shoppingBeanList = CommonUtil.getGuestUserList(session);
		try {
			groupId = PortalUtil.getScopeGroupId(request);
			User user = PortalUtil.getUser(request);
			fullName = (Validator.isNotNull(user)) ? user.getFullName() : StringPool.BLANK;
		} catch (PortalException e) {
			_log.error("Portal exception occured due to :"+e); 
		} catch (SystemException e) {
			_log.error("System exception occured due to :"+e); 
		}
		
		_log.info("The user ("+fullName+") has "+shoppingBeanList.size()+ " items in the cart!");
		
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
		
		_log.info("CartId of "+ fullName +" is found to be "+cartId+".");  
		
		if (Validator.isNotNull(shoppingBeanList) && shoppingBeanList.size() > 0) {
			for (ShoppingBean shoppingBean:shoppingBeanList) {
				ShoppingItem_Cart duplicateItem = null;
				try {
					duplicateItem = ShoppingItem_CartLocalServiceUtil.findByCartAndItemId(cartId, shoppingBean.getItemId());
				} catch (NoSuchShoppingItem_CartException e) {
					_log.info("No item exist with ItemID ->"+shoppingBean.getItemId()+" and  cartID ->"+cartId + "!, "+e.getMessage());  
				} catch (SystemException e) {
					_log.error("System error occurred due to :"+e);
				}
				
				if (Validator.isNotNull(duplicateItem)) {
					int qty = shoppingBean.getQuantity() + duplicateItem.getQuantity(); 
					double totalPrice = getWholeSalePrice(qty, duplicateItem.getItemId(), shoppingBean.getUnitPrice());
					ShoppingItem_CartLocalServiceUtil.updateShoppingItem_Cart(duplicateItem.getId(), qty, totalPrice);
				} else {
					ShoppingItem_CartLocalServiceUtil.insertItemsToCart(shoppingBean.getQuantity(), 
							 cartId, shoppingBean.getItemId(), shoppingBean.getTotalPrice());	
				}
			}
			_log.info("The items were added to DB successfully!"); 
			session.removeAttribute("SHOPPING_ITEMS"); 
		}
	}
	
	private double getWholeSalePrice(int quantity, long itemId, double itemPrice) {
		
		double totalPrice = 0.0;
		List<WholeSale> wholesaleList = WholeSaleLocalServiceUtil.getWholeSaleByQty(itemId, quantity);
		if (Validator.isNotNull(wholesaleList)) {
			totalPrice = quantity * itemPrice;
			for (WholeSale wholeSale: wholesaleList) {
				if (quantity == wholeSale.getQuantity()) {
					totalPrice = wholeSale.getPrice(); 
				} else if (quantity > wholeSale.getQuantity()) {
					totalPrice = quantity * (wholeSale.getPrice() / wholeSale.getQuantity());
				}
			}
		} else {
			totalPrice = quantity * itemPrice;
		}
		
		return totalPrice;
	}

	private static Log _log = LogFactoryUtil.getLog(UserCartSyncAction.class);

}