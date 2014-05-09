package edu.ycp.cs.cs496.TGOH.pesist;
import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs.cs496.TGOH.temp.Courses;
import edu.ycp.cs.cs496.TGOH.temp.Notification;
import edu.ycp.cs.cs496.TGOH.temp.Registration;
import edu.ycp.cs.cs496.TGOH.temp.RegistrationStatus;
import edu.ycp.cs.cs496.TGOH.temp.User;
import edu.ycp.cs.cs496.TGOH.temp.UserType;


public class FakeDatabase implements IDatabase {
	private List<User> users; 
	private List<Courses> courses;
	private List<Registration> registrations;
	private List<Notification> notifications;

	private int registrationCounter = 1;
	private int courseCounter = 1;
	private int notCounter = 1;

	public FakeDatabase() {
		users = new ArrayList<User>();
		User user = new User("d","d","d","d",UserType.STUDENT);
		user.setId(1);
		User user1 = new User("c","c","c","c",UserType.ACCEPTEDTEACHER);
		user1.setId(2);
		users.add(user1);
		users.add(user);

		courses = new ArrayList<Courses>();
		Courses c = new Courses();
		c.setId(courseCounter++);
		c.setCourse("Introduction to Something");
		c.setTeacher("Babcock");
		courses.add(c);
		
		Courses c1 = new Courses();
		c1.setId(courseCounter++);
		c1.setCourse("Introduction to Something Else");
		c1.setTeacher("Hovemeyer");
		courses.add(c1);

		registrations = new ArrayList<Registration>();
		Registration reg = new Registration();
		reg.setId(registrationCounter++);
		reg.setUserId(1);
		reg.setCourseId(1);
		reg.setStatus(RegistrationStatus.PENDING);
		registrations.add(reg);
		
		Registration reg2 = new Registration();
		reg2.setId(registrationCounter++);
		reg2.setUserId(2);
		reg2.setCourseId(1);
		reg2.setStatus(RegistrationStatus.APPROVED);
		registrations.add(reg2);
		
		Registration reg3 = new Registration();
		reg3.setId(registrationCounter++);
		reg3.setUserId(1);
		reg3.setCourseId(2);
		reg3.setStatus(RegistrationStatus.APPROVED);
		registrations.add(reg3);
		
		notifications = new ArrayList<Notification>();
		Notification not = new Notification();
		not.setText("going for a hike");
		not.setId(notCounter);
		not.setCourseId(1);
		notifications.add(not);
		
		Notification not2 = new Notification();
		not2.setText("whew! That was exhausting");
		not2.setId(notCounter++);
		not2.setCourseId(1);
		notifications.add(not2);
		
		Notification not3 = new Notification();
		not3.setText("Welp, I better go for another one now.");
		not3.setId(notCounter++);
		not3.setCourseId(2);
		notifications.add(not3);
	}

	public void addUser(User user) {
		//add user to the list
		users.add(user);
	}
	
	public User getUser(String username){
		for(User user : users){
			if(user.getUserName().equals(username)){
				return user;
			}
		}
		return null;
	}

	// getting a user
	public User getUserfromRegistration(int Username) {
		for (User user1 : users) {
			if (user1.getUserName().equals(Username)) {
				// return a copy
				return user1;
			}
		}
		return null;
	}

	// deleting a user
	public boolean deleteUser(String user) {
		for(User temp : users){
			if(temp.getUserName().equals(user)){
				return users.remove(getUser(user));
			}
		}
		return false;
	}

	public Courses getCourse(int coursename){
		for(Courses x : courses){
			if(x.getId() == coursename){
				return x;
			}
		}
		return null;
	}
	
	public List<Courses> getAllCourse(){
		return new ArrayList<Courses>(courses);
	}

	public void addCourse(Courses course){
		course.setId(courseCounter++);
		courses.add(course);
	}

	public void deleteCourse(int Coursename){
		for(Courses x : courses){
			if(x.getId() == Coursename){
				courses.remove(x);
			}
		}
	}

	public Registration registerUserForCourse(Registration reg) {
		reg.setId(registrationCounter++);
		reg.setStatus(RegistrationStatus.PENDING);
		registrations.add(reg);
		return reg;
	}

	public void RemovingUserFromCourse(User user, Courses course){
		Registration reg = findUserForCourse(user, course);
		registrations.remove(reg.getId());
	}

	public Registration findUserForCourse(User user, Courses course) {
		for(Registration temp : registrations){
			if(temp.getCourseId()==course.getId() && temp.getUserId()==user.getId()){
				return temp;
			}
		}
		return null;
	}
	
	public void AcceptingUserforCourse(User user, Courses course){
		Registration reg = findUserForCourse(user, course);
		reg.setStatus(RegistrationStatus.APPROVED);
	}

	public List<Courses> getCoursefromUser(int user){
		int count = 0;
		List<Courses> course = new ArrayList<Courses>();
		for(Registration temp : registrations){
			if(temp.getUserId() == user && temp.getStatus() == RegistrationStatus.APPROVED){
				course.add(getCourse(temp.getCourseId()));
				count++;
			}
		}
		return course;
	}
	
	public List<User> getPendingUserforCourse(int course){
		List<User> user = new ArrayList<User>();
		for(Registration temp : registrations){
			if(temp.getCourseId() == course && temp.getStatus()==RegistrationStatus.PENDING){
				user.add(getUserfromRegistration(temp.getUserId()));
			}
		}
		return user;
	}

	@Override
	public void changePass(String username, String password) {
		getUser(username).setPassword(password);
	}
	
	public Notification getNotification(int id){
		for(Notification not : notifications){
			if(not.getId() == id){
				return not;
			}
		}
		return null;
	}
	
	public List<Notification> getNotificationForCourse(int courseId){
		List<Notification> not = new ArrayList<Notification>();
		for(Notification temp : notifications){
			if(temp.getCourseId() == courseId){
				not.add(getNotification(temp.getId()));
			}
		}
		return not;
	}
	
	public Notification addNotification(int courseId, String text){
		Notification not = new Notification();
		not.setCourseId(courseId);
		not.setText(text);
		not.setId(notCounter++);
		notifications.add(not);
		return not;
	}
	
	public void removeNotification(int id){
		Notification not = getNotification(id);
		notifications.remove(not);
	}

	@Override
	public Courses getCourseByName(String coursename) {
		for(Courses x : courses){
			if(x.getCourse().equals(coursename)){
				return x;
			}
		}
		return null;
	}
}
