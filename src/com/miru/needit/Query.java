package com.miru.needit;

import java.util.HashMap;
import java.util.Map.Entry;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class Query {	
	
	private static final String TAG = "Query";
	
	private HashMap<String, String> params;
	private int offset;
	private AsyncHttpClient client;
	private JsonHttpResponseHandler callback;
	private boolean mLoading = false;
	
	public Query(JsonHttpResponseHandler callback) {
		params = new HashMap<String, String>();
		setParam(GoogleAPI.KEY_VERSION, GoogleAPI.VERSION);
		resetSettings();	
		client = new AsyncHttpClient();
		this.callback = callback;
	}
	
	public void setQuery(String query) {
		setParam(GoogleAPI.KEY_QUERY, query);
		offset = 0;
		setLoading(false);
	}
	
	public void setParam(String key, String value) {
		params.put(key, value);
	}
	
	public void setParam(String key, int value) {
		params.put(key, String.valueOf(value));
	}
	
	private String buildUrl() {
		Builder bd = Uri.parse(GoogleAPI.BASE_URL).buildUpon();
		for (Entry<String, String> param : params.entrySet()) {
			if (!param.getValue().equals(""))
				bd.appendQueryParameter(param.getKey(), param.getValue());
		}
		return bd.build().toString();
	}
	
	public boolean hasNext() {
		return offset < GoogleAPI.MAX_TOTAL_RESULTS;
	}
	
	public void next() {
		setLoading(true);
		setParam(GoogleAPI.KEY_RESULT_SIZE, GoogleAPI.MAX_RESULT_SIZE);
		setParam(GoogleAPI.KEY_START, offset);
		offset += GoogleAPI.MAX_RESULT_SIZE;
		
		String url = buildUrl();
		Log.d(TAG, "Request url: " + url);
		client.get(url, callback);
	}
	
	public HashMap<String, String> getParams() {
		return params;
	}

	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}

	public void resetSettings() {
		setParam(GoogleAPI.KEY_SIZE, GoogleAPI.SIZES[0]);
		setParam(GoogleAPI.KEY_COLOR, GoogleAPI.COLORS[0]);
		setParam(GoogleAPI.KEY_TYPE, GoogleAPI.TYPES[0]);
		setParam(GoogleAPI.KEY_SITE, GoogleAPI.EMPTY);
	}

	public boolean isLoading() {
		return mLoading;
	}

	public void setLoading(boolean loading) {
		mLoading = loading;
	}
	
}
