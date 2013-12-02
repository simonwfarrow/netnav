package com.andrewandalbert.netnav.server;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class  NetNavServer implements ServletContextListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3577085834473772357L;
	private static ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
	public static Properties props;
	
	/**
	 * @param args
	 */
	public NetNavServer(){
	}
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		ses.shutdownNow();
		try {
			ses.awaitTermination(2, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		String scanInterval = "60";
		
		try {
			//load properties file
			props  = new Properties();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream is = loader.getResourceAsStream("/netnav.properties");
			props.load(is);
			//set scan interval
			scanInterval = (String)props.get("scan.interval");
			
		} catch (Exception e){
			System.out.println("Could not load properties file");
			System.out.println(e.getMessage());
		}
			
		Integer interval = new Integer(scanInterval);
		StatusUpdate status = new StatusUpdate("StatusUpdate");
		status.setDaemon(true);
		ses.scheduleAtFixedRate(status, 1, interval, TimeUnit.MINUTES);	
		
	}
	

}
