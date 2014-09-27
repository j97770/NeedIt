package com.miru.needit;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils.TruncateAt;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

public class ImageActivity extends Activity {

	public static final String URL = "url";
	private SmartImageView ivImage;
	private String mUrl;
	TextView imgurl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		//FloatingViewService.hideView();
		mUrl = getIntent().getStringExtra(URL);

		ivImage = (SmartImageView) findViewById(R.id.ivPhoto);
		ivImage.setImageUrl(mUrl);
		
		imgurl = (TextView) findViewById(R.id.imgurl);
		
		imgurl.setText(mUrl);		
		imgurl.setFocusable(true);
		imgurl.setEllipsize(TruncateAt.MARQUEE);
		imgurl.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG); // Text Under Line
		
		imgurl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent openweb = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
				startActivity(openweb);
			}
		});
		
		ImageButton share = (ImageButton) findViewById(R.id.share);
		share.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Uri imageUri = Uri.parse(mUrl);
			      Intent intent = new Intent(Intent.ACTION_SEND);
			      intent.setType("image/jpeg");

			      intent.putExtra(Intent.EXTRA_STREAM, imageUri);
			      startActivity(Intent.createChooser(intent , "Share"));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.image, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*if (item.getItemId() == R.id.action_share) {

			// Fire implicit intent
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, mUrl);
			shareIntent.setType("image/*");
			startActivity(Intent.createChooser(shareIntent, getResources()
					.getText(R.string.share)));
			return true;
		}*/
		return super.onOptionsItemSelected(item); 
	}

}
