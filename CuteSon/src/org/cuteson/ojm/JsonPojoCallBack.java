package org.cuteson.ojm;


public interface JsonPojoCallBack<T extends JsonPojo> {
	public void onComplete(T t);
	public void onFailures(int erroCode);
}
