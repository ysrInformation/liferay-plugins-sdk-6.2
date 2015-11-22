package com.htmsd.orderpanel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.WindowStateException;

import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.htmsd.util.NotificationUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class OrderPanelPortlet
 */
public class OrderPanelPortlet extends MVCPortlet {
	
	@Override
	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		String keywords = ParamUtil.getString(actionRequest, "keywords");
		String tabName = ParamUtil.getString(actionRequest, "tabName");
		if (keywords.isEmpty()) {
			super.processAction(actionRequest, actionResponse);
		} else {
			PortalUtil.copyRequestParameters(actionRequest, actionResponse);
		}
		actionResponse.setRenderParameter("tab1", tabName);
	}
	
	public void updateOrderStatus(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		_log.info("In updateOrderStatus ..."); 
		int orderStatus = ParamUtil.getInteger(actionRequest, "orderStatus");
		long orderId = ParamUtil.getLong(actionRequest, "orderId");
		String tabName = ParamUtil.getString(actionRequest, "tabName");
		String cancelReason = ParamUtil.getString(actionRequest, "cancelReason");
		
		ShoppingOrderLocalServiceUtil.updateShoppingOrder(orderStatus, orderId, cancelReason); 
		
		ShoppingOrder shoppingOrder = null;
		try {
			shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (Validator.isNotNull(shoppingOrder)) {
			NotificationUtil.sendNotification(shoppingOrder.getGroupId(), 
					shoppingOrder.getUserName(), shoppingOrder.getShippingEmailAddress(), getArticleId(orderStatus), getOrderTokens(), getValueTokens(shoppingOrder));
		}
		
		actionResponse.setWindowState(LiferayWindowState.NORMAL);
		actionResponse.setRenderParameter("tab1", tabName); 
	}
	
	/**
	 * Method for generating Invoice
	 * @param actionRequest
	 * @param actionResponse
	 * @throws MalformedURLException
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 * @throws SystemException
	 * @throws WindowStateException 
	 */
	public void generateInvoice(ActionRequest actionRequest, ActionResponse actionResponse) 
				throws MalformedURLException, FileNotFoundException, DocumentException, SystemException, WindowStateException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		PortletSession portletSession = actionRequest.getPortletSession();
		
		long orderId = ParamUtil.getLong(actionRequest, "orderId");
		String tabName = ParamUtil.getString(actionRequest, "tabName");
		String tempFolderPath = SystemProperties.get(SystemProperties.TMP_DIR)+File.separator+"liferay" + File.separator + "receipts";
		String timeStamp = new SimpleDateFormat("ddMMyyyyhhmm").format(new Date());
		String tempFileName = "Reciept"+timeStamp+".pdf";
		String filePath=tempFolderPath+File.separator+tempFileName;	
		File tempFolder = new File(tempFolderPath);
		if (!tempFolder.exists()) {
			tempFolder.mkdir();
		}
		
		String currentCurrencyId = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
		long currencyId = (Validator.isNull(currentCurrencyId)) ?  0 : Long.valueOf(currentCurrencyId);
		double currencyRate = CommonUtil.getCurrentRate(currencyId);
		
		Document document = new Document(PageSize.A4,50,50,50,50);
		PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(filePath));

		document.open();
		URL imageUrl = actionRequest.getPortletSession().getPortletContext().getResource("/images/logo.png");
		PdfPTable headerTable = GenerateInvoice.generateHeader(imageUrl, themeDisplay.getCompanyId(), orderId, currencyId, currencyRate);
		
		PdfPTable parenttable = new PdfPTable(1);
		parenttable.setWidthPercentage(100);
		parenttable.setSpacingBefore(20f);
		parenttable.setSpacingAfter(5f);
		
		PdfPCell cellTable = new PdfPCell(headerTable);
		cellTable.setBackgroundColor(BaseColor.YELLOW); 
		parenttable.addCell(cellTable);
		
		document.add(parenttable);

		document.close();
		writer.close();
		
		String articleId = "SEND_INVOICE";
		ShoppingOrder shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);
		
		if (Validator.isNotNull(shoppingOrder)) {
			String[] placeHolders = new String[]{"[$USER$]"};
			String[] values = new String[] {shoppingOrder.getUserName()};
			NotificationUtil.sendReceipt(themeDisplay.getScopeGroupId(), shoppingOrder.getShippingEmailAddress(),
					articleId, shoppingOrder.getUserName(), filePath, tempFileName, placeHolders, values);
		}
		
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		String successMessage = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "reciept-has-been-sent-to-the-user");
		SessionMessages.add(actionRequest, "request_processed", successMessage);
		actionResponse.setWindowState(LiferayWindowState.NORMAL);
		actionResponse.setRenderParameter("tab1", tabName);  
	}
	
	private static String[] getOrderTokens(){
		return new String[] {
				"[$USER_NAME$]","[$ORDER_ID$]","[$PRODUCT_DETAILS$]","[$ITEM_PRICE$]","[$QTY$]",
				"[$SUB_TOTAL$]","[$TOTAL$]","[$MOBILE_NO$]","[$ADDRESS$]"
		};
	}
	
	private static String[] getValueTokens(ShoppingOrder shoppingOrder) {
		
		DecimalFormat df = new DecimalFormat("0.00");
		String[] valueTokens = new String[9];
		
		String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
       	String orderId = HConstants.HTMSD + currentYear.substring(2, 4) + shoppingOrder.getOrderId();
		ShoppingItem shoppingItem = CommonUtil.getShoppingItem(shoppingOrder.getShoppingItemId());
		
		valueTokens[0] = shoppingOrder.getUserName();
		valueTokens[1] = orderId;
		valueTokens[2] = (Validator.isNotNull(shoppingItem)? shoppingItem.getProductCode()+ StringPool.DASH +shoppingItem.getName():StringPool.DASH);
		valueTokens[3] = df.format(Validator.isNotNull(shoppingItem)? shoppingItem.getTotalPrice() : 0);
		valueTokens[4] = String.valueOf(shoppingOrder.getQuantity());
		valueTokens[5] = df.format(shoppingOrder.getTotalPrice());
		valueTokens[6] = CommonUtil.getPriceInNumberFormat(shoppingOrder.getTotalPrice(), HConstants.RUPEE_SYMBOL);
		valueTokens[7] = shoppingOrder.getShippingMoble();
		valueTokens[8] = GenerateInvoice.getAddress(shoppingOrder); 
		
		return valueTokens;
	}
	
	private static String getArticleId(int orderStatus) {
		
		String articleId = StringPool.BLANK;
		AssetCategory category = CommonUtil.getAssetCategoryById(orderStatus);
		if (Validator.isNull(category)) return StringPool.BLANK;
		
		if (category.getName().equals(HConstants.CANCEL_ORDER_STATUS)) {
			articleId = "ORDER_CANCELLED";
		} else if (category.getName().equals(HConstants.DELIVERED_STATUS)) {
			articleId = "ORDER_DELIVERED";
		} else if (category.getName().equals(HConstants.SHIPPING_STATUS)) {
			articleId = "ORDER_SHIPPED";
		}
		return articleId;
	}

	private final Log _log = LogFactoryUtil.getLog(OrderPanelPortlet.class);
}
