package ta_backend;

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

import org.json.JSONArray;

public class Backend extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection dbConn = null;
	
	public void init() throws ServletException {
		String dbUrl = "jdbc:postgresql://localhost/ta_test";
	    String user = "pratik";
	    String pass = "";
	
	    try {
			Class.forName("org.postgresql.Driver");
			dbConn = DriverManager.getConnection(dbUrl, user, pass);
	    }
	    catch (Exception e) {
	       		e.printStackTrace();
	    }
	}

    public void destroy() {
    	try{
    		dbConn.close();
    		System.out.println("close");
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String batch = request.getParameter("batch");
		String textboxVal = request.getParameter("textboxVal");
		String queryString = "select name from student_info_" + batch + " where name ilike '%" + textboxVal + "%';";
		JSONArray obj = new JSONArray();
		try {
			PreparedStatement queryStatement = dbConn.prepareStatement(queryString);
			ResultSet result = queryStatement.executeQuery();
			while(result.next()) {
				obj.put(result.getString("name"));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		request.getParameter("data");
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(obj);
		out.flush();
    }
}