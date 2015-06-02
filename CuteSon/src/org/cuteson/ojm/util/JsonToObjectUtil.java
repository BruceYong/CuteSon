package org.cuteson.ojm.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonToObjectUtil {
	

	public static <T> T parseJsonToPojo(T t,String json){
		CLog.d("JsonToObjectUtil", "开始进行json到对象的映射json:"+json);
		
		Field[] fields = t.getClass().getDeclaredFields();
		Field[] superFields = t.getClass().getSuperclass().getDeclaredFields();
		parseByRelect(t,fields,json);
		parseByRelect(t,superFields,json);		
		CLog.d("JsonToObjectUtil", "json到对象的映射json顺利结束！pojo:"+t.getClass().getName());			
		return t;
	}
	
	
	private static <T> void parseByRelect(T t,Field[] fields,String json){
		JSONObject jsonObject;
		try {
			//FTLog.d("parseByRelect", json);
			jsonObject = new JSONObject(json);	
			for(Field field:fields){
				String typeStr = field.getType().toString();
				 field.setAccessible(true);
				 if(jsonObject.has(field.getName())){
					if(typeStr.equals("class java.lang.Integer")){					
					    field.set(t, jsonObject.getInt(field.getName())); 						
					}else if(typeStr.equals("class java.lang.String")){
	
						t.getClass().getDeclaredField(field.getName());
						field.set(t, jsonObject.getString(field.getName()));
					}else if(typeStr.equals("class java.lang.Long")){
						
						field.set(t,jsonObject.getLong(field.getName()));

					}else if(typeStr.equals("interface java.util.List")){
						JSONArray jsonArray = jsonObject.getJSONArray(field.getName());
						Type type =field.getGenericType();
						ParameterizedType parameterizedType=(ParameterizedType) type;
						Class c = (Class)parameterizedType.getActualTypeArguments()[0];
						List list = new ArrayList();
						for(int i=0;i<jsonArray.length();i++){
							String object = jsonArray.getString(i);
							Object o=c.newInstance();
							parseJsonToPojo(o,object);
							list.add(o);
						}
						field.set(t,list);
					}else{//自定义类
						parseJsonToPojo(field.get(t),jsonObject.getString(field.getName()));
					}
				 }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
}
