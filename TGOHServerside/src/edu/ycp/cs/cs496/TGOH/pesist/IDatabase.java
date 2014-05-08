package edu.ycp.cs.cs496.TGOH.pesist;

import java.util.List;

import edu.ycp.cs.cs496.TGOH.temp.Courses;
import edu.ycp.cs.cs496.TGOH.temp.Notification;
import edu.ycp.cs.cs496.TGOH.temp.Registration;
import edu.ycp.cs.cs496.TGOH.temp.User;

public interface IDatabase {
/**
 * Database persistence methods
 * @return 
 */
	public void addUser(User user);
	
	public User getUser(String username);
	
	public User getUserfromRegistration(int Username);
	
	public boolean deleteUser(String user);
	
	public Courses getCourse(int coursename);
	
	public void addCourse(Courses course);
	
	public void deleteCourse(int Coursename);
	
	public Registration findUserForCourse(User user, Courses course);
	
	public List<Courses> getCoursefromUser(int user);
	
	public List<Courses> getAllCourse();

	public void RemovingUserFromCourse(User user, Courses course);

	public Registration registerUserForCourse(Registration reg);
	
	public void AcceptingUserforCourse(User user, Courses course);
	
	public List<User> getPendingUserforCourse(int course);

	public Courses getCourseByName(String coursename);

	public void removeNotification(int id);
	
	public Notification addNotification(int courseId, String text);
	
	public List<Notification> getNotificationForCourse(int courseId);
	
	public Notification getNotification(int id);
	
	public void changePass(final String username, final String password);
}
