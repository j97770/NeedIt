package com.miru.needit;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ImageResult {
	
	private static final String TAG = "ImageResult";
	
	private String fullUrl;
	private String thumbUrl;
	
	public ImageResult(JSONObject json) {
		try {
			fullUrl = json.getString(GoogleAPI.KEY_URL);
			thumbUrl = json.getString(GoogleAPI.KEY_TB_URL);
		} catch (JSONException e) {
			Log.e(TAG, "Error parsing image result", e);
		}		
	}
	
	public static ArrayList<ImageResult> fromJSONArray(JSONArray results) {
		ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
		
		try {
			for (int i = 0; i < results.length(); i++) {
				imageResults.add(new ImageResult(results.getJSONObject(i)));
			}			
		} catch (JSONException e) {
			Log.e(TAG, "Error parsing results array", e);
		}
		
		return imageResults;
	}
	
	public String getFullUrl() {
		return fullUrl;
	}
	
	public String getThumbUrl() {
		return thumbUrl;
	}

	@Override
	public String toString() {
		return thumbUrl;
	}
	
}
