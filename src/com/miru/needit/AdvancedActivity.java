package com.miru.needit;

import java.util.Arrays;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.miru.needit.DialogActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class AdvancedActivity extends Activity {

	private static final String TAG = "AdvancedActivity";
	
	private Spinner spnImageSize;
	private Spinner spnColorFilter;
	private Spinner spnImageType;
	private EditText etSiteFilter;
	
	public static final String PARAMS = "params";
	private HashMap<String, String> mParams = DialogActivity.mQuery.getParams();
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_advanced);
		setupViews();
		setupAdapters();
		setupSettings();
	}
	
	private void setupViews() {
		spnImageSize = (Spinner) findViewById(R.id.spnImageSize);
		spnColorFilter = (Spinner) findViewById(R.id.spnColorFilter);
		spnImageType = (Spinner) findViewById(R.id.spnImageType);
		etSiteFilter = (EditText) findViewById(R.id.etSiteFilter);
	}
	
	private void setupAdapters() {
		setupAdapter(spnImageSize, R.id.spnImageSize, GoogleAPI.SIZES);
		setupAdapter(spnColorFilter, R.id.spnColorFilter, GoogleAPI.COLORS);
		setupAdapter(spnImageType, R.id.spnImageType, GoogleAPI.TYPES);		
	}
	
	private void setupAdapter(Spinner spn, int viewResource, String[] values) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn.setAdapter(adapter);
	}

	private void setupSettings() {
		mParams = DialogActivity.mQuery.getParams();
		setSpinnerFromParams(spnImageSize, GoogleAPI.KEY_SIZE, GoogleAPI.SIZES);
		setSpinnerFromParams(spnColorFilter, GoogleAPI.KEY_COLOR, GoogleAPI.COLORS);
		setSpinnerFromParams(spnImageType, GoogleAPI.KEY_TYPE, GoogleAPI.TYPES);
		etSiteFilter.setText(mParams.get(GoogleAPI.KEY_SITE));
	}
	
	private void setSpinnerFromParams(Spinner spn, String apiKey, String[] apiValues) {
		spn.setSelection(Arrays.asList(apiValues).indexOf(mParams.get(apiKey)));
	}
	
	public void save(View v) {
		updateParams();
		Intent data = new Intent();
		data.putExtra(PARAMS, mParams);
		setResult(Activity.RESULT_OK, data);
		finish();
	}
	
	private void updateParams() {
		mParams.put(GoogleAPI.KEY_TYPE, (String) spnImageType.getSelectedItem());
		mParams.put(GoogleAPI.KEY_COLOR, (String) spnColorFilter.getSelectedItem());
		mParams.put(GoogleAPI.KEY_SIZE, (String) spnImageSize.getSelectedItem());
		mParams.put(GoogleAPI.KEY_SITE, etSiteFilter.getText().toString());
	}
	
	public void reset(View v) {
		spnImageType.setSelection(0);
		spnColorFilter.setSelection(0);
		spnImageSize.setSelection(0);
		etSiteFilter.setText(GoogleAPI.EMPTY);
	}

}
