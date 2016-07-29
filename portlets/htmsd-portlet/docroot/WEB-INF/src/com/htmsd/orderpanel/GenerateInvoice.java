package com.htmsd.orderpanel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;

import com.htmsd.slayer.model.Category;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.htmsd.util.NumberWordConverter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

public class GenerateInvoice {
	
	public static void generateInvoice(ActionRequest actionRequest, PortletSession portletSession, 
			long orderId, long companyId, String filePath) throws DocumentException,
			FileNotFoundException, MalformedURLException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		String currentCurrencyId = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
		long currencyId = (Validator.isNull(currentCurrencyId)) ?  0 : Long.valueOf(currentCurrencyId);
		double currencyRate = CommonUtil.getCurrentRate(currencyId);
		
		Document document = new Document(PageSize.A4,50,50,50,50);
		PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(filePath));

		document.open();
		
		URL imageUrl = actionRequest.getPortletSession().getPortletContext().getResource("/images/logo.png");
		PdfPTable headerTable = generateHeader(imageUrl, companyId, orderId, currencyId, themeDisplay.getScopeGroupId() ,currencyRate);
		
		PdfPTable parenttable = new PdfPTable(1);
		parenttable.setWidthPercentage(100);
		parenttable.setSpacingBefore(20f);
		parenttable.setSpacingAfter(5f);
		
		PdfPCell cellTable = new PdfPCell(headerTable);
		cellTable.setBackgroundColor(BaseColor.WHITE); 
		parenttable.addCell(cellTable);
		
		document.add(parenttable);

		document.close();
		writer.close();
	}
	
	public static PdfPTable generateHeader(URL imageUrl , long companyId, long orderId,
			long currencyId, long groupId, double currencyRate) {
		
		PdfPTable pdftable = new PdfPTable(1);
		try {
			ShoppingOrder shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);
			
			pdftable.getDefaultCell().setBorder(Rectangle.NO_BORDER); 
			pdftable.setWidthPercentage(100);
			pdftable.setSpacingBefore(20f);
			pdftable.setSpacingAfter(10f);

			Image htmsdLogo = Image.getInstance(imageUrl);
			htmsdLogo.setAlignment(Image.ALIGN_CENTER);
			htmsdLogo.setWidthPercentage(10f);
			htmsdLogo.scalePercent(40f);

			Font headerFont = new Font();
			headerFont.setColor(BaseColor.GRAY);
			headerFont.setFamily("Vardana");
			headerFont.setSize(12);
			headerFont.setStyle(Font.BOLD);

			Paragraph headerParagraph = new Paragraph("Receipt",headerFont);
			headerParagraph.add("\nH.T.M.S.D. PET'S");
			headerParagraph.add("\nBREADERS & RESELLER");
			headerParagraph.setAlignment(Paragraph.ALIGN_CENTER);
			headerParagraph.setSpacingBefore(20f);
			headerParagraph.setSpacingAfter(5f);

			PdfPCell imageCell = new PdfPCell(htmsdLogo);
			imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			imageCell.setBorder(Rectangle.NO_BORDER);
			pdftable.addCell(imageCell);

			PdfPCell headerCell = new PdfPCell(headerParagraph);
			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerCell.setBorder(Rectangle.NO_BORDER);
			pdftable.addCell(headerCell);
			
			PdfPCell userDesCell = new PdfPCell(generateUserDetailsTable(companyId, shoppingOrder.getUserId(), shoppingOrder));
			userDesCell.setPadding(20f); 
			userDesCell.setBorder(Rectangle.NO_BORDER);
			pdftable.addCell(userDesCell);

			PdfPCell productDesCell = new PdfPCell(generateProductDetailsTable(shoppingOrder.getUserId(), currencyId, groupId, shoppingOrder, currencyRate));
			productDesCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			productDesCell.setPaddingLeft(10f);
			productDesCell.setPaddingRight(10f);
			productDesCell.setPaddingBottom(5f);
			productDesCell.setBorder(Rectangle.NO_BORDER);
			pdftable.addCell(productDesCell);

			PdfPCell receiptInformationCell = new PdfPCell();
			receiptInformationCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			receiptInformationCell.setPaddingLeft(10f);
			receiptInformationCell.setBorder(Rectangle.NO_BORDER);

			PdfPTable receiptTable = new  PdfPTable(2);
			receiptTable.setWidths(new float[]{0.8f, 1.5f});
			
			double totalPrice = (currencyRate == 0) ? shoppingOrder.getTotalPrice() : shoppingOrder.getTotalPrice() / currencyRate;
			receiptTable.addCell(createLabelCell("Amount in words      :",Rectangle.NO_BORDER));
			receiptTable.addCell(createValueCell(NumberWordConverter.convert((int)totalPrice)+" Only",Rectangle.NO_BORDER));
			receiptTable.addCell(createLabelCell("Receipt Center       :",Rectangle.NO_BORDER));
			receiptTable.addCell(createValueCell(CommonUtil.getSellerCompanyDetails(shoppingOrder.getSellerId(), HConstants.COMPANY_NAME), Rectangle.NO_BORDER));
			receiptInformationCell.addElement(receiptTable);

			pdftable.addCell(receiptInformationCell);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return pdftable;
	}
	
	public static PdfPTable generateUserDetailsTable(long companyId, long userId, ShoppingOrder shoppingOrder)
				throws DocumentException, SystemException, PortalException {

		PdfPTable table = new PdfPTable(4);
		try {
			table.setWidthPercentage(105);
			table.setWidths(new float[]{0.8f, 1.5f, 0.7f,1.0f});
			// 1st Row
			table.addCell(createLabelCell("Invoice No :",Rectangle.NO_BORDER));
			table.addCell(createValueCell(CommonUtil.getBillNumber(shoppingOrder.getOrderId()), Rectangle.NO_BORDER));
			table.addCell(createLabelCell("Date :",Rectangle.NO_BORDER));
			table.addCell(createValueCell(HConstants.DATE_FORMAT.format(new Date()),Rectangle.NO_BORDER));

			// 2nd Row
			table.addCell(createLabelCell("Billing Address :",Rectangle.NO_BORDER));
			table.addCell(createValueCell(getAddress(shoppingOrder), Rectangle.NO_BORDER));
			table.addCell(createLabelCell("Shipping Address :",Rectangle.NO_BORDER));
			table.addCell(createValueCell(getAddress(shoppingOrder), Rectangle.NO_BORDER));

			//3rd Row
			table.addCell(createLabelCell("Mobile No :",Rectangle.NO_BORDER));
			table.addCell(createValueCell(shoppingOrder.getShippingMoble(), Rectangle.NO_BORDER));
			table.addCell(createLabelCell("Alt Mobile No :",Rectangle.NO_BORDER));
			table.addCell(createValueCell(shoppingOrder.getShippingAltMoble(), Rectangle.NO_BORDER));

			//5th Row
			table.addCell(createLabelCell("E - mail :", Rectangle.NO_BORDER));
			table.addCell(createValueCell(shoppingOrder.getShippingEmailAddress(), Rectangle.NO_BORDER));
			table.addCell(createLabelCell("Tin :", Rectangle.NO_BORDER));
			table.addCell(createValueCell(CommonUtil.getSellerCompanyDetails(shoppingOrder.getSellerId(), HConstants.TIN), Rectangle.NO_BORDER));

		} catch(Exception e){
			e.printStackTrace();
		}
		return table;
	}
	
	public static PdfPTable generateProductDetailsTable(long userId, long currencyId, long groupId, ShoppingOrder shoppingOrder, 
			double currencyRate) throws DocumentException {
		
		ShoppingItem shoppingItem = CommonUtil.getShoppingItem(shoppingOrder.getShoppingItemId());
		Category parentCategory = CommonUtil.getShoppingItemParentCategory(shoppingOrder.getShoppingItemId());
		Category category = ShoppingItemLocalServiceUtil.getShoppingItemCategory(shoppingOrder.getShoppingItemId());
		boolean isLiveCategory = Validator.isNotNull(parentCategory) && parentCategory.getName().equals("Live");
		
		int columns = !isLiveCategory ? 7 : 5;
		float[] widths = !isLiveCategory ? new float[]{0.2f, 0.6f, 1.2f, 0.6f, 0.8f,0.8f, 0.8f} : new float[]{0.2f, 0.6f, 1.2f, 0.6f, 0.8f};
		PdfPTable pdftable = new PdfPTable(columns);
		pdftable.setWidthPercentage(80);
		pdftable.setWidths(widths);

		//Header
		pdftable.addCell(createLabelCellForProduct("No",Rectangle.BOTTOM|Rectangle.TOP|Rectangle.RIGHT|Rectangle.LEFT));
		pdftable.addCell(createLabelCellForProduct("Product Type",Rectangle.BOTTOM|Rectangle.TOP|Rectangle.RIGHT|Rectangle.LEFT));
		pdftable.addCell(createLabelCellForProduct("Product Details",Rectangle.BOTTOM|Rectangle.TOP|Rectangle.RIGHT|Rectangle.LEFT));
		pdftable.addCell(createLabelCellForProduct("Quantity",Rectangle.BOTTOM|Rectangle.TOP|Rectangle.RIGHT|Rectangle.LEFT));
		if (!isLiveCategory) {
			pdftable.addCell(createLabelCellForProduct("Price/Unit",Rectangle.BOTTOM|Rectangle.TOP|Rectangle.RIGHT|Rectangle.LEFT));
			pdftable.addCell(createLabelCellForProduct("Commission ( "+shoppingItem.getTax()+" %)",Rectangle.BOTTOM|Rectangle.TOP|Rectangle.RIGHT|Rectangle.LEFT));
		}
		pdftable.addCell(createLabelCellForProduct("Sub Total",Rectangle.BOTTOM|Rectangle.TOP|Rectangle.RIGHT|Rectangle.LEFT));
		
		double price = (currencyRate == 0) ? shoppingItem.getTotalPrice() : shoppingItem.getTotalPrice() / currencyRate;
		double taxPrice = CommonUtil.calculateVat(shoppingOrder.getTotalPrice(), shoppingItem.getTax());
		double subTotal = shoppingOrder.getTotalPrice() - taxPrice;
		double totalPrice = (currencyRate == 0) ? shoppingOrder.getTotalPrice() : shoppingOrder.getTotalPrice() / currencyRate;
		String unitPrice = CommonUtil.getPriceFormat(price, currencyId);
		String quantity =  String.valueOf(shoppingOrder.getQuantity());
		String productDetails = shoppingItem.getProductCode() + "\n" + shoppingItem.getName();
		String currencySymbol = CommonUtil.getCurrencySymbol(currencyId);
		
		taxPrice = (currencyRate == 0) ? taxPrice : taxPrice / currencyRate;
		subTotal = (currencyRate == 0) ? subTotal : subTotal / currencyRate;
		
		//Table Body
		pdftable.addCell(createProductValueCell(String.valueOf(1), Rectangle.RECTANGLE));
		pdftable.addCell(createProductValueCell((Validator.isNotNull(category.getName())) ? category.getName() : "N/A", Rectangle.RECTANGLE));
		pdftable.addCell(createProductValueCell(getProductDetails(shoppingItem, groupId), Rectangle.RECTANGLE));
		pdftable.addCell(createProductValueCell(quantity, Rectangle.RECTANGLE));
		if (!isLiveCategory) {
			pdftable.addCell(createProductValueCell(unitPrice, Rectangle.RECTANGLE));
			pdftable.addCell(createProductValueCell(CommonUtil.getPriceFormat(taxPrice, currencyId), Rectangle.RECTANGLE));
		}
		pdftable.addCell(createProductValueCell(CommonUtil.getPriceFormat(subTotal, currencyId), Rectangle.RECTANGLE)); 
		
		//footer
		int length = isLiveCategory ? 3 : 5;
		for (int i=0;i<length;i++) {
			pdftable.addCell(createLabelCell(StringPool.BLANK, Rectangle.NO_BORDER));
		}
		pdftable.addCell(createLabelCell("Grand Total",Rectangle.RECTANGLE));
		pdftable.addCell(createLabelCell(CommonUtil.getPriceInNumberFormat(totalPrice, currencySymbol), Rectangle.RECTANGLE));
		
		return pdftable;
	}
	
	private static PdfPCell createLabelCell(String text,int border)  {
		Font font = new Font(FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
		PdfPCell cell = new PdfPCell(new Phrase(text,font));
		cell.setBorder(border);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setPaddingTop(5f);
		cell.setPaddingBottom(5f);
		cell.setMinimumHeight(25f);
		return cell;
	}
	
	private static PdfPCell createLabelCellForProduct(String text,int border)  {
		Font font = new Font(FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
		PdfPCell cell = new PdfPCell(new Phrase(text,font));
		cell.setBorder(border);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setBackgroundColor(BaseColor.MAGENTA);
		cell.setPaddingTop(5f);
		cell.setPaddingBottom(5f);
		cell.setMinimumHeight(25f);
		return cell;
	}

	private static PdfPCell createValueCell(String text,int border) {
		Font font = new Font(FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
		PdfPCell cell = new PdfPCell(new Phrase(text,font));
		cell.setBorder(border);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setPaddingTop(5f);
		cell.setPaddingBottom(5f);
		cell.setMinimumHeight(25f);
		return cell;
	}
	
	private static PdfPCell createProductValueCell(String text,int border) {
		Font font = new Font(FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
		PdfPCell cell = new PdfPCell(new Phrase(text,font));
		cell.setBorder(border);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setPaddingTop(5f);
		cell.setPaddingBottom(5f);
		cell.setMinimumHeight(25f);
		return cell;
	}
	
	public static String getAddress(ShoppingOrder shoppingOrder) {
		StringBuilder sb = new StringBuilder();
		sb.append(shoppingOrder.getShippingStreet());
		sb.append(",\n");
		sb.append(shoppingOrder.getShippingCity());
		sb.append(",\n");
		sb.append(CommonUtil.getState(Long.parseLong(shoppingOrder.getShippingState()))); 
		sb.append(",\n");
		sb.append(CommonUtil.getCountry(Long.parseLong(shoppingOrder.getShippingCountry()))); 
		sb.append(",\n");
		sb.append(shoppingOrder.getShippingZip());
		return sb.toString();
	}
	
	public static String getProductDetails(ShoppingItem shoppingItem, long groupId){
		
		StringBuilder sb = new StringBuilder();
		sb.append(shoppingItem.getProductCode());
		sb.append(StringPool.SPACE+StringPool.DASH+StringPool.SPACE);
		sb.append(shoppingItem.getName());
		sb.append("\n");
		if (shoppingItem.getSmallImage() > 0) {
			//String img = "<div><img class=\'product-image\' src='"+CommonUtil.getThumbnailpath(shoppingItem.getSmallImage(), groupId, false)+"'></div>";
			//sb.append(img);
		}
		return sb.toString();
	}
	
	public static String getFilePath(String fileName) {
		
		String tempFolderPath = SystemProperties.get(SystemProperties.TMP_DIR)+File.separator+"liferay" + File.separator + "receipts";
		String filePath = tempFolderPath+File.separator+fileName;	
		File tempFolder = new File(tempFolderPath);
		if (!tempFolder.exists()) {
			tempFolder.mkdir();
		}
		return filePath;
	}
}
