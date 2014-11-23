package wooBackend;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ManageMessages extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection dbConn = null;
	
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

	boolean postMessage (HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
		/*
		 * Request is assumed to have
		 * 	message
		 * 	to_id:
		 * */
		
		String message, toId;
		
		if ((message = request.getParameter("message")) == null || message.equals("")) {
    		return true;
    	}
		
		if ((toId = request.getParameter("to_id")) == null || toId.equals("")) {
    		return true;
    	}
		
		
		try {
			String userInsertStr = "insert into user_messages values (?, ?, ?, ? ,?);";
			PreparedStatement userInsertStmt = dbConn.prepareStatement(userInsertStr);
			userInsertStmt.setString(1, userId);
			userInsertStmt.setString(2, toId);
			userInsertStmt.setString(3, message);
			userInsertStmt.setBoolean(4, false);
			userInsertStmt.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
			userInsertStmt.executeUpdate();
		}
		catch(SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	boolean fetchMessages (HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
		/*
		 * This will fetch all the unread messages for the user and marks all of them as read
		 * */

		
		String queryStr = "select z.message_text, y.name, z.message_time from all_users as y, user_messages as z where z.to_id = ?  and z.from_id = y.user_id order by z.message_time";
//		String updateStr = "update user_messages set read = 'true' where to_id = ? and read = 'false'";
		try {
			PreparedStatement queryStmt = dbConn.prepareStatement(queryStr);
			queryStmt.setString(1, userId);
			ResultSet rs = queryStmt.executeQuery();
			JSONArray unreadMessages = new JSONArray();
			while (rs.next()) {
				JSONObject message = new JSONObject();
				message.put("from", rs.getString("name"));
				message.put("message_text", rs.getString("message_text"));
				message.put("message_time", rs.getTimestamp("message_time").toString());
				unreadMessages.add(message);
			}
			PrintWriter pw = response.getWriter();
			pw.print(unreadMessages);
			pw.flush();
//			
//			PreparedStatement updateStmt = dbConn.prepareStatement(updateStr);
//			updateStmt.setString(1, userId);
//			updateStmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	boolean fetchSent (HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
		/*
		 * This will fetch all the unread messages for the user and marks all of them as read
		 * */

		
		String queryStr = "select z.message_text, y.name, z.message_time from all_users as y, user_messages as z where z.from_id = ?  and z.to_id = y.user_id order by z.message_time";
		
		try {
			PreparedStatement queryStmt = dbConn.prepareStatement(queryStr);
			queryStmt.setString(1, userId);
			ResultSet rs = queryStmt.executeQuery();
			JSONArray unreadMessages = new JSONArray();
			while (rs.next()) {
				JSONObject message = new JSONObject();
				message.put("to", rs.getString("name"));
				message.put("message_text", rs.getString("message_text"));
				message.put("message_time", rs.getTimestamp("message_time").toString());
				unreadMessages.add(message);
			}
			PrintWriter pw = response.getWriter();
			pw.print(unreadMessages);
			pw.flush();
//			
//			PreparedStatement updateStmt = dbConn.prepareStatement(updateStr);
//			updateStmt.setString(1, userId);
//			updateStmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* Request is assumed to have the following parameters
    	 * user_id:
    	 * password:
    	 * operation: post, fetch
     	 */
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
    	
    	if (operation.equals("post")) {
    		error = postMessage(request, response, userId);
    	}
    	else if (operation.equals("fetch")) {
    		error = fetchMessages(request, response, userId);
    	}
    	else if(operation.equals("fetchsent")) {
    		error = fetchSent(request, response, userId);
    	}
    	if (error) {
    		response.sendRedirect("./Error.html");
    		return;
    	}
	}
}
