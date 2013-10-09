package com.htong.domain;

import java.io.Serializable;
import java.util.Date;

public class SQLData implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String wellNum;
	private Date time;
	private float chongci;
	private String weiyi;
	private String zaihe;
	
	public String getWellNum() {
		return wellNum;
	}
	public void setWellNum(String wellNum) {
		this.wellNum = wellNum;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public float getChongci() {
		return chongci;
	}
	public void setChongci(float chongci) {
		this.chongci = chongci;
	}
	public String getWeiyi() {
		return weiyi;
	}
	public void setWeiyi(String weiyi) {
		this.weiyi = weiyi;
	}
	public String getZaihe() {
		return zaihe;
	}
	public void setZaihe(String zaihe) {
		this.zaihe = zaihe;
	}

	

}
