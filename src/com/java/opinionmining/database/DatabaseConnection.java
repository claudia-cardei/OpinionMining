package com.java.opinionmining.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Database Connection implemented as a singleton.
 * 
 * @author Claudia Cardei
 *
 */
public class DatabaseConnection {
	private static Connection connection;
    private static Statement statement;

    private DatabaseConnection() {
    }
    
    public static Connection getConnection() throws SQLException {
    	if (connection == null )
    		connection = DriverManager.getConnection(
    				DatabaseConnectionConstants.DATABASE, 
    				DatabaseConnectionConstants.USER, 
    				DatabaseConnectionConstants.PASSWORD);
    	
    	return connection;
    }
    
    public static PreparedStatement prepareStatement(String query)
    		throws SQLException {
    	return getConnection().prepareStatement(query);
    }
    
    public static Statement getStatement() throws SQLException {
    	if (statement == null)
    		statement = getConnection().createStatement(
    				ResultSet.TYPE_SCROLL_SENSITIVE, 
    				ResultSet.CONCUR_UPDATABLE);
    	
    	return statement;
    }
    
    public static void closeConnection() throws SQLException {
    	statement.close();
    }
}
