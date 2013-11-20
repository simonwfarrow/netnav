var hashTextObj = new Object; // map of device text object
var hashImgObj = new Object; // map of device image objects
var hashDevices = new Object; // hash map of devices
var hashFloors = new Object; // hasp map of floor paper
var paper; // main raphael drawing canvas
var curFloor = "1"; // store which florr is currently selected
var selectedDeviceId = 0; // store device id of the one the user selected from the tables
var site = new Object; // store site config
var tooltip; // text object for showing tooltip
var refreshIntervalObj; // store handle to refresh
var offSelectedDevId = 0; // used to store which id was selected from the
							// offline table, for updating the row colour
var floorSelectedDevId = 0; // used to store which id was selected from the
							// floor table, for updating the row colour
var imgWidth = 750;
var imgHeight = 458;

//display dat.gui
var Options = function() {
	this.locked = true;
	this.refresh = 15;
};
//global var to access dat.gui function attributes
var opts = new Options();
var gui = new dat.GUI();
gui.add(opts, 'locked');
refreshInt = gui.add(opts, 'refresh', 5,30).step(5);
refreshInt.onChange(function(value) {
	setRefresh(value);
});


// called from the settings modal when the refresh rate is changed
function setRefresh(value) {
	window.clearInterval(refreshIntervalObj);
	refreshIntervalObj = window.setInterval(function() {refreshData(curFloor)}, (value*1000));
}

// function to get device text object from map
function gethashTextObj(which) {
	for (key in hashTextObj) {
		if (key == which) {
			return hashTextObj[which];
		}
	}
}

// function to get device image object from map
function gethashImgObj(which) {
	for (key in hashImgObj) {
		if (key == which) {
			return hashImgObj[which];
		}
	}
}

// function to get device object from map
function gethashDevice(which) {
	for (key in hashDevices) {
		if (key == which) {
			return hashDevices[which];
		}
	}
}

// function to get floor paper object from map
function gethashFloor(which) {
	for (key in hashFloors) {
		if (key == which) {
			return hashFloors[which];
		}
	}
}

// functions for moving the image on the canvas
var start = function() {
	if (opts.locked) {return};
	// storing original coordinates
	this.ox = this.attr("x");
	this.oy = this.attr("y");
	this.attr({
	// opacity : .5
	});
	var text = gethashTextObj(this.node.textContent);
	text.hide();
	tooltip.remove();

}, move = function(dx, dy) {
	if (opts.locked) {return};
	// move will be called with dx and dy
	// restrict movement of icon to within boundaries
	var nx = this.ox+dx;
	var ny = this.oy+dy;
	if (nx<0){
		this.attr({x:0});
	} else if (nx>imgWidth) {
		this.attr({x:(imgWidth-20)}); 
	} else {
		this.attr({x:nx});
	}
	if (ny<0){
		this.attr({y:0});
	} else if (ny>imgHeight) {
		this.attr({y:(imgHeight-20)}); 
	} else {
		this.attr({y:ny});
	}
	//this.attr({
	//	x : this.ox + dx,
	//	y : this.oy + dy
	//});
		
}, up = function() {
	if (opts.locked) {return};
	// restoring state
	this.attr({
		opacity : 1
	});
	// had to add the cache header to stop ios caching
	$.ajax({
		type : "PUT",
		url : '../NetNav/rest/devices/move/' + this.node.textContent,
		contentType : "text/plain; charset=utf-8",
		headers : {
			"cache-control" : "no-cache"
		},
		data : this.attr("x") + ":" + this.attr("y")
	// this.node.textContent + ":" +
	});
	var text = gethashTextObj(this.node.textContent);
	text.attr("x", this.attr("x"));
	text.attr("y", this.attr("y"));
	text.show();
};

// functions to show tooltip when users hovers of device image
var hoverIn = function() {

	var id = this.node.textContent;
	var device = gethashDevice(id);
	var deviceText = gethashTextObj(id);
	deviceText.hide();
	var ttString = device.id + ":" + device.deviceName + ":" + device.ipAddress + ":" + device.uptime;
	tooltip = paper.text(this.attr("x"), this.attr("y"), ttString);
	tooltip.attr({ "font-size": 16, "font-weight": "bold", "font-family": "Calibri" });

}, hoverOut = function() {
	tooltip.remove();
	var id = this.node.textContent;
	var deviceText = gethashTextObj(id);
	deviceText.show();
};

