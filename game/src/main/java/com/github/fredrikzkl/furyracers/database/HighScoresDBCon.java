package com.github.fredrikzkl.furyracers.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.github.fredrikzkl.furyracers.Result;
import com.github.fredrikzkl.furyracers.car.Car;
import com.github.fredrikzkl.furyracers.game.GameCore;

public class HighScoresDBCon {

	static Connection myCon;
	static List<Result> highScores;
	
	private static final int TARGET_AMNT_HISCRS = 10;
	
	public static void openConnection(){
		
		try {
		    Class.forName("com.mysql.cj.jdbc.Driver");
		    
		} 
		catch (ClassNotFoundException e) {
		    JOptionPane.showMessageDialog(null, e.getMessage());
		} 
		
		try {
			myCon = DriverManager.getConnection(DBInfo.conType + DBInfo.hostname + ":" + DBInfo.port + "/" + DBInfo.DBname, DBInfo.username, DBInfo.password);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		
		highScores = new ArrayList<Result>();
	}
	
	public static void updateHighScores(List<Result> highScores, List<Car> cars){
		
		int maxHiScrAmnt = highScores.size() + cars.size();
		
		if(maxHiScrAmnt <= TARGET_AMNT_HISCRS){
			addAllHighScores(cars);
			return;
		}
		
		if(highScores.size() < TARGET_AMNT_HISCRS){
			
			fillUpNotFullHighScoreList(cars, highScores.size());
		}
	
		compareAndAddTimes(highScores, cars);
	}
	
	private static void fillUpNotFullHighScoreList(List<Car> cars, int hiScreAmnt){
		
		for(Car car : cars){
			
			if(hiScreAmnt < TARGET_AMNT_HISCRS){
				addHighScore(car, GameCore.getLevel().getMapName());
				hiScreAmnt++;
			}else{
				return;
			}
		}
	}
	
	
	/*
	 * If car is better than one of the highscores, add it to the highscores.
	 * No need to compare it to the rest of the highscores, the DB arranges 
	 * ascending order for us.
	 */
	
	private static void compareAndAddTimes(List<Result> highScores, List<Car> cars){
		
		for(Car car : cars){
			for(Result highScore : highScores){
				
				
				if(highScore.compareTimes(car.getTime()) < 0){
					
					addHighScore(car, getTrackName());

					Collections.sort(highScores);
					
					removeBrokenHighScore(highScores);
					
					break;
				}
			}
		}
		
	}
	
	private static void removeBrokenHighScore(List<Result> highScores){
		
		int lastIndx = highScores.size()-1;
		
		Result oldHighScore = highScores.get(lastIndx);
		
		removeHighScore(oldHighScore.getId());
		highScores.remove(lastIndx);
	}
	
	private static void removeHighScore(int resultID){
		
		String query = "DELETE FROM record WHERE id = " + resultID + ";";
		insertStmt(query);
	}
	
	private static String currentDate(){
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		
		return dateFormat.format(date);
	}
	
	private static void addAllHighScores(List<Car> cars){
		
		for(Car car : cars){
			addHighScoreToDB(car, getTrackName());
		}
	}
	
	private static void addHighScore(Car car, String trackName){
		
		addHighScoreToDB(car, trackName);
		
		addHighScoreToArray(car);
	}
	
	public static void addHighScoreToArray(Car car){
		
		Result newRecord = new Result();
		newRecord.setTime(car.getTime());
		
		highScores.add(newRecord);
	}
	
	public static void addHighScoreToDB(Car car, String trackName){
		
		String query = "INSERT INTO record (username, raceTime, carModel, setOn, trackName) "
				+ "VALUES ('" + car.getUsername() +"', " + car.getTime() + ", '" 
				+ car.getCarModel() + "', '" + currentDate() + "','"
				+ getTrackName() + "');";
		
		insertStmt(query);
	}
	
	public static ArrayList<Result> getHighScores(String trackName){
		
		if(!trackExistsInDB(trackName)){
			addTrackToDB(trackName);
		}
		
		String query = 
				"SELECT id, username, raceTime, carModel, date_format(setOn, '%d-%m-%y') "
				+ "AS recordDate "
				+ "FROM record "
				+ "WHERE trackName = '" + trackName + "' "
				+ "ORDER BY raceTime ASC "
				+ "LIMIT " + TARGET_AMNT_HISCRS + ";";
		
		ArrayList<Result> results = new ArrayList<Result>();
		
		try {
			Statement stmt = myCon.createStatement();
			ResultSet execQuery = stmt.executeQuery(query);
			
			while(execQuery.next()){
				Result result = new Result();
				result.setId(execQuery.getInt("id"));
				result.setUsername(execQuery.getString("username"));
				result.setTime(execQuery.getInt("raceTime"));
				result.setCarModel(execQuery.getString("carModel"));
				result.setDate(execQuery.getString("recordDate"));
				
				results.add(result);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		
		return results;
	}
	
	private static void addTrackToDB(String trackName){
		
		String query = "INSERT INTO track (trackName) VALUES ('" + trackName + "');";
		
		insertStmt(query);
	}
	
	private static void insertStmt(String query){
		
		try {
			Statement stmt = myCon.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	private static boolean trackExistsInDB(String trackName){
		
		String query = "SELECT * FROM track WHERE trackName = '" + trackName + "';";
		
		try {
			Statement stmt = myCon.createStatement();
			ResultSet execQuery = stmt.executeQuery(query);
			
			if(execQuery.next())
				return true;
			
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		
		return false;
	}
	
	private static String getTrackName(){
		
		return GameCore.getLevel().getMapName();
	}

}
