package org.cuteson.ojm.intercept;

import org.cuteson.datacenter.JsonNetWorker;
import org.cuteson.ojm.JsonPojo;
import org.cuteson.ojm.JsonPojoManager;
import org.cuteson.ojm.PojosDomHolder;
import org.cuteson.ojm.util.CLog;

public class TimeConditionInterceptorImp implements ConditionInterceptorInterf<JsonPojo> {
	private String logTag="TimeConditionInterceptImp"; 
	private static ConditionInterceptorInterf<JsonPojo> conditionInterceptImp = new TimeConditionInterceptorImp();
	public static ConditionInterceptorInterf<JsonPojo> getInstance(){
		return conditionInterceptImp; 	
	}

	@Override
	public boolean interceptPojo(JsonPojo jsonPojo,ConditionInterceptCallBack callBack) {
		if(jsonPojo.needUpdateFromService()){
			CLog.d(logTag, "pass:false");
			callBack.interceptComplete(false);
			return false;
		}else{
			CLog.d(logTag, "pass:true");
			callBack.interceptComplete(true);
			return true;
		}
	}

	@Override
	public void interceptBlockProcess(final JsonPojo jsonPojo) {
		JsonNetWorker.updateJson(jsonPojo, new JsonNetWorker.UpdateJsonCallBack<JsonPojo>() {

			@Override
			public void onComplete(JsonPojo jsonPojo) {					
				PojosDomHolder.getInstance().replacePojo(jsonPojo);	
				JsonPojoManager.jsonPojoCallBack(jsonPojo);
				
			}

			@Override
			public void onFailure(int erroCode) {
				JsonPojoManager.jsonPojoFailureCallBack(jsonPojo,erroCode);
				
			}
		});
	}
	
	

}
