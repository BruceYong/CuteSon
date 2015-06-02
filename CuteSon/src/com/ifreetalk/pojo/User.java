package com.ifreetalk.pojo;

import java.util.ArrayList;
import java.util.List;

import org.cuteson.ojm.JsonPojo;

public class User extends JsonPojo{
	private String userId;
	private String userName;
	private Integer age;
	private Son son = new Son();
	private List<Son> sonList = new ArrayList<Son>();
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
	
	public Son getSon() {
		return son;
	}
	public void setSon(Son son) {
		this.son = son;
	}
	


	public List<Son> getSonList() {
		return sonList;
	}
	public void setSonList(List<Son> sonList) {
		this.sonList = sonList;
	}



	public static class Son{
		private Integer sonId;

		public Integer getSonId() {
			return sonId;
		}

		public void setSonId(Integer sonId) {
			this.sonId = sonId;
		}
		
	}

}
