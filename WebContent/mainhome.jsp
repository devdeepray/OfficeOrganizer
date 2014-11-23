<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="./js/jquery.js"></script>
<!--  <link rel="stylesheet" type="text/css" href="http://code.jquery.com/ui/1.9.1/themes/base/jquery-ui.css"> -->
<script type="text/javascript" src="./js/jquery-ui.js"></script>
<script type="text/javascript" src="./js/inbox.js"></script>
<script type="text/javascript" src="./js/sendbox.js"></script>
<script type="text/javascript" src="./js/sentbox.js"></script>
<script type="text/javascript" src="./js/upload.js"></script>
<script language="javascript" type="text/javascript">
	
<%
	String uid = "" + session.getAttribute("uid");
	String passwd = (String) session.getAttribute("passwd");
	out.println("var sess_uid = '" + uid + "';");
	out.println("var sess_pass = '" + passwd + "';");
%>
	var sess_uname;
	function renderMainPage() {
		
		sc_render();
		bc_renderNow();
		renderRecentContacts();
		fetchMessages();
		fetchSent();
		setInterval(fetchMessages, 10000);
		setInterval(fetchSent, 10000);
		

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
				</div>
			</div>
			<div id="rightbar">
				<div id="rightbartop">ABC</div>
				<div id="rightbarbot">
					<script>
						function rbTabShow(arg)
						{
							document.getElementById("inbox").style.display="none";
							document.getElementById("sent").style.display="none";
							document.getElementById("download").style.display="none";
							document.getElementById("upload").style.display="none";
							document.getElementById("sendmess").style.display="none";
							document.getElementById(arg).style.display="block";
						}
					</script>
					<div id="rblabel">
						<div class="rblabelelem inbox_col"><a href="#" onclick="rbTabShow('inbox')">Inbx</a></div>
						<div class="rblabelelem sent_col"><a href="#" onclick="rbTabShow('sent')">Sent</a></div>
						
						<div class="rblabelelem sendmess_col" id="sendbox"><a href="#" onclick="rbTabShow('sendmess')">Send</a>
							
							
						</div>
						<div class="rblabelelem download_col"><a href="#" onclick="rbTabShow('download')">Dnld</a></div>
						<div class="rblabelelem upload_col"><a href="#" onclick="rbTabShow('upload')">Upld</a></div>
					</div>
					<div id="rbcontent">
						<div id="inbox" class="inbox_col">
						x
						</div>
						<div id="sent" class="sent_col">
						y
						</div>
						<div id="sendmess" class="sendmess_col">
							<form action="ManageMessages" method="post" id="sendform">
							<input type="text" name="to_id">
							<input type="text" name="message">
							</form>
							<a href="#" onclick="submitsendform()">Submit</a>
						</div>
						<div id="download" class="download_col">
						z
						</div>
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