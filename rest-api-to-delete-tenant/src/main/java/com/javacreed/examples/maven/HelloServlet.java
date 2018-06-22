/*
 * 
 */
package com.javacreed.examples.maven;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenant.delete.DeleteTenant;

public class HelloServlet extends HttpServlet {

	private static final long serialVersionUID = 1533532266743443618L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		// Set response content type
		String msg = "";
		response.setContentType("text/html");
		String hours = request.getParameter("hours");
		try {
			 msg=DeleteTenant.get(hours);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		try (PrintWriter out = response.getWriter()) {
			out.println("<html><body><h1><p>Running the tenant clean  up script :<br> </p></h1> <br></body></html>");
			out.println("spcified  hours  is :"+hours );
			out.println(msg);
			
		}
	}
}
