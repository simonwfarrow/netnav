package com.andrewandalbert.netnav.server;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@WebListener
public class  NetNavServer implements ServletContextListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3577085834473772357L;
	private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> future = null;
	private StatusUpdate status = null;
	public static Properties props;
	
	/**
	 * @param args
	 */
	public NetNavServer(){
	}
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("SHUTTING DOWN SERVER");
		status.interrupt();
		future.cancel(true);
		ses.shutdownNow();
		try {
			ses.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("TASK CANCELLED");
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
		status = new StatusUpdate("StatusUpdate");
		status.setDaemon(true);
		future = ses.scheduleAtFixedRate(status, 1, interval, TimeUnit.MINUTES);	
		
	}
	

}
