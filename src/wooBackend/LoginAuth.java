package wooBackend;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginAuth extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection dbConn = null;
	
    public LoginAuth() {
        super();
    }

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		init();
		if (request.getParameter("uid") == null || request.getParameter("uid").equals("")) {
    		response.sendRedirect("./Error.html");
    		return;
    	}
    	
    	String userId = request.getParameter("uid");
    	String password = request.getParameter("passwd");

    	//Check user credentials
    	String checkUserStr = "select count(*) from all_users where user_id = ? and password = ?;";
    	PreparedStatement checkUserStmt = null;
    	try {
			checkUserStmt = dbConn.prepareStatement(checkUserStr);
			checkUserStmt.setString(1, userId);
	    	checkUserStmt.setString(2, password);
	    	ResultSet rs = checkUserStmt.executeQuery();
	    	rs.next();
	    	int count = rs.getInt(1);
	    	if (rs.wasNull() || count != 1) {
	    		response.sendRedirect("./Error.html");
	    		return;
	    	}
		}
    	catch (SQLException e) {
			e.printStackTrace();
			response.sendRedirect("./Error.html");
    		return;
		}
    	
    	HttpSession session = request.getSession();
    	session.setAttribute("uid", userId);
    	session.setAttribute("passwd", password);
    	response.sendRedirect("mainhome.jsp");
	}
	
	@Override
	public void init() throws ServletException {
	    String dbUser = "postgres";
	    String dbPass = "Scand1nav1an$s";
	    String dbURL = "jdbc:postgresql://localhost:5432/officeorganizer";
	    
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
}