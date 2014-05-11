package edu.ycp.cs.cs496.TGOH.controller;

import edu.ycp.cs.cs496.TGOH.pesist.Database;
import edu.ycp.cs.cs496.TGOH.pesist.IDatabase;
import edu.ycp.cs.cs496.TGOH.temp.Registration;

public class findreg {
	public Registration findfeg(int id){
		IDatabase db = Database.getInstance();
		return db.findreg(id);
	}
}
