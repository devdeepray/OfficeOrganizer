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

public class ManageContacts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection dbConn = null;
	
	@Override
	public void init() throws ServletException {
		String dbUser = Settings.dbUser;
		String dbPass = Settings.dbPass;
		String dbURL = Settings.dbURL;
    	
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
    protected void finalize() throws Throwable  
    {  
        try { dbConn.close(); } 
        catch (SQLException e) { 
            e.printStackTrace();
        }
        super.finalize();  
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

	boolean searchAll (HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
		/* Request is assumed to have the following parameters in addition to those mentioned earlier
		 * search_query:
		 */
		
		String searchQuery = request.getParameter("search_query");
		if (searchQuery == null || searchQuery.equals("")) {
			return recentContacts(request, response, userId);
		}
		
		String queryStr = "select user_id, name from all_users where name ilike '%" + searchQuery + "%';";
		PreparedStatement queryStmt;
		try {
			queryStmt = dbConn.prepareStatement(queryStr);
			
			ResultSet rs = queryStmt.executeQuery();
			JSONArray matchingContacts = new JSONArray();
			while (rs.next()) {
				matchingContacts.add(rs.getString("name") + ":	" + rs.getString("user_id"));
			}
			PrintWriter pw = response.getWriter();
			pw.print(matchingContacts);
			pw.flush();
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	boolean addContact (HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
		/*
		 * The request is assumed to have the following:
		 * 	contactCount
		 * 	at least contactCpount as many contacts
		 */
		
		String newContactStr = request.getParameter("contactCount");
		if (newContactStr == null || newContactStr.equals("")) {
			return true;
		}
		
		int contactCount = Integer.parseInt(newContactStr);

		String updateStr = "insert into contacts values (?,?,?)";
		String contact = null;
		for (int  i = 0; i < contactCount; i++) {
			if ((contact = request.getParameter("contact" + i)) != null && !contact.equals("")) {
				try {
					PreparedStatement updateStmt = dbConn.prepareStatement(updateStr);
					updateStmt.setString(1, userId);
					updateStmt.setString(2, contact.split(":")[1].trim());
					updateStmt.setInt(3, 1);
					updateStmt.executeUpdate();
				}
				catch (SQLException e) {
					e.printStackTrace();
					return true;
				}
			}
		}
		return false;
	}
	
	boolean getContactInfo (HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
		/* 
		 * Request is assumed to have 
		 * 	contact_id
		 */
		
		String contactId = request.getParameter("contact_id");
		if (contactId == null || contactId.equals("")) {
			return true;
		}
		
		String queryStr = "select * from contacts as X, all_users as Y where X.contact_id = Y.user_id and X.user_id = ? and X.contact_id = ?;";
		try {
			PreparedStatement queryStmt = dbConn.prepareStatement(queryStr);
			queryStmt.setString(1, userId);
			queryStmt.setString(1, contactId);
			ResultSet rs = queryStmt.executeQuery();
			
			rs.next();
			String name = rs.getString("name");
			if (rs.wasNull()) {
				return true;
			}
			String address = rs.getString("address");
			String email = rs.getString("email");
			String phone = rs.getString("phone");
			String dob = rs.getDate("date_of_birth").toString();
			JSONObject contact = new JSONObject();

			if(name != null) contact.put("name", name);
			if(email != null) contact.put("email", email);
			if(phone != null) contact.put("phone", phone);
			if(dob != null) contact.put("dob", dob);
			if(address != null) contact.put("address", address);

			PrintWriter pw = response.getWriter();
			pw.print(contact);
			pw.flush();
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	boolean recentContacts (HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
		String queryStr = "select Y.user_id, Y.name from contacts as X, all_users as Y where X.user_id = ? and X.contact_id = Y.user_id order by X.access_frequency desc limit 10;";
		try {
			PreparedStatement queryStmt = dbConn.prepareStatement(queryStr);
			queryStmt.setString(1, userId);
			ResultSet rs = queryStmt.executeQuery();
			JSONArray recentContacts = new JSONArray();
			while (rs.next()) {
				JSONObject contact = new JSONObject();
				contact.put("name", rs.getString("name"));
				contact.put("uid", rs.getString("user_id"));
				recentContacts.add(contact);
			}
			PrintWriter pw = response.getWriter();
			pw.print(recentContacts);
			pw.flush();
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	boolean getName (HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
		String queryStr = "select name from all_users where user_id = ?;";
		try {
			PreparedStatement queryStmt = dbConn.prepareStatement(queryStr);
			queryStmt.setString(1, userId);
			ResultSet rs = queryStmt.executeQuery();
			PrintWriter pw = response.getWriter();
			rs.next();
			pw.print(rs.getString(1));
			pw.flush();
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	boolean deleteContact(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
		String queryStr = "delete from contacts where user_id = ? and contact_id = ?";
		try {
			PreparedStatement queryStmt = dbConn.prepareStatement(queryStr);
			queryStmt.setString(1, userId);
			queryStmt.setString(2, request.getParameter("contact_id"));
			queryStmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	boolean searchContacts (HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
		/* Request is assumed to have the following parameters in addition to those mentioned earlier
		 * search_query:
		 */
		
		String searchQuery = request.getParameter("search_query");
		if (searchQuery == null || searchQuery.equals("")) {
			return recentContacts(request, response, userId);
		}
		System.out.println("In contacts" + searchQuery);
		String queryStr = "select Y.user_id, Y.name from contacts as X, all_users as Y where X.user_id = ? and X.contact_id = Y.user_id and Y.name ilike '%" + searchQuery + "%' order by X.access_frequency;";
		PreparedStatement queryStmt;
		try {
			queryStmt = dbConn.prepareStatement(queryStr);
			queryStmt.setString(1, userId);
			
			ResultSet rs = queryStmt.executeQuery();
			JSONArray matchingContacts = new JSONArray();
			while (rs.next()) {
				JSONObject contact = new JSONObject();
				contact.put("name", rs.getString("name"));
				contact.put("uid", rs.getString("user_id"));
				matchingContacts.add(contact);
			}
			PrintWriter pw = response.getWriter();
			pw.print(matchingContacts);
			pw.flush();
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
    	 * operation: recent, search
     	 */

    	if (request.getParameter("user_id") == null || request.getParameter("user_id").equals("")) {
    		response.sendRedirect("./Error.html");
    		return;
    	}
    	
    	//Service the query
    	String operation = null;
    	if ((operation = request.getParameter("operation")) == null) {
    		response.sendRedirect("./Error.html");
    		return;
    	}
    	String userId = request.getParameter("user_id");
    	String password = request.getParameter("password");
    	

    	if (operation.equals("getName")){
    		boolean error = getName(request, response, userId);
    		return;
    	}
    	
    	
    	System.out.println(userId + " "  + password);
    	
    	
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
	    		return;
	    	}
		}
    	catch (SQLException e) {
			e.printStackTrace();
			response.sendRedirect("./Error.html");
			 
    		return;
		}
    	
    	boolean error = true;
    	
    	if (operation.equals("getInfo")) {
    		error = getContactInfo(request, response, userId);
    	}
    	else if (operation.equals("recent")) {
    		error = recentContacts(request, response, userId);
    	}
    	else if (operation.equals("searchAll")) {
    		error = searchAll(request, response, userId);
    	}
    	else if (operation.equals("add")) {
    		error = addContact(request, response, userId);
    	}
    	else if (operation.equals("search")) {
    		error = searchContacts(request, response, userId);
    	}
    	else if (operation.equals("delete")) {
    		error = deleteContact(request, response, userId);
    	}
    	
    	if (error) {
    		response.sendRedirect("./Error.html");
    		return;
    	}
	}
}
