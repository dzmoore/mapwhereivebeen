package com.mapwhereivebeen.android.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.mapwhereivebeen.android.R;
import com.mapwhereivebeen.android.model.MapMarker;
import com.mapwhereivebeen.android.model.UserMap;
import com.mapwhereivebeen.android.util.Conca;
import com.mapwhereivebeen.android.util.Utils;
import com.mapwhereivebeen.android.web.WebClient;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getName();
	
	private AtomicReference<PointF> atmLastTouchPoint = new AtomicReference<PointF>(new PointF(100, 100));
	private AtomicReference<PointF> atmFromJsCenter = new AtomicReference<PointF>();
	private MainActivityJavascriptInterface mainActivityJavascriptInterface = new MainActivityJavascriptInterface();
	private String userMapIdentifier;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState.containsKey(getString(R.string.key_user_map_identifier))) {
			userMapIdentifier = savedInstanceState.getString(getString(R.string.key_user_map_identifier));
			loadWebView();
			
		} else {
			Utils.runAsync(new Runnable() {
				@Override
				public void run() {
                    checkForExistingMap();
				}
			});
		}
		
		final View contentView = findViewById(R.id.fullscreen_content);

		if (contentView instanceof WebView) {
            contentView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(
                    final ContextMenu menu,
                    final View v, 
                    final ContextMenuInfo menuInfo) 
                {
                	if (Utils.isDevelopment(getResources())) {
                        ((WebView)contentView).loadUrl(
                            "javascript:print_out('success !" + String.valueOf(System.nanoTime()) + "')"
                        );
                	}
                }
            });

            contentView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, final MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_OUTSIDE:
                            final PointF defaultP = new PointF(100,100);
                            atmLastTouchPoint.set(defaultP);
                            break;
                        default:
                            final PointF p = new PointF(event.getX(), event.getY());
                            atmLastTouchPoint.set(p);
                    }
                    
    				Log.v(TAG, "atmLastTouchPoint: " + String.valueOf(atmLastTouchPoint));
    				
                    return false;
                }
            });
            
            final WebView contentWebView = (WebView) contentView;
            
            final Button btnMarker = (Button) findViewById(R.id.btn_marker);
            btnMarker.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					sendSetMarkerCenter(contentWebView);
					
					if (Utils.isDevelopment(getResources())) {
                        Toast.makeText(MainActivity.this, "Sent setMarkerCenter", Toast.LENGTH_SHORT).show();
					}
					
					final PointF currentCenter = atmFromJsCenter.get();
					Utils.runAsync(new Runnable() {
						@Override
						public void run() {
							PointF center = currentCenter;
							final long startTimeNs = System.nanoTime();
							final long timeoutTimeNs = startTimeNs + 5000000L;
							while (center == null) {
								if (System.nanoTime() >= timeoutTimeNs) {
									return;
								}
								
								try {
									Thread.sleep(100L);
								} catch (InterruptedException e) { }
								
								center = atmFromJsCenter.get();
							}
							
							addMarkerCoordinatesToDatabase(center);
						}
					});
				}
			});
            

		}

	}

	@SuppressLint("SetJavaScriptEnabled")
	private void loadWebView() {
		final WebView contentWebView = (WebView) findViewById(R.id.fullscreen_content);
		final WebSettings webSettings = contentWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		contentWebView.addJavascriptInterface(mainActivityJavascriptInterface, "MainActivityJavascriptInterface");
		contentWebView.loadUrl("file:///android_asset/web/main.html");
	}
	
	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		if (userMapIdentifier != null && userMapIdentifier.length() > 0) {
            outState.putString(getString(R.string.key_user_map_identifier), userMapIdentifier);
		}
		
		super.onSaveInstanceState(outState);
	}
	
	private void checkForExistingMap() {
		final File installFile = new File(getFilesDir(), "INSTALLATION");
		if (installFile.exists()) {
			final Properties props = new Properties();
			try {
				props.loadFromXML(new FileInputStream(installFile));
				
			} catch (final Exception e) {
				Log.e(TAG, "error reading properties", e);
			}
			
			if (props.containsKey(getString(R.string.key_user_map_identifier))) {
				userMapIdentifier = props.getProperty(getString(R.string.key_user_map_identifier));
			}
			
		} else {
			final WebClient client = new WebClient();
			userMapIdentifier = client.getJSONObject(Conca.t(
                Utils.getServerUrl(
                    getResources()), 
                    getString(R.string.url_suffix_usermaps),
                    getString(R.string.url_suffix_createmap)
            ));
			
			final Properties props = new Properties();
			props.put(getString(R.string.key_user_map_identifier), userMapIdentifier);
			try {
				props.storeToXML(new FileOutputStream(installFile), "");
				
			} catch (final Exception e) {
				Log.e(TAG, "error storing props", e);
			}
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					loadWebView();
				}
			});
		}
	}

	private void addMarkerCoordinatesToDatabase(final PointF currentCenter) {
		final MapMarker marker = new MapMarker();
		final UserMap userMap = new UserMap();
		userMap.setId(Long.valueOf(getResources().getInteger(R.dimen.dev_map_id)));
		marker.setUserMap(userMap);
		
		marker.setLatitude((double) currentCenter.x);
		marker.setLongitude((double) currentCenter.y);
		
		final WebClient webClient = new WebClient();
		webClient.post(
            Conca.t(Utils.getServerUrl(getResources()), getString(R.string.url_suffix_mapmarkers)),
            marker
        );
	}	
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}
	
	private void sendSetMarkerCenter(final WebView contentWebview) {
		contentWebview.loadUrl("javascript:androidSetMarkerCenter()");
	}

	public class MainActivityJavascriptInterface {
		@JavascriptInterface
		public void updateCenter(final float lat, final float lng) {
			final PointF p = new PointF(lat, lng);
			atmFromJsCenter.set(p);
			
			Log.d(TAG, "updated lat/lon: " + String.valueOf(p));
		}
	}

}
