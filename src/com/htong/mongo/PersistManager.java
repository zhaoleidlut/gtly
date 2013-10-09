package com.htong.mongo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.document.mongodb.MongoOperations;

public enum PersistManager {
	INSTANCE;
	private static MongoOperations mongoTemplate;
	
	public MongoOperations getMongoTemplate() {
		if (mongoTemplate == null) {
//			ApplicationContext appContext = new ClassPathXmlApplicationContext(
//					"mongo-config.xml");
			ApplicationContext appContext = new ClassPathXmlApplicationContext("mongo-config.xml");   

			mongoTemplate = (MongoOperations) appContext
					.getBean("mongoTemplate");
		}
		return mongoTemplate;
	}
	

}
