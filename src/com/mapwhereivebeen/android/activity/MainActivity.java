package com.mapwhereivebeen.android.activity;

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

import com.mapwhereivebeen.android.R;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getName();
	
	private AtomicReference<PointF> atmLastTouchPoint = new AtomicReference<PointF>(new PointF(100, 100));
	private AtomicReference<PointF> atmFromJsCenter = new AtomicReference<PointF>();
	private MainActivityJavascriptInterface mainActivityJavascriptInterface = new MainActivityJavascriptInterface();
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		final View contentView = findViewById(R.id.fullscreen_content);

		if (contentView instanceof WebView) {
            contentView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(
                    final ContextMenu menu,
                    final View v, 
                    final ContextMenuInfo menuInfo) 
                {
                	((WebView)contentView).loadUrl("javascript:print_out('success !" + String.valueOf(System.nanoTime()) + "')");
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

			final WebView contentWebview = (WebView) contentView;
			final WebSettings webSettings = contentWebview.getSettings();
			webSettings.setJavaScriptEnabled(true);

			contentWebview.addJavascriptInterface(mainActivityJavascriptInterface, "MainActivityJavascriptInterface");
			contentWebview.loadUrl("file:///android_asset/web/testmap.html");
		}

	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
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
