package com.andrewandalbert.netnav.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.andrewandalbert.netnav.dao.DeviceDAO;
import com.andrewandalbert.netnav.model.Device;
import com.andrewandalbert.netnav.model.Device.DEVICE_TYPE;
import com.andrewandalbert.netnav.model.Log;
import com.andrewandalbert.netnav.resources.DeviceResource;
import com.andrewandalbert.netnav.server.NetNavServer;

//Will map the resource to the URL devices
@Path("/devices")
public class DevicesResource {
	 // Allows to insert contextual objects into the class, 
	  // e.g. ServletContext, Request, Response, UriInfo
	  @Context
	  UriInfo uriInfo;
	  @Context
	  Request request;


	  // Return the list of devices to the user in the browser
	  @GET
	  @Produces(MediaType.TEXT_XML)
	  public List<Device> getDevicesBrowser() {
	    List<Device> devices = new ArrayList<Device>();
	    devices.addAll(DeviceDAO.instance.getModel().values());
	    return devices; 
	  }
	  
	  // Return the list of devices for applications
	  @GET
	  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	  public List<Device> getDevices() {
	    List<Device> devices = new ArrayList<Device>();
	    devices.addAll(DeviceDAO.instance.getModel().values());
	    return devices; 
	  }
	  
	  
	  // returns the number of devices
	  // /count
	  // to get the total number of records
	  @GET
	  @Path("count")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String getCount() {
	    int count = DeviceDAO.instance.getModel().size();
	    return String.valueOf(count);
	  }
	  
	  /* gets the count of online devices*/
	  @GET
	  @Path("count/offline")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String getOfflineount() {
		  List<Device> devices = new ArrayList<Device>();
		  devices.addAll(DeviceDAO.instance.getModel().values());
		  Iterator<Device> it = devices.iterator();
		  int count = 0;
		  
		  while (it.hasNext()){
			  Device dev = it.next();
			  if (dev.getStatus().equals("Offline")) count++;
		  }
	    return String.valueOf(count);
	  }
	  
	  /* gets the count of offline devices*/
	  @GET
	  @Path("count/online")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String getOnlineount() {
		  List<Device> devices = new ArrayList<Device>();
		  devices.addAll(DeviceDAO.instance.getModel().values());
		  Iterator<Device> it = devices.iterator();
		  int count = 0;
		  
		  while (it.hasNext()){
			  Device dev = it.next();
			  if (dev.getStatus().equals("Online")) count++;
		  }
	    return String.valueOf(count);
	  }
	  
	  @POST
	  @Produces(MediaType.TEXT_HTML)
	  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	  public void newDevice(MultivaluedMap<String, String> formParams,
	      @Context HttpServletResponse servletResponse) throws IOException {
		  
		int count = DeviceDAO.instance.getModel().size();
		String license = (String)NetNavServer.props.get("license");
		if (count<Integer.valueOf(license)){ 
		    
			List<String> deviceNameList = formParams.get("deviceName");
			List<String> ipList = formParams.get("ip");
			List<String> deviceTypeList = formParams.get("deviceType");
			List<String> floorList = formParams.get("floors");
			List<String> alertList = formParams.get("alert");
			boolean alert;
			
			if(alertList!=null && alertList.get(0).equals("Alert")){
				  alert = true;
			  } else {
				  alert = false;
			  }
			
		    Device device = new Device(deviceNameList.get(0),ipList.get(0),DEVICE_TYPE.valueOf(deviceTypeList.get(0)),floorList.get(0),alert);
		
		    //DeviceDAO.instance.getModel().put(id, device);
		    DeviceDAO.instance.putDevice(device);
		    
		    //try re-direct back to main page, as on iOS it stops on rest page
		    servletResponse.sendRedirect("../index.html");
		} else {
			System.out.println("LICENSE LIMITATION REACHED : " + license);
		}
	  }
	  
	  @Path("delete/{device}")
	  @PUT
	  @Consumes("text/plain")
	  @Produces(MediaType.TEXT_HTML)
	  public void deleteDevice(@PathParam("device") Long id,@Context HttpServletResponse servletResponse) throws IOException{
		  Device device = DeviceDAO.instance.getModel().remove(id);
		  DeviceDAO.instance.deleteDevice(device);
		  if(device==null)
		    throw new RuntimeException("Delete: Device with " + id +  " not found");  
		  //servletResponse.sendRedirect("../NetNav/index.html");
	  }
	  
	  @Path("move/{device}")
	  @PUT
	  //@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	  @Consumes("text/plain") 
	  @Produces(MediaType.TEXT_HTML)
	  public void updateDevice(@PathParam("device") Long id,String data,@Context HttpServletResponse servletResponse) throws IOException{
		  //new location co-ords
		  String[] values = data.split(":");
		  //get id from url i.e. /device/id
		  DeviceResource devRes = new DeviceResource(uriInfo, request, id);
		  Device device = devRes.getDevice();
		  device.setxLoc(new Integer(values[0]));
		  device.setyLoc(new Integer(values[1]));
		  DeviceDAO.instance.updateDevice(device,Log.ACTION_TYPE.MOVE);
	  }
	  
	  //used for updating notes
	  @Path("update/{device}")
	  @PUT
	  //@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	  @Produces(MediaType.TEXT_HTML)
	  public void updateDeviceNotes(@PathParam("device") Long id,String data,@Context HttpServletResponse servletResponse) throws IOException{
		  DeviceResource devRes = new DeviceResource(uriInfo, request, id);
		  Device device = devRes.getDevice();
		  device.setNotes(data);
		  DeviceDAO.instance.updateDevice(device,Log.ACTION_TYPE.UDPATE);
		
	  }
	  
	  @Path("{device}")
	  @POST
	  //@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	  @Produces(MediaType.TEXT_HTML)
	  public void updateDevice(@PathParam("device") Long id,
			  MultivaluedMap<String, String> formParams,
			  @Context HttpServletResponse servletResponse) throws IOException{
		  
		  List<String> deviceNameList = formParams.get("deviceName");
		  List<String> deviceTypeList = formParams.get("deviceType");
		  List<String> ipList = formParams.get("ip");
		  List<String> macList = formParams.get("mac");
		  List<String> floorsList = formParams.get("floors");
		  List<String> alertList = formParams.get("alert");
		  
		  DeviceResource devRes = new DeviceResource(uriInfo, request, id);
		  Device device = devRes.getDevice();
		  device.setDeviceName(deviceNameList.get(0));
		  device.setDeviceType(DEVICE_TYPE.valueOf(deviceTypeList.get(0)));
		  device.setIpAddress(ipList.get(0));
		  device.setMacAddress(macList.get(0));
		  device.setFloor(floorsList.get(0));
		  
		  if(alertList!=null && alertList.get(0).equals("Alert")){
			  device.setAlert(true);
		  } else {
			  device.setAlert(false);
		  }
		  DeviceDAO.instance.updateDevice(device,Log.ACTION_TYPE.EDIT);
		  
		  servletResponse.sendRedirect("../../index.html");
	  }
	  
	  // Defines that the next path parameter after device is
	  // treated as a parameter and passed to the DeviceResources
	  // Allows to type http://localhost:8081/..../rest/devices/{id}
	  // 1 will be treaded as parameter device and passed to DeviceRespurce
	  @Path("{device}")
	  public DeviceResource getDevice(@PathParam("device") Long id) {
	    return new DeviceResource(uriInfo, request, id);
	  }
	  
	} 
