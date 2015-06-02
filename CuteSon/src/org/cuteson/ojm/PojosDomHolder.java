package org.cuteson.ojm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.cuteson.datacenter.JsonFileWorker;
import org.cuteson.datacenter.JsonNetWorker;
import org.cuteson.datacenter.JsonFileWorker.FileCallBack;
import org.cuteson.datacenter.JsonNetWorker.UpdateJsonCallBack;
import org.cuteson.ojm.util.CLog;

public class PojosDomHolder{
	
	private static PojosDomHolder pojosDomHolder = new PojosDomHolder();
	public static PojosDomHolder getInstance(){
		return pojosDomHolder;
	}
	private String defaultPackage;
	private List<JsonPojo> finishedPojoList;
	private List<JsonPojo> lazyPojoList;
	public String getDefaultPackage() {
		return defaultPackage;
	}
	public void setDefaultPackage(String defaultPackage) {
		this.defaultPackage = defaultPackage;
	}
	
	
	public void addLazyPojo(final JsonPojo jsonPojo){
		CLog.d("PojosDomHolder", "addLazyPojoToHolder"+jsonPojo.getClass().getName());
		synchronized (this) {		
			if(lazyPojoList==null){
				lazyPojoList=new ArrayList<JsonPojo>();
			}
			lazyPojoList.add(jsonPojo);
		}
	}
	
	public <T> T getLazyPojo(Class<T> clazz){
		synchronized (this) {		
			for(JsonPojo t :lazyPojoList){
				CLog.d("getLazyPojo", "T class:"+t.getClass().getName());
				CLog.d("getLazyPojo", "clazz class:"+clazz.getName());
				if(t.getClass().equals(clazz)){
					return (T)t;
				}
			}	
			return null;
		}
	}
	
	public void removeLazyPojo(JsonPojo t){
		synchronized (this) {
			if(lazyPojoList==null){
				lazyPojoList.remove(t);
			}
		}
	}

	public void addPojo(final JsonPojo t){
		CLog.d("PojosDomHolder", "addPojoToHolder"+t.getClass().getName());
		synchronized (this) {
			if(finishedPojoList==null){
				finishedPojoList=new ArrayList<JsonPojo>();
			}
			finishedPojoList.add(t);
		}
	}
	
	public void replacePojo(final JsonPojo t){
		CLog.d("PojosDomHolder", "addPojoToHolder"+t.getClass().getName());
		synchronized (this) {
			if(finishedPojoList==null){
				finishedPojoList=new ArrayList<JsonPojo>();
				finishedPojoList.add(t);
			}else{
				for(JsonPojo t1:finishedPojoList){
					if(t1.getClass().equals(t.getClass())){
						finishedPojoList.remove(t1);
						finishedPojoList.add(t);
						break;
					}
				}
			}
			
		}
	}
	
	public <T extends JsonPojo> T getPojo(Class<T> clazz){
		synchronized (this) {
			if(finishedPojoList!=null){		
				for(JsonPojo t :finishedPojoList){
					if(t.getClass().equals(clazz)){
						return (T)t;
					}
				}			
			}
			return null;
		}
	}
	
	private  void removePojo(JsonPojo t){
		synchronized (this) {
			if(finishedPojoList==null){
				finishedPojoList.remove(t);
			}
		}
	}

	
	public  boolean inMemory(Class<JsonPojo> clazz){
		return getPojo(clazz)!=null;
	}
	
	
	
	
	public <T extends JsonPojo> T getPojoFromHolder(Class<T> clazz){
		synchronized (this) {
			if(finishedPojoList!=null){		
				for(JsonPojo t :finishedPojoList){
					if(t.getClass().equals(clazz)){
						return (T)t;
					}
				}			
			}
			if(lazyPojoList!=null){
				for(JsonPojo t :lazyPojoList){
					if(t.getClass().equals(clazz)){
						return (T)t;
					}
				}
			}
			return null;
		}
	}
	
	
	
	//加载
	public void loadPojoToHolder(final JsonPojo t){
		CLog.d("PojosDomHolder", "开始将这个pojo添加到内存中常住，这个pojo类的数据从磁盘中读取...pojo:"+t.getClass().getName());
		String json="";
		final String filePath = t.getFilePath();
		String dirPath = filePath.substring(0, filePath.lastIndexOf("/"));
		File dir = new File(dirPath);
		File file = new File(filePath);
		if(!dir.exists()||!file.exists()){
			CLog.d("PojosDomHolder", "我擦，磁盘中居然没有这个pojo类的数据！pojo:"+t.getClass().getName());
			dir.mkdirs();
			CLog.d("PojosDomHolder", "我去服务器请求数据吧！pojo:"+t.getClass().getName());
			JsonNetWorker.updateJson(t,new UpdateJsonCallBack<JsonPojo>(){
				@Override
				public void onComplete(JsonPojo t) {
					removeLazyPojo(t);					
					addPojo(t);										
				}
				@Override
				public void onFailure(int errocode) {
					// TODO Auto-generated method stub					
				}
				
			});
						
		}else{
			JsonFileWorker.readJsonFromlocalFile(t,new FileCallBack<JsonPojo>(){
				@Override
				public void onComplete(JsonPojo t) {
					removeLazyPojo(t);					
					addPojo(t);	
				}

				@Override
				public void onFailure() {
					
				}
				
			});
		}			
	}
	
}
