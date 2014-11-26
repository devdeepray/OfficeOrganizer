function confirmAppointment(appt_id ,start_time, end_time, date)
{
	$.ajax({
		async: false,
	    url     : "ManageAppointments",
	    type    : "POST",
	    data    : "user_id="+sess_uid+"&password="+sess_pass+"&operation=add&appointment_id="+appt_id + "&start_time=" + start_time +
	    			"&end_time=" + end_time + "&date=" + date,
	    success : function(jsondata){ 
	    	fetchAppointments();
	    	fetchPending();
	    } 
	});
}


function fetchPending()
{
	
	var messageData = null;
	$.ajax({
		async: false,
	    url     : "ManageAppointments",
	    type    : "POST",
	    data    : "user_id="+sess_uid+"&password="+sess_pass+"&operation=fetchunconfirmed",
	    success : function(jsondata){

	    	messageData = jsondata;
	    } 
	});
	data = '';
	var jsonObj = JSON.parse(messageData);
	for(var i=0; i<jsonObj.length; i++)
		{
			data+='<div id="singleappointment'+(i%2)+'">\
				<table id="pendingtable">\
					<tr><td>'+ jsonObj[i].venue +'</td><td>'+jsonObj[i].start_time+'</td>\
					<td> - '+jsonObj[i].end_time+'</td><td><a href="#" onclick=deleteAppointment('+jsonObj[i].id+')>Del</a></td></tr>\
					<tr><td colspan=3>'+jsonObj[i].agenda+'</td><tr>\
					<tr><td colspan=3><a href="#" onclick="confirmAppointment('+jsonObj[i].id+', ' + jsonObj[i].start_time_id + ', ' +jsonObj[i].end_time_id + ', \'' + jsonObj[i].start_time.split(' ')[0] + '\')">Confirm</a></td></tr>\
				</table>\
				</div>';
		}
	
	document.getElementById("pending").innerHTML = data;
}

