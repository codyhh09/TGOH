package edu.ycp.cs.cs496.TGOH.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.ycp.cs.cs496.TGOH.JSON.JSON;
import edu.ycp.cs.cs496.TGOH.controller.AddUserController;
import edu.ycp.cs.cs496.TGOH.controller.DeleteUserController;
import edu.ycp.cs.cs496.TGOH.controller.GetUserController;
import edu.ycp.cs.cs496.TGOH.controller.PutPasswordController;
import edu.ycp.cs.cs496.TGOH.controller.ReplaceStatus;
import edu.ycp.cs.cs496.TGOH.controller.getPendingTeachers;
import edu.ycp.cs.cs496.TGOH.temp.User;
import edu.ycp.cs.cs496.TGOH.temp.UserType;

public class Userpage extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo == null || pathInfo.equals("") || pathInfo.equals("/")) {
			getPendingTeachers con = new getPendingTeachers();
			List<User> user = con.getPT();
			
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("text/plain");
			JSON.getObjectMapper().writeValue(resp.getWriter(), user);
			return ;
		}
		
		// Get the user name
		if (pathInfo.startsWith("/")){
			pathInfo = pathInfo.substring(1);
		}
		
		// Use a GetUsercontroller to find the user in the database
		GetUserController controller = new GetUserController();
		User user = controller.getUser(pathInfo);
		
		
		if (user == null) {
			// No such item, so return a NOT FOUND response
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			resp.setContentType("text/plain");
			resp.getWriter().println("No such user: " + pathInfo);
			return;
		}
		
		// Set status code and content type
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");
		
		// Return the item in JSON format
		JSON.getObjectMapper().writeValue(resp.getWriter(), user);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		User user = JSON.getObjectMapper().readValue(req.getReader(), User.class);
		// Use a GetUser controller to find the item in the database
		AddUserController controller = new AddUserController();
		controller.addUser(user);
		// Set status code and content type
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");
		
		// writing the operation out.
		JSON.getObjectMapper().writeValue(resp.getWriter(), user);
	}

	
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo == null || pathInfo.equals("") || pathInfo.equals("/")) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.setContentType("text/plain");
			resp.getWriter().println("No user listed");
			return;
		}
		// Get the item name
		if (pathInfo.startsWith("/")){
			pathInfo = pathInfo.substring(1);
		}

		DeleteUserController deleteUser = new DeleteUserController();
		deleteUser.deleteUser(pathInfo);

		// Set status code and content type
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");
		
		// Return the item in JSON format
		JSON.getObjectMapper().writeValue(resp.getWriter(), pathInfo);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, JsonGenerationException, JsonMappingException {
		String pathInfo = req.getPathInfo();
		String pass = null;
		String user = null;
		if (pathInfo == null || pathInfo.equals("") || pathInfo.equals("/")) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.setContentType("application/json");
			return;
		}else{
			// Get the item name
			if (pathInfo.startsWith("/")){
				pathInfo = pathInfo.substring(1);
			}	

			GetUserController controller = new GetUserController();
			if (pathInfo.contains("/")){
				user = pathInfo.substring(0, pathInfo.indexOf('/'));
				pass = pathInfo.substring(pathInfo.indexOf('/')+1,pathInfo.length());
			}else{
				if(controller.getUser(pathInfo).getType().equals(UserType.PENDINGTEACHER)){
					ReplaceStatus cont = new ReplaceStatus();
					cont.changeStat(pathInfo);
					// Set status code and content type
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.setContentType("application/json");
					
					// writing the operation out.
					JSON.getObjectMapper().writeValue(resp.getWriter(), pathInfo);
					return;
				}else{
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					resp.setContentType("text/plain");
					resp.getWriter().println("No password");
					return;
				}
			}
			// Use a GetItemByName controller to find the item in the database
			PutPasswordController con = new PutPasswordController();
			con.changePass(user, pass);
			
			// Set status code and content type
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("application/json");
			
			// writing the operation out.
			JSON.getObjectMapper().writeValue(resp.getWriter(), user);
		}
	}
}


