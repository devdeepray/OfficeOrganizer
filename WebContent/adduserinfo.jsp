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
			
			sess_uid = '2';
			sess_pass = 'pass2';
			
		</script>
		<script type="text/javascript" src="./js/adduserinfo.js"></script>
		
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
			
			#infoForm {
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
				<div id="infoForm" style="position: relative; top: 2%;">
					<form id="InfoForm" action="LoginAuth" method="POST">
						<ul style="list-style: none;">
						
							<li id="li_9" >
								<label>Date of Birth</label>
								<div>
									<input type="text" id="dobText" name="dob">
								</div> 
								<br>
							</li>
						
							<li id="li_9" >
								<label>Email ID</label>
								<div>
									<input type="text" id="emailText" name="email">
								</div> 
								<br>
							</li>		
							
							<li id="li_9" >
								<label>Phone number</label>
								<div>
									<input type="text" id="phoneText" name="phone">
								</div> 
								<br>
							</li>	
							
							<li id="li_4" >
								<label class="description" for="element_4">Address</label>
								<div>
									<textarea id="addressText" name="address" class="text_small" rows="4" cols="50"></textarea>
								</div> 
								<br>
							</li>	
							
							<input id="operation" type="hidden" name="operation" value="addInfo"/>
							<input id="uid" type="hidden" name="uid"/>
							<input id="passwd" type="hidden" name="passwd"/>
							
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