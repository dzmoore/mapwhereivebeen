package com.mapwhereivebeen.android.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapwhereivebeen.android.R;
import com.mapwhereivebeen.android.model.FeatureLayerMarker;
import com.mapwhereivebeen.android.model.FeatureLayerMarker.Geometry;
import com.mapwhereivebeen.android.model.MapMarker;
import com.mapwhereivebeen.android.model.UserMap;
import com.mapwhereivebeen.android.util.Conca;
import com.mapwhereivebeen.android.util.Utils;
import com.mapwhereivebeen.android.web.WebClient;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getName();
	
	private AtomicReference<PointF> atmLastTouchPoint = new AtomicReference<PointF>(new PointF(100, 100));
	private MainActivityJavascriptInterface mainActivityJavascriptInterface = new MainActivityJavascriptInterface();
	private String userMapIdentifier;
	private ArrayList<MapMarker> mapMarkers;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(getString(R.string.key_user_map_identifier))) {
                userMapIdentifier = savedInstanceState.getString(getString(R.string.key_user_map_identifier));
            }
            
            if (savedInstanceState.containsKey(getString(R.string.key_map_markers))) {
            	@SuppressWarnings("unchecked")
				final ArrayList<MapMarker> mapMarkersFromSave 
            		= (ArrayList<MapMarker>) savedInstanceState.getSerializable(getString(R.string.key_map_markers));
            	
				mapMarkers = mapMarkersFromSave;
            }
            
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
					sendSetMarkerCenter(contentWebView, 1, "A Title", "A Description");
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
		
		if (mapMarkers != null && mapMarkers.size() > 0) {
            outState.putSerializable(getString(R.string.key_map_markers), mapMarkers);
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
                Utils.getServerUrl(getResources()), 
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
		}
		
		Log.d(TAG, Conca.t("userMapIdentifier: ", userMapIdentifier));
		
		final WebClient client = new WebClient();
		final String mapMarkersJson = client.getJSONObject(Conca.t(
		    Utils.getServerUrl(getResources()),
		    getString(R.string.url_suffix_mapmarkers),
		    getString(R.string.url_suffix_bymapidentifier),
		    userMapIdentifier
        ));
		
		final List<MapMarker> mapMarkers = new Gson().fromJson(mapMarkersJson, new TypeToken<Collection<MapMarker>>(){}.getType());
		this.mapMarkers = new ArrayList<MapMarker>(mapMarkers);
		Log.d(TAG, Conca.t("mapMarkers: ", String.valueOf(mapMarkers)));
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loadWebView();
			}
		});
	}

	private void addCoordinatesToDatabase(final PointF currentCenter) {
		final MapMarker marker = new MapMarker();
		final UserMap userMap = new UserMap();
		userMap.setMapIdentifier(userMapIdentifier);
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
	
	private void sendSetMarkerCenter(
        final WebView contentWebview, 
        final long markerId, 
        final String markerTitle, 
        final String markerDesc) 
	{
		contentWebview.loadUrl(Conca.t(
            "javascript:androidAddMarkerCenter('",
            String.valueOf(markerId),
            "','",
            markerTitle, "', '",
            markerDesc, "')"
        ));
            
	}

	public class MainActivityJavascriptInterface {
		@JavascriptInterface
		public void addCoordinatesToDatabase(final float lat, final float lng) {
			Log.d(TAG, Conca.t(
                "addMarkerCoordinatesToDatabase lat/lon: [", 
                String.valueOf(lat), ", ", 
                String.valueOf(lng), "]"
            ));
			
			MainActivity.this.addCoordinatesToDatabase(new PointF(lat, lng));
		}
		
		@JavascriptInterface
		public void loadMapMarkers() {
			if (mapMarkers != null) {
				final WebView contentWebView = (WebView) findViewById(R.id.fullscreen_content);
				for (final MapMarker eaMapMarker : mapMarkers) {
					final FeatureLayerMarker featureLayerMarker = new FeatureLayerMarker();
					featureLayerMarker.setType("Feature");
					final Geometry geometry = new Geometry();
					geometry.setType("Point");
					geometry.setCoordinates(new double[] { eaMapMarker.getLongitude(), eaMapMarker.getLatitude() } );
					featureLayerMarker.setGeometry(geometry);
					
					final Map<String, String> properties = new HashMap<String, String>();
					properties.put("title", "marker");
					properties.put("description", "description");
					properties.put("marker-size", "large");
					properties.put("marker-color", "#f0a");
					featureLayerMarker.setProperties(properties);
					
//					contentWebView.loadUrl(Conca.t(
//                        "javascript:androidAddMapMarker(",
//                        new Gson().toJson(featureLayerMarker),
//                        ")"
//                    ));
					
					 try {
//						final String encoded = URLEncoder.encode(new Gson().toJson(featureLayerMarker), "UTF-8");
//						Log.d(TAG, Conca.t("encoded: ", encoded));
						 
						final String mapMarkerJson = new Gson().toJson(featureLayerMarker);
						Log.d(TAG, Conca.t("mapMarkerJson: ", mapMarkerJson));
						
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								contentWebView.loadUrl(Conca.t(
                                    "javascript:androidAddMapMarker(",
                                    mapMarkerJson,
                                    ")"
                                ));
							}
						}); 
					} catch (final Exception e) {
						Log.e(TAG, "encoding error", e);
					}
					 
				}
			}
		}
	}

}
