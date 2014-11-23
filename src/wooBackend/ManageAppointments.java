package wooBackend;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ManageAppointments  extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection dbConn = null;
	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    
	@Override
    public void init() throws ServletException {
//    	String dbUser = "pratik";
//    	String dbPass = "";
//    	String dbURL = "jdbc:postgresql://localhost/app_project";
    	String dbUser = "postgres";
    	String dbPass = "Scand1nav1an$s";
    	String dbURL = "jdbc:postgresql://localhost/officeorganizer";
    	
    	try {
    		Class.forName("org.postgresql.Driver");
			dbConn = DriverManager.getConnection(dbURL, dbUser, dbPass);
			
		}
    	catch (SQLException e) {
			e.printStackTrace();
		}
    	catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	
    }
	
	@Override
    public void destroy() {
    	try {
			dbConn.close();
		}
    	catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    boolean getRooms(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String getRoomsStr = " select * from meeting_rooms;";
		PreparedStatement getRoomsStmt;
		try {
			getRoomsStmt = dbConn.prepareStatement(getRoomsStr);
			ResultSet rs = getRoomsStmt.executeQuery();
	    	JSONArray finalRes = new JSONArray();
			while (rs.next()) {
				JSONObject room = new JSONObject();
				room.put("room_no", rs.getInt("room_no"));
				room.put("capacity", rs.getInt("capacity"));
				finalRes.add(room);
			}
			PrintWriter pw = response.getWriter();
			pw.print(finalRes);
			pw.flush();
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		
    	return false;
    }
    
    boolean createAppointment(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
    	/*
		 * In this case, the request should contain the following apart from the previously assumed parameters:
		 * 	date:
		 * 	start_time:
		 * 	end_time:
		 * 	room_no:
		 * 	agenda:
		 * 	attendees list: user's names (colon separated for now)
		 * */

		String dateStr = request.getParameter("date");
		String roomStr = request.getParameter("room_no");
		if (dateStr == null || request.getParameter("start_time") == null || request.getParameter("end_time") == null || roomStr == null) {
			System.out.println("Some error1");
			return true;
		}
		
		int startTime = Integer.parseInt(request.getParameter("start_time"));
		int endTime = Integer.parseInt(request.getParameter("end_time"));
		
		String agenda = request.getParameter("agenda");
		int attendeeCount  = Integer.parseInt(request.getParameter("attendeeCount"));

		ArrayList<String> attendees = new ArrayList<String>();
		for (int  i = 0; i < attendeeCount; i++) {
			String attendee = null;
			if ((attendee = request.getParameter("attendee" + i)) != null && !attendee.equals("")) {
				attendees.add(attendee.split(":")[1].trim());
			}
		}	
		
		
		int roomNo = Integer.parseInt(roomStr);
		Date appointmentDate = null;
		try {
			appointmentDate = new Date(formatter.parse(dateStr).getTime());
		}
		catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		//check if this appointment clashes with some other
		String clashQueryStr = "select count(*) from all_appointments as X natural join user_appointments as Y where X.appointment_time <= ? and X.appointment_end >= ? and (Y.user_id = ? or X.created_by = ?);";
		try {
			PreparedStatement clashQueryStmt = dbConn.prepareStatement(clashQueryStr);
			clashQueryStmt.setInt(1, startTime);
			clashQueryStmt.setInt(2, endTime);
			clashQueryStmt.setString(3, userId);
			clashQueryStmt.setString(4, userId);
			ResultSet rs = clashQueryStmt.executeQuery();
			rs.next();
			
			int count = rs.getInt(1);
	    	if (rs.wasNull() || count > 0) {
	    		System.out.println("Some error2");
	    		//return true;
	    	}
		}
		catch (SQLException e1) {
			e1.printStackTrace();
			return true;
		}
		
		//get next appointment_id
		int nextId = 0;
		String getCountStr = "select count(*) from all_appointments;";
		Statement queryStmt;
		try {
			queryStmt = dbConn.createStatement();
			ResultSet rs = queryStmt.executeQuery(getCountStr);
			rs.next();
			int count = rs.getInt(1);
			if (!rs.wasNull()) {
				nextId = count + 1;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		
		String insertStr = "insert into all_appointments values (?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement insertStmt = dbConn.prepareStatement(insertStr);
			insertStmt.setInt(1, nextId);
			insertStmt.setDate(2, appointmentDate);
			insertStmt.setInt(3, startTime);
			insertStmt.setInt(4, endTime);
			insertStmt.setInt(5, roomNo);
			insertStmt.setString(6, agenda);
			insertStmt.setString(7, userId);
			insertStmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		try {
			for (String attendee : attendees) {
				System.out.println(attendee);
				String insertUserApptStr = "insert into user_appointments values(?, ?, ?);";
				PreparedStatement insertUserApptStmt = dbConn.prepareStatement(insertUserApptStr);
				insertUserApptStmt.setInt(1, nextId);
				insertUserApptStmt.setString(2, attendee.trim());
				insertUserApptStmt.setString(3, "unconfirmed");
				System.out.println(insertUserApptStmt.toString());
				insertUserApptStmt.executeUpdate();	
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
    }
    
    boolean deleteAppointment(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
    	/*
    	 * Request is assumed to contain the following parameters apart from the previously assumed parameters
    	 * 	appointment_id
    	 * */
    	
    	String appointmentIdStr = null;
    	int appointmentId = Integer.parseInt(appointmentIdStr);
    	
    	if ((appointmentIdStr = request.getParameter("appointment_id")) == null || appointmentIdStr.equals("")) {
    		return true;
    	}
    	
    	String getAppointmentCreatorStr = "select created_by from all_appointments where appointment_id = ?;";
    	try {
			PreparedStatement getAppointmentCreatorStmt = dbConn.prepareStatement(getAppointmentCreatorStr);
			getAppointmentCreatorStmt.setInt(1, appointmentId);
			ResultSet rs = getAppointmentCreatorStmt.executeQuery();
			rs.next();
			String creator = "";
			if (!rs.wasNull()) creator =rs.getString(1);
			
			if (creator != userId) {
				String deleteAppointmentStr = "delete from user_appointments where appointment_id = ? and user_id = ?;";
				PreparedStatement deleteAppointmentStmt = dbConn.prepareStatement(deleteAppointmentStr);
				deleteAppointmentStmt.setInt(1, appointmentId);
				deleteAppointmentStmt.setString(2, userId);
				deleteAppointmentStmt.executeUpdate();
			}
			else if (creator == userId) {
				String deleteAppointmentStr = "delete from all_appointments where appointment_id = ?;";
				PreparedStatement deleteAppointmentStmt = dbConn.prepareStatement(deleteAppointmentStr);
				deleteAppointmentStmt.setInt(1, appointmentId);
				deleteAppointmentStmt.executeUpdate();
			}
		} 
    	catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
    	return false;
    }
    
    boolean addAppointment(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
    	/*
    	 * Request is assumed to contain the following parameters apart from the previously assumed parameters
    	 * 	appointment_id
    	 * */
    	
    	String appointmentIdStr = request.getParameter("appointment_id");
    	if (appointmentIdStr == null) {
    		return true;
    	}
    	
    	int appointmentId = Integer.parseInt(appointmentIdStr);
    	
    	//check if this appointment clashes with some other
		String clashQueryStr = "select count(*) from all_appointments as X natural join user_appointments as Y where X.appointment_time <= (select appointment_time from all_appointments where appointment_id = ?) and X.appointment_end >= (select appointment_end from all_appointments where appointment_id = ?) and (Y.user_id = ? or X.created_by = ?);";
		try {
			PreparedStatement clashQueryStmt = dbConn.prepareStatement(clashQueryStr);
			clashQueryStmt.setInt(1, appointmentId);
			clashQueryStmt.setInt(2, appointmentId);
			clashQueryStmt.setString(3, userId);
			clashQueryStmt.setString(4, userId);
			ResultSet rs = clashQueryStmt.executeQuery();
			rs.next();
			
			int count = rs.getInt(1);
	    	if (rs.wasNull() || count > 0) {
	    		return true;
	    	}
		}
		catch (SQLException e1) {
			e1.printStackTrace();
			return true;
		}
    	
    	String updateQueryStr = "update user_appointments set status=? where user_id = ? and appointment_id = ?;";
    	try {
			PreparedStatement updateQueryStmt = dbConn.prepareStatement(updateQueryStr);
			updateQueryStmt.setString(1, "confirmed");
			updateQueryStmt.setString(2, userId);
			updateQueryStmt.setInt(3, appointmentId);
			updateQueryStmt.executeUpdate();
		}
    	catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
    	
    	return false;
    }
    
    boolean modifyAppointment(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
    	/*
		 * In this case, the request should contain the following apart from the previously assumed parameters:
		 * 	date:
		 * 	start_time:
		 * 	end_time:
		 * 	room_no:
		 * 	agenda:
		 * 	attendees list: user's names (colon separated for now) All the attendees
		 * */
    	
    	return deleteAppointment(request, response, userId) && createAppointment(request, response, userId);
    	
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	/*
    	 * Request is assumed to have the following parameters in general
    	 * user_id
    	 * password
    	 * operation: create, delete, add, modify
    	 * */
    	init();
    	if (request.getParameter("user_id") == null || request.getParameter("user_id").equals("")) {
    		response.sendRedirect("./Error.html");
    		System.out.println("In appts: userId null");
    		return;
    	}
    	
    	String userId = request.getParameter("user_id");
    	String password = request.getParameter("password");

    	//Check user credentials
    	String checkUserStr = "select count(*) from all_users where user_id = ? and password = ?";
    	PreparedStatement checkUserStmt = null;
    	try {
			checkUserStmt = dbConn.prepareStatement(checkUserStr);
			checkUserStmt.setString(1, userId);
	    	checkUserStmt.setString(2, password);
	    	ResultSet rs = checkUserStmt.executeQuery();
	    	rs.next();
	    	int count = rs.getInt(1);
	    	if (rs.wasNull() || count != 1) {
	    		response.sendRedirect("./Error.html: Foreign user");
	    		return;
	    	}
		}
    	catch (SQLException e) {
			e.printStackTrace();
			response.sendRedirect("./Error.html");
    		return;
		}
    	
    	//Service the query
    	String operation = null;
    	if ((operation = request.getParameter("operation")) == null) {
    		response.sendRedirect("./Error.html: NOOP");
    		return;
    	}
    	
    	boolean error = true;
    	
    	if (operation.equals("redirect")) {
    		request.getSession().setAttribute("user_id", userId);
    		request.getSession().setAttribute("password", password);
    		response.sendRedirect("createappointment.jsp");
    	}
    	else if (operation.equals("create")) {
    		error = createAppointment(request, response, userId);
    	}
    	else if (operation.equals("delete")) {
    		error = deleteAppointment(request, response, userId);
    	}
    	else if (operation.equals("modify")) {
    		error = modifyAppointment(request, response, userId);
    	}
    	else if (operation.equals("add")) {
    		error = addAppointment(request, response, userId);
    	}
    	else if (operation.equals("rooms")) {
    		error = getRooms(request, response);
    	}
    	if (error) {
	    	response.sendRedirect("./Error.html");
	    	System.out.println("Some error");
    		return;
    	}
    }
}