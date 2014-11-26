
function deleteSentbox(mess_id)
{
	$.ajax({
	async: false,
    url     : "ManageMessages",
    type    : "POST",
    data    : "user_id="+sess_uid+"&password="+sess_pass+"&operation=deletesent&message_id="+mess_id,
    success : function(jsondata){

    	fetchSent();
    } 
	});
}


function fetchSent()
{
	
	var messageData = null;
	$.ajax({
		async: false,
	    url     : "ManageMessages",
	    type    : "POST",
	    data    : "user_id="+sess_uid+"&password="+sess_pass+"&operation=fetchsent",
	    success : function(jsondata){

	    	messageData = jsondata;
	    } 
	});
	data = '';
	var jsonObj = JSON.parse(messageData);
	for(var i=0; i<jsonObj.length; i++)
		{
			data+='<div id="singlemessage'+(i%2)+'">\
				<table id="messagetable">\
					<tr><td>'+ jsonObj[i].to +'</td><td>'+jsonObj[i].message_time+'</td><td><a href="#" onclick=deleteSentbox('+jsonObj[i].message_id+')>Del</a></td></tr>\
					<tr><td colspan=3>'+jsonObj[i].message_text+'</td><tr>\
				</table>\
				</div>';
		}
	
	document.getElementById("sent").innerHTML = data;
}