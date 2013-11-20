package com.andrewandalbert.netnav.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name = "LOG" )
@XmlRootElement
public class Log {

	public enum ACTION_TYPE {ADD,EDIT,DELETE,UDPATE,MOVE}
	
	private Long id;
	private String user;
	private Date date;
	private ACTION_TYPE action;
	private String description;
	private Long deviceId;
	
	@ManyToOne
	@JoinColumn(name="deviceId")
	private Device device;
	
	public void setDeviceId(Long id){
		this.deviceId = id;
	}
	public Long getDeviceId(){
		return deviceId;
	}
	
	public Log() {
		// this form used by Hibernate
	}
	
	public Log(ACTION_TYPE action, String user, String description,Device device){	
		this.action = action;
		this.user = user;
		this.description = description;
		this.date = Calendar.getInstance().getTime();
		this.device = device;
		this.deviceId = device.getId();
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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Enumerated(EnumType.STRING)
	public ACTION_TYPE getAction() {
		return action;
	}

	public void setAction(ACTION_TYPE action) {
		this.action = action;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