//offline device count
function getOfflineDeviceCount(){

	var offlineCount = "";
	$.ajax({
		url : '../NetNav/rest/devices/count/offline',
		contentType : "text/plain; charset=utf-8",
		type : 'GET',
		dataType : 'text',
		async : false,
		success : function(data) {
			offlineCount = data;
		}
	});
	
	return offlineCount;
}

//online device count
function getOnlineDeviceCount(){
	
	var onlineCount = "";
	$.ajax({
		url : '../NetNav/rest/devices/count/online',
		contentType : "text/plain; charset=utf-8",
		type : 'GET',
		dataType : 'text',
		async : false,
		success : function(data) {
			onlineCount = data;
		}
	});
	
	return onlineCount;
	
}

// load the the data on page load
function initialLoad() {
	
	// paper = Raphael(document.getElementById("floorPlans"),"100%", "100%");
	paper = Raphael(document.getElementById("floorPlans"), imgWidth,imgHeight);

	$.ajax({
		url : '../NetNav/rest/config',
		contentType : "application/json; charset=utf-8",
		type : 'GET',
		headers : {
			"cache-control" : "no-cache"
		},
		dataType : 'json',
		success : function(result) {

			floorList = document.getElementById("floorList");
			for ( var i = 1; i <= result.siteConfig.floors.length; i++) {

				var option = document.createElement("option");
				option.text = result.siteConfig.floors[i - 1];
				floorList.add(option, null);
				hashFloors[i] = result.siteConfig.floors[i - 1];

				// set the client refresh time
				//replaced with dat.gui in v1.1 - 201113
				//setRefresh(result.siteConfig.clientRefresh);
				site = result.siteConfig;
			}

			paper.image("images/" + gethashFloor(1) + ".png", 0, 0, imgWidth,imgHeight);
			curFloor = floorList[0].text;
			refreshData(curFloor);
		}
	});
}

