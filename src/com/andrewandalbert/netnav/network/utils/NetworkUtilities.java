package com.andrewandalbert.netnav.network.utils;

import java.io.IOException;
import java.io.InputStream;

import org.snmp4j.smi.OID;

import com.andrewandalbert.netnav.server.NetNavServer;

public class NetworkUtilities {
	
	
	public static String getMacAddress(String ip) {
		
		String mac = "";
		
		Runtime rt = Runtime.getRuntime();
		Process pr = null;
		
		try {
			pr = rt.exec("arp -a");
			InputStream is = pr.getInputStream();
			
			mac =  getMacAddressFromStream(is,ip);
				
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return mac;
		
	}
	
	public static String ping(String ip){
		
		Runtime rt = Runtime.getRuntime();
		Process pr = null;
		String status = "Offline";
		
		String os = (String)NetNavServer.props.get("os");
		try {
			
			String pingInt = (String)NetNavServer.props.get("ping.attempts");
			
			if (os.equals("windows")) {
				pr = rt.exec("ping " + ip + " -n 2");
			} else {
				//linux
				pr = rt.exec("ping " + ip + " -c" + pingInt);
			}
			
			InputStream is = pr.getInputStream();
			
			if (getIPStatusFromStream(is,ip)) {
				status = "Online";
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return status;
	}
	
	private static boolean getIPStatusFromStream(java.io.InputStream is,String ip) {
		
		boolean online = false;
		
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\n");

	    //read and discard first blank line
	    s.next();
	    
	    while (s.hasNext()){
		    String result = new String(s.next());
		    String os = (String)NetNavServer.props.get("os");
		    
		    if (os.equals("windows")){
			    //windows
			    if (result.contains("TTL")){
			    	online = true;
			    }
		    } else {
			    //linux
			    if (result.contains("64 bytes from")){
			    	online = true;
			    }
	    	}
	    }
	    
	    return online;
	
	}
	
	private static String getMacAddressFromStream(java.io.InputStream is,String ip) {
		
		String mac = "";
		
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\n");

	    //read and discard first blank line
	    //s.next(); //windows only
	    
	    while (s.hasNext()){
		    String result = new String(s.next());

		    String os = (String)NetNavServer.props.get("os");
		    
		    if (os.equals("windows")){
		    	if (result.contains(ip)){ 
		    		//windows
			    	try {
			    		mac = result.substring(24, (24+17));
			    	} catch (IndexOutOfBoundsException iobe) {
			    		//do nothing
			    	}
		    	}
		    } else {
			    if (result.contains("("+ip+")")){ 
			    	//linux
			    	try {
			    		//? (192.168.1.3) at 74:e5:0b:a4:87:fa [ether] on eth0
			    		//? (192.168.1.1) at 00:26:5a:17:66:9d [ether] on eth0
			    		//? (192.168.1.91) at <incomplete> on eth0
			    		if (result.indexOf("<incomplete>")==-1){
				    		int pos = result.indexOf("at");
				    		int start = pos+3;
				    		mac = result.substring(start, (start+17));
			    		}
			    	} catch (IndexOutOfBoundsException iobe) {
			    		//do nothing
			    	}
			    }
		    }
	    }
	    
	    return mac;
	
	}
	
	public static String getUpTime(String ip){
		String uptime = "";
		SnmpClient client;
		try {
			client = new SnmpClient("udp:"+ip+"/161");
			uptime = client.getAsString(new OID("1.3.6.1.2.1.1.3.0")); //uptime OID
			client.stop();
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
		client = null;
		return uptime;
	}
}
