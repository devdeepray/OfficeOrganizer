function bindAutoComplete() {	
				$( ".inputAttendees" ).autocomplete({
					search: function (){},
					source: function(request,response){
						$.ajax({
							dataType : "json",
							type : "POST",
							url : "ManageContacts",
							data : "operation=searchAppt&user_id=" + sess_uid + "&password=" + sess_pass + "&search_query=" + request.term,
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
			
			$(function() {
				$( "#datepicker" ).datepicker();
			});
			
			function renderDropDowns() {
				var jsontxt = null;
				$.ajax({
					async   : false,
				    url     : "ManageAppointments",
				    type    : "POST",
				    data    : "operation=rooms&user_id="+sess_uid+"&password="+sess_pass,
				    success : function(jsondata){
				    	jsontxt = jsondata;
				    } 
				});
				var json_obj = JSON.parse(jsontxt);
				var select = document.getElementById("roomChooser");
				for (var i = 0; i < json_obj.length; i++) {
					var el = document.createElement("option");
					el.textContent = json_obj[i]["room_no"] + ':	' + json_obj[i]["capacity"];
					el.value = json_obj[i]["room_no"];
					select.appendChild(el);
				}
			}	
			
			function addHidden() {
			    var input = document.createElement('input');
			    input.id = 'attendeCount';
			    input.type = 'hidden';
			    input.name = 'attendeeCount';
			    input.value = '0';
			    document.getElementById("appointmentForm").appendChild(input);
			}
			
			$(document).ready(function() {
			    var maxAttendees = 50;
			    var wrapper = $("#attendees");
			    var addButton = $("#addNewAttendees");
			    var currAttendees = 0;
			    
			    bindAutoComplete();
			    renderDropDowns();
			    $("#user_id").val(sess_uid);
			    $("#password").val(sess_pass);
			    
			    $(addButton).click(function(e){
			        e.preventDefault();
			        if(currAttendees < maxAttendees){
			        	currAttendees++;
			            $(wrapper).append('<div><input type="text" name="attendee' + currAttendees + '" size="50" class="inputAttendees" id="textBoxAttendee' + currAttendees + '"/></div>'); //add input box
			            bindAutoComplete();
			            $("#attendeeCount").val((currAttendees + 1) + "");
			            $("#textBoxAttendee" + currAttendees).focus();
			        }
			    });
			});	