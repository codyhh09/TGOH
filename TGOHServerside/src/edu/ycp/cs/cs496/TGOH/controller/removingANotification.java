package edu.ycp.cs.cs496.TGOH.controller;

import edu.ycp.cs.cs496.TGOH.pesist.Database;
import edu.ycp.cs.cs496.TGOH.pesist.IDatabase;

public class removingANotification {
	public void RemovingANotification(int id){
		IDatabase db = Database.getInstance();
		db.removeNotification(id);
	}
}
