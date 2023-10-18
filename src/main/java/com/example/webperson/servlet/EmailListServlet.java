package com.example.webperson.servlet;

import com.example.webperson.bean.User;
import com.example.webperson.dao.UserIO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@WebServlet(name = "EmailListServlet", urlPatterns = { "/emailList" }, initParams = {
		@WebInitParam(name = "relativePathToFile", value = "/WEB-INF/EmailList.txt") })
public class EmailListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		HttpSession session = req.getSession();
		String myExercise = req.getParameter("myExercise");
		String url = "/WEB-INF/views/" + myExercise + ".jsp";
		GregorianCalendar currentDate = new GregorianCalendar();
		int currentYear = currentDate.get(Calendar.YEAR);
		req.setAttribute("currentYear", currentYear);
		String action = req.getParameter("action");
		System.out.println("EmailListServlet action: " + action);
		log("action=" + action);
		if (action == null) {
			action = "join";
		}
		if (action.equals("join")) {
			url = "/WEB-INF/views/" + myExercise + ".jsp";
		} else if (action.equals("add")) {
			String firstName = req.getParameter("firstName");
			String lastName = req.getParameter("lastName");
			String email = req.getParameter("email");

			User user = new User(firstName, lastName, email);
			String message;
			if (firstName == null || lastName == null || email == null || firstName.isEmpty() || lastName.isEmpty()
					|| email.isEmpty()) {
				message = "Please fill out all three text boxes.";
				url = "/WEB-INF/views/" + myExercise + ".jsp";
			} else {
				message = "";
				if (myExercise.equals("ch08ex1View")) {
					session.setAttribute("user", user);
					Cookie c1 = new Cookie("emailCookie", email);
					c1.setMaxAge(60 * 60 * 24 * 365 * 2);
					c1.setPath("/");
					resp.addCookie(c1);
					Cookie c2 = new Cookie("firstNameCookie", firstName);
					c2.setMaxAge(60 * 60 * 24 * 365 * 2);
					c2.setPath("/");
					resp.addCookie(c2);
				}
				url = "/WEB-INF/views/thanks_" + myExercise + ".jsp";
			}
			req.setAttribute("user", user);
			req.setAttribute("message", message);
		}
		Date currentDateJoin = new Date();
		req.setAttribute("currentDate", currentDateJoin);
		String path = getServletContext().getRealPath(getInitParameter("relativePathToFile"));
		ArrayList<User> users = UserIO.getUsers(path);
		session.setAttribute("users", users);
		getServletContext().getRequestDispatcher(url).forward(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
}
