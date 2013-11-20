package com.andrewandalbert.netnav.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import com.andrewandalbert.netnav.dao.DeviceDAO;
import com.andrewandalbert.netnav.dao.SiteConfigDAO;
import com.andrewandalbert.netnav.model.Device;
import com.andrewandalbert.netnav.model.SiteConfig;
import com.andrewandalbert.netnav.model.SiteFloors;

@Path("/config")
public class SiteConfigResource {
	
	  @Context
	  UriInfo uriInfo;
	  @Context
	  Request request;
	  Long id;
	
	  
	   //Application integration     
	  @GET
	  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	  public List<SiteConfig> getSiteConfig() {
		  List<SiteConfig> config = new ArrayList<SiteConfig>();
		  config.addAll(SiteConfigDAO.instance.getModel().values());
		  return config;
	  }
	  
	  // For the browser
	  @GET
	  @Produces(MediaType.TEXT_XML)
	  public List<SiteConfig> getSiteConfigHTML() {
		  List<SiteConfig> config = new ArrayList<SiteConfig>();
		  config.addAll(SiteConfigDAO.instance.getModel().values());
		  return config;
	  }
	  
	  @PUT
	  @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	  public Response putSiteConfig(JAXBElement<SiteConfig> config) {
	    SiteConfig c = config.getValue();
	    System.out.println(c.toString());
	    return putAndGetResponse(c);
	  }
	  
	  @DELETE
	  public void deleteSiteConfig() {
	    SiteConfig config = SiteConfigDAO.instance.getModel().remove(id);
	    if(config==null)
	      throw new RuntimeException("Delete: Site Config  with " + id +  " not found");
	  }
	  
	  @POST
	  @Produces(MediaType.TEXT_HTML)
	  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	  public void newConfig(MultivaluedMap<String, String> formParams,
	      @Context HttpServletResponse servletResponse) throws IOException {
		//if site already exists then this is an update, there should only be one site - bit of a bodge
		List<String> floorsList = formParams.get("floors");
		List<String> siteNameList = formParams.get("siteName");
		List<String> emailAddressList = formParams.get("emailAddress");
		List<String> alertList = formParams.get("alert");
		
		SiteConfig eConfig = SiteConfigDAO.instance.getModel().get(new Long(1));
		if (eConfig==null){
		    SiteConfig config = new SiteConfig(siteNameList.get(0),emailAddressList.get(0),floorsList);
		    SiteConfigDAO.instance.putSiteConfig(config);
		} else {
			eConfig.setSiteName(siteNameList.get(0));
			eConfig.setEmailAddress(emailAddressList.get(0));
			eConfig.setFloors(floorsList);
			
			if(alertList!=null && alertList.get(0).equals("Alert")){
				  eConfig.setAlert(true);
			  } else {
				  eConfig.setAlert(false);
			  }
			
			SiteConfigDAO.instance.updateSiteConfig(eConfig);		
		}
	    
	    servletResponse.sendRedirect("../index.html");
	  }
	  
	  private Response putAndGetResponse(SiteConfig config) {
	    Response res;
	    if(SiteConfigDAO.instance.getModel().containsKey(config.getSiteId())) {
	      res = Response.noContent().build();
	    } else {
	      res = Response.created(uriInfo.getAbsolutePath()).build();
	    }
	    SiteConfigDAO.instance.getModel().put(config.getSiteId(), config);
	    SiteConfigDAO.instance.updateSiteConfig(config);
	    return res;
	  }
	  
	  

	} 