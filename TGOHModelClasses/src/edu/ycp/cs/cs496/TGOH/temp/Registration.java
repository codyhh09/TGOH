package edu.ycp.cs.cs496.TGOH.temp;

public class Registration {
	private int id;
	private int userId;
	private int courseId;
	private RegistrationStatus status;

	public Registration() {
		
	}
	
	public Registration(int userId, int courseId) {
		this.userId = userId;
		this.courseId = courseId;
		status = RegistrationStatus.PENDING;
		id = 0;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
	
	public int getCourseId() {
		return courseId;
	}
	
	public void setStatus(RegistrationStatus status) {
		this.status = status;
	}
	
	public RegistrationStatus getStatus() {
		return status;
	}

}
