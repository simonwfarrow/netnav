package com.andrewandalbert.netnav.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.andrewandalbert.netnav.dao.DeviceDAO;
import com.andrewandalbert.netnav.dao.LogDAO;
import com.andrewandalbert.netnav.model.Device;
import com.andrewandalbert.netnav.model.Log;

//Will map the resource to the URL devices
@Path("/logs")
public class LogsResource {
	 // Allows to insert contextual objects into the class, 
	  // e.g. ServletContext, Request, Response, UriInfo
	  @Context
	  UriInfo uriInfo;
	  @Context
	  Request request;


	  // Return the list of devices to the user in the browser
	  @GET
	  @Produces(MediaType.TEXT_XML)
	  public List<Log> getLogsBrowser() {
	    List<Log> logs = new ArrayList<Log>();
	    logs.addAll(LogDAO.instance.getModel().values());
	    return logs; 
	  }
	  
	  // Return the list of devices for applications
	  @GET
	  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	  public List<Log> getLogs() {
	    List<Log> logs = new ArrayList<Log>();
	    logs.addAll(LogDAO.instance.getModel().values());
	    return logs; 
	  }
	  
	  
	  // retuns the number of devices
	  // /count
	  // to get the total number of records
	  @GET
	  @Path("count")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String getCount() {
	    int count = LogDAO.instance.getModel().size();
	    return String.valueOf(count);
	  }
	  
	  @Path("delete")
	  @PUT
	  @Consumes("text/plain")
	  public void deleteLogs(){
		 LogDAO.instance.deleteLogs(); 
	  }
	  
	  
	} 
