package edu.ycp.cs.cs496.TGOH.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs.cs496.TGOH.JSON.JSON;
import edu.ycp.cs.cs496.TGOH.controller.addingANotification;
import edu.ycp.cs.cs496.TGOH.controller.getANotification;
import edu.ycp.cs.cs496.TGOH.controller.getAllNotifications;
import edu.ycp.cs.cs496.TGOH.controller.gettingACourse;
import edu.ycp.cs.cs496.TGOH.controller.removingANotification;
import edu.ycp.cs.cs496.TGOH.temp.Notification;

public class Notificationpage extends HttpServlet {
private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if(pathInfo == null){
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.setContentType("text/plain");
			resp.getWriter().println("Need to get a course id");
			return;
		}
		
		// Get the item name
		if (pathInfo.startsWith("/")){
			pathInfo = pathInfo.substring(1);
		}
		if (pathInfo.contains("/")){
			pathInfo = pathInfo.substring(pathInfo.indexOf('/')+1, pathInfo.length());
		}else {
			int courseId = Integer.parseInt(pathInfo);
			getAllNotifications gt = new getAllNotifications();
			List<Notification> cs = gt.getAllNotifications(courseId);
			
			// Set status code and content type
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("application/json");
			
			// Return the item in JSON format
			JSON.getObjectMapper().writeValue(resp.getWriter(), cs);
			
			return;
		}
		int notId = Integer.parseInt(pathInfo);
		
		getANotification controller = new getANotification();
		Notification not = controller.GetANotification(notId);
		
		if (not == null) {
			// No such item, so return a NOT FOUND response
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			resp.setContentType("text/plain");
			resp.getWriter().println("Not a notification doesn't exist");
			return;
		}
		// Set status code and content type
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");
		
		// Return the item in JSON format
		JSON.getObjectMapper().writeValue(resp.getWriter(), not);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Notification not = JSON.getObjectMapper().readValue(req.getReader(), Notification.class);
		// Use a GetUser controller to find the item in the database
		addingANotification con = new addingANotification();
		con.AddingANotification(not.getText(), not.getCourseId());
		// Set status code and content type
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");
		
		// writing the operation out.
		JSON.getObjectMapper().writeValue(resp.getWriter(), not);
	}

	
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {		
		String pathInfo = req.getPathInfo();
		if (pathInfo == null || pathInfo.equals("") || pathInfo.equals("/")) {
			resp.getWriter().write("we delete nothing");
		}else{
			// Get the item name
			if (pathInfo.startsWith("/")) {
				pathInfo = pathInfo.substring(1);
			}
			int Id = Integer.parseInt(pathInfo);
			// Use a GetItemByName controller to find the item in the database
			removingANotification controller = new removingANotification();
			controller.RemovingANotification(Id);
			
			// Set status code and content type
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("application/json");
			
			getANotification controller2 = new getANotification();
			
			
			// writing the operation out.
			//JSON.getObjectMapper().writeValue(resp.getWriter(), controller2.GetANotification(Id).getText());
			JSON.getObjectMapper().writeValue(resp.getWriter(), Id);
		}
	}
}
