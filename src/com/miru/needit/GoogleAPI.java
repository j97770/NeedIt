package com.miru.needit;

public class GoogleAPI {
	
	// Request
	
	public static final String BASE_URL = "https://ajax.googleapis.com/ajax/services/search/images";
	
	public static final String KEY_VERSION = "v";
	public static final String VERSION = "1.0";
	
	public static final String KEY_QUERY = "q";
	public static final String KEY_RESULT_SIZE = "rsz";
	public static final String KEY_START = "start";
	
	public static final int MAX_RESULT_SIZE = 8;
	public static final int MAX_TOTAL_RESULTS = 64;
	
	public static final String KEY_SIZE = "imgsz";
	public static final String[] SIZES = {"All Sizes", "Small", "Medium", "Large", "XLarge"};
	
	public static final String KEY_COLOR = "imgcolor";
	public static final String[] COLORS = {"All Colors", "Black", "Blue", "Brown", "Gray", "Green", "Orange", 
		                                   "Pink", "Purple", "Red", "Teal", "White", "Yellow"};
	
	public static final String KEY_TYPE = "imgtype";
	public static final String[] TYPES = {"All Types", "Face", "Photo", "Clipart", "Lineart"};
	
	public static final String KEY_SITE = "as_sitesearch";
	public static final String EMPTY = "";
	
	
	// Response
	
	public static final String KEY_STATUS = "responseStatus";
	public static final int RESULT_OK = 200;
	
	public static final String KEY_DATA = "responseData";           
	public static final String KEY_RESULTS = "results";
	
	public static final String KEY_URL = "url";
	public static final String KEY_TB_URL = "tbUrl";

}
