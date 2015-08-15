package com.htmsd.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public interface HConstants {

	public static String ITEM_ID = "itemId";
	public static String NAME = "name";
	public static String DESCRIPTION = "description";
	public static String PRICE = "price";
	public static String TOTAL_PRICE="totalPrice";
	public static String CATEGORY = "category";
	public static String TAG = "tag";
	public static String CATEGORY_ID = "categoryId";
	public static String TAG_ID = "tagId";
	public static String IMAGE = "image";
	public static String IMAGE_ID = "imageId";
	public static String IMAGE_UPLOAD_PREVIEW = "image_upload_preview";
	public static String DELETE_IMAGE = "deleteImage";
	public static String status = "status";
	public static String ITEM_FOLDER_NAME = "Items Added";

	public static int IMAGES_UPLOAD_LIMIT = 5;
	public static int NEW = 0;
	public static int APPROVE = 1;
	public static int REJECT = 5;
	public static int PENDING = 2;
	public static int SUCCESSFUL = 3;
	public static int DELIVERED = 4;  
	public static String RUPPEE_SYMBOL = "&#8377;";
	
	public static String PENDING_STATUS = "Pending";
	public static String DELIVERED_STATUS = "Delivered";
	
	public static DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

}
