package com.andrewandalbert.netnav.model;

import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Table( name = "DEVICES" )
@XmlRootElement
public class Device {

	public static enum DEVICE_TYPE {SWITCH,AP};
	
	private Long id;
	private String deviceName;
	private DEVICE_TYPE deviceType;
	private String ipAddress;
	private String macAddress;
	private int channel;
	private String floor;
	private int xLoc;
	private int yLoc;
	private String status;
	private boolean alert;
	private String notes;
	private String uptime;
	
	

	public Device() {
		// this form used by Hibernate
	}
	
	public Device(String deviceName,String ipAddress, DEVICE_TYPE type, String floor, boolean alert){
		// for application use, to create new devices
		this.deviceName = deviceName;
		this.ipAddress = ipAddress;
		this.status = "Unknown";
		this.macAddress = "";
		this.deviceType = type;
		this.floor = floor;
		this.alert = alert;
	}
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public int getxLoc() {
		return xLoc;
	}
	public void setxLoc(int xLoc) {
		this.xLoc = xLoc;
	}
	public int getyLoc() {
		return yLoc;
	}
	public void setyLoc(int yLoc) {
		this.yLoc = yLoc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Enumerated(EnumType.STRING)
	public DEVICE_TYPE getDeviceType(){
		return deviceType;
	}
	public void setDeviceType(DEVICE_TYPE type){
		this.deviceType = type;
	}
	
	/*@Override
	public String toString(){
		return "device{id:"+id
			+",deviceName:"+deviceName
			+",deviceType:"+deviceType
			+",ipAddress:"+ipAddress
			+",macAddress:"+macAddress
			+",channel:"+channel
			+",floor:"+floor
			+",status:"+status
			+",xLoc:"+xLoc
			+",yLoc:"+yLoc
			+",alert:"+alert
			+"}";
	}*/

	@Override
	public String toString(){
		return "Name:"+deviceName
			+",Type:"+deviceType
			+",IP:"+ipAddress
			+",MAC:"+macAddress
			+",Floor:"+floor
			+",Status:"+status
			+",Uptime:"+uptime
			+",Alert:"+alert;
	}
	
	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	public boolean isAlert() {
		return alert;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNotes() {
		return notes;
	}
	
	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}
}

