package com.andrewandalbert.netnav.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity 
@Table(name="SITE_CONFIG")
@XmlRootElement
public class SiteConfig {

	private Long site_id;
	private String siteName;
	private String emailAddress;
	private List<String> floors;
	private boolean alert;
	
	public SiteConfig(){
		
	}
	
	public SiteConfig(String siteName,String emailAddress, List<String> floors){
		this.emailAddress = emailAddress;
		this.siteName = siteName;
		this.floors = floors;

		this.alert = true;
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	public Long getSiteId() {
		return site_id;
	}
	public void setSiteId(Long siteId) {
		this.site_id = siteId;
	}
	
	public String getSiteName(){
		return siteName;
	}
	
	public void setSiteName(String siteName){
		this.siteName = siteName;
	}
	
	@ElementCollection
	//need to set lazy to false for collections i.e. list otherwise session is null
	@LazyCollection(LazyCollectionOption.FALSE)
	@CollectionTable(name="SITE_FLOORS", joinColumns=@JoinColumn(name="site_id"))
	@Column(name="floorimage")
	public List<String> getFloors() {
		return floors;
	} 
	
	public void setFloors(List<String> floors){
		this.floors = floors;
	}

	public String getEmailAddress() {
		return emailAddress;
	} 
	
	public void setEmailAddress(String emailAddress){
		this.emailAddress = emailAddress;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	public boolean isAlert() {
		return alert;
	}
}
