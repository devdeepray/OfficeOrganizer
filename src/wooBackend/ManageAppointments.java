package wooBackend;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ManageAppointments extends HttpServlet {

	public Connection openConnection() throws ServletException {
		String dbUser = Settings.dbUser;
		String dbPass = Settings.dbPass;
		String dbURL = Settings.dbURL;

		try {
			Class.forName("org.postgresql.Driver");
			Connection dbConn = DriverManager.getConnection(dbURL, dbUser,
					dbPass);
			return dbConn;

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	void closeConnection(Connection dbConn) {
		try {
			dbConn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	boolean fetchUnconfirmedAppointments(HttpServletRequest request,
			HttpServletResponse response, String userId, Connection dbConn)
			throws IOException {
		/*
		 * This will fetch all the unconfirmed appointments for a user
		 */

		String queryStr = "select appointment_id, appointment_date, appointment_time, room_no,"
				+ " agenda, appointment_end from user_appointments natural join all_appointments "
				+ "where user_id = ? and appointment_date > current_date and status = 'unconfirmed' "
				+ "order by (appointment_date, appointment_time)";
		try {
			PreparedStatement queryStmt = dbConn.prepareStatement(queryStr);
			queryStmt.setString(1, userId);
			ResultSet rs = queryStmt.executeQuery();
			JSONArray appts = new JSONArray();
			while (rs.next()) {
				JSONObject appt = new JSONObject();
				int start_slotid = rs.getInt("appointment_time");
				int end_slotid = rs.getInt("appointment_end");
				appt.put("start_time", rs.getDate("appointment_date")
						.toString()
						+ " "
						+ start_slotid
						/ 2
						+ ":"
						+ (start_slotid % 2 == 0 ? "00" : "30"));
				appt.put("start_time_id", start_slotid);
				appt.put("end_time", rs.getDate("appointment_date").toString()
						+ " " + end_slotid / 2 + ":"
						+ (end_slotid % 2 == 0 ? "00" : "30"));
				appt.put("end_time_id", end_slotid);
				appt.put("venue", "Room " + rs.getInt("room_no"));
				appt.put("agenda", rs.getString("agenda"));
				appt.put("id", rs.getString("appointment_id"));
				appts.add(appt);
			}
			PrintWriter pw = response.getWriter();
			pw.print(appts);
			pw.flush();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

	boolean getRooms(HttpServletRequest request, HttpServletResponse response,
			Connection dbConn) throws IOException {
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
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}

		return false;
	}

	boolean fetchConfirmedAppointments(HttpServletRequest request,
			HttpServletResponse response, String userId, Connection dbConn)
			throws IOException {
		/*
		 * This will fetch all the confirmed, upcoming appointments for a user
		 */

		String queryStr = "select distinct * from "
				+ "(select a.appointment_id, appointment_date, appointment_time, room_no, agenda "
				+ "from user_appointments as u RIGHT OUTER JOIN all_appointments as a "
				+ "on a.appointment_id = u.appointment_id "
				+ "where ((user_id = ? and status = 'confirmed') or created_by = ?) and appointment_date > current_date "
				+ "order by (appointment_date, appointment_time)) as x";
		try {
			PreparedStatement queryStmt = dbConn.prepareStatement(queryStr);
			queryStmt.setString(1, userId);
			queryStmt.setString(2, userId);
			ResultSet rs = queryStmt.executeQuery();
			JSONArray appts = new JSONArray();
			while (rs.next()) {
				JSONObject appt = new JSONObject();
				int slotid = rs.getInt("appointment_time");
				appt.put("time", rs.getDate("appointment_date").toString()
						+ " " + slotid / 2 + ":"
						+ (slotid % 2 == 0 ? "00" : "30"));
				appt.put("venue", "Room " + rs.getInt("room_no"));
				appt.put("agenda", rs.getString("agenda"));
				appt.put("id", rs.getString("appointment_id"));
				appts.add(appt);
			}
			PrintWriter pw = response.getWriter();
			pw.print(appts);
			pw.flush();
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

	boolean createAppointment(HttpServletRequest request,
			HttpServletResponse response, String userId, Connection dbConn)
			throws IOException {
		/*
		 * In this case, the request should contain the following apart from the
		 * previously assumed parameters: date: mm-dd-yyyy start_time: end_time:
		 * room_no: agenda: attendees list: user's names (colon separated for
		 * now)
		 */

		String dateStr = request.getParameter("date");
		DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

		java.util.Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return true;
		}

		System.out.println(date.toString());
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		String roomStr = request.getParameter("room_no");
		if (dateStr == null || request.getParameter("start_time") == null
				|| request.getParameter("end_time") == null || roomStr == null) {
			System.err.println("Error parsing date");
			return true;
		}

		int startTime = Integer.parseInt(request.getParameter("start_time"));
		int endTime = Integer.parseInt(request.getParameter("end_time"));

		String agenda = request.getParameter("agenda");
		int attendeeCount = Integer.parseInt(request
				.getParameter("attendeeCount"));

		ArrayList<String> attendees = new ArrayList<String>();
		for (int i = 0; i < attendeeCount; i++) {
			String attendee = null;
			if ((attendee = request.getParameter("attendee" + i)) != null
					&& !attendee.equals("")) {
				attendees.add(attendee.split(":")[0].trim());
			}
		}

		int roomNo = Integer.parseInt(roomStr);

		// check if this appointment clashes with some other

		String clashQueryStr = "select count(*)"
				+ "from user_appointments as u right outer join all_appointments as a on a.appointment_id = u.appointment_id"
				+ " where ((user_id = ? and status = 'confirmed') or created_by = ?) and "
				+ "((not appointment_end <= ?) or (not appointment_time >= ?)) and appointment_date = ?";
		try {

			PreparedStatement clashQueryStmt = dbConn
					.prepareStatement(clashQueryStr);
			clashQueryStmt.setString(1, userId);
			clashQueryStmt.setString(2, userId);
			clashQueryStmt.setInt(3, startTime);
			clashQueryStmt.setInt(4, endTime);
			clashQueryStmt.setDate(5, sqlDate);

			ResultSet rs = clashQueryStmt.executeQuery();
			rs.next();

			int count = rs.getInt(1);
			System.out.println(count);
			if (rs.wasNull() || count > 0) {

				return true;

			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return true;
		}

		// get next appointment_id
		int nextId = 0;
		String getCountStr = "select max(appointment_id) from all_appointments;";
		Statement queryStmt;
		try {
			queryStmt = dbConn.createStatement();
			ResultSet rs = queryStmt.executeQuery(getCountStr);
			rs.next();
			int count = rs.getInt(1);
			if (!rs.wasNull()) {
				nextId = count + 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}

		String insertStr = "insert into all_appointments values (?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement insertStmt = dbConn.prepareStatement(insertStr);
			insertStmt.setInt(1, nextId);
			insertStmt.setDate(2, sqlDate);
			insertStmt.setInt(3, startTime);
			insertStmt.setInt(4, endTime);
			insertStmt.setInt(5, roomNo);
			insertStmt.setString(6, agenda);
			insertStmt.setString(7, userId);
			insertStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		try {
			for (String attendee : attendees) {
				System.out.println(attendee);
				String insertUserApptStr = "insert into user_appointments values(?, ?, ?);";
				PreparedStatement insertUserApptStmt = dbConn
						.prepareStatement(insertUserApptStr);
				insertUserApptStmt.setInt(1, nextId);
				insertUserApptStmt.setString(2, attendee.trim());
				insertUserApptStmt.setString(3, "unconfirmed");
				System.out.println(insertUserApptStmt.toString());
				insertUserApptStmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

	boolean deleteAppointment(HttpServletRequest request,
			HttpServletResponse response, String userId, Connection dbConn)
			throws IOException {
		/*
		 * Request is assumed to contain the following parameters apart from the
		 * previously assumed parameters appointment_id
		 */

		String appointmentIdStr = request.getParameter("appointment_id");

		if (appointmentIdStr == null || appointmentIdStr.equals("")) {
			return true;
		}
		int appointmentId = Integer.parseInt(appointmentIdStr);
		try {
			dbConn.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String getAppointmentCreatorStr = "select created_by from all_appointments where appointment_id = ?;";
		try {
			PreparedStatement getAppointmentCreatorStmt = dbConn
					.prepareStatement(getAppointmentCreatorStr);
			getAppointmentCreatorStmt.setInt(1, appointmentId);
			ResultSet rs = getAppointmentCreatorStmt.executeQuery();
			rs.next();
			String creator = rs.getString(1);
			if (rs.wasNull()) {
				return true;
			}
			if (!creator.equals(userId)) {
				String deleteAppointmentStr = "delete from user_appointments where appointment_id = ? and user_id = ?;";
				PreparedStatement deleteAppointmentStmt = dbConn
						.prepareStatement(deleteAppointmentStr);
				deleteAppointmentStmt.setInt(1, appointmentId);
				deleteAppointmentStmt.setString(2, userId);
				deleteAppointmentStmt.executeUpdate();
				String userInsertStr = "insert into sent_messages (from_id, to_id, message_text, read, message_time) values (?, ?, ?, ? ,?);";
				PreparedStatement userInsertStmt = dbConn
						.prepareStatement(userInsertStr);
				userInsertStmt.setString(1, userId);
				userInsertStmt.setString(2, creator);
				userInsertStmt.setString(3, "User " + userId
						+ " has deleted appointment ID " + appointmentId);
				userInsertStmt.setBoolean(4, false);
				userInsertStmt.setTimestamp(5,
						new java.sql.Timestamp(System.currentTimeMillis()));
				userInsertStmt.executeUpdate();
			} else if (creator.equals(userId)) {
				System.out.println("asfasd");
				String getAllAttendeesStr = "select user_id from user_appointments where appointment_id = ?";
				PreparedStatement getUsersStmt = dbConn
						.prepareStatement(getAllAttendeesStr);
				getUsersStmt.setInt(1, appointmentId);
				ResultSet rsUsers = getUsersStmt.executeQuery();
				while (rsUsers.next()) {
					String userInsertStr = "insert into sent_messages  (from_id, to_id, message_text, read, message_time) values (?, ?, ?, ? ,?);";
					PreparedStatement userInsertStmt = dbConn
							.prepareStatement(userInsertStr);
					userInsertStmt.setString(1, creator);
					userInsertStmt.setString(2, rsUsers.getString(1));
					userInsertStmt.setString(3, "User " + creator
							+ " has deleted appointment ID " + appointmentId);
					userInsertStmt.setBoolean(4, false);
					userInsertStmt.setTimestamp(5, new java.sql.Timestamp(
							System.currentTimeMillis()));
					userInsertStmt.executeUpdate();
				}
				String deleteAppointmentStr = "delete  from all_appointments where appointment_id = ?;";
				PreparedStatement deleteAppointmentStmt = dbConn
						.prepareStatement(deleteAppointmentStr);
				deleteAppointmentStmt.setInt(1, appointmentId);
				deleteAppointmentStmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				dbConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return true;
		}

		try {
			dbConn.commit();
			dbConn.setAutoCommit(true);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		return false;
	}

	boolean addAppointment(HttpServletRequest request,
			HttpServletResponse response, String userId, Connection dbConn)
			throws IOException {
		/*
		 * Request is assumed to contain the following parameters apart from the
		 * previously assumed parameters appointment_id
		 */
		String dateStr = request.getParameter("date");
		DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
		System.out.println(dateStr);
		java.util.Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		System.out.println(date.toString());
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());

		String appointmentIdStr = request.getParameter("appointment_id");
		if (appointmentIdStr == null || appointmentIdStr.equals("")) {
			return true;
		}
		int appointmentId = Integer.parseInt(appointmentIdStr);
		int appointmentTime = Integer.parseInt(request
				.getParameter("start_time"));
		int appointmentEnd = Integer.parseInt(request.getParameter("end_time"));

		// check if this appointment clashes with some other

		String clashQueryStr = "select count(*)"
				+ "from user_appointments as u right outer join all_appointments as a on a.appointment_id = u.appointment_id"
				+ " where ((user_id = ? and status = 'confirmed') or created_by = ?) and "
				+ "((not appointment_end <= ?) or (not appointment_time >= ?)) and appointment_date = ?";
		try {

			PreparedStatement clashQueryStmt = dbConn
					.prepareStatement(clashQueryStr);
			clashQueryStmt.setString(1, userId);
			clashQueryStmt.setString(2, userId);
			clashQueryStmt.setInt(3, appointmentTime);
			clashQueryStmt.setInt(4, appointmentEnd);
			clashQueryStmt.setDate(5, sqlDate);

			ResultSet rs = clashQueryStmt.executeQuery();
			rs.next();

			int count = rs.getInt(1);
			System.out.println(count);
			if (rs.wasNull() || count > 0) {

				return true;

			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return true;
		}

		String updateQueryStr = "update user_appointments set status=? where user_id = ? and appointment_id = ?;";
		try {
			PreparedStatement updateQueryStmt = dbConn
					.prepareStatement(updateQueryStr);
			updateQueryStmt.setString(1, "confirmed");
			updateQueryStmt.setString(2, userId);
			updateQueryStmt.setInt(3, appointmentId);
			updateQueryStmt.executeUpdate();
			String userInsertStr = "insert into sent_messages (from_id, to_id, message_text, read, message_time) values (?, (select created_by from all_appointments where appointment_id = ?), ?, ? ,?);";
			PreparedStatement userInsertStmt = dbConn
					.prepareStatement(userInsertStr);
			userInsertStmt.setString(1, userId);
			userInsertStmt.setInt(2, appointmentId);
			userInsertStmt.setString(3, "User " + userId
					+ " has deleted appointment ID " + appointmentId);
			userInsertStmt.setBoolean(4, false);
			userInsertStmt.setTimestamp(5,
					new java.sql.Timestamp(System.currentTimeMillis()));
			userInsertStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}

		return false;
	}

	boolean modifyAppointment(HttpServletRequest request,
			HttpServletResponse response, String userId, Connection dbConn)
			throws IOException {
		/*
		 * In this case, the request should contain the following apart from the
		 * previously assumed parameters: date: start_time: end_time: room_no:
		 * agenda: attendees list: user's names (colon separated for now) All
		 * the attendees
		 */
		int appointmentId = Integer.parseInt(request
				.getParameter("appointment_id"));
		String getAppointmentCreatorStr = "select created_by from all_appointments where appointment_id = ?;";
		try {
			PreparedStatement getAppointmentCreatorStmt = dbConn
					.prepareStatement(getAppointmentCreatorStr);
			getAppointmentCreatorStmt.setInt(1, appointmentId);
			ResultSet rs = getAppointmentCreatorStmt.executeQuery();
			rs.next();
			String creator = rs.getString(1);
			if (!rs.wasNull()) {
				return true; // Appointment without creator.
			}
			if (creator.equals(userId)) {
				return deleteAppointment(request, response, userId, dbConn)
						&& createAppointment(request, response, userId, dbConn);
			}
		} catch (SQLException e) {
		}
		return true;
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/*
		 * Request is assumed to have the following parameters in general
		 * user_id password operation: create, delete, add, modify
		 */

		Connection dbConn = openConnection();

		init();
		if (request.getParameter("user_id") == null
				|| request.getParameter("user_id").equals("")) {
			response.sendRedirect("./Error.html");
			System.out.println("In appts: userId null");
			closeConnection(dbConn);
			return;
		}

		String userId = request.getParameter("user_id");
		String password = request.getParameter("password");

		// Check user credentials
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
				response.sendRedirect("./Error.html");
				closeConnection(dbConn);
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendRedirect("./Error.html");
			closeConnection(dbConn);
			return;
		}

		// Service the query
		String operation = null;
		if ((operation = request.getParameter("operation")) == null) {
			response.sendRedirect("./Error.html");
			closeConnection(dbConn);
			return;
		}

		boolean error = true;

		if (operation.equals("redirect")) {
			request.getSession().setAttribute("user_id", userId);
			request.getSession().setAttribute("password", password);
			response.sendRedirect("createappointment.jsp");
		} else if (operation.equals("create")) {
			error = createAppointment(request, response, userId, dbConn);
		} else if (operation.equals("delete")) {
			error = deleteAppointment(request, response, userId, dbConn);
		} else if (operation.equals("modify")) {
			error = modifyAppointment(request, response, userId, dbConn);
		} else if (operation.equals("add")) {
			error = addAppointment(request, response, userId, dbConn);
		} else if (operation.equals("rooms")) {
			error = getRooms(request, response, dbConn);
		} else if (operation.equals("fetchupcoming")) {
			error = fetchConfirmedAppointments(request, response, userId,
					dbConn);
		} else if (operation.equals("fetchunconfirmed")) {
			error = fetchUnconfirmedAppointments(request, response, userId,
					dbConn);
		}
		if (error) {
			System.out.println("Some error");
		}
		closeConnection(dbConn);
		return;
	}
}