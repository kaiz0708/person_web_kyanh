package com.example.webperson.servlet;

import com.example.webperson.bean.Product;
import com.example.webperson.bean.User;
import com.example.webperson.dao.ProductIO;
import com.example.webperson.dao.UserIO;
import com.example.webperson.util.CookieUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(urlPatterns = { "/download" })
public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		if (action == null) {
			action = "viewAlbums";
		}
		String url = "/WEB-INF/views/ch09ex1View.jsp";
		if (action.equals("viewAlbums")) {
			url = "/WEB-INF/views/ch09ex1View.jsp";
		} else if (action.equals("checkUser")) {
			url = checkUser(req, resp);
		} else if (action.equals("viewCookies")) {
			url = "/WEB-INF/views/view_cookiesView.jsp";
		} else if (action.equals("deleteCookies")) {
			url = deleteCookies(req, resp);
		}
		getServletContext().getRequestDispatcher(url).forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		String action = req.getParameter("action");
		String url = "/WEB-INF/views/ch09ex1View.jsp";
		if (action.equals("registerUser")) {
			url = registerUser(req, resp);
		}
		getServletContext().getRequestDispatcher(url).forward(req, resp);
	}

	private String checkUser(HttpServletRequest req, HttpServletResponse resp) {

		String productCode = req.getParameter("productCode");
		HttpSession session = req.getSession();
		ServletContext sc = this.getServletContext();
		String productPath = sc.getRealPath("/WEB-INF/products.txt");
		Product product = ProductIO.getProduct(productCode, productPath);
		session.setAttribute("product", product);
		User user = (User) session.getAttribute("user");
		String url;
		if (user == null) {
			Cookie[] cookies = req.getCookies();
			String emailAddress = CookieUtil.getCookieValue(cookies, "emailCookie");
			if (emailAddress == null || emailAddress.equals("")) {
				url = "/WEB-INF/views/registerView.jsp";
			}
			else {
				String path = sc.getRealPath("/WEB-INF/EmailList.txt");
				user = UserIO.getUser(emailAddress, path);
				session.setAttribute("user", user);
				url = "/WEB-INF/views/" + productCode + "_downloadView.jsp";
			}
		}
		// if User object exists, go to Downloads page
		else {
			url = "/WEB-INF/views/" + productCode + "_downloadView.jsp";
		}
		return url;
	}

	private String registerUser(HttpServletRequest req, HttpServletResponse resp) {
		String email = req.getParameter("email");
		String firstName = req.getParameter("firstName");
		String lastName = req.getParameter("lastName");
		User user = new User();
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setLastName(lastName);

		String url = "/WEB-INF/views/registerView.jsp";
		String message;
		if (firstName == null || lastName == null || email == null || firstName.isEmpty() || lastName.isEmpty()
				|| email.isEmpty()) {
			message = "Please fill out all three text boxes.";
			url = "/WEB-INF/views/registerView.jsp";
		} else {
			message = "";
			ServletContext sc = getServletContext();
			String path = sc.getRealPath("/WEB-INF/EmailList.txt");
			UserIO.add(user, path);
			HttpSession session = req.getSession();
			session.setAttribute("user", user);
			Cookie c1 = new Cookie("emailCookie", email);
			c1.setMaxAge(60 * 60 * 24 * 365 * 2); // set age to 2 years
			c1.setPath("/"); // allow entire app to access it
			resp.addCookie(c1);
			Cookie c2 = new Cookie("firstNameCookie", firstName);
			c2.setMaxAge(60 * 60 * 24 * 365 * 2); // set age to 2 years
			c2.setPath("/");
			resp.addCookie(c2);
			Product product = (Product) session.getAttribute("product");
			url = "/WEB-INF/views/" + product.getCode() + "_downloadView.jsp";
		}
		req.setAttribute("user", user);
		req.setAttribute("message", message);
		return url;
	}

	private String deleteCookies(HttpServletRequest req, HttpServletResponse resp) {

		HttpSession session = req.getSession();
		session.invalidate();
		Cookie[] cookies = req.getCookies();
		for (Cookie cookie : cookies) {
			cookie.setMaxAge(0);
			cookie.setPath("/");
			resp.addCookie(cookie);
		}
		String url = "/WEB-INF/views/delete_cookiesView.jsp";
		return url;
	}
}
