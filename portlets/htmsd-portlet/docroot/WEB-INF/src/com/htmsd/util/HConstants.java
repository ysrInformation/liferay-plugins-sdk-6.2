package com.htmsd.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public interface HConstants {
 
	public static String ITEM_ID = "itemId";
	public static String NAME = "name";
	public static String PRODUCT_CODE = "productCode";
	public static String DESCRIPTION = "description";
	public static String PRICE = "price";
	public static String TOTAL_PRICE = "totalPrice";
	public static String TAX = "tax";
	public static String QUANTITY = "quantity";
	public static String UNILIMITED_QUANTITY = "unlimitedQuanity";
	public static String CATEGORY = "category";
	public static String PARENT_CATEGORY="parentCategory";
	public static String PARENT_CATEGORY_ID="parentCategoryId";
	public static String TAG = "tag";
	public static String CATEGORY_ID = "categoryId";
	public static String TAG_ID = "tagId";
	public static String IMAGE = "image";
	public static String IMAGE_ID = "imageId";
	public static String IMAGE_UPLOAD_PREVIEW = "image_upload_preview";
	public static String VEDIO_URL = "vedioURL";
	public static String DELETE_IMAGE = "deleteImage";
	public static String REMARK = "remark";
	public static String status = "status";
	public static String ITEM_FOLDER_NAME = "Items Added";
	public static String TITLE_REVIEWER = "Title Reviewer";
	public static String DESCRIPTION_REVIEWER = "Description Reveiwer";
	public static String IMAGE_REVIEWER = "Image Reviewer";
	public static String PRICE_REVIEWER = "Price Reviewer";
	public static String STOCK_REVIEWER = "Stock Reviewer";
	public static String VIDEOURL_REVIEWER = "Video Reviewer";
	public static String TAG_REVIEWER = "Tag Reviewer";
	public static String CATEGORY_REVIEWER = "Category Reviewer";
	public static String STAFF_ROLE = "Staff";
	public static String WHOLESALE_DISCOUNT="wholeSaleDiscount";
	public static String WHOLESALE_QUANTITY="wholeSaleQuantity";
	public static String WHOLESALE_PRICE="wholeSalePrice";
	public static String STAFF_REMARKS="staffRemark";
	public static String SMALL_IMAGE = "smallImage";
	public static String TIN = "Tin";
	public static String COMPANY_NAME="Company Name";
	public static int IMAGES_UPLOAD_LIMIT = 5;
	public static int WHOLESALE_LIMIT = 3;
	public static int NEW = 0;
	public static int APPROVE = 1;
	public static int REJECT = 5;
	public static int PENDING = 2;
	public static int SUCCESSFUL = 3;
	public static int DELIVERED = 4;
	public static int NOT_DELIVERED = 6;
	public static int SHIPPING = 7;
	public static int CATEGORY_UPDATED = 11;
	public static int TAG_UPDATED = 12;
	public static int TITLE_UPDATED = 13;
	public static int PRICE_UPDATED = 14;
	public static int IMAGE_UPDATE = 15;
	public static int STOCK_UPDATED = 16;
	public static int DESCRIPTION_UPDATED = 17;
	public static int VEDIO_UPDATED = 18;
	public static int ITEM_UPDATED = 10;
	public static int ITEM_ADDED = 9;
	public static int INITIAL_QUANTITY = 1;
	public static int CANCEL_ORDER = 20;
	public static int MAX_QUANTITY = 500;
	public static String RUPPEE_SYMBOL = "&#8377;";
	public static String ITEM_ADDED_TEMPLATE="ITEM_ADDED_TEMPLATE";
	public static String ITEM_ADDED_UPDATED_TEMPLATE="ITEM_ADDED_UPDATED_TEMPLATE";
	
 
	// PAGES
	public static String PAGE_SHOPPING_LIST_DETAILS = "/html/shoppinglist/details.jsp";
	public static String PAGE_SHOPPING_CART_DETAILS = "/html/shoppingcart/view.jsp";

	public static String PENDING_STATUS = "Pending";
	public static String DELIVERED_STATUS = "Delivered";
	public static String HTMSD = "HTMSD";
	public static String NOT_DELIVERED_STATUS = "Not Delivered";
	public static String SHIPPING_STATUS = "Shipping";
	public static String PROCESSING_STATUS = "Processing";
	public static String ORDER_CANCELLED_STATUS = "Order Cancelled";
	public static String CANCEL_ORDER_STATUS = "Cancel Order";
	public static String ORDER_REVIEW_STATUS = "Order Review";

	public static DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

	public static final String JSP_PAGE = "jspPage";
	public static final String BACK_URL = "backURL";

	public static final int SORT_PRICE_HIGH_TO_LOW = 0;
	public static final int SORT_PRICE_LOW_TO_HIGH = 1;
	public static final String CASH_ON_DELIVERY = "Cash On Delivery";
	
	//EMAIL NOTIFICATION 
	public static final String EMAIL_SUBJECT ="Subject";
	public static final String EMAIL_MESSAGE ="Message";
	
	//Price Symbol
	public static final String RUPEE_SYMBOL = "Rs.";
	
	public static final String CURRENCY_PORTLET_PREFERENCE = "CURRENCY_PORTLET";
	public static final String ASSET_VOCABULARY_ORDER_STATUS = "Order Status";
	public static final int BAR_CODE_LENGTH = 13;
	public static final String CST = "CST";
	public static final String IS_NEW_ITEM ="isNewItem"; 
	
	//OTP
	public static final String OTP_API_KEY = "a7a482de-64a1-11e6-a8cd-00163ef91450";
	public static final String OTP_SEND_ENDPOINT ="https://2factor.in/API/V1/"+OTP_API_KEY+"/SMS/${user_phone_no}/AUTOGEN";
	public static final String OTP_VERIFY_ENDPOINT = "https://2factor.in/API/V1/"+OTP_API_KEY+"/SMS/VERIFY/${session_id}/${otp}";
}
