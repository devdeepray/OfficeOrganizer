<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="./js/jquery.js"></script>
<link rel="stylesheet" type="text/css" href="./css/jquery-ui.css">
<script type="text/javascript" src="./js/jquery-ui.js"></script>
<script type="text/javascript" src="./js/inbox.js"></script>
<script type="text/javascript" src="./js/sendbox.js"></script>
<script type="text/javascript" src="./js/sentbox.js"></script>
<script type="text/javascript" src="./js/upload.js"></script>

<script type="text/javascript" src="./js/appointments.js"></script>
<script type="text/javascript" src="./js/pending.js"></script>
<script type="text/javascript" src="./js/createappointment.js"></script>
<script type="text/javascript" src="./js/memo.js"></script>

<script language="javascript" type="text/javascript">
	
<%String uid = "" + session.getAttribute("uid");
			String passwd = (String) session.getAttribute("passwd");
			out.println("var sess_uid = '" + uid + "';");
			out.println("var sess_pass = '" + passwd + "';");%>
	var sess_uname;
	function renderMainPage() {

		sc_render();
		bc_renderNow();
		renderRecentContacts();
		fetchMessages();
		fetchSent();
		fetchAppointments();
		fetchPending();
		fetchMemos();
		setInterval(fetchMessages, 10000);
		setInterval(fetchSent, 10000);
		setInterval(fetchAppointments, 10000);
		setInterval(fetchPending, 10000);
		bindAutoCompleteSendMessage();
		 prepareAppointmentForm();
		 setInterval(fetchMemos, 10000);

			document.getElementById("addContact").innerHTML = '<a target="_blank" href="addcontacts.jsp?sess_uid='+sess_uid+'&sess_pass='+sess_pass+'">Add contact</a>';
	}
</script>
<script src="js/smallcalendar.js" type="text/javascript"></script>
<script src="js/bigcalendar.js" type="text/javascript"></script>
<script src="js/searchcontacts.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="./css/mainhome.css">
<link rel="stylesheet" type="text/css" href="./css/smallcalendar.css">
<link rel="stylesheet" type="text/css" href="./css/bigcalendar.css">
<link rel="stylesheet" type="text/css" href="./css/searchcontacts.css">
<link rel="stylesheet" type="text/css" href="./css/inbox.css">
<link rel="stylesheet" type="text/css" href="./css/appointments.css">
<link rel="stylesheet" type="text/css" href="./css/memo.css">

</head>
<body onload="renderMainPage()">
	<div id="fullpage">
		<div id="titout">
			<div id="tit">
				<div id="tittext">Web office organizer V1.0</div>
			</div>
		</div>
		<div id="bod">
			<div id="leftsection">
				<div id="leftbar">
					<div id="leftbartop">
						<div id="searchContactsBox" class="contactsSearch">
							<input type="text" size="20" id="contactsSearchText"
								class="contactsSearch" />
							<div id="contactsList"></div>
							<div id="addContact">
							</div>
						</div>
					</div>
					<div id="leftbarbot">
						<div id="smallCalendarBox" class="smallCalendar"></div>
					</div>
				</div>
				<div id="midbarout">
					<div id="midbar">
						<div id="bigCalendarNav" class="bigCalendar"></div>
						<div id="bigCalendarTabLab" class="bigCalendar"></div>
						<div id="bigCalendarBox" class="bigCalendar"></div>

					</div>
					<div id="apptForm" style="position: absolute; top: 0%; left:25%; width: 50%; display:none;">
					
				</div>
				</div>
			</div>
			<div id="rightbar">
				<div id="rightbartop">
					<script>
						function rtTabShow(arg) {
							document.getElementById("appointments").style.display = "none";
							document.getElementById("pending").style.display = "none";
							document.getElementById("memo").style.display = "none";
							document.getElementById("settings").style.display = "none";
							document.getElementById(arg).style.display = "block";
						}
					</script>
					<div id="rtlabel">
						<div class="rtlabelelem appointments_col">
							<a href="#" onclick="rtTabShow('appointments')">Aptt</a>
						</div>
						<div class="rtlabelelem pending_col">
							<a href="#" onclick="rtTabShow('pending')">Pndg</a>
						</div>

						<div class="rtlabelelem memo_col">
							<a href="#" onclick="rtTabShow('memo')">Memo</a>
						</div>
						
					</div>
					<div id="rtcontent">
						<div id="appointments" class="appointments_col"></div>
						<div id="pending" class="pending_col"></div>
						<div id="memo" class="memo_col">
							<div id="createdmemos" class="memo_col" style="overflow:auto; width: 100%; height: 70%; background-color: pink;">
							</div>
							<div id="createnewmemo" class="memo_col" style="overflow:auto; width:100%; height:30%; ">
							<form action="ManageMemos" method="post" id="memocreateform">
								<input type="text" id="memo_title" name="title">
								<textarea 
									name="message" id="text"></textarea>
							</form>
							<a href="#" onclick="createMemo()">Submit</a>
							</div>
						</div>
						<div id="settings" class="settings_col"></div>
					</div>
				</div>
				<div id="rightbarbot">
					<script>
						function rbTabShow(arg) {
							document.getElementById("inbox").style.display = "none";
							document.getElementById("sent").style.display = "none";
							document.getElementById("download").style.display = "none";
							document.getElementById("upload").style.display = "none";
							document.getElementById("sendmess").style.display = "none";
							document.getElementById(arg).style.display = "block";
						}
					</script>
					<div id="rblabel">
						<div class="rblabelelem inbox_col">
							<a href="#" onclick="rbTabShow('inbox')">Inbx</a>
						</div>
						<div class="rblabelelem sent_col">
							<a href="#" onclick="rbTabShow('sent')">Sent</a>
						</div>

						<div class="rblabelelem sendmess_col" id="sendbox">
							<a href="#" onclick="rbTabShow('sendmess')">Send</a>


						</div>
						<div class="rblabelelem download_col">
							<a href="#" onclick="rbTabShow('download')">Dnld</a>
						</div>
						<div class="rblabelelem upload_col">
							<a href="#" onclick="rbTabShow('upload')">Upld</a>
						</div>
					</div>
					<div id="rbcontent">
						<div id="inbox" class="inbox_col"></div>
						<div id="sent" class="sent_col"></div>
						<div id="sendmess" class="sendmess_col">
							<form action="ManageMessages" method="post" id="sendform">
								<input type="text" id="sendmessidbox" name="to_id">
								<textarea type="text"
									name="message" id="sendmessmessbox"></textarea>
							</form>
							<a href="#" onclick="submitsendform()">Submit</a>
						</div>
						<div id="download" class="download_col"></div>
						<div id="upload" class="upload_col">
							<form action="ManageFiles" method="post" id="uploadform">
								<input type="file" name="file" id="fileuploaddata">
							</form>
							<a href="#" onclick="submituploadform()">Submit</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>