package com.andrewandalbert.netnav.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.andrewandalbert.netnav.model.Device;
import com.andrewandalbert.netnav.model.Log;

public enum LogDAO {
	instance;

	private TreeMap<Long, Log> contentProvider = new TreeMap<Long, Log>();
	private SessionFactory sessionFactory;

	private LogDAO() {

		//load devices from DB
		//if session null then connect
		if (sessionFactory==null) {
			sessionFactory = new Configuration()
			.configure() // configures settings from hibernate.cfg.xml
			.buildSessionFactory();
		}

		Session session = sessionFactory.openSession();
		session = sessionFactory.openSession();
		session.beginTransaction();
		//StringBuilder query = new StringBuilder("from Log");
		//query.append(" order by date");
		Criteria criteria = session.createCriteria(Log.class);
		criteria.addOrder(Order.desc("id"));
		//List result = session.createQuery( query.toString() ).list();
		List result = criteria.list();
		for ( Log log : (List<Log>) result ) {
			contentProvider.put(log.getId(), log);
		}
		session.getTransaction().commit();
		session.close();
	}
	
	public void putLog(Log log) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(log);
		session.getTransaction().commit();
		session.close();
		contentProvider.put(log.getId(), log);
	}
	
	
	//not working
	public void deleteLogs(){
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		String hql = String.format("delete from %s","Log");
	    Query query = session.createQuery(hql);
	    query.executeUpdate();
	    session.getTransaction().commit();
	    session.close();
		contentProvider.clear();
	}
	public TreeMap<Long, Log> getModel(){
		return contentProvider;
	}

}

