package com.java.opinionmining.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DictionaryModel {
	
	private static final String sqlQueryGetWord =
			"select * from dictionar where cuvant = ?";
	
	public boolean existsWordInDictionary(String word) {
		boolean result = false;
		
		try {
			PreparedStatement statement = DatabaseConnection.prepareStatement(sqlQueryGetWord);
			statement.setString(1, word);
			
			if (statement.execute()) {
				ResultSet queryResult = statement.getResultSet();
			
				if (queryResult.next()) {
					result = true;
				}
				
				queryResult.close();
			}
			
			statement.close();
			 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
