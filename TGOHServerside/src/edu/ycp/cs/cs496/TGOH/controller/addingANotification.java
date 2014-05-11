package edu.ycp.cs.cs496.TGOH.controller;

import edu.ycp.cs.cs496.TGOH.pesist.Database;
import edu.ycp.cs.cs496.TGOH.pesist.IDatabase;
import edu.ycp.cs.cs496.TGOH.temp.Notification;

public class addingANotification {
	public Notification AddingANotification(Notification not){
		IDatabase db = Database.getInstance();
		return db.addNotification(not);
	}
}
