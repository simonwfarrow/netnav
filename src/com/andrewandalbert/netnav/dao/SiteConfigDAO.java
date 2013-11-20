package com.andrewandalbert.netnav.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.andrewandalbert.netnav.model.SiteConfig;

public enum SiteConfigDAO {
	instance;

	private Map<Long, SiteConfig> contentProvider = new HashMap<Long, SiteConfig>();
	private SessionFactory sessionFactory;
	
	private SiteConfigDAO(){
		 
		if (sessionFactory==null){
			sessionFactory = new Configuration().configure().buildSessionFactory();
		}
		Session session = sessionFactory.openSession();
		session = sessionFactory.openSession();
		session.beginTransaction();
		List result = session.createQuery( "from SiteConfig" ).list();
		for ( SiteConfig config : (List<SiteConfig>) result ) {
			contentProvider.put(config.getSiteId(), config);
		}
		session.getTransaction().commit();
		session.close();
		
	}
	
	public Map<Long, SiteConfig> getModel(){
		return contentProvider;
	}
	
	public void putSiteConfig(SiteConfig config) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(config);
		session.getTransaction().commit();
		session.close();
		contentProvider.put(config.getSiteId(), config);
	}
	
	public void updateSiteConfig(SiteConfig config) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(config);
		session.getTransaction().commit();
		session.close();
	}

}
