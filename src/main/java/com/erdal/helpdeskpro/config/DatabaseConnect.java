package com.erdal.helpdeskpro.config;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnect {
	

	//PostgreSQL provides connection details according to the request.
	
	private static String  URL= "jdbc:postgresql://localhost:5432/helpdesk_pro";
		
	private static String USERNAME = "proHelpDesk";
	
	private static String PASSWORD = "password";
	
	
	
	
	//Connection Method
	
	public static Connection connect() throws SQLException  {
		
		
		
		Connection conn=DriverManager.getConnection(URL,USERNAME,PASSWORD);
		
		
		try {
			
			Class.forName("org.postgresql.Driver");
			
			 DatabaseMetaData dbData=conn.getMetaData();
			 
			 System.out.println("Connect to : "+ dbData.getDatabaseProductName() + " / "
			                                   + dbData.getDatabaseMinorVersion());
		} catch (Exception egnore) {
			
			
			
		}
		
		return conn;
	}
	public static void initializeDatabase() {
	    try (Connection conn = connect(); 
	         Statement statment = conn.createStatement()) {

	        String sql = Files.readString(Paths.get("db/schema.sql"));
	        statment.execute(sql);

	        System.out.println("Database initialized successfully.");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
