package org.cuteson.ojm.intercept;


public interface ConditionInterceptorInterf<T> {
	/**
	 * 拦截pojo,拦下来返回false，否则返回true
	 * @param jsonPojo
	 * @param callBack
	 * @return
	 */
	public boolean interceptPojo(T t,ConditionInterceptCallBack callBack);
	/**
	 * 这个拦截器拦截下来后对应的处理方法
	 * @param t
	 */
	public void interceptBlockProcess(T t);
}
