function bindAutoComplete() {
	$( ".contactsSearch" ).autocomplete({
		search: function (){},
		source: function(request,response){
			$.ajax({
				dataType : "json",
				type : "POST",
				url : "ManageContacts",
				data : "operation=search&user_id=" + sess_uid + "&password=" + sess_pass + "&search_query=" + $("#contactsSearchText").val(),
				success: function(data){
					renderContacts(JSON.parse(JSON.stringify(data)));
					//response(data);
				}
			});
		},
		minLength: 0,
		select: function (event,ui){
			$("#myform").submit();
		}
	});
};

function setViewUidAs(uid){
	
	bc_CURRENT_USER = uid; 
	bc_renderNow();
}

function renderRecentContacts () {
	var jsontxt = null;
	$.ajax({
		async: false,
	    url     : "ManageContacts",
	    type    : "POST",
	    data    : "operation=recent&user_id="+sess_uid+"&password="+sess_pass,
	    success : function(jsondata){
	    	jsontxt = jsondata;
	    } 
	});
	
	renderContacts(JSON.parse(jsontxt));
	
	/*var json_obj = JSON.parse(jsontxt);
	
	var contacts_list = '<table class="hoverMenu">';
	
	for (var i = 0; i < json_obj.length; i++) {
		contacts_list += '<tr><td> <ul><li><a href="#">' + json_obj[i]["name"] + ' &#9662;</a> \
            <ul> \
        <li><a href="#">Laptops</a></li> \
        <li><a href="#">Monitors</a></li> \
        <li><a href="#">Printers</a></li> \
    </ul> </li></ul></td></tr>';
//		contacts_list += '<tr><td>' + json_obj[i] + '</td></tr>';
	}
	contacts_list += '</table>';
	
	document.getElementById("searchContactsBox").innerHTML += contacts_list;*/
	
}


function deleteContact(arg)
{
	$.ajax({
		async: false,
	    url     : "ManageContacts",
	    type    : "POST",
	    data    : "operation=delete&user_id="+sess_uid+"&password="+sess_pass+"&contact_id=" + arg
	});
	renderRecentContacts ();
}

function openChatWindow(arg)
{
	rbTabShow("sendmess");
	document.getElementById("sendmessidbox").value=arg;
}
function renderContacts (json_obj) {
	var contacts_list = '<table id="menuwrapper">';
	
	for (var i = 0; i < json_obj.length; i++) {
		contacts_list += '<tr><td> <ul><li><a href="#">' + json_obj[i].name + ' &#9662;</a> \
		<ul> <li><a href="#" onclick=\
		"setViewUidAs('+ json_obj[i].uid +')">View \
		Calendar</a></li><li><a href="#" onclick="openChatWindow(\''+json_obj[i].uid+':'+json_obj[i].name+'\')">Chat</a></li> 	<li><a href="#" onclick="deleteContact('+json_obj[i].uid+')">Delete</a></li></ul> </li>\
		</ul></td></tr>';
//		contacts_list += '<tr><td>' + json_obj[i] + '</td></tr>';
	}
	contacts_list += '</table>';
	
	document.getElementById("contactsList").innerHTML = contacts_list;
	bindAutoComplete();
}
