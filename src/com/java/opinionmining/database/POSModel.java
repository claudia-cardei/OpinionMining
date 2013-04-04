package com.java.opinionmining.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class POSModel {

	private static final String sqlAddPhrase =
			"insert into pos (text, result) values (?, ?)";
	private static final String sqlGetResult =
			"select result from pos where text = ?";
	
	public void addPhrase(String phrase, String processedPhrase) {
		try {
			PreparedStatement statement =
					DatabaseConnection.prepareStatement(sqlAddPhrase);
			statement.setString(1, phrase);
			statement.setString(2, processedPhrase);
			
			statement.execute();
			statement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getProcessResult(String phrase) {
		try {
			PreparedStatement statement = 
					DatabaseConnection.prepareStatement(sqlGetResult);
			statement.setString(1, phrase);
					
			if ( statement.execute() == false )
				return null;
			
			ResultSet queryResult = statement.getResultSet();
			
			if (queryResult.next() == false) {
				return null;
			}
			else {
				return queryResult.getString("result");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
