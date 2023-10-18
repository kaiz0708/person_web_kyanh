package com.example.webperson.servlet;

import java.io.IOException;

import com.example.webperson.bean.Cart;
import com.example.webperson.bean.LineItem;
import com.example.webperson.bean.Product;
import com.example.webperson.dao.ProductIO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = { "/cart" })
public class CartServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		String url = "/WEB-INF/views/ch09ex2View.jsp";
		ServletContext sc = getServletContext();
		String action = req.getParameter("action");
		if (action == null) {
			action = "cart"; // default action
		}
		if (action.equals("shop")) {
			url = "/WEB-INF/views/ch09ex2View.jsp"; // the "index" page
		} else if (action.equals("cart")) {
			String productCode = req.getParameter("productCode");
			String quantityString = req.getParameter("quantity");

			HttpSession session = req.getSession();
			Cart cart;
			final Object lock = req.getSession().getId().intern();
			synchronized(lock) {
			    cart = (Cart) session.getAttribute("cart");
			}
			if (cart == null) {
				cart = new Cart();
			}
			int quantity;
			try {
				quantity = Integer.parseInt(quantityString);
				if (quantity < 0) {
					quantity = 1;
				}
			} catch (NumberFormatException nfe) {
				quantity = 1;
			}
			String path = sc.getRealPath("/WEB-INF/products.txt");
			Product product = ProductIO.getProduct(productCode, path);

			LineItem lineItem = new LineItem();
			lineItem.setProduct(product);
			lineItem.setQuantity(quantity);
			if (quantity > 0) {
				cart.addItem(lineItem);
			} else if (quantity == 0) {
				cart.removeItem(lineItem);
			}

			synchronized(lock) {
			    session.setAttribute("cart", cart);
			}
			url = "/WEB-INF/views/cartView.jsp";
		} else if (action.equals("checkout")) {
			url = "/WEB-INF/views/checkoutView.jsp";
		} else if (action.equals("cartview")) {
			url = "/WEB-INF/views/cartView.jsp";
		}
		sc.getRequestDispatcher(url).forward(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		doPost(req, resp);
	}
}
