package edu.ycp.cs.cs496.TGOH.controller;

import edu.ycp.cs.cs496.TGOH.pesist.Database;
import edu.ycp.cs.cs496.TGOH.pesist.IDatabase;
import edu.ycp.cs.cs496.TGOH.temp.Courses;

public class AddingCourses {
	public boolean addCourse(String Username,Courses Course){
		IDatabase db = Database.getInstance();
		return db.addACourse(Username, Course);
	}
}
