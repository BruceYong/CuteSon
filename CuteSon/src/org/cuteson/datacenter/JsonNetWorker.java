package org.cuteson.datacenter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.cuteson.datacenter.HttpHelper.HttpGetCallBack;
import org.cuteson.ojm.JsonPojo;
import org.cuteson.ojm.JsonPojoCallBack;
import org.cuteson.ojm.JsonPojoManager;
import org.cuteson.ojm.PojosDomHolder;
import org.cuteson.ojm.util.CLog;
import org.cuteson.ojm.util.JsonToObjectUtil;

public class JsonNetWorker {
	//维护下载的下载队列
	private static List<Class> jsonTaskList = new ArrayList<Class>();
	private static ExecutorService threadPool = Executors.newCachedThreadPool();
	
	public static interface UpdateJsonCallBack<T>{
		public void onComplete(T t);
		public void onFailure(int erroCode);
	}
		
	public static <T extends JsonPojo> void updateJson(final T t,final UpdateJsonCallBack<T> updateJsonCallBack){
		synchronized (jsonTaskList) {
			if(jsonTaskList.contains(t.getClass())){//已经在下载了
				return;
			}else{
				jsonTaskList.add(t.getClass());
			}
		}
			
		Thread thread = new Thread(){
			@Override
			public void run() {		
				CLog.d("JsonNetWorker", "去服务器更新数据pojo:"+t.getClass().getName()+"Thread:"+Thread.currentThread().getName());
				HttpHelper.httpGet(t.getUrl(), new HttpGetCallBack() {			
					@Override
					public void onFailure(int erroCode) {
						updateJsonCallBack.onFailure(erroCode);
					}				
					@Override
					public void onComplete(HttpResponse httpResponse) {
						String str;
						FileOutputStream fileOutputStream = null;	
						try {					
							str = EntityUtils.toString(httpResponse.getEntity());
							CLog.d("JsonNetWorker", "服务器的pojo数据下好了:"+str);
							
							synchronized (jsonTaskList) {
								CLog.d("JsonNetWorker", "移除下载队列中的这个任务pojo:"+t.getClass().getName());
								jsonTaskList.remove(t.getClass());
							}
							
							JsonToObjectUtil.parseJsonToPojo(t, str);
							t.setLastTime(System.currentTimeMillis());																				
																		
							CLog.d("JsonNetWorker", "开始把跟新的数据写到磁盘中...:"+str);
							String filePath = t.getFilePath();
							String dirPath = filePath.substring(0, filePath.lastIndexOf("/"));
							File dir = new File(dirPath);
							dir.mkdirs();
							File file = new File(t.getFilePath());
							
											
							fileOutputStream = new FileOutputStream(file);					
							byte[] b= str.getBytes();
							
							fileOutputStream.write(b);	
							
							CLog.d("JsonNetWorker", "顺利写到了磁盘中...:"+str);
							if(updateJsonCallBack!=null){
								updateJsonCallBack.onComplete(t);
							}
							
						} catch (Exception e1) {
							e1.printStackTrace();
						} finally{
							try {
								if(fileOutputStream!=null){
									fileOutputStream.close();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
					}
				});
			}			
		};
		
		threadPool.execute(thread);
	}
}
