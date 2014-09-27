package com.miru.needit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.miru.needit.GoogleAPI;
import com.miru.needit.ImageAdapter;
import com.miru.needit.ImageResult;
import com.miru.needit.Query;
import com.miru.needit.AdvancedActivity;
import com.miru.needit.ImageActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.miru.needit.NMapPOIflagType;
import com.miru.needit.NMapViewerResourceProvider;

import com.nhn.android.maps.NMapView;
import com.miru.needit.ApplicationInfo;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView.OnMapStateChangeListener;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;

public class DialogActivity extends NMapActivity implements
		OnMapStateChangeListener, OnCalloutOverlayListener {

	NMapView mMapView;
	private static final String API_KEY = "c3e4e1121c24cba0817a2fa2223e58c4";
	private NMapController mMapController;
	// 지도 리소스 제공자를 불러옴
	NMapViewerResourceProvider mMapViewerResourceProvider = null;
	// 지도 리소스 오버레이 매니저
	NMapOverlayManager mOverlayManager;

	LayoutAnimationController controller;

	private ArrayList<String> array_sort = new ArrayList<String>();
	int textlength = 0;
	private GridView mGrid;
	private static ArrayList<ApplicationInfo> mApplications;

	private ArrayList<ImageResult> mImageResults;
	private ImageAdapter mAdapter;
	static Query mQuery;

	static final int REQUEST_SETTINGS = 0;

	WebView webview;
	View webview_divider;
	ImageButton openweb_google;
	ImageButton openweb_naver;
	ImageButton openweb_daum;
	ImageButton openweb_youtube;
	ImageButton openweb_wikipedia;

	ImageButton imagesearch_google;
	ImageButton back;

	LinearLayout IconBar;
	ImageView app_icon;
	LinearLayout layoutMenu;
	ImageButton menuSettings;
	ImageButton menuExit;
	ImageButton menuHide;

	EditText search_google;
	EditText search_naver;
	EditText search_daum;
	EditText search_youtube;
	EditText search_wikipedia;
	EditText search_googleimage;
	GridView gvImages;

	LinearLayout applicationMenu;
	LinearLayout locationMenu;
	LinearLayout layout_googleimage;

	Button searchNearby;
	Button showMapView;
	Button hideMapView;

	private TextView myLocation;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mMapView = new NMapView(this);

		String viewstyle = getSharedPreferences(
				getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getString("viewstyle", "VIEWSTYLE_FLOATING");

		if (viewstyle.equals("VIEWSTYLE_FLOATING")) {
			FloatingViewService.hideView();
		} else if (viewstyle.equals("VIEWSTYLE_NOTIFICATION")) {
		}

		setContentView(R.layout.activity_dialog);

		imagesearch_google = (ImageButton) findViewById(R.id.imagesearch_google);
		back = (ImageButton) findViewById(R.id.back);

		// 탭 활성화
		activateTab();

		// 구글 이미지 검색
		setupGoogleImageSearch();
		setupAdapter();
		setupQuery();
		setupOnScrollListener();

		// 맵뷰 활성화
		activateMapView();

		// 웹뷰 활성화
		activateWebView();

		// 검색바 활성화
		activateSearchbars();

		// 내 위치 텍스트
		myLocation = (TextView) findViewById(R.id.myLocation);
		myLocation.setSingleLine(true);
		myLocation.setEllipsize(TruncateAt.MARQUEE);
		myLocation.setSelected(true);

		mContext = getBaseContext();

		this.currentLocation();

		app_icon = (ImageView) findViewById(R.id.app_icon);
		layoutMenu = (LinearLayout) findViewById(R.id.layout_menu);
		menuSettings = (ImageButton) findViewById(R.id.btn_settings);
		menuExit = (ImageButton) findViewById(R.id.btn_exit);
		menuHide = (ImageButton) findViewById(R.id.btn_hide);

		final Animation menu_animation = AnimationUtils.loadAnimation(mContext,
				R.anim.dock_right_enter);
		menu_animation.setDuration(100);
		final Animation menu_hide_animation = AnimationUtils.loadAnimation(
				mContext, R.anim.fast_fade_out);
		menu_hide_animation.setDuration(250);

		app_icon.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibe.vibrate(25);

				layoutMenu.setVisibility(View.VISIBLE);
				layoutMenu.setAnimation(menu_animation);
				return false;
			}
		});

		menuSettings.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent settings = (new Intent(DialogActivity.this,
						SettingsActivity.class));
				startActivity(settings);
			}
		});

		menuExit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				app_exit();
			}
		});

		menuHide.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				layoutMenu.setVisibility(View.GONE);
				layoutMenu.setAnimation(menu_hide_animation);
			}
		});

		search_google = (EditText) findViewById(R.id.search_google);
		search_naver = (EditText) findViewById(R.id.search_naver);
		search_daum = (EditText) findViewById(R.id.search_daum);
		search_youtube = (EditText) findViewById(R.id.search_youtube);
		search_wikipedia = (EditText) findViewById(R.id.search_wikipedia);
		search_googleimage = (EditText) findViewById(R.id.search_googleimage);

		openweb_google = (ImageButton) findViewById(R.id.openweb_google);
		openweb_naver = (ImageButton) findViewById(R.id.openweb_naver);
		openweb_daum = (ImageButton) findViewById(R.id.openweb_daum);
		openweb_youtube = (ImageButton) findViewById(R.id.openweb_youtube);
		openweb_wikipedia = (ImageButton) findViewById(R.id.openweb_wikipedia);

		searchNearby = (Button) findViewById(R.id.searchNearby);
		showMapView = (Button) findViewById(R.id.showMapView);
		hideMapView = (Button) findViewById(R.id.hideMapView);

		IconBar = (LinearLayout) findViewById(R.id.layout_icon);

		search_google.setText("");
		search_naver.setText("");
		search_daum.setText("");
		search_youtube.setText("");
		search_wikipedia.setText("");

		applicationMenu = (LinearLayout) findViewById(R.id.app_menu);
		locationMenu = (LinearLayout) findViewById(R.id.menu_location);

		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				activateTab();
				gvImages.setVisibility(View.GONE);
				search_googleimage.setText("");
				IconBar.setVisibility(View.VISIBLE);

			}
		});

		openweb_google.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String url = webview.getUrl();
				Intent openweb = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(openweb);

			}
		});

		openweb_naver.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String url = webview.getUrl();
				Intent openweb = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(openweb);

			}
		});

		openweb_daum.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String url = webview.getUrl();
				Intent openweb = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(openweb);

			}
		});

		openweb_youtube.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String url = webview.getUrl();
				Intent openweb = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(openweb);

			}
		});

		openweb_wikipedia.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String url = webview.getUrl();
				Intent openweb = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(openweb);

			}
		});

		final ImageButton voicegoogle = (ImageButton) findViewById(R.id.voicesearch_google);
		final ImageButton voicenaver = (ImageButton) findViewById(R.id.voicesearch_naver);
		final ImageButton voicedaum = (ImageButton) findViewById(R.id.voicesearch_daum);
		final ImageButton voiceyoutube = (ImageButton) findViewById(R.id.voicesearch_youtube);
		final ImageButton voicewikipedia = (ImageButton) findViewById(R.id.voicesearch_wikipedia);
		final ImageButton voicegoogleimage = (ImageButton) findViewById(R.id.voicesearch_googleimage);

		voicegoogle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				voiceGoogle();

			}
		});

		voicenaver.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				voiceNaver();

			}
		});

		voicedaum.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				voiceDaum();

			}
		});

		voiceyoutube.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				voiceYoutube();

			}
		});

		voicewikipedia.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				voiceWikipedia();

			}
		});

		voicegoogleimage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				voiceGoogleImage();

			}
		});
	}

	public void activateWebView() {
		webview = (WebView) findViewById(R.id.webview_searchResult);
		webview_divider = findViewById(R.id.webview_divider);
		webview.setWebViewClient(new WebClient());
		WebSettings set = webview.getSettings();
		set.setJavaScriptEnabled(true);
		set.setEnableSmoothTransition(true);
		set.setBuiltInZoomControls(true);
		// webview.loadUrl();

		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		// 웹뷰 확대버튼 설정
		boolean hidezoom = getSharedPreferences(
				getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getBoolean("hidezoom", true);
		if (hidezoom == true) {
			set.setBuiltInZoomControls(false);
		} else if (hidezoom == false) {
			set.setBuiltInZoomControls(true);
		}
	}

	public void activateMapView() {

		mMapView.setClickable(true);
		mMapController = mMapView.getMapController();
		mMapView.setBuiltInZoomControls(true, null);
		mMapView = (NMapView) findViewById(R.id.mapView);
		mMapView.setApiKey("c3e4e1121c24cba0817a2fa2223e58c4");
		mMapController.setMapViewPanoramaMode(true);
		mMapController.setMapViewBicycleMode(true);

		String mapviewmode = getSharedPreferences(
				getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getString("mapviewmode", "VIEW_MODE_HYBRID");

		if (mapviewmode.equals("VIEW_MODE_HYBRID")) {
			mMapController.setMapViewMode(NMapView.VIEW_MODE_HYBRID);
		} else if (mapviewmode.equals("VIEW_MODE_VECTOR")) {
			mMapController.setMapViewMode(NMapView.VIEW_MODE_VECTOR);
		}

		// initialize map view
		mMapView.setClickable(true);
		mMapView.setEnabled(true);
		mMapView.setFocusable(true);
		mMapView.setFocusableInTouchMode(true);
		mMapView.requestFocus();

		// use map controller to zoom in/out, pan and set map center, zoom level
		// etc.
		mMapController = mMapView.getMapController();

		// use built in zoom controls
		NMapView.LayoutParams lp = new NMapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				NMapView.LayoutParams.BOTTOM_RIGHT);
		mMapView.setBuiltInZoomControls(true, lp);

	}

	public void activateTab() {
		final LinearLayout searchgoogle = (LinearLayout) findViewById(R.id.google);
		final LinearLayout searchnaver = (LinearLayout) findViewById(R.id.naver);
		final LinearLayout searchdaum = (LinearLayout) findViewById(R.id.daum);
		final LinearLayout searchyoutube = (LinearLayout) findViewById(R.id.youtube);
		final LinearLayout searchwikipedia = (LinearLayout) findViewById(R.id.wikipedia);
		final LinearLayout searchlocal = (LinearLayout) findViewById(R.id.local);
		final LinearLayout searchgoogleimage = (LinearLayout) findViewById(R.id.layout_googleimage);

		final ImageButton google = (ImageButton) findViewById(R.id.tab_google);
		final ImageButton naver = (ImageButton) findViewById(R.id.tab_naver);
		final ImageButton daum = (ImageButton) findViewById(R.id.tab_daum);
		final ImageButton youtube = (ImageButton) findViewById(R.id.tab_youtube);
		final ImageButton wikipedia = (ImageButton) findViewById(R.id.tab_wikipedia);
		final ImageButton local = (ImageButton) findViewById(R.id.tab_local);

		google.setSelected(true);
		searchgoogle.setVisibility(View.VISIBLE);
		searchnaver.setVisibility(View.GONE);
		searchdaum.setVisibility(View.GONE);
		searchyoutube.setVisibility(View.GONE);
		searchwikipedia.setVisibility(View.GONE);
		searchlocal.setVisibility(View.GONE);
		searchgoogleimage.setVisibility(View.GONE);

		PreferenceManager.setDefaultValues(this, R.xml.pref_tabsettings, true);
		// 네이버 탭 숨김
		boolean tabnaver = getSharedPreferences(
				getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getBoolean("tabnaver", false);
		if (tabnaver == false) {
			naver.setVisibility(View.GONE);
		} else if (tabnaver == true) {
			naver.setVisibility(View.VISIBLE);
		}
		// 다음 탭 설정
		boolean tabdaum = getSharedPreferences(
				getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getBoolean("tabdaum", false);
		if (tabdaum == false) {
			daum.setVisibility(View.GONE);
		} else if (tabdaum == true) {
			daum.setVisibility(View.VISIBLE);
		}
		// 유튜브 탭 설정
		boolean tabyoutube = getSharedPreferences(
				getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getBoolean("tabyoutube", false);
		if (tabyoutube == false) {
			youtube.setVisibility(View.GONE);
		} else if (tabyoutube == true) {
			youtube.setVisibility(View.VISIBLE);
		}
		// 위키백과 탭 설정
		boolean tabwikipedia = getSharedPreferences(
				getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getBoolean("tabwikipedia", false);
		if (tabwikipedia == false) {
			wikipedia.setVisibility(View.GONE);
		} else if (tabwikipedia == true) {
			wikipedia.setVisibility(View.VISIBLE);
		}
		// 로컬 탭 설정
		boolean tablocal = getSharedPreferences(
				getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getBoolean("tablocal", false);
		if (tablocal == false) {
			local.setVisibility(View.GONE);
		} else if (tablocal == true) {
			local.setVisibility(View.VISIBLE);
		}

		google.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				google.setSelected(true);
				naver.setSelected(false);
				daum.setSelected(false);
				youtube.setSelected(false);
				wikipedia.setSelected(false);
				local.setSelected(false);

				searchgoogle.setVisibility(View.VISIBLE);
				searchnaver.setVisibility(View.GONE);
				searchdaum.setVisibility(View.GONE);
				searchlocal.setVisibility(View.GONE);
				searchyoutube.setVisibility(View.GONE);
				searchwikipedia.setVisibility(View.GONE);
				searchgoogleimage.setVisibility(View.GONE);

				webview.setVisibility(View.GONE);
				webview_divider.setVisibility(View.GONE);

				mMapView.setVisibility(View.GONE);

				applicationMenu.setVisibility(View.GONE);

				search_naver.setText("");
				search_daum.setText("");
				search_youtube.setText("");
				search_wikipedia.setText("");

				openweb_naver.setVisibility(View.GONE);
				openweb_daum.setVisibility(View.GONE);
				openweb_youtube.setVisibility(View.GONE);
				openweb_wikipedia.setVisibility(View.GONE);

				IconBar.setVisibility(View.VISIBLE);

				hideMapView.setVisibility(View.GONE);
				showMapView.setVisibility(View.VISIBLE);

			}
		});

		imagesearch_google.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO
				searchgoogle.setVisibility(View.GONE);
				searchgoogleimage.setVisibility(View.VISIBLE);
				webview_divider.setVisibility(View.GONE);
				webview.setVisibility(View.GONE);
				hideMapView.setVisibility(View.GONE);
				showMapView.setVisibility(View.VISIBLE);
			}
		});

		naver.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				naver.setSelected(true);
				google.setSelected(false);
				daum.setSelected(false);
				youtube.setSelected(false);
				local.setSelected(false);
				wikipedia.setSelected(false);

				searchnaver.setVisibility(View.VISIBLE);
				searchgoogle.setVisibility(View.GONE);
				searchdaum.setVisibility(View.GONE);
				searchlocal.setVisibility(View.GONE);
				searchyoutube.setVisibility(View.GONE);
				searchwikipedia.setVisibility(View.GONE);
				searchgoogleimage.setVisibility(View.GONE);

				webview.setVisibility(View.GONE);
				webview_divider.setVisibility(View.GONE);

				gvImages.setVisibility(View.GONE);
				search_googleimage.setText("");

				mMapView.setVisibility(View.GONE);

				applicationMenu.setVisibility(View.GONE);

				search_google.setText("");
				search_daum.setText("");
				search_youtube.setText("");
				search_wikipedia.setText("");

				openweb_google.setVisibility(View.GONE);
				openweb_daum.setVisibility(View.GONE);
				openweb_youtube.setVisibility(View.GONE);
				openweb_wikipedia.setVisibility(View.GONE);

				IconBar.setVisibility(View.VISIBLE);

				hideMapView.setVisibility(View.GONE);
				showMapView.setVisibility(View.VISIBLE);
			}
		});

		daum.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				daum.setSelected(true);
				naver.setSelected(false);
				google.setSelected(false);
				youtube.setSelected(false);
				wikipedia.setSelected(false);
				local.setSelected(false);

				searchdaum.setVisibility(View.VISIBLE);
				searchnaver.setVisibility(View.GONE);
				searchgoogle.setVisibility(View.GONE);
				searchlocal.setVisibility(View.GONE);
				searchyoutube.setVisibility(View.GONE);
				searchwikipedia.setVisibility(View.GONE);
				searchgoogleimage.setVisibility(View.GONE);

				webview.setVisibility(View.GONE);
				webview_divider.setVisibility(View.GONE);

				gvImages.setVisibility(View.GONE);
				search_googleimage.setText("");

				mMapView.setVisibility(View.GONE);

				applicationMenu.setVisibility(View.GONE);

				search_naver.setText("");
				search_google.setText("");
				search_youtube.setText("");
				search_wikipedia.setText("");

				openweb_naver.setVisibility(View.GONE);
				openweb_google.setVisibility(View.GONE);
				openweb_youtube.setVisibility(View.GONE);
				openweb_wikipedia.setVisibility(View.GONE);

				IconBar.setVisibility(View.VISIBLE);

				hideMapView.setVisibility(View.GONE);
				showMapView.setVisibility(View.VISIBLE);
			}
		});

		youtube.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				youtube.setSelected(true);
				local.setSelected(false);
				naver.setSelected(false);
				daum.setSelected(false);
				google.setSelected(false);
				wikipedia.setSelected(false);

				searchyoutube.setVisibility(View.VISIBLE);
				searchlocal.setVisibility(View.GONE);
				searchnaver.setVisibility(View.GONE);
				searchdaum.setVisibility(View.GONE);
				searchgoogle.setVisibility(View.GONE);
				searchwikipedia.setVisibility(View.GONE);
				searchgoogleimage.setVisibility(View.GONE);

				webview.setVisibility(View.GONE);
				webview_divider.setVisibility(View.GONE);

				gvImages.setVisibility(View.GONE);
				search_googleimage.setText("");

				mMapView.setVisibility(View.GONE);

				search_naver.setText("");
				search_daum.setText("");
				search_google.setText("");

				openweb_naver.setVisibility(View.GONE);
				openweb_daum.setVisibility(View.GONE);
				openweb_google.setVisibility(View.GONE);
				openweb_wikipedia.setVisibility(View.GONE);

				applicationMenu.setVisibility(View.GONE);

				IconBar.setVisibility(View.VISIBLE);

				hideMapView.setVisibility(View.GONE);
				showMapView.setVisibility(View.VISIBLE);
			}
		});

		wikipedia.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wikipedia.setSelected(true);
				youtube.setSelected(false);
				local.setSelected(false);
				naver.setSelected(false);
				daum.setSelected(false);
				google.setSelected(false);

				searchwikipedia.setVisibility(View.VISIBLE);
				searchyoutube.setVisibility(View.GONE);
				searchlocal.setVisibility(View.GONE);
				searchnaver.setVisibility(View.GONE);
				searchdaum.setVisibility(View.GONE);
				searchgoogle.setVisibility(View.GONE);
				searchgoogleimage.setVisibility(View.GONE);

				webview.setVisibility(View.GONE);
				webview_divider.setVisibility(View.GONE);

				gvImages.setVisibility(View.GONE);
				search_googleimage.setText("");

				mMapView.setVisibility(View.GONE);

				search_naver.setText("");
				search_daum.setText("");
				search_google.setText("");
				search_youtube.setText("");

				openweb_naver.setVisibility(View.GONE);
				openweb_daum.setVisibility(View.GONE);
				openweb_google.setVisibility(View.GONE);
				openweb_youtube.setVisibility(View.GONE);

				applicationMenu.setVisibility(View.GONE);

				IconBar.setVisibility(View.VISIBLE);

				hideMapView.setVisibility(View.GONE);
				showMapView.setVisibility(View.VISIBLE);
			}
		});

		local.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				local.setSelected(true);
				naver.setSelected(false);
				daum.setSelected(false);
				google.setSelected(false);
				youtube.setSelected(false);
				wikipedia.setSelected(false);
				// searchlocal.setVisibility(View.VISIBLE);
				searchnaver.setVisibility(View.GONE);
				searchdaum.setVisibility(View.GONE);
				searchgoogle.setVisibility(View.GONE);
				searchyoutube.setVisibility(View.GONE);
				searchwikipedia.setVisibility(View.GONE);
				searchgoogleimage.setVisibility(View.GONE);

				webview.setVisibility(View.GONE);
				webview_divider.setVisibility(View.GONE);

				gvImages.setVisibility(View.GONE);
				search_googleimage.setText("");

				mMapView.setVisibility(View.GONE);

				applicationMenu.setVisibility(View.VISIBLE);

				IconBar.setVisibility(View.GONE);

				hideMapView.setVisibility(View.GONE);
				showMapView.setVisibility(View.VISIBLE);

				loadApplications(true);
				bindApplications();
			}
		});
	}

	public void activateSearchbars() {
		final EditText etgoogle = (EditText) findViewById(R.id.search_google);
		final EditText etnaver = (EditText) findViewById(R.id.search_naver);
		final EditText etdaum = (EditText) findViewById(R.id.search_daum);
		final EditText etyoutube = (EditText) findViewById(R.id.search_youtube);
		final EditText etwikipedia = (EditText) findViewById(R.id.search_wikipedia);
		final EditText etgoogleimage = (EditText) findViewById(R.id.search_googleimage);
		final EditText etlocal = (EditText) findViewById(R.id.search_local);

		etgoogle.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				switch (actionId) {
				case EditorInfo.IME_ACTION_SEARCH:
					String value = etgoogle.getText().toString();
					;
					// Intent search = new Intent(Intent.ACTION_VIEW,
					// Uri.parse("https://www.google.co.kr/search?q=" + value));
					// startActivity(search);
					IconBar.setVisibility(View.GONE);

					webview_divider.setVisibility(View.VISIBLE);
					webview.setVisibility(View.VISIBLE);
					webview.loadUrl("https://www.google.co.kr/search?q="
							+ value);
					openweb_google.setVisibility(View.VISIBLE);
					break;
				default:
					return false;
				}
				return true;
			}
		});

		etnaver.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				switch (actionId) {
				case EditorInfo.IME_ACTION_SEARCH:
					String value = etnaver.getText().toString();
					;
					// Intent search = new Intent(Intent.ACTION_VIEW,
					// Uri.parse("http://m.search.naver.com/search.naver?where=nexearch&query="
					// + value));
					// startActivity(search);
					IconBar.setVisibility(View.GONE);

					webview_divider.setVisibility(View.VISIBLE);
					webview.setVisibility(View.VISIBLE);
					webview.loadUrl("http://m.search.naver.com/search.naver?where=nexearch&query="
							+ value);
					openweb_naver.setVisibility(View.VISIBLE);
					break;
				default:
					return false;
				}
				return true;
			}
		});

		etdaum.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				switch (actionId) {
				case EditorInfo.IME_ACTION_SEARCH:
					String value = etdaum.getText().toString();
					;
					// Intent search = new Intent(Intent.ACTION_VIEW,
					// Uri.parse("http://m.search.daum.net/search?w=tot&nil_mtopsearch=btn&DA=YZRR&q="
					// + value));
					// startActivity(search);
					IconBar.setVisibility(View.GONE);

					webview_divider.setVisibility(View.VISIBLE);
					webview.setVisibility(View.VISIBLE);
					webview.loadUrl("http://m.search.daum.net/search?w=tot&nil_mtopsearch=btn&DA=YZRR&q="
							+ value);
					openweb_daum.setVisibility(View.VISIBLE);
					break;
				default:
					return false;
				}
				return true;
			}
		});

		etyoutube.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				switch (actionId) {
				case EditorInfo.IME_ACTION_SEARCH:
					String value = etyoutube.getText().toString();
					;
					// Intent search = new Intent(Intent.ACTION_VIEW,
					// Uri.parse("http://m.youtube.com/#/results?q=" + value));
					// startActivity(search);
					IconBar.setVisibility(View.GONE);

					webview_divider.setVisibility(View.VISIBLE);
					webview.setVisibility(View.VISIBLE);
					webview.loadUrl("http://m.youtube.com/#/results?q=" + value);
					openweb_youtube.setVisibility(View.VISIBLE);
					break;
				default:
					return false;
				}
				return true;
			}
		});

		etwikipedia.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				switch (actionId) {
				case EditorInfo.IME_ACTION_SEARCH:
					String value = etwikipedia.getText().toString();
					;
					IconBar.setVisibility(View.GONE);

					webview_divider.setVisibility(View.VISIBLE);
					webview.setVisibility(View.VISIBLE);
					webview.loadUrl("http://ko.m.wikipedia.org/w/index.php?search="
							+ value);
					openweb_wikipedia.setVisibility(View.VISIBLE);
					break;
				default:
					return false;
				}
				return true;
			}
		});

		etgoogleimage.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				switch (actionId) {
				case EditorInfo.IME_ACTION_SEARCH:
					gvImages.setVisibility(View.VISIBLE);
					IconBar.setVisibility(View.GONE);
					onSearch(v);
					break;
				default:
					return false;
				}
				return true;
			}
		});
	}

	private void setupGoogleImageSearch() {
		gvImages = (GridView) findViewById(R.id.gvImages);

		gvImages.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ImageResult imageInfo = (ImageResult) parent
						.getItemAtPosition(position);
				Intent i = new Intent(getApplicationContext(),
						ImageActivity.class);
				i.putExtra(ImageActivity.URL, imageInfo.getFullUrl());
				startActivityForResult(i, 0);
			}

		});
	}

	public void voiceGoogle() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.voice_search));
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		startActivityForResult(intent, 1);
	}

	public void voiceNaver() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.voice_search));
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		startActivityForResult(intent, 2);
	}

	public void voiceDaum() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.voice_search));
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		startActivityForResult(intent, 3);
	}

	public void voiceYoutube() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.voice_search));
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		startActivityForResult(intent, 4);
	}

	public void voiceWikipedia() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.voice_search));
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		startActivityForResult(intent, 5);
	}

	public void voiceGoogleImage() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.voice_search));
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		startActivityForResult(intent, 6);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		final EditText etgoogle = (EditText) findViewById(R.id.search_google);
		final EditText etnaver = (EditText) findViewById(R.id.search_naver);
		final EditText etdaum = (EditText) findViewById(R.id.search_daum);
		final EditText etyoutube = (EditText) findViewById(R.id.search_youtube);
		final EditText etwikipedia = (EditText) findViewById(R.id.search_wikipedia);
		final EditText etgoogleimage = (EditText) findViewById(R.id.search_googleimage);

		if (requestCode == 1 && resultCode == RESULT_OK) {
			ArrayList<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			for (int i = 0; i < results.size(); i++) {
				// Intent search = new Intent(Intent.ACTION_VIEW,
				// Uri.parse("https://www.google.co.kr/search?q=" +
				// results.get(i)+"\n"));
				// startActivity(search);
				IconBar.setVisibility(View.GONE);

				etgoogle.setText(results.get(i) + "\n");
				webview_divider.setVisibility(View.VISIBLE);
				webview.setVisibility(View.VISIBLE);
				webview.loadUrl("https://www.google.co.kr/search?q="
						+ results.get(i) + "\n");
				openweb_google.setVisibility(View.VISIBLE);
			}

		} else if (requestCode == 2 && resultCode == RESULT_OK) {
			ArrayList<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			for (int i = 0; i < results.size(); i++) {
				// Intent search = new Intent(Intent.ACTION_VIEW,
				// Uri.parse("http://m.search.naver.com/search.naver?where=nexearch&query="
				// + results.get(i)+"\n"));
				// startActivity(search);
				IconBar.setVisibility(View.GONE);

				etnaver.setText(results.get(i) + "\n");
				webview_divider.setVisibility(View.VISIBLE);
				webview.setVisibility(View.VISIBLE);
				webview.loadUrl("http://m.search.naver.com/search.naver?where=nexearch&query="
						+ results.get(i) + "\n");
				openweb_naver.setVisibility(View.VISIBLE);
			}
		} else if (requestCode == 3 && resultCode == RESULT_OK) {
			ArrayList<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			for (int i = 0; i < results.size(); i++) {
				// Intent search = new Intent(Intent.ACTION_VIEW,
				// Uri.parse("http://m.search.daum.net/search?w=tot&nil_mtopsearch=btn&DA=YZRR&q="
				// + results.get(i)+"\n"));
				// startActivity(search);
				IconBar.setVisibility(View.GONE);

				etdaum.setText(results.get(i) + "\n");
				webview_divider.setVisibility(View.VISIBLE);
				webview.setVisibility(View.VISIBLE);
				webview.loadUrl("http://m.search.daum.net/search?w=tot&nil_mtopsearch=btn&DA=YZRR&q="
						+ results.get(i) + "\n");
				openweb_daum.setVisibility(View.VISIBLE);
			}
			super.onActivityResult(requestCode, resultCode, data);
		} else if (requestCode == 4 && resultCode == RESULT_OK) {
			ArrayList<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			for (int i = 0; i < results.size(); i++) {
				// Intent search = new Intent(Intent.ACTION_VIEW,
				// Uri.parse("http://www.youtube.com/results?search_query=" +
				// results.get(i)+"\n"));
				// startActivity(search);
				IconBar.setVisibility(View.GONE);

				etyoutube.setText(results.get(i) + "\n");
				webview_divider.setVisibility(View.VISIBLE);
				webview.setVisibility(View.VISIBLE);
				webview.loadUrl("http://www.youtube.com/results?search_query="
						+ results.get(i) + "\n");
				openweb_youtube.setVisibility(View.VISIBLE);
			}
			super.onActivityResult(requestCode, resultCode, data);
		} else if (requestCode == 5 && resultCode == RESULT_OK) {
			ArrayList<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			for (int i = 0; i < results.size(); i++) {
				IconBar.setVisibility(View.GONE);

				etwikipedia.setText(results.get(i) + "\n");
				webview_divider.setVisibility(View.VISIBLE);
				webview.setVisibility(View.VISIBLE);
				webview.loadUrl("http://ko.m.wikipedia.org/w/index.php?search="
						+ results.get(i) + "\n");
				openweb_wikipedia.setVisibility(View.VISIBLE);
			}
			super.onActivityResult(requestCode, resultCode, data);
		} else if (requestCode == 6 && resultCode == RESULT_OK) {
			ArrayList<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			for (int i = 0; i < results.size(); i++) {
				IconBar.setVisibility(View.GONE);

				etgoogleimage.setText(results.get(i) + "\n");
				mAdapter.clear();
				// hideSoftKeyboard(v);

				String query = search_googleimage.getText().toString();
				mQuery.setQuery(query);
				mQuery.next();
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	class WebClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	/*
     * 
     */
	private void updateWithNewLocation(Location location) {
		String latlongString = null;
		final String myAddress = latlongString;
		// TextView myLocationText;
		// myLocationText = (TextView)findViewById(R.id.myLocationText);
		String addressString = getString(R.string.loading_location);

		final LinearLayout searchgoogle = (LinearLayout) findViewById(R.id.google);
		final LinearLayout searchnaver = (LinearLayout) findViewById(R.id.naver);
		final LinearLayout searchdaum = (LinearLayout) findViewById(R.id.daum);
		final LinearLayout searchlocal = (LinearLayout) findViewById(R.id.local);
		final LinearLayout searchyoutube = (LinearLayout) findViewById(R.id.youtube);
		final LinearLayout searchwikipedia = (LinearLayout) findViewById(R.id.wikipedia);
		final LinearLayout searchgoogleimage = (LinearLayout) findViewById(R.id.layout_googleimage);

		final ImageButton google = (ImageButton) findViewById(R.id.tab_google);
		final ImageButton naver = (ImageButton) findViewById(R.id.tab_naver);
		final ImageButton daum = (ImageButton) findViewById(R.id.tab_daum);
		final ImageButton youtube = (ImageButton) findViewById(R.id.tab_youtube);
		final ImageButton wikipedia = (ImageButton) findViewById(R.id.tab_wikipedia);
		final ImageButton local = (ImageButton) findViewById(R.id.tab_local);

		LocationManager locationManager;
		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getSystemService(context);

		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latlongString = "위도: " + lat + "경도 : " + lng;
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			Geocoder gc = new Geocoder(this, Locale.getDefault());
			try {
				List<Address> addresses = gc.getFromLocation(latitude,
						longitude, 1);
				final StringBuilder sb = new StringBuilder();
				final StringBuilder snb = new StringBuilder();
				if (addresses.size() > 0) {
					final Address address = addresses.get(0);
					for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
						sb.append(address.getAddressLine(i));
					boolean showcountry = getSharedPreferences(
							getPackageName() + "_preferences",
							Context.MODE_PRIVATE).getBoolean("showcountry",
							true);
					if (showcountry) {
						sb.append(address.getCountryName()).append(" ");
					}
					sb.append(address.getAdminArea()).append(" ");
					sb.append(address.getLocality() + " ");
					// sb.append(address.getPostalCode()).append("\n");
					// sb.append(address.getFeatureName()).append("\n");
					sb.append(address.getSubLocality() + " ");
					sb.append(address.getThoroughfare() + " ");
					sb.append(address.getSubThoroughfare());
					addressString = sb.toString();

					snb.append(address.getLocality() + " ");
					snb.append(address.getSubLocality() + " ");

					mMapViewerResourceProvider = new NMapViewerResourceProvider(
							this);

					mOverlayManager = new NMapOverlayManager(this, mMapView,
							mMapViewerResourceProvider);

					final int markerId = NMapPOIflagType.PIN;

					final NMapPOIdata poiData = new NMapPOIdata(0,
							mMapViewerResourceProvider);
					poiData.beginPOIdata(0);
					poiData.removeAllPOIdata();
					poiData.addPOIitem(address.getLongitude(),
							address.getLatitude(), addressString, markerId, 0);
					poiData.endPOIdata();

					NMapPOIdataOverlay poiDataOverlay = mOverlayManager
							.createPOIdataOverlay(poiData, null);

					poiDataOverlay.showAllPOIdata(0);

					// 오버레이 매니저 불러옴
					mOverlayManager.setOnCalloutOverlayListener(this);

					locationMenu.setVisibility(View.VISIBLE);

					searchNearby.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							webview_divider.setVisibility(View.VISIBLE);
							webview.setVisibility(View.VISIBLE);
							webview.loadUrl("https://www.google.co.kr/search?q="
									+ snb.toString());
							openweb_google.setVisibility(View.VISIBLE);

							google.setSelected(true);
							naver.setSelected(false);
							daum.setSelected(false);
							youtube.setSelected(false);
							local.setSelected(false);

							searchgoogle.setVisibility(View.VISIBLE);
							searchnaver.setVisibility(View.GONE);
							searchdaum.setVisibility(View.GONE);
							searchlocal.setVisibility(View.GONE);
							searchyoutube.setVisibility(View.GONE);
							searchwikipedia.setVisibility(View.GONE);

							applicationMenu.setVisibility(View.GONE);

							search_google.setText(snb.toString());
							search_naver.setText("");
							search_daum.setText("");
							search_youtube.setText("");
							search_wikipedia.setText("");

							mMapView.setVisibility(View.GONE);
							mMapView.setVisibility(View.GONE);
							hideMapView.setVisibility(View.GONE);
							showMapView.setVisibility(View.VISIBLE);

							gvImages.setVisibility(View.GONE);
							searchgoogleimage.setVisibility(View.GONE);

							openweb_naver.setVisibility(View.GONE);
							openweb_daum.setVisibility(View.GONE);
							openweb_youtube.setVisibility(View.GONE);
							openweb_wikipedia.setVisibility(View.GONE);

							IconBar.setVisibility(View.GONE);

						}
					});

					showMapView.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {

							google.setSelected(false);
							naver.setSelected(true);
							daum.setSelected(false);
							youtube.setSelected(false);
							wikipedia.setSelected(false);
							local.setSelected(false);

							searchgoogle.setVisibility(View.GONE);
							searchnaver.setVisibility(View.GONE);
							searchdaum.setVisibility(View.GONE);
							searchlocal.setVisibility(View.GONE);
							searchyoutube.setVisibility(View.GONE);
							searchwikipedia.setVisibility(View.GONE);

							applicationMenu.setVisibility(View.GONE);

							search_google.setText(sb.toString());
							search_naver.setText("");
							search_daum.setText("");
							search_youtube.setText("");
							search_wikipedia.setText("");

							openweb_naver.setVisibility(View.GONE);
							openweb_daum.setVisibility(View.GONE);
							openweb_youtube.setVisibility(View.GONE);
							openweb_wikipedia.setVisibility(View.GONE);

							IconBar.setVisibility(View.GONE);

							webview.setVisibility(View.GONE);
							webview_divider.setVisibility(View.GONE);

							gvImages.setVisibility(View.GONE);
							searchgoogleimage.setVisibility(View.GONE);

							mMapView.setVisibility(View.VISIBLE);
							hideMapView.setVisibility(View.VISIBLE);
							showMapView.setVisibility(View.GONE);

							poiData.removeAllPOIdata();
							poiData.removePOIitem(null);
							poiData.addPOIitem(address.getLongitude(),
									address.getLatitude(), myAddress, markerId,
									0);
							poiData.endPOIdata();

						}
					});

					hideMapView.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {

							google.setSelected(true);
							naver.setSelected(false);
							daum.setSelected(false);
							youtube.setSelected(false);
							local.setSelected(false);
							wikipedia.setSelected(false);

							searchgoogle.setVisibility(View.VISIBLE);
							searchnaver.setVisibility(View.GONE);
							searchdaum.setVisibility(View.GONE);
							searchlocal.setVisibility(View.GONE);
							searchyoutube.setVisibility(View.GONE);
							searchwikipedia.setVisibility(View.GONE);

							applicationMenu.setVisibility(View.GONE);

							search_google.setText("");
							search_naver.setText("");
							search_daum.setText("");
							search_youtube.setText("");
							search_wikipedia.setText("");

							openweb_google.setVisibility(View.GONE);
							openweb_naver.setVisibility(View.GONE);
							openweb_daum.setVisibility(View.GONE);
							openweb_youtube.setVisibility(View.GONE);
							openweb_wikipedia.setVisibility(View.GONE);

							IconBar.setVisibility(View.VISIBLE);
							mMapView.setVisibility(View.GONE);
							hideMapView.setVisibility(View.GONE);
							showMapView.setVisibility(View.VISIBLE);

							poiData.removeAllPOIdata();
							poiData.removePOIitem(null);
							poiData.endPOIdata();

						}
					});
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (!locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			// alertCheckGPS();
			myLocation.setText(getString(R.string.gpsoff));
		}
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			// alertCheckGPS();
			myLocation.setText(getString(R.string.gpsoff));
		}
		myLocation.setText(addressString);
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private void alertCheckGPS() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.gpsdisabled))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.enablegps),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								moveConfigGPS();
							}
						})
				.setNegativeButton(getString(R.string.nothanks),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void moveConfigGPS() {
		Intent gpsOptionsIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}

	private void currentLocation() {
		LocationManager locationManager;
		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getSystemService(context);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = null;
		provider = locationManager.getBestProvider(criteria, true);

		Location location;

		if (provider == null) {
			Toast.makeText(getBaseContext(), "GPS Provider does not exist!!",
					2000).show();
			provider = LocationManager.NETWORK_PROVIDER;

		} else {
			provider = LocationManager.NETWORK_PROVIDER;

		}
		location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		updateWithNewLocation(location);
		locationManager.requestLocationUpdates(provider, 2000, 10,
				locationListener);
	}

	public void app_exit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.exit_app))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.exit),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								stopService(new Intent(DialogActivity.this,
										FloatingViewService.class));
								String viewstyle = getSharedPreferences(
										getPackageName() + "_preferences",
										Context.MODE_PRIVATE).getString(
										"viewstyle", "");

								if (viewstyle.equals("VIEWSTYLE_NOTIFICATION")) {
									stopService(new Intent(DialogActivity.this,
											NotificationService.class));
									NotificationService.notificationManager
											.cancel(NotificationService.MY_NOTIFICATION_ID);
								}
								System.exit(1);
							}
						})
				.setNegativeButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void bindApplications() {
		if (mGrid == null) {
			mGrid = (GridView) findViewById(R.id.all_apps);
		}
		mGrid.setAdapter(new ApplicationsAdapter(this, mApplications));
		mGrid.setSelection(0);
		mGrid.setOnItemClickListener(new ApplicationLauncher());
	}

	private static ApplicationInfo getApplicationInfo(PackageManager manager,
			Intent intent) {
		final ResolveInfo resolveInfo = manager.resolveActivity(intent, 0);

		if (resolveInfo == null) {
			return null;
		}

		final ApplicationInfo info = new ApplicationInfo();
		final ActivityInfo activityInfo = resolveInfo.activityInfo;
		info.icon = activityInfo.loadIcon(manager);
		if (info.title == null || info.title.length() == 0) {
			info.title = activityInfo.loadLabel(manager);
		}
		if (info.title == null) {
			info.title = "";
		}
		return info;
	}

	private void loadApplications(boolean isLaunching) {
		if (isLaunching && mApplications != null) {
			return;
		}

		PackageManager manager = getPackageManager();

		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		final List<ResolveInfo> apps = manager.queryIntentActivities(
				mainIntent, 0);
		Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

		if (apps != null) {
			final int count = apps.size();

			if (mApplications == null) {
				mApplications = new ArrayList<ApplicationInfo>(count);
			}
			mApplications.clear();

			for (int i = 0; i < count; i++) {
				ApplicationInfo application = new ApplicationInfo();
				ResolveInfo info = apps.get(i);

				application.title = info.loadLabel(manager);
				application.setActivity(new ComponentName(
						info.activityInfo.applicationInfo.packageName,
						info.activityInfo.name), Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				application.icon = info.activityInfo.loadIcon(manager);

				mApplications.add(application);
			}
		}
	}

	private class ApplicationsAdapter extends ArrayAdapter<ApplicationInfo> {
		private Rect mOldBounds = new Rect();

		public ApplicationsAdapter(Context context,
				ArrayList<ApplicationInfo> apps) {
			super(context, 0, apps);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ApplicationInfo info = mApplications.get(position);

			if (convertView == null) {
				final LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.application, parent,
						false);
			}

			Drawable icon = info.icon;

			if (!info.filtered) {
				final Resources resources = getContext().getResources();
				int width = (int) resources
						.getDimension(android.R.dimen.app_icon_size);
				int height = (int) resources
						.getDimension(android.R.dimen.app_icon_size);

				final int iconWidth = icon.getIntrinsicWidth();
				final int iconHeight = icon.getIntrinsicHeight();

				if (icon instanceof PaintDrawable) {
					PaintDrawable painter = (PaintDrawable) icon;
					painter.setIntrinsicWidth(width);
					painter.setIntrinsicHeight(height);
				}

				if (width > 0 && height > 0
						&& (width < iconWidth || height < iconHeight)) {
					final float ratio = (float) iconWidth / iconHeight;

					if (iconWidth > iconHeight) {
						height = (int) (width / ratio);
					} else if (iconHeight > iconWidth) {
						width = (int) (height * ratio);
					}

					final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
							: Bitmap.Config.RGB_565;
					final Bitmap thumb = Bitmap.createBitmap(width, height, c);
					final Canvas canvas = new Canvas(thumb);
					canvas.setDrawFilter(new PaintFlagsDrawFilter(
							Paint.DITHER_FLAG, 0));
					mOldBounds.set(icon.getBounds());
					icon.setBounds(0, 0, width, height);
					icon.draw(canvas);
					icon.setBounds(mOldBounds);
					icon = info.icon = new BitmapDrawable(thumb);
					info.filtered = true;
				}
			}

			final TextView textView = (TextView) convertView
					.findViewById(R.id.label);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null,
					null);
			textView.setText(info.title);

			return convertView;
		}
	}

	private class ApplicationLauncher implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView parent, View v, int position,
				long id) {
			ApplicationInfo app = (ApplicationInfo) parent
					.getItemAtPosition(position);
			startActivity(app.intent);
			finish();
		}
	}

	private void showApplications(boolean animate) {

		mGrid.setVisibility(View.VISIBLE);
	}

	private class ShowApplications implements View.OnClickListener {
		public void onClick(View v) {
			showApplications(true);
		}
	}

	@Override
	public void onMapInitHandler(NMapView mapview, NMapError errorInfo) {
		if (errorInfo == null) { // success
			// mMapController.setMapCenter(new NGeoPoint(126.978371,
			// 37.5666091), 11);
		} else { // fail
			android.util.Log.e("NMAP",
					"onMapInitHandler: error=" + errorInfo.toString());
		}
	}

	@Override
	public void onZoomLevelChange(NMapView mapview, int level) {
	}

	@Override
	public void onMapCenterChange(NMapView mapview, NGeoPoint center) {
	}

	@Override
	public void onAnimationStateChange(NMapView arg0, int animType,
			int animState) {
	}

	@Override
	public void onMapCenterChangeFine(NMapView arg0) {
	}

	@Override
	public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
			NMapOverlayItem arg1, Rect arg2) {
		Toast.makeText(this, arg1.getTitle(), Toast.LENGTH_SHORT).show();
		return null;
	}

	private void setupAdapter() {
		mImageResults = new ArrayList<ImageResult>();
		mAdapter = new ImageAdapter(this, mImageResults);
		gvImages.setAdapter(mAdapter);
	}

	private void setupQuery() {
		mQuery = new Query(new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject response) {
				mQuery.setLoading(false);
				mAdapter.addAll(ImageResult.fromJSONArray(getResults(response)));
			}

		});
	}

	private void setupOnScrollListener() {
		gvImages.setOnScrollListener(new OnScrollListener() {

			private static final int THRESHOLD = 6;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (!mQuery.isLoading() && mQuery.hasNext()
						&& visibleItemCount != 0) {
					if (firstVisibleItem + visibleItemCount > totalItemCount
							- THRESHOLD) {
						mQuery.next();
					}
				}
			}

		});
	}

	private JSONArray getResults(JSONObject response) {
		JSONArray results = new JSONArray();

		try {
			if (response.getInt(GoogleAPI.KEY_STATUS) == GoogleAPI.RESULT_OK) {
				results = response.getJSONObject(GoogleAPI.KEY_DATA)
						.getJSONArray(GoogleAPI.KEY_RESULTS);
			}
		} catch (JSONException e) {
			// Log.e(TAG, "Error parsing response", e);
		}

		return results;
	}

	public void onSearch(View v) {
		mAdapter.clear();
		// hideSoftKeyboard(v);

		String query = search_googleimage.getText().toString();
		mQuery.setQuery(query);
		mQuery.next();

		// Toast.makeText(this, "Searching for " + query,
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// activateTab();
		activateMapView();
		activateWebView();
		// activateSearchbars();
	}

	public void onUserLeaveHint() {
		super.onUserLeaveHint();
		String viewstyle = getSharedPreferences(
				getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getString("viewstyle", "VIEWSTYLE_FLOATING");

		if (viewstyle.equals("VIEWSTYLE_FLOATING")) {
			FloatingViewService.showView();
		} else if (viewstyle.equals("VIEWSTYLE_NOTIFICATION")) {
		}

	}

	public void onDestroy() {
		super.onDestroy();
		String viewstyle = getSharedPreferences(
				getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getString("viewstyle", "VIEWSTYLE_FLOATING");

		if (viewstyle.equals("VIEWSTYLE_FLOATING")) {
			FloatingViewService.showView();
		} else if (viewstyle.equals("VIEWSTYLE_NOTIFICATION")) {
		}
	}
}
