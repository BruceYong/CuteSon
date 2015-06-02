package com.ifreetalk.ftalk.basestruct;

import org.cuteson.ojm.JsonPojo;

public class Gift extends JsonPojo {
	private Long giftId=0L;
	private String giftName="";
	private Integer num=0;
	private String imgurl="";
	public Long getGiftId() {
		return giftId;
	}
	public void setGiftId(Long giftId) {
		this.giftId = giftId;
	}
	public String getGiftName() {
		return giftName;
	}
	public void setGiftName(String giftName) {
		this.giftName = giftName;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	
}
