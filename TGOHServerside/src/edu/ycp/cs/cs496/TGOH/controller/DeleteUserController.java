package edu.ycp.cs.cs496.TGOH.controller;

import edu.ycp.cs.cs496.TGOH.User.User;
import edu.ycp.cs.cs496.TGOH.pesist.Database;
import edu.ycp.cs.cs496.TGOH.pesist.IDatabase;

public class DeleteUserController {
	public void deleteUser(User user){
		IDatabase db = Database.getInstance();
		db.deleteUser(user);
	}
}