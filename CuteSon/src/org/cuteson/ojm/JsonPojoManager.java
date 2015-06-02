package org.cuteson.ojm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cuteson.ojm.util.CLog;
import org.cuteson.ojm.util.SAXXmlParseUtil;


public class JsonPojoManager {
	private static JsonPojoManager jsonPojoManager;
	private static Map<Class<? extends JsonPojo>, List<JsonPojoCallBack<? extends JsonPojo>>> callBackMap = new HashMap<Class<? extends JsonPojo>, List<JsonPojoCallBack<? extends JsonPojo>>>();
	public static JsonPojoManager getInstance(){
		if(jsonPojoManager==null){
			jsonPojoManager = new JsonPojoManager();
		}
		return jsonPojoManager;
	}

	private JsonPojoManager(){//绝对不能让外界创建这个实例			
		InputStream inStream = null;
		inStream = getClass().getResourceAsStream("/jsonpojo.xml");;
		SAXXmlParseUtil.parseJsonXML(inStream);
	            	
	}	
	
	public static <T extends JsonPojo> void addJsonCallBackToMap(Class<T> clazz,JsonPojoCallBack<T> jsonPojoCallBack){
		synchronized (callBackMap) {
			if(!callBackMap.containsKey(clazz)){
				List<JsonPojoCallBack<? extends JsonPojo>> list = new ArrayList<JsonPojoCallBack<? extends JsonPojo>>();
				list.add(jsonPojoCallBack);
				callBackMap.put(clazz, list);
			}else{
				callBackMap.get(clazz).add(jsonPojoCallBack);
			}
		}
	}
	
	
		
	public static <T extends JsonPojo> void  jsonPojoCallBack(T t){
		CLog.d("jsonPojoCallBack", "看看有没等待这个数据的回调pojo:"+t.getClass().getName());
		synchronized (callBackMap) {
			if(callBackMap.containsKey(t.getClass())){
				CLog.d("getJsonPojoCallBack", "还真tm有！");
				boolean pass = t.conditionIntercept();
				if(pass){//拦截通过			
					CLog.d("getJsonPojoCallBack", "果断执行回调pojo:"+t.getClass().getName());
					for(JsonPojoCallBack jsonPojoCallBack:callBackMap.get(t.getClass())){
						jsonPojoCallBack.onComplete(t);
					}
					callBackMap.remove(t.getClass());//用完即焚
				}				
			}else{
				CLog.d("getJsonPojoCallBack", "没有需要回调的！");
			}
		}
	}
	
	public static <T extends JsonPojo> void  jsonPojoFailureCallBack(T t,int erroCode){
		CLog.d("jsonPojoCallBack", "看看有没等待这个数据的回调pojo:"+t.getClass().getName());
		synchronized (callBackMap) {
			if(callBackMap.containsKey(t.getClass())){
				CLog.d("getJsonPojoCallBack", "还真tm有！");						
				CLog.d("getJsonPojoCallBack", "果断执行回调pojo:"+t.getClass().getName());
				for(JsonPojoCallBack jsonPojoCallBack:callBackMap.get(t.getClass())){
					jsonPojoCallBack.onFailures(erroCode);
				}
				callBackMap.remove(t.getClass());//用完即焚								
			}else{
				CLog.d("getJsonPojoCallBack", "没有需要回调的！");
			}
		}
	}
	
	public <T extends JsonPojo> T getPojo(Class<T> clazz,JsonPojoCallBack<T> jsonPojoCallBack){						
		T jsonPojo = PojosDomHolder.getInstance().getPojoFromHolder(clazz);
		if(jsonPojo!=null){
			boolean pass = jsonPojo.conditionIntercept();
			if(!pass){
				addJsonCallBackToMap(clazz, jsonPojoCallBack);
			}else{
				jsonPojoCallBack.onComplete(jsonPojo);
				return jsonPojo;
			}
		}else{
			CLog.e("getPojo", "你没有配置这个对象哦，亲！pojo:"+clazz.getName());
		}				
		return null;
		
	}
}
