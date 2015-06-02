package org.cuteson.ojm.intercept;

import java.io.File;

import org.cuteson.datacenter.JsonNetWorker;
import org.cuteson.datacenter.JsonNetWorker.UpdateJsonCallBack;
import org.cuteson.ojm.JsonPojo;
import org.cuteson.ojm.JsonPojoManager;
import org.cuteson.ojm.PojosDomHolder;
import org.cuteson.ojm.util.CLog;

public class FileConditionInterceptorImp implements ConditionInterceptorInterf<JsonPojo>{
	private String logTag="FileConditionInterceptorImp"; 
	private static ConditionInterceptorInterf<JsonPojo> conditionInterceptImp = new FileConditionInterceptorImp();
	public static ConditionInterceptorInterf<JsonPojo> getInstance(){
		return conditionInterceptImp; 	
	}
	@Override
	public boolean interceptPojo(JsonPojo jsonPojo,
			ConditionInterceptCallBack callBack) {
		final String filePath = jsonPojo.getFilePath();
		String dirPath = filePath.substring(0, filePath.lastIndexOf("/"));
		File dir = new File(dirPath);
		File file = new File(filePath);
		if(!dir.exists()||!file.exists()){
			return false;
		}else{
			return true;
		}	
	}

	@Override
	public void interceptBlockProcess(final JsonPojo jsonPojo) {
		CLog.d(logTag, "我擦，磁盘中居然没有这个pojo类的数据！pojo:"+jsonPojo.getClass().getName());
		final String filePath = jsonPojo.getFilePath();
		String dirPath = filePath.substring(0, filePath.lastIndexOf("/"));
		File dir = new File(dirPath);
		dir.mkdirs();
		CLog.d(logTag, "我去服务器请求数据吧！pojo:"+jsonPojo.getClass().getName());
		JsonNetWorker.updateJson(jsonPojo,new UpdateJsonCallBack<JsonPojo>(){
			@Override
			public void onComplete(JsonPojo jsonPojo) {
				PojosDomHolder.getInstance().removeLazyPojo(jsonPojo);					
				PojosDomHolder.getInstance().addPojo(jsonPojo);	
				JsonPojoManager.jsonPojoCallBack(jsonPojo);
			}
			@Override
			public void onFailure(int errocode) {
				JsonPojoManager.jsonPojoFailureCallBack(jsonPojo,errocode);					
			}
			
		});
		
	}

}
