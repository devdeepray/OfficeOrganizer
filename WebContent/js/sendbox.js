
function submitsendform()
{
	
	var form = document.getElementById("sendform");
	var toid = form.elements[0].value;
	var message = encodeURIComponent(form.elements[1].value);
	$.ajax({
		async: false,
	    url     : "ManageMessages",
	    type    : "POST",
	    data    : "user_id="+sess_uid+"&password="+sess_pass+"&operation=post"+"&to_id="+toid+"&message="+message,
	    success : function(jsondata){
	    	alert("Message sent");
	    } 
	});
}