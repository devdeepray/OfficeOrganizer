
function deleteInbox(mess_id)
{;
	$.ajax({
	async: false,
    url     : "ManageMessages",
    type    : "POST",
    data    : "user_id="+sess_uid+"&password="+sess_pass+"&operation=delete&message_id="+mess_id,
    success : function(jsondata){

    	fetchMessages();
    } 
	});
}

function fetchMessages()
{
	
	var messageData = null;
	$.ajax({
		async: false,
	    url     : "ManageMessages",
	    type    : "POST",
	    data    : "user_id="+sess_uid+"&password="+sess_pass+"&operation=fetch",
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
					<tr><td>'+ jsonObj[i].from +'</td><td>'+jsonObj[i].message_time+'</td><td><a href="#" onclick=deleteInbox('+jsonObj[i].message_id+')>Del</a></td></tr>\
					<tr><td colspan=3>'+jsonObj[i].message_text+'</td><tr>\
				</table>\
				</div>';
		}
	
	document.getElementById("inbox").innerHTML = data;
}