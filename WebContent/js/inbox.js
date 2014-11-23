


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
					<tr><td>'+ jsonObj[i].from +'</td><td>'+jsonObj[i].message_time+'</td></tr>\
					<tr><td colspan=2>'+jsonObj[i].message_text+'</td><tr>\
				</table>\
				</div>';
		}
	
	document.getElementById("inbox").innerHTML = data;
}