package wooBackend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.simple.JSONArray;

@MultipartConfig
public class ManageFiles   extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection dbConn = null;
	private int maxFileSize = 10000000;
	
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
	
	boolean fetchFileList(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException {
		String queryStr = "select file_name from all_files where user_id = ?;";
		PreparedStatement queryStmt;
		try {
			queryStmt = dbConn.prepareStatement(queryStr);
			queryStmt.setString(1, userId);
			ResultSet rs = queryStmt.executeQuery();
			JSONArray fileList = new JSONArray();
			while (rs.next()) {
				fileList.add(rs.getString("file_name"));
			}
			PrintWriter pw = response.getWriter();
			pw.print(fileList);
			pw.flush();
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	boolean postFile (HttpServletRequest request, HttpServletResponse response, String userId) throws IOException, IllegalStateException, ServletException {
		/*
		 * Request is assumed to have 
		 * 	file: Part
		 */
		
        Part filePart = request.getPart("file");  
        if (filePart == null || filePart.getSize() > maxFileSize) {	
        	return true;
        }
        
        String fileName = request.getParameter("file_name");
        System.out.println("In file upload1" + fileName);
    	InputStream inputStream = filePart.getInputStream();
    	
    	//Check if already present
    	String checkFileStr = "select count(*) from all_files where file_name = ? and user_id = ?;";
    	String insertStr = "insert into all_files(file, user_id, file_name) values(?, ?, ?);";
    	try {
			PreparedStatement checkFileStmt = dbConn.prepareStatement(checkFileStr);
			checkFileStmt.setString(1, fileName);
			checkFileStmt.setString(2, userId);
			ResultSet rs = checkFileStmt.executeQuery();
			rs.next();
			int count = rs.getInt(1);
			if (rs.wasNull()) {
				return true;
			}
			if (count > 0) {
				insertStr = "update all_files set file = ? where user_id = ? and file_name = ?;";
			}
			
		}
    	catch (SQLException e) {
    		e.printStackTrace();
    		return true;
		}
    	
    	
    	try {
			PreparedStatement insertStmt = dbConn.prepareStatement(insertStr);
			insertStmt.setString(2, userId);
			insertStmt.setString(3, fileName);
			insertStmt.setBinaryStream(1, inputStream, filePart.getSize());
			insertStmt.executeUpdate();
			inputStream.close();
		}
    	catch (SQLException e) {
    		e.printStackTrace();
    		return true;
		}
    	
		return false;
	}
	
	boolean fetchFile (HttpServletRequest request, HttpServletResponse response, String userId) throws IOException, IllegalStateException, ServletException {
		/*
		 * Request is assumed to have 
		 * 	file_name:
		 */
		
		String fileName;
		if ((fileName = request.getParameter("file_name")) == null || fileName.equals("")) {
			
			return true;
		}
		response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
		try {
			String fetchStr = "select file from all_files where user_id = ? and file_name = ?;";
			
			PreparedStatement fetchStmt = dbConn.prepareCall(fetchStr);
			fetchStmt.setString(1, userId);
			fetchStmt.setString(2, fileName);
			System.out.println(fetchStmt.toString());
			ResultSet rs = fetchStmt.executeQuery();
			rs.next();
			byte file[] = rs.getBytes(1);
			OutputStream outputStream = response.getOutputStream();
			outputStream.write(file);
			outputStream.flush();
		}
		catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* Request is assumed to have the following parameters
    	 * user_id:
    	 * password:
    	 * operation: post, fetch
     	 */
		
		
		
		if (request.getParameter("user_id") == null || request.getParameter("user_id").equals("")) {
			System.out.println("user id null");
    		response.sendRedirect("./Error.html");
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
	    		System.out.println("user id null foreign user");
	    		return;
	    	}
		}
    	catch (SQLException e) {
			e.printStackTrace();
			response.sendRedirect("./Error.html");
    		return;
		}
    	String operation = null;
    	if ((operation = request.getParameter("operation")) == null) {
    		response.sendRedirect("./Error.html");
    		return;
    	}
    	
    	boolean error = true;
    	
    	if (operation.equals("fetchList")) {
    		error = fetchFileList(request, response, userId);
    	}
    	else if (operation.equals("post")) {
    		error = postFile(request, response, userId);
    	}
    	else if (operation.equals("fetch")) {
    		error = fetchFile(request, response, userId);
    	}
    	if (error) {
    		response.sendRedirect("./Error.html");
    		return;
    	}
	}
}
