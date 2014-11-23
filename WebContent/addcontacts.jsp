<!DOCTYPE html>
<html>
	<head>	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Add Contacts</title>
		<script type="text/javascript" src="./js/jquery.js"></script>
		<link rel="stylesheet" type="text/css" href="http://code.jquery.com/ui/1.9.1/themes/base/jquery-ui.css">
		<script type="text/javascript" src="./js/jquery-ui.js"></script>
		<script type="text/javascript">
			<%-- <%				
				out.println("var sess_uid = " + (String) session.getAttribute("user_id") + ";");
				out.println("var sess_pass = " + (String) session.getAttribute("password") + ";");
			%> --%>
			
			function bindAutoComplete() {	
				$( ".inputContacts" ).autocomplete({
					search: function (){},
					source: function(request,response){
						$.ajax({
							dataType : "json",
							type : "POST",
							url : "ManageContacts",
							data : "operation=searchAll&user_id=" + sess_uid + "&password=" + sess_pass + "&search_query=" + request.term,
							success: function(data){
								response(data);
							}
						});
					},
					minLength: 2,
					select: function (event,ui){
						$("#myform").submit();
					}
				});
			};
			
			function addHidden() {
			    var input = document.createElement('input');
			    input.id = 'attendeCount';
			    input.type = 'hidden';
			    input.name = 'contactCount';
			    input.value = '0';
			    document.getElementById("appointmentForm").appendChild(input);
			}
			
			$(document).ready(function() {
			    var maxContacts = 50;
			    var wrapper = $("#contacts");
			    var addButton = $("#addNewContacts");
			    var currContacts = 0;
			    
			    bindAutoComplete();
			    $("#user_id").val(sess_uid);
			    $("#password").val(sess_pass);
			    
			    $(addButton).click(function(e){
			        e.preventDefault();
			        if(currContacts < maxContacts){
			        	currContacts++;
			            $(wrapper).append('<div><input type="text" name="contact' + currContacts + '" size="50" class="inputContacts" id="textBoxContact' + currContacts + '"/></div>'); //add input box
			            bindAutoComplete();
			            $("#contactCount").val((currContacts + 1) + "");
			            $("#textBoxContact" + currContacts).focus();
			        }
			    });
			});	
		</script>
		
		<div id="tot_div" style="width:80%;margin-left:20%;margin-right:20%">
			<body style="margin: 0 auto">
				<div class="main_form">
					<form id="appointmentForm" action="ManageContacts" method="POST">
						<ul style="list-style: none;">
							<li id="li_13" >
								<label class="description" for="element_13">Contacts</label>
								<div id="contacts">
									<input id="textBoxContact0" type="text" size="50" name="contact0" class="inputContacts"/>
								</div> 
								<div>
									<button id="addNewContacts">Add more contacts</button>
								</div>
								<br>
							</li>
							
							<li class="buttons">
								<input type="hidden" name="form_id" value="893660" />    
								<input id="saveForm" class="button_text" type="submit" name="submit" value="Submit" />
							</li>
						</ul>
						<input id="contactCount" type="hidden" name="contactCount"/>
						<input id="operation" type="hidden" name="operation" value="add"/>
						<input id="user_id" type="hidden" name="user_id"/>
						<input id="password" type="hidden" name="password"/>
					</form>
				</div>
			<br>
		</body>
	</div>
</html>