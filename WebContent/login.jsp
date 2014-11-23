<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Web office organizer - Log in</title>
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

	#titout{
	width: 100%;
	height: 10%;
	}
	#tit {
		position: relative;
		width: 98%;
		height: 80%;
		top: 20%;
		left: 1%;
		background-color: #33CFFF;
		font-size: 7vh;
		text-align: center;
		box-shadow: 10px 10px 20px #4F5965;
		overflow: hidden;
		line-height: 100%;
	}
	
	#loginForm {
		width: 100%;
		font-size: 100%;
		text-align: center;
		margin-top: 12%;
	}
</style>
</head>
<body>
	<div id="fullpage">
		<div id="titout">
			<div id="tit">
				<div id="tittext">Web office organizer V1.0</div>
			</div>
		</div>
		<div id = "loginForm">
			<form action="LoginAuth" method="post">
				User ID:<br> <input type="text" name="uid"> <br>
				Password:<br> <input type="password" name="passwd"> <br>
				<input type="submit" value="Submit">
			</form>
		</div>
	</div>
</body>
</html>