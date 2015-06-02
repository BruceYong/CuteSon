package org.cuteson.ojm.util;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.cuteson.ojm.JsonPojo;
import org.cuteson.ojm.PojosDomHolder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXXmlParseUtil {
	public static void parseJsonXML(InputStream inStream){		
		try {
	        //创建解析器
	        SAXParserFactory spf = SAXParserFactory.newInstance();
	        SAXParser saxParser = spf.newSAXParser();
	        PojosXmlContentHandler handler = new PojosXmlContentHandler();
	        saxParser.parse(inStream, handler);
	        inStream.close();
	        CLog.d("JsonPojoManager()", "解析jsonpojo.xml结束");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private static class PojosXmlContentHandler extends DefaultHandler  {
		private String defaultPackage;
		private String currentPackage;
		private JsonPojo currentPojo;
	    private String tagName = null;
	    
	    @Override
	    public void startDocument() throws SAXException {
	    	CLog.d("JsonPojoManager()", "解析jsonpojo.xml开始...");     
	    }
	   
	    @Override
	    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
	    	
	    	if(localName.equals("pojos")){
	    		for(int i=0;i<atts.getLength();i++){              			
	        		if(atts.getQName(i).equals("defaultpackage")){	            			
	        			defaultPackage=atts.getValue(i);	            			
	        		}
	    		}
	    	}     	
	    	
	    	if(localName.equals("pojo")){         		
	        	for(int i=0;i<atts.getLength();i++){  
	        		if(atts.getQName(i).equals("package")){//懒加载        			
        				String packageName = atts.getValue(i);
        				currentPackage = packageName;        			
	        		}
	        		if(atts.getQName(i).equals("class")){	            			
	            		try {
	            			String packName=defaultPackage;
	            			if(currentPackage!=null){
	            				packName = currentPackage;		
	            			}
	            			CLog.d("XMLContentHandler", "解析一个pojo类"+packName+"."+atts.getValue(i)+"开始...");
	            			Class<?> clazz = Class.forName(packName+"."+atts.getValue(i));
	            			currentPojo = (JsonPojo) clazz.newInstance();	
	            			CLog.d("XMLContentHandler", currentPojo.getClass().getName()+"类实例化成功");         			
						} catch (Exception e) {
							CLog.d("XMLContentHandler", currentPojo.getClass().getName()+"类实例化失败");
							e.printStackTrace();
						}
	        		}  
	        		if(atts.getQName(i).equals("lazy")){//懒加载
	        			if(currentPojo!=null){
	        				String lazy = atts.getValue(i);
	        				if(lazy.equals("true")){
	        					currentPojo.setLazy(true);
	        				}else{
	        					currentPojo.setLazy(false);
	        				}
	        			}
	        		}
	        		
	        	}
	    	}
	        this.tagName = localName;
	    }
	   
	    @Override
	    public void characters(char[] ch, int start, int length) throws SAXException {
	    	String data = new String(ch, start, length);
	        if(tagName!=null){
	        	if(currentPojo!=null){	
	            	if(tagName.equals("url")){
	            		CLog.d("pojo的url：", data);
	            		currentPojo.setUrl(data);
	            	}else if(tagName.equals("filepath")){
	            		CLog.d("pojo的filepath：", data);
	            		currentPojo.setFilePath(data);
	            	}else if(tagName.equals("updateTime")){
	            		CLog.d("pojo的updateTime：", data);
	            		currentPojo.setUpdateTime(Long.valueOf(data));
	            	} 
	        	}
	        }
	    }
	    
	    @Override
	    public void endElement(String uri, String localName, String name) throws SAXException {
	        if(localName.equals("pojo")){
	        	CLog.d("XMLContentHandler", "解析一个pojo类结束pojo:"+currentPojo.getClass().getName());
	        	if(!currentPojo.getLazy()){//不是懒加载
	        		CLog.d("XMLContentHandler", "不是懒加载pojo："+currentPojo.getClass().getName());
	        		currentPojo.conditionIntercept();
	        	}else{
	        		CLog.d("XMLContentHandler", "懒加载，暂时不去加载数据pojo："+currentPojo.getClass().getName());	        		
	        	}
	        	PojosDomHolder.getInstance().addLazyPojo(currentPojo);	 
	        	currentPackage=null;
	        }
	       
	        this.tagName = null;
	    }
	}
	

}
