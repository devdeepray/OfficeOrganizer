package wooBackend;

import java.io.IOException;
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
import javax.servlet.http.HttpSession;

public class LoginAuth extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection dbConn = null;

	public LoginAuth() {
		super();
	}

	void addInfo(HttpServletRequest request, HttpServletResponse response,
			String userId) throws IOException {
		/*
		 * Request is assumed to have address email phone dob
		 */

		String address = request.getParameter("address");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		String dob = request.getParameter("dob");

		String updateInfoStr = "update all_users set address = ?, email = ?, date_of_birth = ?, phone_number = ? where user_id = ?";
		try {
			PreparedStatement updateInfoStmt = dbConn
					.prepareStatement(updateInfoStr);
			if (address.equals(""))
				updateInfoStmt.setString(1, null);
			else
				updateInfoStmt.setString(1, address);

			if (email.equals(""))
				updateInfoStmt.setString(1, null);
			else
				updateInfoStmt.setString(2, email);

			if (dob.equals(""))
				updateInfoStmt.setString(1, null);
			else {
				Date dobDate = null;
				try {
					SimpleDateFormat formatter = new SimpleDateFormat(
							"dd/MM/yyyy");
					dobDate = new Date(formatter.parse(dob).getTime());
					updateInfoStmt.setDate(3, dobDate);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}

			if (phone.equals(""))
				updateInfoStmt.setString(1, null);
			else
				updateInfoStmt.setString(4, phone);

			updateInfoStmt.setString(5, userId);

			updateInfoStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			dbConn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		super.finalize();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		init();
		if (request.getParameter("uid") == null
				|| request.getParameter("uid").equals("")) {
			response.sendRedirect("./Error.html");
			return;
		}

		String userId = request.getParameter("uid");
		String password = request.getParameter("passwd");

		String operation = null;
		if ((operation = request.getParameter("operation")) == null) {
			response.sendRedirect("./Error.html");
			return;
		}

		if (operation.equals("addInfo")) {
			addInfo(request, response, userId);
			return;
		}

		// Check user credentials
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
		} catch (SQLException e) {
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
		String dbUser = Settings.dbUser;
		String dbPass = Settings.dbPass;
		String dbURL = Settings.dbURL;

		try {
			Class.forName("org.postgresql.Driver");
			dbConn = DriverManager.getConnection(dbURL, dbUser, dbPass);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		try {
			dbConn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}