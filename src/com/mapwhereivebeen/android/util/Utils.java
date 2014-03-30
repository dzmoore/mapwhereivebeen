package com.mapwhereivebeen.android.util;

import com.mapwhereivebeen.android.R;

import android.content.res.Resources;
import android.os.AsyncTask;


public class Utils {
    public static void runAsync(final Runnable runnable) {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                runnable.run();
                return null;
            }
            
        }.execute();
    }
	
	public static boolean isProduction(final Resources res) {
		return res.getBoolean(R.bool.is_production);
	}
	
	public static boolean isDevelopment(final Resources res) {
		return !isProduction(res);
	}
	
	public static String getServerUrl(final Resources res) {
		return isProduction(res) ? res.getString(R.string.prod_server_url) : res.getString(R.string.dev_server_url);
	}
}
