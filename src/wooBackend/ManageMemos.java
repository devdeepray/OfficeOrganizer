package wooBackend;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ManageMemos extends HttpServlet {
	private static final long serialVersionUID = 1L;
	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	
	public Connection openConnection() throws ServletException {
		String dbUser = Settings.dbUser;
		String dbPass = Settings.dbPass;
		String dbURL = Settings.dbURL;
    	
    	try {
    		Class.forName("org.postgresql.Driver");
    		Connection dbConn = DriverManager.getConnection(dbURL, dbUser, dbPass);
    		return dbConn;
			
		}
    	catch (SQLException e) {
			e.printStackTrace();
		}
    	catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	return null;
    }
	
	void closeConnection(Connection dbConn) {
		try {
			dbConn.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	boolean postMemo(HttpServletRequest request, HttpServletResponse response, String userId, Connection dbConn) throws IOException {
		/* Request is assumed to have the following parameters
    	 * memo_text
    	 * date, time possibly blank
    	 * 
     	 */
		
		String memo_text = request.getParameter("text");
    	if (memo_text == null || memo_text.equals("")) {
    		return true;
    	}
    	
    	String title = request.getParameter("title");
    	
    	
    	
    	String insertMemoStr = "insert into all_memos(user_id, memo_text, memo_title) values (?, ?, ?);";
    	try {
    		PreparedStatement insertMemoStmt = dbConn.prepareStatement(insertMemoStr);
    		insertMemoStmt.setString(1, userId);
    		insertMemoStmt.setString(2, memo_text);
    		insertMemoStmt.setString(3, title);
    		insertMemoStmt.executeUpdate();
    	}
    	catch (SQLException e) {
    		e.printStackTrace();
    	}
		
		return true;
	}
	
	boolean fetchMemo(HttpServletRequest request, HttpServletResponse response, String userId, Connection dbConn) throws IOException {
		String fetchMemoStr = "select * from all_memos where user_id = ?";
		System.out.println(userId+"asiafs");
		try {
			PreparedStatement fetchMemoStmt = dbConn.prepareStatement(fetchMemoStr);
			fetchMemoStmt.setString(1, userId);
			ResultSet rs = fetchMemoStmt.executeQuery();
			JSONArray memos = new JSONArray();
			while (rs.next()) {
				JSONObject memo = new JSONObject();
				memo.put("memo_id", rs.getInt("memo_id"));
				memo.put("text", rs.getString("memo_text"));
				memo.put("title", rs.getString("memo_title"));
				memos.add(memo);
			}
			PrintWriter pw = response.getWriter();
			pw.print(memos);
			pw.flush();
			pw.close();
		}
		catch (SQLException e) {
			return true;
		}
		
		return false;
	}
	
	boolean deleteMemo(HttpServletRequest request, HttpServletResponse response, String userId, Connection dbConn) throws IOException {
		/*
		 * REquest is to have a memo_id*/
		
		String memoIdStr = request.getParameter("memo_id");
		if (memoIdStr == null || memoIdStr.equals("")) {
			return true;
		}
		int memoId = Integer.parseInt(memoIdStr);
		
		String deleteMemoStr = "delete from all_memos where memo_id = ?;";
		try {
			PreparedStatement deleteMemoStmt = dbConn.prepareStatement(deleteMemoStr);
			deleteMemoStmt.setInt(1, memoId);
			deleteMemoStmt.executeUpdate();
		}
		catch (SQLException e) {
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
    	 * 
     	 */
		
		Connection dbConn = openConnection();
		
		if (request.getParameter("user_id") == null || request.getParameter("user_id").equals("")) {
    		response.sendRedirect("./Error.html");
    		System.out.println("In memos: userId null");
    		closeConnection(dbConn);
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
	    		closeConnection(dbConn);
	    		return;
	    	}
		}
    	catch (SQLException e) {
			e.printStackTrace();
			response.sendRedirect("./Error.html");
			closeConnection(dbConn);
    		return;
		}
    	
    	//Service the query
    	String operation = null;
    	if ((operation = request.getParameter("operation")) == null) {
    		response.sendRedirect("./Error.html");
    		closeConnection(dbConn);
    		return;
    	}
    	
    	boolean error = true;
    	
    	if (operation.equals("postmemo")) {
    		error = postMemo(request, response, userId, dbConn);
    	}
    	else if (operation.equals("fetchmemo")) {
    		error = fetchMemo(request, response, userId, dbConn);
    	}
    	else if(operation.equals("deletememo")) {
    		error = deleteMemo(request, response, userId, dbConn);
    	}
    	closeConnection(dbConn);
		return;
	}
}
