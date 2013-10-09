package com.htong.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLWell {
	
	private String wellnum;
	private int bengjing;
	private float hanshui;
	private float midu;
	
	public static Map<String, SQLWell> getWellsMap() {
		Map<String, SQLWell> wellMap = new HashMap<String, SQLWell>();
		for(SQLWell well : getWells()) {
			wellMap.put(well.getWellnum(), well);
		}
		return wellMap;
	}
	
	
	public static List<SQLWell> getWells() {
		List<SQLWell> sqlWells = new ArrayList<SQLWell>();
		
		SQLWell well1 = new SQLWell();
		SQLWell well2 = new SQLWell();
		SQLWell well3 = new SQLWell();
		SQLWell well4 = new SQLWell();
		SQLWell well5 = new SQLWell();
		SQLWell well6 = new SQLWell();
		SQLWell well7 = new SQLWell();
		SQLWell well8 = new SQLWell();
		SQLWell well9 = new SQLWell();
		SQLWell well10 = new SQLWell();
		
		well1.setWellnum("A");
		well2.setWellnum("B");
		well3.setWellnum("C");
		well4.setWellnum("D");
		well5.setWellnum("E");
		well6.setWellnum("F");
		well7.setWellnum("G");
		well8.setWellnum("H");
		well9.setWellnum("I");
		well10.setWellnum("J");
		
		well1.setBengjing(56);
		well2.setBengjing(50);
		well3.setBengjing(63);
		well4.setBengjing(50);
		well5.setBengjing(95);
		well6.setBengjing(44);
		well7.setBengjing(70);
		well8.setBengjing(70);
		well9.setBengjing(70);
		well10.setBengjing(70);
		
		well1.setHanshui(89);
		well2.setHanshui(98.3f);
		well3.setHanshui(95.5f);
		well4.setHanshui(95.3f);
		well5.setHanshui(97.9f);
		well6.setHanshui(29);
		well7.setHanshui(98.2f);
		well8.setHanshui(98.2f);
		well9.setHanshui(46);
		well10.setHanshui(62);
		
		well1.setMidu(0.8946f);
		well2.setMidu(0.8955f);
		well3.setMidu(0.9043f);
		well4.setMidu(0.8983f);
		well5.setMidu(0.912f);
		well6.setMidu(0.9146f);
		well7.setMidu(0.9349f);
		well8.setMidu(0.9291f);
		well9.setMidu(0.9564f);
		well10.setMidu(0.957f);
		
		
		sqlWells.add(well1);
		sqlWells.add(well2);
		sqlWells.add(well3);
		sqlWells.add(well4);
		sqlWells.add(well5);
		sqlWells.add(well6);
		sqlWells.add(well7);
		sqlWells.add(well8);
		sqlWells.add(well9);
		sqlWells.add(well10);
		
		return sqlWells;
	}
	
	
	public String getWellnum() {
		return wellnum;
	}
	public void setWellnum(String wellnum) {
		this.wellnum = wellnum;
	}
	public int getBengjing() {
		return bengjing;
	}
	public void setBengjing(int bengjing) {
		this.bengjing = bengjing;
	}
	public float getHanshui() {
		return hanshui;
	}
	public void setHanshui(float hanshui) {
		this.hanshui = hanshui;
	}
	public float getMidu() {
		return midu;
	}
	public void setMidu(float midu) {
		this.midu = midu;
	}
	
	
	
	

}
