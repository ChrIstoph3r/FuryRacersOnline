package com.github.fredrikzkl.furyracers.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.RoundedRectangle;

import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.Result;
import com.github.fredrikzkl.furyracers.assets.Fonts;
import com.github.fredrikzkl.furyracers.assets.Sounds;
import com.github.fredrikzkl.furyracers.assets.Sprites;
import com.github.fredrikzkl.furyracers.car.Car;

public class ScoreBoard {

	private long 
	startTime;
	
	private int 
	scoreBoardVisibleSeconds, scoreBoardFontHeight, 
	origScreenWidth;
	
	private boolean 
	closedPlayed, movePlayed, 
	timerStarted, returnToMenuTimerDone;

	private float 
	resultsBoardWidth, highscoreBoardWidth, 
	endXposResults, screenHeight, margin, 
	endOfResultsY, movementPerUpdate, screenWidth,
	scalingValue, resultsBoardPosX, resultsBoardPosY, 
	highScorePosX, highScorePosY, highScoreHeaderX, highScoreHeaderY;
	
	private String secondsLeft = "";
	
	private List<Car> cars;
	private ArrayList<Player> players;

	public ScoreBoard(List<Car> cars2, List<Player> players2) {

		this.cars = cars2;
		this.players = (ArrayList<Player>) players2;
				
		scoreBoardVisibleSeconds = 12;
		scoreBoardFontHeight = Fonts.scoreBoardHeader.getHeight();
		screenWidth = Application.screenSize.width;
		screenHeight = Application.screenSize.height;
		origScreenWidth = 1366; 
		scalingValue = (screenWidth*1.4f)/origScreenWidth;
		resultsBoardWidth = Sprites.resultsBoard.getWidth()*scalingValue;
		highscoreBoardWidth = Sprites.highscoresBoard.getWidth()*scalingValue;
		margin = resultsBoardWidth/10;

		resultsBoardPosX = - resultsBoardWidth;
		highScorePosX = screenWidth + highscoreBoardWidth;
		resultsBoardPosY = Application.screenSize.height/10;
		highScorePosY = Application.screenSize.height/10;
		endXposResults = screenWidth/2 - resultsBoardWidth;
		movementPerUpdate = screenWidth/342f;

		movePlayed = closedPlayed = returnToMenuTimerDone = false;
	}
	
	
	public void drawScoreBoard(ArrayList<Result> highScores) {
		
		if(!movePlayed){
			Sounds.scoreboardMove.play();
			movePlayed = true;
		}
		
		drawBoardBackgrounds();
		
		if (resultsBoardPosX < endXposResults) {
			
			resultsBoardPosX += movementPerUpdate;
			highScorePosX -= movementPerUpdate*3/2;
		}else{
			
			drawScores();
			drawHighScoresHeader();
			drawHighScores(highScores);
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
		
		List<Car> sortedCars = cars;
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
			
			String time = car.getformattedTimeElapsed() + "    Score: +" + (i + 1);
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
		
		highScoreHeaderX = highScorePosX + margin;
		highScoreHeaderY = highScorePosY + margin;
		
		Fonts.scoreBoardHeader.drawString(highScoreHeaderX, highScoreHeaderY, "Online High Scores:", Fonts.headerColor);
	}
	
	private void stillLoadingScores(){
		String loadingTxt = "STILL loading scores... Do u EVEN net??";
		Fonts.scoreBoardText.drawString(highScoreHeaderX, highScoreHeaderY + margin*2, loadingTxt);
	}
	
	private void drawHighScores(ArrayList<Result> highScores){
		
		if(highScores.size() == 0){
			stillLoadingScores();
			return;
		}
		
		int i = 1;
		float columnWidth = (highscoreBoardWidth - margin*2)/8;
		
		float column1 = highScoreHeaderX;
		float column2 = column1 + columnWidth + margin;
		float column3 = column2 + columnWidth + margin;
		
		for (Result record : highScores) {
			
			float yOffset = (margin/2 + scoreBoardFontHeight)*i;
			
			float yPosNameAndTime = highScoreHeaderY + yOffset;
			
			Fonts.scoreBoardText.drawString(column1, yPosNameAndTime, i + ".");
			Fonts.scoreBoardText.drawString(column2, yPosNameAndTime, record.getFormattedTime());
			Fonts.scoreBoardText.drawString(column3, yPosNameAndTime, record.getUsername());
			
			float yPosCarAndDate = yPosNameAndTime + scoreBoardFontHeight;
			
			Fonts.scoreBoardText.drawString(column2, yPosCarAndDate, record.getCarModel());
			Fonts.scoreBoardText.drawString(column3, yPosCarAndDate, record.getDate());
			i++;
		}
	}
	
	private void drawMenuCountdown() {
		
		if (!timerStarted) {
			startTime = System.nanoTime();
			timerStarted = true;
		}
		
		long currentTime = System.nanoTime();
		long secondsElapsed = TimeUnit.NANOSECONDS.toSeconds(currentTime - startTime);
		
		secondsLeft = "" + (scoreBoardVisibleSeconds - secondsElapsed);
		
		int boxWidth = Fonts.countdown.getWidth(""+scoreBoardVisibleSeconds);
		int stringWidth = Fonts.countdown.getWidth(secondsLeft);
		int stringHeight = Fonts.countdown.getHeight(secondsLeft);
		
		float margin = stringHeight/4;
		
		float xPos = screenWidth - (stringWidth + 2*margin);
		float yPos = screenHeight - (stringHeight + margin);
		
		float xPosBox = screenWidth - (boxWidth + 2*margin);
		float yPosBox = yPos - margin;
		
		float boxTransparacy = 0.25f;

		RoundedRectangle infoBox = new RoundedRectangle(xPosBox, yPosBox, (float)boxWidth + margin, (float)stringHeight + margin , 4f);
		Graphics g = new Graphics();
		g.setColor(new Color(0f, 0f, 0f, boxTransparacy));
		g.fill(infoBox);
		
		Fonts.countdown.drawString(xPos, yPos, secondsLeft);

		if (scoreBoardVisibleSeconds - secondsElapsed <= 0) {
			returnToMenuTimerDone = true;
		}
	}
	
	public boolean isReturnToMenuTimerDone() {
		return returnToMenuTimerDone;
	}
}