// function to change the floor display
function changeFloor(obj) {

	curFloor = obj[obj.selectedIndex].text;
	// remove all images from paper and reset vars
	paper.clear();
	paper.remove();
	hashTextObj = new Object;
	hashImgObj = new Object;
	hashDevices = new Object;

	var table = document.getElementById("deviceTable");

	// we have 2 hdrs, so delete all rows except first 2
	while (table.rows.length > 1) {
		table.deleteRow(-1);
	}

	// add floor image and call refreshdata to populate
	paper = Raphael(document.getElementById("floorPlans"), imgWidth,imgHeight);
	paper.image("images/" + gethashFloor(obj.selectedIndex + 1) + ".png", 0, 0,
			imgWidth,imgHeight);
	refreshData(curFloor);
}
function refreshData(floor) {

	$
			.ajax({
				url : '../NetNav/rest/devices',
				contentType : "application/json; charset=utf-8",
				type : 'GET',
				headers : {
					"cache-control" : "no-cache"
				},
				dataType : 'json',
				success : function(result) {

					// get table object
					var table = document.getElementById("deviceTable");
					var offlineTable = document.getElementById("offlineDeviceTable");
					var insert = '';
					var insertOffline = '';
					
					$
							.each(
									result.device,
									function(index, item) {
			
										// get previous device object
										var existingDevice = gethashDevice(item.id);

										if (existingDevice == null) {

											// new device
											// store in hashmap for later
											// retrieval
											hashDevices[item.id] = item;

											if (floor == item.floor) {
												// user is looking at floor for
												// this device so add to devices
												// table and display icon
												// create row insert statement
												insert += '<tr id="'
														+ item.id
														+ '" onclick="selectRow(this)">'
														+ '<td id="id">'
														+ item.id
														+ '</td><td id="name">'
														+ item.deviceName
														+ '</td><td id="ip">'
														+ item.ipAddress
														+ '</td><td id="mac">'
														+ item.macAddress
														+ '</td><td id="type">'
														+ item.deviceType
														+ '</td><td id="status">'
														+ item.status
														+ '</td><td id="uptime">'
														+ item.uptime
														+ '</td></tr>';

												// print device on image

												if (item.deviceType == "SWITCH") {
													if (item.status == "Online") {
														var newDevice = eval("paper.image('images/Switch-Online.png',"
																+ item.xLoc
																+ ","
																+ item.yLoc
																+ ",75,25);");
													} else {
														var newDevice = eval("paper.image('images/Switch-Offline.png',"
																+ item.xLoc
																+ ","
																+ item.yLoc
																+ ",75,25);");
													}
												} else {
													if (item.status == "Online") {
														var newDevice = eval("paper.image('images/WiFi-Online.png',"
																+ item.xLoc
																+ ","
																+ item.yLoc
																+ ",50,50);");
													} else {
														var newDevice = eval("paper.image('images/WiFi-Offline.png',"
																+ item.xLoc
																+ ","
																+ item.yLoc
																+ ",50,50);");
													}
												}

												newDevice.drag(move, start, up);
												newDevice.hover(hoverIn,hoverOut);
												newDevice.attr("cursor","pointer");

												// set device id in textContent
												// should try using Element.data
												// insetad
												newDevice.node.textContent = item.id;
												// store in hashmap
												hashImgObj[item.id] = newDevice;

												// create text of item id and
												// store in array so we can
												// retrieve it and hide it when
												// user clicks on circle to move
												var newDeviceText = eval("paper.text("
														+ item.xLoc
														+ ", "
														+ item.yLoc
														+ ","
														+ item.id + ");");
												hashTextObj[item.id] = newDeviceText;

											}

										} else { // !existingDevice==null

											// existing device
											
											// store updated item over
											// the top of the existing
											// item - need to do this to update notes
											hashDevices[item.id] = item;
											
											if (floor == item.floor) {
												// for table, may as well just
												// set the status value rather
												// then checking then setting -
												// more performant
												eval('var rowItem = table.rows.namedItem("'+ item.id + '");');
												rowItem.cells.namedItem("status").textContent = item.status;
												
												// get existing image, remove
												// and repace if status has
												// changed
												if (item.status != existingDevice.status) {

													var img = gethashImgObj(item.id);
													img.remove();

													if (item.deviceType == "SWITCH") {
														if (item.status == "Online") {
															var newDevice = eval("paper.image('images/Switch-Online.png',"
																	+ item.xLoc
																	+ ","
																	+ item.yLoc
																	+ ",75,25);");
														} else {
															var newDevice = eval("paper.image('images/Switch-Offline.png',"
																	+ item.xLoc
																	+ ","
																	+ item.yLoc
																	+ ",75,25);");
														}
													} else {
														if (item.status == "Online") {
															var newDevice = eval("paper.image('images/WiFi-Online.png',"
																	+ item.xLoc
																	+ ","
																	+ item.yLoc
																	+ ",50,50);");
														} else {
															var newDevice = eval("paper.image('images/WiFi-Offline.png',"
																	+ item.xLoc
																	+ ","
																	+ item.yLoc
																	+ ",50,50);");
														}
													}

													newDevice.drag(move, start,up);
													newDevice.hover(hoverIn,hoverOut);
													newDevice.attr("cursor","pointer");

													// set device id in
													// textContent should try
													// using Element.data
													// instead
													newDevice.node.textContent = item.id;

													// overwrite object in
													// hashmap
													hashImgObj[item.id] = newDevice;

													// create text of item id
													// and store in array so we
													// can retrieve it and hide
													// it when user clicks on
													// circle to move
													var text = gethashTextObj(item.id);
													text.remove();
													var newDeviceText = eval("paper.text("
															+ item.xLoc
															+ ", "
															+ item.yLoc
															+ ","
															+ item.id + ");");
													hashTextObj[item.id] = newDeviceText;

													
												}

											}

										}
										// if the item is now online try and
										// find in offline table and remove
										eval('var rowItem = offlineTable.rows.namedItem("'+ item.id + '");');
										if (item.status == "Online") {
											if (rowItem != null) {
												offlineTable
														.deleteRow(rowItem.rowIndex);
											}
										} else {//if offline and not already in the table, add it
											if (rowItem == null) {
												insertOffline += '<tr id="'
														+ item.id
														+ '" onclick="selectOfflineRow(this)">'
														+ '<td id="id">'
														+ item.id
														+ '</td><td id="name">'
														+ item.deviceName
														+ '</td><td id="ip">'
														+ item.ipAddress
														+ '</td><td id="mac">'
														+ item.macAddress
														+ '</td><td id="type">'
														+ item.deviceType
														+ '</td><td id="floor">'
														+ item.floor
														+ '</td></tr>';
											}
										}
										// }

									});
					$('#deviceTable').append(insert);
					$('#offlineDeviceTable').append(insertOffline);
					
					//animate the currently selected device, this catches the floor change
					var deviceImg = gethashImgObj(selectedDeviceId);
					animateDevice(deviceImg);
				}
				
			});
}

