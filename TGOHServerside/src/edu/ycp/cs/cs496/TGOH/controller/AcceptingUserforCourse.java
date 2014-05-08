package edu.ycp.cs.cs496.TGOH.controller;

import edu.ycp.cs.cs496.TGOH.pesist.Database;
import edu.ycp.cs.cs496.TGOH.pesist.IDatabase;
import edu.ycp.cs.cs496.TGOH.temp.Courses;
import edu.ycp.cs.cs496.TGOH.temp.Registration;
import edu.ycp.cs.cs496.TGOH.temp.User;

public class AcceptingUserforCourse {
	public void acceptingUserforCourse(User user, Courses course){
		IDatabase db = Database.getInstance();
		db.AcceptingUserforCourse(user, course);
	}
}
