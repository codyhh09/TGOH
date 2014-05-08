package edu.ycp.cs.cs496.TGOH.controller;

import edu.ycp.cs.cs496.TGOH.pesist.Database;
import edu.ycp.cs.cs496.TGOH.pesist.IDatabase;
import edu.ycp.cs.cs496.TGOH.temp.Courses;

public class GetCourseById {
	public Courses getCourse(int coursename){
		IDatabase db = Database.getInstance();
		return db.getCourse(coursename);
	}
}
