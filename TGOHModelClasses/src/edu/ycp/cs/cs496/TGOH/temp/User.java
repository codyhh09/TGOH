package edu.ycp.cs.cs496.TGOH.temp;

public class User {
	private int id;
	private String Username;
	private String FirstName;
	private String LastName;
	private String Password;
	private UserType type;
	
	public User(){
		
	}
	
	public User(String Username, String FirstName, String LastName, String Password, UserType type){
		this.Username = Username;
		this.FirstName = FirstName;
		this.LastName = LastName;
		this.Password = Password;
		this.type = type;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public String getUserName() {
		return Username;
	}
	
	public void setUserName(String name) {
		this.Username = name;
	}
	
	public String getFirstName() {
		return FirstName;
	}
	
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	
	public String getLastName() {
		return LastName;
	}
	
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	
	public String getPassword() {
		return Password;
	}
	
	public void setPassword(String password) {
		Password = password;
	}

	public UserType getType() {
		return type;
	}
	
	public void setType(UserType type) {
		this.type = type;
	}
}
