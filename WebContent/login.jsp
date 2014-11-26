<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title>Web office organizer - Log in</title>
<style>
	
	#loginForm {
		width: 100%;
		font-size: 100%;
		text-align: center;
		margin-top: 12%;
	}
</style>
<link rel="stylesheet" type="text/css" href="./css/mainhome.css">

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
				<input type="hidden" name="operation" value="login">
				<input type="submit" value="Submit">
			</form>
		</div>
	</div>
</body>
</html>