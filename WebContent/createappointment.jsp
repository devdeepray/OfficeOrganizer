<!DOCTYPE html>
<html>
	<head>	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Appointment/Meeting Creator</title>
		<script type="text/javascript" src="./js/jquery.js"></script>
		<!-- <link rel="stylesheet" type="text/css" href="http://code.jquery.com/ui/1.9.1/themes/base/jquery-ui.css"> -->
		<link rel="stylesheet" type="text/css" href="./css/jquery-ui.css">
		<script type="text/javascript" src="./js/jquery-ui.js"></script>
		<script type="text/javascript">
			<%-- <%				
				out.println("var sess_uid = " + (String) session.getAttribute("user_id") + ";");
				out.println("var sess_pass = " + (String) session.getAttribute("password") + ";");
			%> --%>
			
		</script>
		<script type="text/javascript" src="./js/createappointment.js"></script>
		
		<style>
			body {
				font-family: monaco, monospace;
				margin: 0 auto;
				padding: 0;
				height: 100%;
				background-color: #96E0FF;
			}
			
			#fullpage {
				position: fixed !important;
				position: absolute;
				top: 0;
				right: 0;
				bottom: 0;
				left: 0;
			}
		
			#tit {
				width: 100%;
				height: 10%;
				background-color: #33CFFF;
				font-size: 200%;
				text-align: center;
				box-shadow: 10px 10px 20px #4F5965;
				overflow: hidden;
			}
			
			#tittext {
				padding-top: 1%;
			}
			
			#apptForm {
				width: 100%;
				padding-left: 34%;
				font-size: 100%;
			}
		</style>
		
		<body style="margin: 0 auto">
			<div id="fullpage">
				<div id="tit">
					<div id="tittext">Web office organizer V1.0</div>
				</div>
				<div id="apptForm" style="position: relative; top: 2%;">
					<form id="appointmentForm" action="ManageAppointments" method="POST">
						<ul style="list-style: none;">
						
							<li id="li_9" >
								<label>Appointment date</label>
								<div>
									<input type="text" id="datepicker" name="date">
								</div> 
								<br>
							</li>
						
							<li id="li_9" >
								<label>Starting time for the appointment</label>
								<div>
									<select id="startChooser" name="start_time">
										<option value ="0">00:00</option><option value ="1">00:30</option><option value ="2">01:00</option><option value ="3">01:30</option><option value ="4">02:00</option><option value ="5">02:30</option><option value ="6">03:00</option><option value ="7">03:30</option><option value ="8">04:00</option><option value ="9">04:30</option><option value ="10">05:00</option><option value ="11">05:30</option><option value ="12">06:00</option><option value ="13">06:30</option><option value ="14">07:00</option><option value ="15">07:30</option><option value ="16">08:00</option><option value ="17">08:30</option><option value ="18">09:00</option><option value ="19">09:30</option><option value ="20">10:00</option><option value ="21">10:30</option><option value ="22">11:00</option><option value ="23">11:30</option><option value ="24">12:00</option><option value ="25">12:30</option><option value ="26">13:00</option><option value ="27">13:30</option><option value ="28">14:00</option><option value ="29">14:30</option><option value ="30">15:00</option><option value ="31">15:30</option><option value ="32">16:00</option><option value ="33">16:30</option><option value ="34">17:00</option><option value ="35">17:30</option><option value ="36">18:00</option><option value ="37">18:30</option><option value ="38">19:00</option><option value ="39">19:30</option><option value ="40">20:00</option><option value ="41">20:30</option><option value ="42">21:00</option><option value ="43">21:30</option><option value ="44">22:00</option><option value ="45">22:30</option><option value ="46">23:00</option><option value ="47">23:30</option>
									</select>
								</div> 
								<br>
							</li>		
							
							<li id="li_9" >
								<label>Ending time for the appointment</label>
								<div>
									<select id="endChooser" name="end_time">
										<option value ="0">00:00</option><option value ="1">00:30</option><option value ="2">01:00</option><option value ="3">01:30</option><option value ="4">02:00</option><option value ="5">02:30</option><option value ="6">03:00</option><option value ="7">03:30</option><option value ="8">04:00</option><option value ="9">04:30</option><option value ="10">05:00</option><option value ="11">05:30</option><option value ="12">06:00</option><option value ="13">06:30</option><option value ="14">07:00</option><option value ="15">07:30</option><option value ="16">08:00</option><option value ="17">08:30</option><option value ="18">09:00</option><option value ="19">09:30</option><option value ="20">10:00</option><option value ="21">10:30</option><option value ="22">11:00</option><option value ="23">11:30</option><option value ="24">12:00</option><option value ="25">12:30</option><option value ="26">13:00</option><option value ="27">13:30</option><option value ="28">14:00</option><option value ="29">14:30</option><option value ="30">15:00</option><option value ="31">15:30</option><option value ="32">16:00</option><option value ="33">16:30</option><option value ="34">17:00</option><option value ="35">17:30</option><option value ="36">18:00</option><option value ="37">18:30</option><option value ="38">19:00</option><option value ="39">19:30</option><option value ="40">20:00</option><option value ="41">20:30</option><option value ="42">21:00</option><option value ="43">21:30</option><option value ="44">22:00</option><option value ="45">22:30</option><option value ="46">23:00</option><option value ="47">23:30</option>
									</select>
								</div> 
								<br>
							</li>	
							
							<li id="li_9" >
								<label>Meeting room</label>
								<div>
									<select id="roomChooser" name="room_no"></select>
								</div> 
								<br>
							</li>	
							
							<li id="li_4" >
								<label class="description" for="element_4">Appointment/Meeting Agenda </label>
								<div>
									<textarea id="agendaText" name="agenda" class="text_small" rows="4" cols="50"></textarea>
								</div> 
								<br>
							</li>	
							
							<li id="li_13" >
								<label class="description" for="element_13">Attendees</label>
								<div id="attendees">
									<input id="textBoxAttendee0" type="text" size="50" name="attendee0" class="inputAttendees"/>
								</div> 
								<div>
									<button id="addNewAttendees">Add more attendees</button>
								</div>
								<br>
							</li>
							
							<input id="attendeeCount" type="hidden" name="attendeeCount"/>
							<input id="operation" type="hidden" name="operation" value="create"/>
							<input id="user_id" type="hidden" name="user_id"/>
							<input id="password" type="hidden" name="password"/>
							
							<li class="buttons">
								<input type="hidden" name="form_id" value="893660" />    
								<input id="saveForm" class="button_text" type="submit" name="submit" value="Submit" />
							</li>
						</ul>
					</form>
				</div>
			<br>
		</div>
	</body>
</html>