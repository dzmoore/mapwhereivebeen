package com.mapwhereivebeen.android.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapwhereivebeen.android.BuildConfig;
import com.mapwhereivebeen.android.util.Conca;

public class WebClient {
	private static final String TAG = WebClient.class.getName();
	
	public void post(final String addr, final Object requestObj) {
		try {
			postJson(addr, new Gson().toJson(requestObj));
			
		} catch (final Exception e) {
			Log.e(TAG, "error occurred", e);
		}
	}
	
	
	public <O> List<O> getList(final String addr, final Class<O> responseType) {
		String respStr = null;
		List<O> fromJson = null;
		try {
			Log.d(TAG, Conca.t("getList: addr: ", addr, "; responseType: ", String.valueOf(responseType)));
			
			respStr = getJSONObject(addr);
			
			fromJson = new Gson().fromJson(respStr, new TypeToken<Collection<O>>(){}.getType());
			
			Log.d(TAG, Conca.t("getList: addr: ", addr, "; fromJson: ", String.valueOf(fromJson)));
			
		} catch (final Exception e) {
			Log.e(TAG, "error occurred", e);
		}
		
		return fromJson;
	}
	
	private String postJson(final String addr, final String requestJson)
        throws 
        UnsupportedEncodingException, 
        IOException,
        ClientProtocolException 
    {
		final DefaultHttpClient client = new DefaultHttpClient();
		final HttpPost httpPost = new HttpPost(addr);
		
		final StringEntity strEnt = new StringEntity(requestJson);
		
		httpPost.setEntity(strEnt);
		
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");
		
		final ResponseHandler<String> respHandler = new BasicResponseHandler();
		
        Log.d(
            TAG, 
            Conca.t("attempting to send json to '", addr,
            "'; requestJson=", requestJson)
        );
		
		final String respStr = client.execute(httpPost, respHandler);
		
		return respStr;
	}
	
	public String getJSONObject(final String addr) {
		String result = "";
		
		try {
			final URLConnection conn = createURLConnection(addr);
			
			result = getResponseText(conn);
			
			Log.d(TAG, Conca.t("getJSONObject: addr: ", addr, "; responseText: ", result));
			
		} catch (Exception e) {
            Log.e(TAG, Conca.t(
                    "an error occurred while attempting to get a JSON object from [", 
                    addr, 
                    "]"
                ), 
                e
            );
			
			publishException(e);
		}
		
		return result;
	}
	
	private void publishException(final Exception e) {
		// TODO Auto-generated method stub
		
	}


	private String getResponseText(final URLConnection conn) {
		final StringBuilder sb = new StringBuilder();
		BufferedReader bufReader = null;
		try {
			bufReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			
			while ((line = bufReader.readLine()) != null) {
				sb.append(line);
			}
			
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "err", e);
			}
			
			publishException(e);	
			
		} finally {
			if (bufReader != null) {
				try {
					bufReader.close();
					
				} catch (Exception e) { }
			}
		}
		
		return sb.toString();
	}
	
	private URLConnection createURLConnection(final String addr) throws IOException {
		final URL url = new URL(addr);
		final URLConnection connection = url.openConnection();
		connection.setUseCaches(true);	
		connection.setConnectTimeout(25000);
		
		return connection;
	}
}
