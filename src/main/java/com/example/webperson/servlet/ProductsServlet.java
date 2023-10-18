package com.example.webperson.servlet;

import com.example.webperson.bean.Product;
import com.example.webperson.dao.ProductIO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(urlPatterns = { "/loadProducts" })
public class ProductsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String path = getServletContext().getRealPath("/WEB-INF/products.txt");
		ArrayList<Product> products = ProductIO.getProducts(path);
		session.setAttribute("products", products);
		String url = "/WEB-INF/views/ch09ex2View.jsp";
		getServletContext().getRequestDispatcher(url).forward(req, resp);
	}
}
