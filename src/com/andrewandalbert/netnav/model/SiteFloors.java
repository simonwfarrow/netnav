package com.andrewandalbert.netnav.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;

@Entity 
@Table(name="SITE_FLOORS")
@XmlRootElement
public class SiteFloors {

	private Long id;
	private String imageName;
	
	public SiteFloors(){
		
	}
	
	public SiteFloors(String imageName){
		this.imageName = imageName;
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
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	
	
}
