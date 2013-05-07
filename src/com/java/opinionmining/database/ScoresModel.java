package com.java.opinionmining.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.java.opinionmining.affectivescores.AffectiveScores;

public class ScoresModel {
	
	private static final String sqlAddWord = 
			"insert into scores (cuvant, scor1, scor2, scor3) values (?, ?, ?, ?)";
	private static final String sqlGetScores = "select * from scores where cuvant = ?";

	
	public void addWord(String word, double score1, double score2, double score3) {
		word = word.toLowerCase();
		
		try {
			PreparedStatement statement = DatabaseConnection.prepareStatement(sqlAddWord);
			statement.setString(1, word);
			statement.setDouble(2, score1);
			statement.setDouble(3, score2);
			statement.setDouble(4, score3);
			
			statement.execute();
			statement.close();
			
		} catch (SQLException e) {
			System.out.println(word);
		}
	}
	
	public AffectiveScores getScores(String word) {
		word = word.toLowerCase();
		
		AffectiveScores result = new AffectiveScores(word);
		
		try {
			PreparedStatement statement = DatabaseConnection.prepareStatement(sqlGetScores);
			statement.setString(1, word);
			
			if (statement.execute()) {
				ResultSet queryResult = statement.getResultSet();
			
				if (queryResult.next()) {
					result.setScore1(queryResult.getDouble(2));
					result.setScore2(queryResult.getDouble(3));
					result.setScore3(queryResult.getDouble(4));
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
