package org.cuteson.ojm;

import java.util.ArrayList;
import java.util.List;

import org.cuteson.ojm.intercept.ConditionInterceptCallBack;
import org.cuteson.ojm.intercept.ConditionInterceptorInterf;
import org.cuteson.ojm.intercept.FileConditionInterceptorImp;
import org.cuteson.ojm.intercept.TimeConditionInterceptorImp;

public class JsonPojo {
	private String packageName;
	private String url;//网络地址
	private String filePath;//本地存储路径
	private Long updateTime=0L;//时效
	private Long lastTime=0L;//最后修改的时间
	private Boolean lazy=false;//懒加载
	
	
	private List<ConditionInterceptorInterf<JsonPojo>> conditionInterceptList = new ArrayList<ConditionInterceptorInterf<JsonPojo>>();
	public JsonPojo(){
		conditionInterceptList.add(TimeConditionInterceptorImp.getInstance());
		conditionInterceptList.add(FileConditionInterceptorImp.getInstance());
	}
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	public Long getLastTime() {
		return lastTime;
	}
	public void setLastTime(Long lastTime) {
		this.lastTime = lastTime;
	}
	public Boolean getLazy() {
		return lazy;
	}
	public void setLazy(Boolean lazy) {
		this.lazy = lazy;
	}
	
	public boolean needUpdateFromService(){//需要更新判断	
		if(updateTime==0){
			return false;
		}
		return lastTime+updateTime<System.currentTimeMillis();
		
	}
	
	public boolean conditionIntercept(){
		boolean passed  =true;
		for(final ConditionInterceptorInterf<JsonPojo> conditionInterceptorInterf:conditionInterceptList){
			boolean pass = conditionInterceptorInterf.interceptPojo(this, new ConditionInterceptCallBack() {				
				@Override
				public void interceptComplete(boolean pass) {
					if(!pass){
						conditionInterceptorInterf.interceptBlockProcess(JsonPojo.this);
					}
					
				}
			});
			if(!pass){
				passed = false;
			}
		}
		return passed;
	}

}
