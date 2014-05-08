package edu.ycp.cs.cs496.TGOH.controller;

import edu.ycp.cs.cs496.TGOH.pesist.Database;
import edu.ycp.cs.cs496.TGOH.pesist.IDatabase;
import edu.ycp.cs.cs496.TGOH.temp.User;

public class DeleteUserController {
	public void deleteUser(String user){
		IDatabase db = Database.getInstance();
		db.deleteUser(user);
	}
}