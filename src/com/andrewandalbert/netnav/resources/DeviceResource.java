package com.andrewandalbert.netnav.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import com.andrewandalbert.netnav.dao.DeviceDAO;
import com.andrewandalbert.netnav.model.Device;
import com.andrewandalbert.netnav.model.Log;

public class DeviceResource {
	
	  @Context
	  UriInfo uriInfo;
	  @Context
	  Request request;
	  Long id;
	  public DeviceResource(UriInfo uriInfo, Request request, Long id) {
	    this.uriInfo = uriInfo;
	    this.request = request;
	    this.id = id;
	  }
	  
	   //Application integration     
	  @GET
	  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	  public Device getDevice() {
	    Device device = DeviceDAO.instance.getModel().get(id);
	    if(device==null)
	      throw new RuntimeException("Get: Device with " + id +  " not found");
	    return device;
	  }
	  
	  // For the browser
	  @GET
	  @Produces(MediaType.TEXT_XML)
	  public Device getDeviceHTML() {
	    Device device = DeviceDAO.instance.getModel().get(id);
	    if(device==null)
	      throw new RuntimeException("Get: Device with " + id +  " not found");
	    return device;
	  }
	  
	  @PUT
	  @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	  public Response putDevice(JAXBElement<Device> device) {
	    Device c = device.getValue();
	    System.out.println(c.toString());
	    return putAndGetResponse(c);
	  }
	  
	  @DELETE
	  public void deleteDevice() {
	    Device device = DeviceDAO.instance.getModel().remove(id);
	    if(device==null)
	      throw new RuntimeException("Delete: Device with " + id +  " not found");
	  }
	  
	  private Response putAndGetResponse(Device device) {
	    Response res;
	    if(DeviceDAO.instance.getModel().containsKey(device.getId())) {
	      res = Response.noContent().build();
	    } else {
	      res = Response.created(uriInfo.getAbsolutePath()).build();
	    }
	    DeviceDAO.instance.getModel().put(device.getId(), device);
	    DeviceDAO.instance.updateDevice(device,Log.ACTION_TYPE.UDPATE);
	    return res;
	  }
	  
	  

	} 