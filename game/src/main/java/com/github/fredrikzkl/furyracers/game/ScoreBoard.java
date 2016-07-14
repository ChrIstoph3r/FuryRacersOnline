package com.github.fredrikzkl.furyracers.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.assets.Fonts;
import com.github.fredrikzkl.furyracers.assets.Sounds;
import com.github.fredrikzkl.furyracers.assets.Sprites;
import com.github.fredrikzkl.furyracers.car.Car;

public class ScoreBoard {

	private long 
	startTime;
	
	private int 
	initialSeconds, scoreBoardFontHeight, 
	origScreenWidth;
	
	private boolean 
	closedPlayed, movePlayed, 
	timerStarted, returnToMenuTimerDone;

	private float 
	resultsBoardWidth, highscoreBoardWidth, 
	endXposResults, screenHeight, margin, 
	endOfResultsY, movementPerUpdate, screenWidth,
	scalingValue, resultsBoardPosX, resultsBoardPosY, 
	highScorePosX, highScorePosY;
	
	private String secondsLeft = "";
	
	private ArrayList<Car> cars;
	private ArrayList<Player> players;

	public ScoreBoard(List<Car> cars2, List<Player> players2) {

		this.cars = (ArrayList<Car>) cars2;
		this.players = (ArrayList<Player>) players2;
				
		initialSeconds = 10;
		scoreBoardFontHeight = Fonts.scoreBoardHeader.getHeight();
		screenWidth = Application.screenSize.width;
		screenHeight = Application.screenSize.height;
		origScreenWidth = 1366; 
		scalingValue = (screenWidth*1.4f) / origScreenWidth;
		resultsBoardWidth = Sprites.resultsBoard.getWidth() * scalingValue;
		highscoreBoardWidth = Sprites.highscoresBoard.getWidth() * scalingValue;
		margin = resultsBoardWidth / 10;

		resultsBoardPosX = - resultsBoardWidth;
		highScorePosX = screenWidth + highscoreBoardWidth;
		resultsBoardPosY = Application.screenSize.height / 10;
		highScorePosY = Application.screenSize.height / 10;
		endXposResults = screenWidth / 2 - resultsBoardWidth;
		movementPerUpdate = screenWidth/342f;

		movePlayed = closedPlayed = returnToMenuTimerDone = false;
	}

	public void drawScoreBoard() {
		
		if(!movePlayed){
			Sounds.scoreboardMove.play();
			movePlayed = true;
		}
		
		drawBoardBackgrounds();
		
		if (resultsBoardPosX < endXposResults) {
			resultsBoardPosX += movementPerUpdate;
			highScorePosX -= movementPerUpdate*3/2;
		} else {
			drawScores();
			drawHighScoresHeader();
			drawMenuCountdown();
			if(!closedPlayed){
				Sounds.scoreboardClose.play();
				closedPlayed = true;
			}
		}
	}
	
	private void drawBoardBackgrounds(){
		
		Sprites.resultsBoard.draw(resultsBoardPosX, resultsBoardPosY, scalingValue);
		Sprites.highscoresBoard.draw(highScorePosX, highScorePosY, scalingValue);
	}
	
	private void drawScores() {
		
		drawResultsHeader();
		drawRaceResults();

		drawTotalScoreHeader();
		drawTotalScores();
	}
	
	private void drawResultsHeader(){
		
		Fonts.scoreBoardHeader.drawString(resultsBoardPosX + margin, resultsBoardPosY + margin, "Results:", Fonts.headerColor);
	}
	
	private void drawRaceResults(){
		
		ArrayList<Car> sortedCars = cars;
		Collections.sort(sortedCars);
		int amountOfCars = sortedCars.size();
		int startIndex = amountOfCars - 1;
		float yPos = resultsBoardPosY;
		
		for (int i = startIndex; i > -1; i--) {
			Car car = sortedCars.get(i);
			
			String player = "Player " + car.getPlayerNr() + ":";
			
			float yOffset = margin + scoreBoardFontHeight *(amountOfCars-i);
			float xPosPlayerNr = resultsBoardPosX + margin;
			yPos = resultsBoardPosY + yOffset;
			
			Fonts.scoreBoardText.drawString(xPosPlayerNr, yPos, player);
			
			String time = car.getTimeElapsed() + "    Score: +" + (i + 1);
			float xPosPlayerTime = xPosPlayerNr + margin*3.5f;
			Fonts.scoreBoardText.drawString(xPosPlayerTime, yPos, time);
		}
		
		endOfResultsY = yPos; 
	}
	
	private void drawTotalScoreHeader(){
		
		float xPos = resultsBoardPosX + margin;
		float yPos = endOfResultsY + margin;
		
		Fonts.scoreBoardHeader.drawString(xPos, yPos, "Total Score:", Fonts.headerColor);
	}
	
	private void drawTotalScores(){
		
		ArrayList<Player> sortedPlayers = players;
		Collections.sort(sortedPlayers);
		
		int i = 0;
		float xPos = resultsBoardPosX + margin;
		
		for (Player player : sortedPlayers) {
			
			String usernameWithScores = player.getUsername() + ": " + player.getScore();
			float yOffset = margin + scoreBoardFontHeight*(i+1);
			float yPos = endOfResultsY + yOffset;
			
			Fonts.scoreBoardText.drawString(xPos, yPos, usernameWithScores);
			i++;
		}
	}
	
	private void drawHighScoresHeader() {
		
		Fonts.scoreBoardHeader.drawString(highScorePosX + margin, highScorePosY + margin, "High Scores:", Fonts.headerColor);

	}
	
	private void drawMenuCountdown() {
		if (!timerStarted) {
			startTime = System.nanoTime();
			timerStarted = true;
		}
		
		long currentTime = System.nanoTime();
		long secondsElapsed = TimeUnit.NANOSECONDS.toSeconds(currentTime - startTime);
		
		secondsLeft = "" + (initialSeconds - secondsElapsed);
		
		int stringWidth = Fonts.scoreBoardHeader.getWidth(secondsLeft);
		float xPos = screenWidth - stringWidth;
		float yPos = screenHeight - scoreBoardFontHeight;
		
		Fonts.scoreBoardHeader.drawString(xPos, yPos, secondsLeft);

		if (initialSeconds - secondsElapsed <= 0) {
			returnToMenuTimerDone = true;
		}
	}
	
	public boolean isReturnToMenuTimerDone() {
		return returnToMenuTimerDone;
	}
}
