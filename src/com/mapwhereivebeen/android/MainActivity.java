package com.mapwhereivebeen.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		final View contentView = findViewById(R.id.fullscreen_content);

		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			}
		});

		if (contentView instanceof WebView) {
            final WebView contentWebview = (WebView)contentView;
            final WebSettings webSettings = contentWebview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            
            contentWebview.loadUrl("file:///android_asset/web/testmap.html");
		}
		
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

}
