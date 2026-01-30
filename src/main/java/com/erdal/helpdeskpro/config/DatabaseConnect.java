package com.erdal.helpdeskpro.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnect {
	
	
	//PostgreSQL provides connection details according to the request.
	
	private static String  URL= "jdbc:postgresql://localhost:5432/taskdb";
		
	private static String USERNAME = "pro-helpdesk";
	
	private static String PASSWORD = "password";
	
	
	
	
	//Connection Method
	
	public static Connection connect() throws SQLException  {
		
		Connection conn=DriverManager.getConnection(URL,USERNAME,PASSWORD);
		
		
		try {
			 DatabaseMetaData dbData=conn.getMetaData();
			 
			 System.out.println("Connect to : "+ dbData.getDatabaseProductName() + " / "
			                                   + dbData.getDatabaseMinorVersion());
		} catch (Exception egnore) {
			
			
			
		}
		
		return conn;
	}

}