function animateDevice(device){
	device.animate({
		2 : {
			transform : "s1.5"
		},
		3 : {
			transform : "s1"
		},
	}, 600, 'easeOut');
}

// stored device id of selected row
function selectRow(row) {
	// de-highlight old row
	var table = document.getElementById("deviceTable");
	eval('var oldRow = table.rows.namedItem("' + floorSelectedDevId + '");');
	if (oldRow != null) {
		oldRow.style.backgroundColor = "#efefef";
	}
	var table = document.getElementById("offlineDeviceTable");
	eval('var oldRow = table.rows.namedItem("' + offSelectedDevId + '");');
	if (oldRow != null) {
		oldRow.style.backgroundColor = "#efefef";
	}
	// store selected device so we can delete it when user clicks the delete
	// button
	// or we can load the device for the edit button
	selectedDeviceId = row.cells[0].textContent;
	floorSelectedDevId = row.cells[0].textContent;
	// highlight current row
	row.style.backgroundColor = "#4E9CAF";

	// animate selected device image object
	var deviceImg = gethashImgObj(selectedDeviceId);
	animateDevice(deviceImg);
	
	//load notes
	var dev = gethashDevice(selectedDeviceId);
	document.getElementById("devNotes").value = dev.notes;
}

// stored device id of selected row
function selectOfflineRow(row) {
	// de-highlight old row
	var table = document.getElementById("offlineDeviceTable");
	eval('var oldRow = table.rows.namedItem("' + offSelectedDevId + '");');
	if (oldRow != null) {
		oldRow.style.backgroundColor = "#efefef";
	}
	var table = document.getElementById("deviceTable");
	eval('var oldRow = table.rows.namedItem("' + floorSelectedDevId + '");');
	if (oldRow != null) {
		oldRow.style.backgroundColor = "#efefef";
	}
	// store selected device so we can delete it when user clicks the delete
	// button
	// or we can load the device for the edit button
	selectedDeviceId = row.cells[0].textContent;
	offSelectedDevId = row.cells[0].textContent;
	// highlight current row
	row.style.backgroundColor = "#4E9CAF";

	// if dev of different floor change focus to that floor
	var dev = gethashDevice(selectedDeviceId);
	if (dev.floor != curFloor) {

		var select = document.getElementById("floorList");
		for (var i = 0; i <= select.length; i++) {
			if (select.options[i].value == dev.floor) {
				select.options[i].selected = true;
				select.onchange();
			}
		}
	}
	// animate selected device image object
	var deviceImg = gethashImgObj(selectedDeviceId);
	animateDevice(deviceImg);

	//load notes
	var dev = gethashDevice(selectedDeviceId);
	document.getElementById("devNotes").value = dev.notes;
	
}

// delete selected device
function deleteDevice() {
	$.ajax({
		url : '../NetNav/rest/devices/delete/' + selectedDeviceId,
		type : 'PUT',
		headers : {
			"cache-control" : "no-cache"
		},
		success : function(response) {
			hashDevices[selectedDeviceId] = null;

			var txt = gethashTextObj(selectedDeviceId);
			txt.remove();
			var img = gethashImgObj(selectedDeviceId);
			img.remove();

			var table = document.getElementById("deviceTable");
			eval('var rowItem = table.rows.namedItem("' + selectedDeviceId
					+ '");');
			table.deleteRow(rowItem.rowIndex);

			var offTable = document.getElementById("offlineDeviceTable");
			eval('var offRowItem = offTable.rows.namedItem("'
					+ selectedDeviceId + '");');
			offTable.deleteRow(offRowItem.rowIndex);

			refreshData(curFloor);
			alert("Deleted");
		}
	});
}

