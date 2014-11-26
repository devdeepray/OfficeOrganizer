
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
	fetchMessages();
	fetchSent();
}


function bindAutoCompleteSendMessage() {
	$( "#sendmessidbox" ).autocomplete({
		search: function (){},
		source: function(request,response){
			$.ajax({
				dataType : "json",
				type : "POST",
				url : "ManageContacts",
				data : "operation=search&user_id=" + sess_uid + "&password=" + sess_pass + "&search_query=" + $("#sendmessidbox").val(),
				success: function(data){
					response($.map(data, function(item){ return item.uid + ":"+item.name;  }));
				}
			});
		},
		minLength: 0,
		select: function (event,ui){
			$("#myform").submit();
		}
	});
};