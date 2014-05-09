package edu.ycp.cs.cs496.TGOH.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs.cs496.TGOH.JSON.JSON;
import edu.ycp.cs.cs496.TGOH.controller.AddingANewCourse;
import edu.ycp.cs.cs496.TGOH.controller.AddingCoursesToUser;
import edu.ycp.cs.cs496.TGOH.controller.GetCourseById;
import edu.ycp.cs.cs496.TGOH.controller.GetUserController;
import edu.ycp.cs.cs496.TGOH.controller.RemovingACourse;
import edu.ycp.cs.cs496.TGOH.controller.getAllCourses;
import edu.ycp.cs.cs496.TGOH.controller.getUserfromRegistration;
import edu.ycp.cs.cs496.TGOH.controller.gettingACourse;
import edu.ycp.cs.cs496.TGOH.temp.Courses;
import edu.ycp.cs.cs496.TGOH.temp.User;

public class CoursesPage extends HttpServlet {
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
		// Use a GettingACourse controller to find the item in the database
		gettingACourse controller = new gettingACourse();
		Courses course = controller.getCourse(pathInfo);
		
		if (course == null) {
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
		JSON.getObjectMapper().writeValue(resp.getWriter(), course);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Courses course = JSON.getObjectMapper().readValue(req.getReader(), Courses.class);
		
		// Use a GetUser controller to find the item in the database
		AddingANewCourse controller = new AddingANewCourse();
		controller.addCourse(course);

		// Set status code and content type
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");
		
		// writing the operation out.
		JSON.getObjectMapper().writeValue(resp.getWriter(), course);
	}

	
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo == null || pathInfo.equals("") || pathInfo.equals("/")) {
			// Set status code and content type
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			resp.setContentType("application/json");
			resp.getWriter().write("we delete nothing");
			return;
		}
		
		// Get the item name
		if (pathInfo.startsWith("/")){
			pathInfo = pathInfo.substring(1);
		}
		
		int courseId = Integer.parseInt(pathInfo);
				
		RemovingACourse deleteUser = new RemovingACourse();
		deleteUser.removingACourse(courseId);
			
		// send response
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");	
			
		GetCourseById controller = new GetCourseById();
			
		JSON.getObjectMapper().writeValue(resp.getWriter(), controller.getCourse(courseId));

	}
}
