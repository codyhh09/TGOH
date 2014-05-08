package edu.ycp.cs.cs496.TGOH.temp;

import java.util.ArrayList;
import java.util.List;

// FIXME: should be "Course" (singular)
public class Courses {
	private int id;
	private String Coursename;
	private String Teacher; // maybe should be user id of instructor?
	
	public Courses(){
		
	} 
	
	public Courses(String Coursename){
		this.Coursename = Coursename;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setCourse(String courseName) {
		this.Coursename = courseName;
	}
	
	public String getCourse() {
		return Coursename;
	}
	
	public void setTeacher(String string) {
		Teacher = string;
	}
	
	public String getTeacher() {
		return Teacher;
	}
}
