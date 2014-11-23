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
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DisplayCalendar extends HttpServlet {
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
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	/* Request is assumed to have the following parameters
    	 * user_id:
    	 * password:
    	 * date: In the form  
    	 */
    	
    	if (request.getParameter("user_id") == null || request.getParameter("user_id").equals("")) {
    		response.sendRedirect("./Error.html");
    		return;
    	}
    	
    	String userId = request.getParameter("user_id");
    	
    	//Service the query
    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	Date startDate = null;
		try {
			startDate = new Date(formatter.parse((String) request.getParameter("date")).getTime());
		}
		catch (ParseException e1) {
			e1.printStackTrace();
		}
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(startDate);
    	String getAppointmentStr = " select * from all_appointments as x natural join user_appointments as y where x.appointment_date = ? and ((y.user_id = ? and y.status = 'confirmed') or x.created_by = ?);";
		PreparedStatement getAppointmentStmt = null;
    	ResultSet rs = null;
    	JSONObject finalRes = new JSONObject();
		for (int i = 0; i < 7; i++) {
    		try {
    			getAppointmentStmt = dbConn.prepareStatement(getAppointmentStr);
        		getAppointmentStmt.setDate(1, startDate);
        		getAppointmentStmt.setString(2, userId);
        		getAppointmentStmt.setString(3, userId);
        		rs = getAppointmentStmt.executeQuery();
        		JSONObject dayRes = new JSONObject();
    			JSONArray endTime = new JSONArray();
    			JSONArray startTime = new JSONArray();
    			JSONArray agenda = new JSONArray();
    			JSONArray room = new JSONArray();
        		while (rs.next()) {
        			startTime.add(rs.getInt("appointment_time"));
        			endTime.add(rs.getInt("appointment_end"));
        			agenda.add(rs.getString("agenda"));
        			room.add(rs.getInt("room_no"));
        		}
        		dayRes.put("agenda", agenda);
        		dayRes.put("start", startTime);
        		dayRes.put("end", endTime);
        		dayRes.put("room", room);
        		finalRes.put(startDate.toString(), dayRes);
        		calendar.add(Calendar.DATE, 1);
        		startDate = new Date(calendar.getTime().getTime());
    		}
    		catch (SQLException e) {
    			e.printStackTrace();
    		}
    	}
		
		PrintWriter pw = response.getWriter();
		pw.print(finalRes);
		pw.flush();
	}
}
