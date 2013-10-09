package com.htong.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.htong.domain.SQLData;
import com.htong.domain.SQLWell;

public class SQLDataDao {

	public void insert(SQLData sqlData) {
		MySessionFactory msf = new MySessionFactory();
		Session session = msf.currentSession();
		Transaction ts = null;
		try {
			 ts = session.beginTransaction();

			session.save(sqlData);
			session.flush();

			ts.commit();
		} catch (HibernateException e) {
			if(ts!=null) {
				ts.rollback();
			}
			e.printStackTrace();
		} finally {
			MySessionFactory.closeSession();
		}
		
	}
	
	public List<SQLData> getDatas(String wellNum, String startDate, String endDate) {
		SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		
		MySessionFactory msf = new MySessionFactory();
		Session session = msf.currentSession();
		Transaction ts = null;
		List<SQLData> sqlDataList = new ArrayList<SQLData>();
		try {
			ts = session.beginTransaction();
//			Query query = session.createQuery("select distinct a.time from SQLData as a where a.sqlWell.wellnum='"+wellNum+"' and a.time between '"+startDate + "' and '" + endDate + "'");
			Query query = session.createQuery("from SQLData as a where a.wellNum='"+wellNum+"' and a.time between '"+startDate + "' and '" + endDate + "'");
			Iterator it = query.list().iterator();
			while(it.hasNext()) {
				sqlDataList.add((SQLData) it.next());
			}
			ts.commit();
		} catch (HibernateException e) {
			if(ts!=null) {
				ts.rollback();
			}
			e.printStackTrace();
		} finally {
			MySessionFactory.closeSession();
		}
		return sqlDataList;
	}
	
	public List<SQLData> getAllDatas(String wellNum) {
		MySessionFactory msf = new MySessionFactory();
		Session session = msf.currentSession();
		Transaction ts = null;
		List<SQLData> sqlDataList = new ArrayList<SQLData>();
		try {
			ts = session.beginTransaction();
//			Query query = session.createQuery("select distinct a.time from SQLData as a where a.sqlWell.wellnum='"+wellNum+"' and a.time between '"+startDate + "' and '" + endDate + "'");
			Query query = session.createQuery("from SQLData as a where a.wellNum='"+wellNum+"' order by a.time");
			Iterator it = query.list().iterator();
			while(it.hasNext()) {
				sqlDataList.add((SQLData) it.next());
			}
			ts.commit();
		} catch (HibernateException e) {
			if(ts!=null) {
				ts.rollback();
			}
			e.printStackTrace();
		} finally {
			MySessionFactory.closeSession();
		}
		return sqlDataList;
	}

}
