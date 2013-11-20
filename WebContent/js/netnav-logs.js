// load the log table
function getLogs() {

	$.ajax({
		url : '../NetNav/rest/logs',
		contentType : "application/json; charset=utf-8",
		type : 'GET',
		headers : {
			"cache-control" : "no-cache"
		},
		dataType : 'json',
		success : function(result) {

			var insert = '';
			
			$.each(result.log,function(index, item) {
			
				insert += '<tr id="'
					+ item.id + '">'
					+ '<td id="id">'
					+ item.id
					+ '</td><td id="user">'
					+ item.user
					+ '</td><td id="date">'
					+ item.date
					+ '</td><td id="action">'
					+ item.action
					+ '</td><td id="description">'
					+ item.description
					+ '</td><td id="deviceId">'
					+ item.deviceId
					+ '</td></tr>';
				
				
			});	
			$('#logTable').append(insert);
		}
	});
}