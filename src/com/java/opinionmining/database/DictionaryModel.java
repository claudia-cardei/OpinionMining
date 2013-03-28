package com.java.opinionmining.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DictionaryModel {
	
	private static final String sqlQueryGetWord =
			"select * from dictionar where cuvant = ?";
	
	public boolean existsWordInDictionary(String word) {
		try {
			PreparedStatement statement =
					DatabaseConnection.prepareStatement(sqlQueryGetWord);
			statement.setString(1, word);
			
			boolean result = statement.execute();
			
			if (result == false) {
				return false;
			}
			
			ResultSet queryResult = statement.getResultSet();
			
			if (queryResult.next() == false) {
				return false;
			}
			
			queryResult.close();
			 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
