package com.mapwhereivebeen.android.util;

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
	
	
}
