package com.andrewandalbert.netnav.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.SessionFactory;

import com.andrewandalbert.netnav.dao.DeviceDAO;
import com.andrewandalbert.netnav.dao.LogDAO;
import com.andrewandalbert.netnav.dao.SiteConfigDAO;
import com.andrewandalbert.netnav.model.Device;
import com.andrewandalbert.netnav.model.Log;
import com.andrewandalbert.netnav.model.SiteConfig;
import com.andrewandalbert.netnav.network.utils.NetworkUtilities;

public class StatusUpdate extends Thread {

	private boolean stop;
	
	public StatusUpdate(String str) {
		super(str);
	}
	
	
	@Override
	public void interrupt() {
		super.interrupt();
		stop = true;
	}

	@Override
	public void run() {

		List<Device> devices = new ArrayList<Device>();
	    devices.addAll(DeviceDAO.instance.getModel().values());
	    Iterator<Device> it = devices.iterator();
	    while(it.hasNext() && !stop){
	    	Device device = it.next();
	  
	    	//update device status by pinging
	    	String oldStatus = device.getStatus();
	    	String oldMac = device.getMacAddress();
	    	
	    	String newStatus = NetworkUtilities.ping(device.getIpAddress()); 
	    	device.setStatus(newStatus);
	    	
	    	//get mac address of device if empty
	    	String newMac = "";
	    	if (oldMac.equals("")){
	 
	    		newMac =  NetworkUtilities.getMacAddress(device.getIpAddress());

	    		if (!newMac.equals("")) {
	    			device.setMacAddress(newMac);
	    			DeviceDAO.instance.updateDevice(device,Log.ACTION_TYPE.UDPATE);
	    		}
	    	}
	    	
	    	//TODO perform any other network checks here
	    	
	    	//update device uptime
	    	device.setUptime(NetworkUtilities.getUpTime(device.getIpAddress()));
	    	DeviceDAO.instance.updateDevice(device,null); //do not add log entry for uptime updates
	    	
	    	//if the status has changed update the device
	    	if(!newStatus.equals(oldStatus)){
	    		System.out.println("Device:" + device.getDeviceName() + " has changed: " + oldStatus + ">" + newStatus);
	    		DeviceDAO.instance.updateDevice(device,Log.ACTION_TYPE.UDPATE);
	    		
	    		//send email if device has gone offline and alerts are enabled for site and the device
	    		SiteConfig config = SiteConfigDAO.instance.getModel().get(new Long(1));
		    	if (device.isAlert() && config.isAlert() && newStatus.equals("Offline")){
		    		RaiseAlert.sendEmail(device);
		    	}
	    	}
	    	
	    }
	}
}
