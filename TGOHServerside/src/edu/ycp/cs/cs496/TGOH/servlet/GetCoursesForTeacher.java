package edu.ycp.cs.cs496.TGOH.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs.cs496.TGOH.JSON.JSON;
import edu.ycp.cs.cs496.TGOH.controller.GetCoursefromUser;
import edu.ycp.cs.cs496.TGOH.controller.getTeachersCourse;
import edu.ycp.cs.cs496.TGOH.temp.Courses;

public class GetCoursesForTeacher extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// path info will be /<user id>
		String pathInfo = req.getPathInfo();
		if (pathInfo == null) {
			throw new ServletException("Invalid path info");
		}
		if (pathInfo.startsWith("/")) {
			pathInfo = pathInfo.substring(1);
		}
		
		int userId = Integer.parseInt(pathInfo);
		getTeachersCourse controller = new getTeachersCourse();
		List<Courses> courses = controller.getTC(userId);    /* find courses for user using controller */
				
		resp.setContentType("application/json");
		JSON.getObjectMapper().writeValue(resp.getWriter(), courses);
	}
}
