package org.cuteson.datacenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.cuteson.ojm.JsonPojo;
import org.cuteson.ojm.JsonPojoManager;
import org.cuteson.ojm.PojosDomHolder;
import org.cuteson.ojm.util.CLog;
import org.cuteson.ojm.util.JsonToObjectUtil;

public class JsonFileWorker {
	//文件加载队列
	private static List<Class> list = new ArrayList<Class>();
	private static ExecutorService threadPool = Executors.newCachedThreadPool();
	public static interface FileCallBack<T>{
		public void onComplete(T t);
		public void onFailure();
	}
	
	public static  void readJsonFromlocalFile(final JsonPojo t,final FileCallBack<JsonPojo> fileCallBack){
		synchronized (list) {
			if(list.contains(t.getClass())||PojosDomHolder.getInstance().inMemory(((Class<JsonPojo>) t.getClass()))){
				return;
			}else{
				list.add(t.getClass());
			}
		}		
				
		Thread thread = new Thread(){
			@Override
			public void run(){
				CLog.d("JsonFileWorker", "开始从磁盘中读取这个pojo类的数据...Thread:"+Thread.currentThread().getName());
						
				String json="";
				File file = new File(t.getFilePath());
				FileInputStream fileInputStream = null;
				try {
					fileInputStream = new FileInputStream(file);				
					byte[] b= new byte[1024];
					while(-1!=(fileInputStream.read(b))){
						json+=new String(b, "UTF-8");
					}		
					
				} catch (Exception e) {
					
					e.printStackTrace();
				}finally{
					try {
						fileInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				CLog.d("PojosDomHolder", "从磁盘中读取这个pojo类的数据结束pojo:"+t.getClass().getName());				
				JsonToObjectUtil.parseJsonToPojo(t, json);	
				t.setLastTime(file.lastModified());
				
						
				synchronized (list) {
					list.remove(t.getClass());
				}		
			
				fileCallBack.onComplete(t);
				
				CLog.d("PojosDomHolder", "所有工作结束，对象妥妥的到内存中了pojo:"+t.getClass().getName());
				
			}	
		};
		threadPool.execute(thread);
	}

}
