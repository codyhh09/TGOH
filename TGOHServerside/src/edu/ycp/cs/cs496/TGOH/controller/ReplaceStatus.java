package edu.ycp.cs.cs496.TGOH.controller;

import edu.ycp.cs.cs496.TGOH.pesist.Database;
import edu.ycp.cs.cs496.TGOH.pesist.IDatabase;

public class ReplaceStatus {
	public void changeStat(String username){
		IDatabase db = Database.getInstance();
		db.changeStatus(username);
	}
}
