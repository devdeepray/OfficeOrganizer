
function deleteAppointment(appt_id)
{
	$.ajax({
	async: false,
    url     : "ManageAppointments",
    type    : "POST",
    data    : "user_id="+sess_uid+"&password="+sess_pass+"&operation=delete&appointment_id="+appt_id,
    success : function(jsondata){
    	fetchAppointments();
    } 
	});
}

function fetchAppointments()
{
	
	var messageData = null;
	$.ajax({
		async: false,
	    url     : "ManageAppointments",
	    type    : "POST",
	    data    : "user_id="+sess_uid+"&password="+sess_pass+"&operation=fetchupcoming",
	    success : function(jsondata){

	    	messageData = jsondata;
	    } 
	});
	data = '';
	var jsonObj = JSON.parse(messageData);
	for(var i=0; i<jsonObj.length; i++)
		{
			data+='<div id="singleappointment'+(i%2)+'">\
				<table id="appointmenttable">\
					<tr><td>'+ jsonObj[i].venue +'</td><td>'+jsonObj[i].time+'</td><td><a href="#" onclick=deleteAppointment('+jsonObj[i].id+')>Del</a></td></tr>\
					<tr><td colspan=2>'+jsonObj[i].agenda+'</td><tr>\
				</table>\
				</div>';
		}
	
	document.getElementById("appointments").innerHTML = data;
}

