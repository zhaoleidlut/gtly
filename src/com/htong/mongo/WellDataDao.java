package com.htong.mongo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.document.mongodb.query.Criteria;
import org.springframework.data.document.mongodb.query.Query;

import com.mongodb.BasicDBObject;

public class WellDataDao {
	
	public WellData getWellDataByNum(String wellNum) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date start = null;
		Date end = null;
		try {
			start = sdf.parse("2012-10-10 0:0:0");
			end = sdf.parse("2013-10-10 0:0:0");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		BasicDBObject query = new BasicDBObject();
		// 井号
		//query.put("well_num", wellNum);
		
		BasicDBObject index = new BasicDBObject();// 时间条件
		index.put("$gte", start);
		index.put("$lt", end);

		query.put("datetime", index);
		
		Query myQuery = new Query(Criteria.where("well_num").is(wellNum).and("device_time").is(index));
		WellData wellData = PersistManager.INSTANCE.getMongoTemplate().findOne("WellData_草13-15", myQuery, WellData.class);
		
		return wellData;
		
	}

}
