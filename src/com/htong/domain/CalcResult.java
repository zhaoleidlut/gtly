package com.htong.domain;
/**
 * 计算结果
 * @author 赵磊
 *
 */
public class CalcResult {
	private String wellNum;
	private String time;
	private String value;		//单位：立方米
	private String valueDun;	//单位：吨
	private String unit;	//单位
	private String num;		//功图个数
	
	private String areaValue;	//面积产液量
	
	private String timeLong;	//时间段
	private String valueOld;	//原始理论值
	
	
	public String getTimeLong() {
		return timeLong;
	}
	public void setTimeLong(String timeLong) {
		this.timeLong = timeLong;
	}
	public String getValueOld() {
		return valueOld;
	}
	public void setValueOld(String valueOld) {
		this.valueOld = valueOld;
	}
	public String getWellNum() {
		return wellNum;
	}
	public void setWellNum(String wellNum) {
		this.wellNum = wellNum;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getValueDun() {
		return valueDun;
	}
	public void setValueDun(String valueDun) {
		this.valueDun = valueDun;
	}
	public String getAreaValue() {
		return areaValue;
	}
	public void setAreaValue(String areaValue) {
		this.areaValue = areaValue;
	}
	
	
}
