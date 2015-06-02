package org.cuteson.datacenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

public class HttpHelper{
	private static ExecutorService threadPool = Executors.newCachedThreadPool();
	public final static int NET_EXCEPTION=0;
	public final static int CONNECT_TIMEOUT=1;
	
	public static interface HttpGetCallBack{
		public void onComplete(HttpResponse httpResponse);
		public void onFailure(int erroCode);
	}
	
	public static void httpGet(final String url,final HttpGetCallBack callback){
		 Thread thread = new Thread(){  
	            public void run(){
	            	httpGetTask(url,callback);
	            }  
		  
		      };
	     threadPool.execute(thread);
	}
	
	private static void httpGetTask(String url,HttpGetCallBack callback){
		HttpGet httpGet = new HttpGet(url);
    	try
		{
    		BasicHttpParams httpParams = new BasicHttpParams();
    		HttpConnectionParams.setConnectionTimeout(httpParams, 1000*10);
    		DefaultHttpClient client = new DefaultHttpClient(httpParams);
			HttpResponse httpResponse = client.execute(httpGet);			
			callback.onComplete(httpResponse);
		} catch (ConnectTimeoutException e){
			callback.onFailure(HttpHelper.CONNECT_TIMEOUT);
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			callback.onFailure(HttpHelper.NET_EXCEPTION);
			e.printStackTrace();
		} catch (IOException e) {
			callback.onFailure(HttpHelper.NET_EXCEPTION);
			e.printStackTrace();
		}
	}
	
	public static interface HttpPostCallBack{
		public void onComplete(HttpResponse httpResponse);
		public void onFailure(int erroCode);
	}
	
	public static void httpPost(final String url,final HttpPostCallBack callback,final Map<String,String> valuesMap){
			Thread thread =  new Thread() {  
	            public void run()
	            {
	            	httpPostTask(url,callback,valuesMap);
	            }    
	        }; 
	        threadPool.execute(thread);
	}
	
	private static void httpPostTask(String url,HttpPostCallBack callback,Map<String,String> valuesMap){
		HttpPost httpPost = new HttpPost(url);
    	try
		{
    		BasicHttpParams httpParams = new BasicHttpParams();
    		HttpConnectionParams.setConnectionTimeout(httpParams, 1000*10);
    		
    		Set<String> keySet = valuesMap.keySet();
    		List <NameValuePair> params = new ArrayList<NameValuePair>();  
    		
    		for(String key:keySet){
    		     params.add(new BasicNameValuePair(key, valuesMap.get(key)));  
    		}
    		
    		httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));  
    		DefaultHttpClient client = new DefaultHttpClient(httpParams);
			HttpResponse httpResponse = client.execute(httpPost);
			
			callback.onComplete(httpResponse);
		} catch (ConnectTimeoutException e){
			callback.onFailure(HttpHelper.CONNECT_TIMEOUT);
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			callback.onFailure(HttpHelper.NET_EXCEPTION);
			e.printStackTrace();
		} catch (IOException e) {
			callback.onFailure(HttpHelper.NET_EXCEPTION);
			e.printStackTrace();
		}
	}	
}