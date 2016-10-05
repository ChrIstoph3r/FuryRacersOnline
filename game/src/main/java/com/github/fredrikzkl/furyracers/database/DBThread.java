package com.github.fredrikzkl.furyracers.database;

import java.util.ArrayList;

import com.github.fredrikzkl.furyracers.Result;
import com.github.fredrikzkl.furyracers.game.GameCore;

public class DBThread implements Runnable{

	
	private ArrayList<Result> retrieveDBHighScores(){
		
		return HighScoresDBCon.getHighScores(getTrackName());
	}
	
	private String getTrackName(){
		return GameCore.getLevel().getMapName();
	}
	
	@Override
	public void run() {
		
		HighScoresDBCon.openConnection();
		
		HighScoresDBCon.updateHighScores(retrieveDBHighScores(), GameCore.cars);
		
		GameCore.setHighScores(retrieveDBHighScores());
	}

}
