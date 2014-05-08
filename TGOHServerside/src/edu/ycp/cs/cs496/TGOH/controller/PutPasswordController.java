package edu.ycp.cs.cs496.TGOH.controller;

import edu.ycp.cs.cs496.TGOH.pesist.Database;
import edu.ycp.cs.cs496.TGOH.pesist.IDatabase;
import edu.ycp.cs.cs496.TGOH.temp.User;

public class PutPasswordController {
	public void changePass(String username, String password){
		IDatabase db = Database.getInstance();
		db.changePass(username, password);
	}
}
