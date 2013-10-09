package com.htong.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class MySessionFactory {
	private static final ThreadLocal threadLocal = new ThreadLocal();
	private static final Configuration cfg = new Configuration();
	private static SessionFactory sessionFactory;
	
	public static Session currentSession() {
		Session session = (Session) threadLocal.get();
		if(session == null) {
			if(sessionFactory == null) {
				try {
					cfg.configure();
					sessionFactory = cfg.buildSessionFactory();
				} catch (HibernateException e) {
					
					e.printStackTrace();
				}
			}
			System.out.println(sessionFactory == null);
			session = sessionFactory.openSession();
			threadLocal.set(session);
		}
		return session;
	}
	public static void closeSession() {
		Session session = (Session) threadLocal.get();
		threadLocal.set(null);
		if(session != null) {
			session.close();
		}
	}

}
