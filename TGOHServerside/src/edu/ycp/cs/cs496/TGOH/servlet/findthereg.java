package edu.ycp.cs.cs496.TGOH.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs.cs496.TGOH.JSON.JSON;
import edu.ycp.cs.cs496.TGOH.controller.GetCourseById;
import edu.ycp.cs.cs496.TGOH.controller.findUserForCourse;
import edu.ycp.cs.cs496.TGOH.controller.findreg;
import edu.ycp.cs.cs496.TGOH.controller.getAllCourses;
import edu.ycp.cs.cs496.TGOH.controller.getUserfromRegistration;
import edu.ycp.cs.cs496.TGOH.temp.Courses;
import edu.ycp.cs.cs496.TGOH.temp.Registration;

public class findthereg extends HttpServlet{
private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo == null || pathInfo.equals("") || pathInfo.equals("/")) {
			getAllCourses gt = new getAllCourses();
			List<Courses> cs = gt.getAllcourses();
			
			// Set status code and content type
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("application/json");
			
			// Return the item in JSON format
			JSON.getObjectMapper().writeValue(resp.getWriter(), cs);
			
			return;
		}
		
		// Get the item name
		if (pathInfo.startsWith("/")){
			pathInfo = pathInfo.substring(1);
		}
		int id = Integer.parseInt(pathInfo);
		
		findreg con = new findreg();
		Registration reg = con.findfeg(id);
		
		if (reg == null) {
			// No such item, so return a NOT FOUND response
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			resp.setContentType("text/plain");
			resp.getWriter().println("Not a course: " + pathInfo);
			return;
		}
		// Set status code and content type
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");
		
		// Return the item in JSON format
		JSON.getObjectMapper().writeValue(resp.getWriter(), reg);
	}
}
