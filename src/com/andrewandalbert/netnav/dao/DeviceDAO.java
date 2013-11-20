package com.andrewandalbert.netnav.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import com.andrewandalbert.netnav.model.Device;
import com.andrewandalbert.netnav.model.Log;

public enum DeviceDAO {
	instance;

	private Map<Long, Device> contentProvider = new HashMap<Long, Device>();
	private SessionFactory sessionFactory;

	private DeviceDAO() {

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
		List result = session.createQuery( "from Device" ).list();
		for ( Device device : (List<Device>) result ) {
			contentProvider.put(device.getId(), device);
		}
		session.getTransaction().commit();
		session.close();
	}
	
	public void putDevice(Device device) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(device);
		
		//add audit log record
		Log log = new Log(Log.ACTION_TYPE.ADD,"admin",device.toString(),device);
		LogDAO.instance.putLog(log);
		
		session.getTransaction().commit();
		session.close();
		contentProvider.put(device.getId(), device);
	}
	public Map<Long, Device> getModel(){
		return contentProvider;
	}
	
	public void updateDevice(Device device,Log.ACTION_TYPE type) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(device);

		//add audit log record
		if (type!=null){
			Log log = new Log(type,"admin",device.toString(),device);
			LogDAO.instance.putLog(log);
		}
		
		session.getTransaction().commit();
		session.close();
	}
	
	public void deleteDevice(Device device) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.delete(device);
		//add audit log record
		Log log = new Log(Log.ACTION_TYPE.DELETE,"admin",device.toString(),device);
		LogDAO.instance.putLog(log);
	
		session.getTransaction().commit();
		session.close();
	}

}

