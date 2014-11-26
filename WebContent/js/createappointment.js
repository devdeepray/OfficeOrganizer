function bindAutoCompleteNewAppointment() {	
	$( ".inputAttendees" ).autocomplete({
		search: function (){},
		source: function(request,response){
			$.ajax({
				dataType : "json",
				type : "POST",
				url : "ManageContacts",
				data : "operation=search&user_id=" + sess_uid + "&password=" + sess_pass + "&search_query=" + request.term,
				success: function(data){
					response($.map(data, function(item){ return item.uid + ":"+item.name;  }));
				}
			});
		},
		minLength: 2,
		select: function (event,ui){
			$("#myform").submit();
		}
	});
};



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


function prepareAppointmentForm() {
	
	data = '<form id="appointmentForm" action="ManageAppointments" method="POST">\
	<ul style="list-style: none;">\
		<li id="li_9" >\
			<label>Appointment date</label>\
			<div>\
				<input type="text" id="datepicker" name="date">\
			</div> \
			<br>\
		</li>\
			<li id="li_9" >\
			<label>Starting time for the appointment</label>\
			<div>\
				<select id="startChooser" name="start_time">\
					<option value ="0">00:00</option><option value ="1">00:30</option><option value ="2">01:00</option><option value ="3">01:30</option><option value ="4">02:00</option><option value ="5">02:30</option><option value ="6">03:00</option><option value ="7">03:30</option><option value ="8">04:00</option><option value ="9">04:30</option><option value ="10">05:00</option><option value ="11">05:30</option><option value ="12">06:00</option><option value ="13">06:30</option><option value ="14">07:00</option><option value ="15">07:30</option><option value ="16">08:00</option><option value ="17">08:30</option><option value ="18">09:00</option><option value ="19">09:30</option><option value ="20">10:00</option><option value ="21">10:30</option><option value ="22">11:00</option><option value ="23">11:30</option><option value ="24">12:00</option><option value ="25">12:30</option><option value ="26">13:00</option><option value ="27">13:30</option><option value ="28">14:00</option><option value ="29">14:30</option><option value ="30">15:00</option><option value ="31">15:30</option><option value ="32">16:00</option><option value ="33">16:30</option><option value ="34">17:00</option><option value ="35">17:30</option><option value ="36">18:00</option><option value ="37">18:30</option><option value ="38">19:00</option><option value ="39">19:30</option><option value ="40">20:00</option><option value ="41">20:30</option><option value ="42">21:00</option><option value ="43">21:30</option><option value ="44">22:00</option><option value ="45">22:30</option><option value ="46">23:00</option><option value ="47">23:30</option>\
				</select>\
			</div> \
			<br>\
		</li>\
		<li id="li_9" >\
			<label>Ending time for the appointment</label>\
			<div>\
				<select id="endChooser" name="end_time">\
					<option value ="0">00:00</option><option value ="1">00:30</option><option value ="2">01:00</option><option value ="3">01:30</option><option value ="4">02:00</option><option value ="5">02:30</option><option value ="6">03:00</option><option value ="7">03:30</option><option value ="8">04:00</option><option value ="9">04:30</option><option value ="10">05:00</option><option value ="11">05:30</option><option value ="12">06:00</option><option value ="13">06:30</option><option value ="14">07:00</option><option value ="15">07:30</option><option value ="16">08:00</option><option value ="17">08:30</option><option value ="18">09:00</option><option value ="19">09:30</option><option value ="20">10:00</option><option value ="21">10:30</option><option value ="22">11:00</option><option value ="23">11:30</option><option value ="24">12:00</option><option value ="25">12:30</option><option value ="26">13:00</option><option value ="27">13:30</option><option value ="28">14:00</option><option value ="29">14:30</option><option value ="30">15:00</option><option value ="31">15:30</option><option value ="32">16:00</option><option value ="33">16:30</option><option value ="34">17:00</option><option value ="35">17:30</option><option value ="36">18:00</option><option value ="37">18:30</option><option value ="38">19:00</option><option value ="39">19:30</option><option value ="40">20:00</option><option value ="41">20:30</option><option value ="42">21:00</option><option value ="43">21:30</option><option value ="44">22:00</option><option value ="45">22:30</option><option value ="46">23:00</option><option value ="47">23:30</option>\
				</select>\
			</div> \
			<br>\
		</li>\
		<li id="li_9" >\
			<label>Meeting room</label>\
			<div>\
				<select id="roomChooser" name="room_no"></select>\
			</div> \
			<br>\
		</li>\
		<li id="li_4" >\
			<label class="description" for="element_4">Appointment/Meeting Agenda </label>\
			<div>\
				<textarea id="agendaText" name="agenda" class="text_small" rows="4" cols="50"></textarea>\
			</div>\
			<br>\
		</li>\
		<li id="li_13" >\
			<label class="description" for="element_13">Attendees</label>\
			<div id="attendees">\
				<input id="textBoxAttendee0" type="text" size="50" name="attendee0" class="inputAttendees"/>\
			</div>\
			<div>\
				<button id="addNewAttendees">Add more attendees</button>\
			</div>\
			<br>\
		</li>\
		<input id="attendeeCount" type="hidden" value="1" name="attendeeCount"/>\
		<input id="operation" type="hidden" name="operation" value="create"/>\
		<input id="user_id" type="hidden" name="user_id"/>\
		<input id="password" type="hidden" name="password"/>\
		<button onclick="submitNewAppointmentForm()">Submit</button>\
		<button onclick="hideNewAppointment()">Close</button>\
	</ul>\
</form>';
	
	$( "#datepicker" ).datepicker({ dateFormat: 'dd-mm-yy' });
	
	document.getElementById("apptForm").innerHTML = data;
	var maxAttendees = 50;
	var wrapper = $("#attendees");
	var addButton = $("#addNewAttendees");
	var currAttendees = 0;

	
	renderDropDowns();
	bindAutoCompleteNewAppointment();
	$("#user_id").val(sess_uid);
	$("#password").val(sess_pass);

	$(addButton).click(function(e){
		e.preventDefault();
		if(currAttendees < maxAttendees){
			currAttendees++;
			$(wrapper).append('<div><input type="text" name="attendee' + currAttendees + '" size="50" class="inputAttendees" id="textBoxAttendee' + currAttendees + '"/></div>'); //add input box
			bindAutoCompleteNewAppointment();
			$("#attendeeCount").val((currAttendees + 1) + "");
			$("#textBoxAttendee" + currAttendees).focus();
		}
	});
}

function showNewAppointment(day, tsid)
{
	document.getElementById("apptForm").style.display="block";
	$( "#datepicker" ).datepicker({ dateFormat: 'dd-mm-yy' });
	document.getElementById("datepicker").value = day;
	document.getElementById("startChooser").value = tsid;
	
}

function hideNewAppointment()
{
	document.getElementById("apptForm").style.display="none";
	prepareAppointmentForm();
}

function submitNewAppointmentForm()
{
	$.post('ManageAppointments', $('#appointmentForm').serialize());
	hideNewAppointment();
}
