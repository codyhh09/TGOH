package edu.ycp.cs.cs496.TGOH.controller;

import java.util.List;

import edu.ycp.cs.cs496.TGOH.pesist.Database;
import edu.ycp.cs.cs496.TGOH.pesist.IDatabase;
import edu.ycp.cs.cs496.TGOH.temp.User;

public class getPendingTeachers {
	public List<User> getPT(){
		IDatabase db = Database.getInstance();
		return db.getPendingTeacher();
	}
}