//delete selected device
function updateDeviceNotes() {
	if (selectedDeviceId!=0){
		var devNotes = document.getElementById("devNotes");
		var notes = devNotes.value;
		$.ajax({
			url : '../NetNav/rest/devices/update/' + selectedDeviceId,
			type : 'PUT',
			data : notes,
			headers : {
				"cache-control" : "no-cache"
			},
			success : function(response) {
				refreshData(curFloor);
				alert("Updated");
			}
		});
	} else {
		alert("No device selected");
	}
}

// delete all logs
function deleteLogs() {
	$.ajax({
		url : '../NetNav/rest/logs/delete',
		type : 'PUT',
		headers : {
			"cache-control" : "no-cache"
		},
		success : function(response) {
			alert("All log entries have been removed");
		}
	});
}

// script for opening edit modal and passing in device data
$(document).on(
		"click",
		".open-EditDeviceModal",
		function() {
			device = gethashDevice(selectedDeviceId);

			$(".modal-body #EdeviceName").val(device.deviceName);
			$(".modal-body #EdeviceType").val(device.deviceType);
			$(".modal-body #Eip").val(device.ipAddress);
			$(".modal-body #Emac").val(device.macAddress);
			
			objAlert = document.getElementById("Ealert");
			if (device.alert=="true"){
				objAlert.checked = true;
			} else {
				objAlert.checked = false;
			}
			

			objSelect = document.getElementById("Efloors");
			// remove all options before re-adding them
			while (objSelect.length > 0) {
				objSelect.remove(0);
			}

			for (floor in hashFloors) {
				var option = document.createElement("option");
				option.text = hashFloors[floor];
				objSelect.add(option, null);
			}

			// dynamically set sumbit action url to include id
			$('.modal-body #editDeviceForm').attr('action',
					'../NetNav/rest/devices/' + selectedDeviceId);

			$('#editDeviceModal').modal('show');

		});

$(document).on("click", ".open-AddDeviceModal", function() {
	
	
	
		// add code to populate floor drop down
		objSelect = document.getElementById("Afloors");
		// remove all options before re-adding them
		while (objSelect.length > 0) {
			objSelect.remove(0);
		}
	
		for (floor in hashFloors) {
			var option = document.createElement("option");
			option.text = hashFloors[floor];
			objSelect.add(option, null);
		}
	
		$('#addDeviceModal').modal('show');
	
});

// script for opening site modal and passing in site data
$(document).on("click", ".open-SiteConfigModal", function() {

	$(".modal-body #CsiteName").val(site.siteName);
	$(".modal-body #CemailAddress").val(site.emailAddress);
	$(".modal-body #Crefresh").val(site.clientRefresh);

	objAlert = document.getElementById("Calert");
	if (site.alert=="true"){
		objAlert.checked = true;
	} else {
		objAlert.checked = false;
	}
	
	objSelect = document.getElementById("Cfloors");
	// remove all options before re-adding them
	while (objSelect.length > 0) {
		objSelect.remove(0);
	}

	for (floor in hashFloors) {
		var option = document.createElement("option");
		option.text = hashFloors[floor];
		option.selected = true;
		objSelect.add(option, null);
	}

	$('#siteConfigModal').modal('show');
});

// use in site config modal to add a floor to the list box
function addFloor() {
	var option = document.createElement("option");
	option.text = document.getElementById("CfloorName").value;
	objSelect = document.getElementById("Cfloors");
	objSelect.add(option, null);

	// make sure all items are automatically selected otherwise we could sumbit
	// and remove all floors!
	for (var i = 0; i <= objSelect.length; i++) {
		objSelect.options[i].selected = true;
	}

}

function reload() {
	window.location.reload(true);
}