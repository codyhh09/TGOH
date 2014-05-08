package edu.ycp.cs.cs496.TGOH.controller;

import edu.ycp.cs.cs496.TGOH.pesist.Database;
import edu.ycp.cs.cs496.TGOH.pesist.IDatabase;
import edu.ycp.cs.cs496.TGOH.temp.User;

public class GetUserController {
	public User getUser(String username){
		IDatabase db = Database.getInstance();
		return db.getUser(username);
	}
}
