package com.miru.needit;

import com.nhn.android.maps.NMapView;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

public class FloatingViewService extends Service {

	private WindowManager windowManager;
	private static ImageButton topHome;

	private View layout;
	private WindowManager.LayoutParams paramsF;
	private int mode;
	Handler handler = new Handler();
	private long touchtime;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static void showView() {
		topHome.setVisibility(View.VISIBLE);
	}

	public static void hideView() {
		topHome.setVisibility(View.GONE);
	}

	public void onCreate() {
		super.onCreate();
		
		String viewstyle = getSharedPreferences(
				getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getString("viewstyle", "VIEWSTYLE_FLOATING");

		if (viewstyle.equals("VIEWSTYLE_FLOATING")) {
			updateView();
		} else if (viewstyle.equals("VIEWSTYLE_NOTIFICATION")) {
			stopService(new Intent(FloatingViewService.this, FloatingViewService.class));
		}
	}
	
	public void updateView() {
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		layout = ((LayoutInflater) getApplicationContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.layout_floating, null);
		topHome = (ImageButton) layout.findViewById(R.id.floating_button);

		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;

		windowManager.addView(layout, params);
		paramsF = params;
		attach();
		mode = 1;
		try {
			topHome.setClickable(true);
			topHome.setImageResource(R.drawable.ic_float);
			topHome.setOnTouchListener(new View.OnTouchListener() {
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						topHome.setImageResource(R.drawable.ic_float_pressed);
						topHome.setX(0.0f);
						touchtime = System.currentTimeMillis();
						// Get current time in nano seconds.

						initialX = paramsF.x;
						initialY = paramsF.y;
						initialTouchX = event.getRawX();
						initialTouchY = event.getRawY();
						mode = 0;
						break;
					case MotionEvent.ACTION_UP:
						topHome.setImageResource(R.drawable.ic_float);
						if (System.currentTimeMillis() - touchtime < 75) {
							Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
							vibe.vibrate(25);
							topHome.setVisibility(View.GONE);
							Intent lsp = new Intent(getBaseContext(),
									DialogActivity.class);
							lsp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							getApplication().startActivity(lsp);
						}
						mode = 1;
						break;
					case MotionEvent.ACTION_MOVE:
						paramsF.x = initialX
								+ (int) (event.getRawX() - initialTouchX);
						paramsF.y = initialY
								+ (int) (event.getRawY() - initialTouchY);
						windowManager.updateViewLayout(layout, paramsF);
						mode = 0;
						break;
					}
					return false;
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void attach() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				attach();

			}
		}, 10);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int x = paramsF.x + layout.getWidth() / 2;
		Log.e("Needit", topHome.getX() + "");
		if (mode == 1) {
			if (paramsF.x < topHome.getWidth() / 20) {
				if (topHome.getX() > -topHome.getWidth() / 2) {
					paramsF.x = 0;
					windowManager.updateViewLayout(layout, paramsF);
					topHome.setX(topHome.getX()
							- (topHome.getWidth() / 2 + topHome.getX()) / 10);
					return;
				}
			} else if (size.x - paramsF.x - topHome.getWidth() < topHome
					.getWidth() / 20) {
				if (topHome.getX() < topHome.getWidth() / 2) {
					paramsF.x = size.x - topHome.getWidth();
					windowManager.updateViewLayout(layout, paramsF);
					topHome.setX(topHome.getX()
							+ (topHome.getWidth() / 2 - topHome.getX()) / 10);
					return;
				}
			}
		}
		if (mode != 1)
			return;
		if (x < 5 || size.x - x - layout.getWidth() / 2 < 5) {
			return;
		}
		if (x < size.x / 2) {
			paramsF.x -= paramsF.x / 10;
		} else {
			paramsF.x += (size.x - layout.getWidth() - paramsF.x) / 10;
		}
		windowManager.updateViewLayout(layout, paramsF);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (topHome != null)
			windowManager.removeView(layout);
	}
}
