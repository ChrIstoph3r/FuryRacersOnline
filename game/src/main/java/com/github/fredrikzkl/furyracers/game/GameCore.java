package com.github.fredrikzkl.furyracers.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.websocket.EncodeException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.Result;
import com.github.fredrikzkl.furyracers.assets.Fonts;
import com.github.fredrikzkl.furyracers.assets.Sounds;
import com.github.fredrikzkl.furyracers.car.Car;
import com.github.fredrikzkl.furyracers.car.Properties;
import com.github.fredrikzkl.furyracers.network.GameSession;

public class GameCore extends BasicGameState {

	public final static int 
	maxLaps = 1, menuID = 0;
	
	private int 
	screenWidth, screenHeight;

	public float 
	initalZoom, zoom,
	biggest, infoFontSize; 

	private long 
	startTimeCountdown, nanoSecondsElapsed, 
	currentTimeCountDown, secondsElapsed,
	startGoSignalTime, goSignalTimeElapsed, secondsLeft;

	private boolean 
	raceStarted, countdownStarted, startGoSignal, goSignal, 
	raceFinished, threePlayed, twoPlayed, onePlayed, goPlayed,
	isHiScrUpdtd;

	private static boolean 
	makeNewMissile;
	
	public static boolean 
	finalRoundSaid, crowdFinishedPlayed,
	itmEqpd;

	private String IP;

	private ScoreBoard scoreboard;
	public static ArrayList<Car> cars;
	public static List<Player> players;
	private Camera camera;
	private static Level level;
	static Item missile;
	static ArrayList<Result> highScores;


	public void init(GameContainer container, StateBasedGame sbg) throws SlickException {
		initVariables();
	}

	public void mapInput(CourseHandler course, ArrayList<Player> players) throws SlickException {

		GameSession.setGameState(getID());
		Application.setInMenu(false);
		GameCore.players = players;
		level = new Level(course);
		camera = new Camera(0, 0, level);
		camera.setZoom(0.3f);
		createCars();
		scoreboard = new ScoreBoard(cars, players);
	}

	public void update(GameContainer container, StateBasedGame game, int deltaTime) throws SlickException {

		checkForKeyboardInput(container, game);
		startCountdown();
		checkCountdown();
		updateItem(deltaTime);
		updateCars(container, game, deltaTime);
		checkDistances();
		camera.zoomLogic();
		camera.updateCamCoordinates();
		highScoreCheck();
	}
	
	private void highScoreCheck(){
		
		if(raceFinished && !isHiScrUpdtd){
			
			isHiScrUpdtd = true;
			Application.initOnlineHighScores();
		}
	}

	public void render(GameContainer container, StateBasedGame sbg, Graphics g) throws SlickException {

		firstCamRelocation(g);
		level.drawSubMap(g);
		level.drawCars(g, cars);
		level.drawTopMap(g);
		level.drawMissile(g, missile);
		secondCamRelocation(g);
		drawPlayerInfo(g);
		countdown(g);
		drawIp();
		updateScoreBoard(container, sbg);
		
	}
	
	private void updateScoreBoard(GameContainer container, StateBasedGame sbg) throws SlickException{
		
		if (raceFinished)
			scoreboard.drawScoreBoard(highScores);
		if (scoreboard.isReturnToMenuTimerDone()) {
			returnToMenu(container, sbg);
		}
	}
	
	public static Level getLevel(){
		
		return level;
	}
	
	public void updateItem(int deltaTime){
		
		if(makeNewMissile){
			genItemOnMap();
			makeNewMissile = false;
			System.out.println("Making new missile(GameCore.updateItem)");
		}
		
		missile.update(deltaTime, false);
	}
	
	public static void activateMissile(String idOfLanchrCar) throws IOException, EncodeException{
		
		Car carToTarget = findRndmCarExcept(idOfLanchrCar);
		
		if(carToTarget == null) return; 
		
		missile.launchMissile(carToTarget);
	}
	
	private static Car findRndmCarExcept(String carId){
		
		int lastCarIndx = cars.size()-1;
		
		while(true){
			
			if(cars.size()<2) return null;
			
			int rndmCarIndx = randomInt(0, lastCarIndx);
			
			Car carToTarget = cars.get(rndmCarIndx); 
			
			if(!carToTarget.getId().equals(carId))
				return carToTarget;
		}
	}
	
	private static int randomInt(int min, int max){
	
		int range = (max - min) + 1;  
		return (int) (Math.random()*range) + min;
	}
	
	public void genItemOnMap(){
		
		int numOfRoadTiles = level.getRoadTileIDs().size();
		
		int rndmRoadTileIndx = randomInt(0, numOfRoadTiles-1);
		
		int propertyNumberMissile = 12;
		
		Properties stats = Properties.values()[propertyNumberMissile];
		
		Vector2f startCoords = level.getRoadTileIDs().get(rndmRoadTileIndx);
		
		missile = new Item(stats, level.tilePosToPos(startCoords));

	}

	public void createCars() throws SlickException {

		cars = new ArrayList<Car>();
		for (Player player : players) {
			createCar(player.getPlayerNr(), player.getId(), player.getCarComboNr());
		}
	}
	
	public static void setHighScores(ArrayList<Result> highScores){
		
		GameCore.highScores = highScores;
	}
	
	private void drawIp(){
		
		int stringWidth = Fonts.infoFont.getWidth(IP);
		Fonts.infoFont.drawString(screenWidth - stringWidth, 10, IP);
	}

	private void startCountdown() {

		if (!countdownStarted) {
			startTimeCountdown = System.nanoTime();
			countdownStarted = true;
		}
	}

	private void countdown(Graphics g) {

		if(!raceStarted) {
			countdownAnnouncer();
			String secondsLeftString = "" + secondsLeft;
			drawCountdown(secondsLeftString);
			
		}else if(startGoSignal){
			startGoSignalTime = System.currentTimeMillis();
			startGoSignal = false;
			goSignal = true;
		}

		if(goSignal){
			
			long currentTime = System.currentTimeMillis();
			goSignalTimeElapsed = currentTime - startGoSignalTime;
			
			if(goSignalTimeElapsed < 1500){
				drawCountdown("RACE!");
				
				if(!goPlayed){
					Sounds.go.play();
					goPlayed = true;
				}
			}else {
				goSignal = false;
			}
		}
	}
	
	private void drawCountdown(String string){
		
		Color countdownColor = new Color(221, 0, 0);
		int stringWidth = Fonts.header.getWidth(string);
		float margin = screenHeight/10;
		float startX = screenWidth/2 - stringWidth/2 + margin;
		float startY = screenHeight/2 - margin;
		
		Fonts.header.drawString(startX, startY, string, countdownColor);
	}
	
	private void countdownAnnouncer(){
		
		if (secondsLeft > 2) {
			if (!threePlayed) {
				Sounds.three.play();
				threePlayed = true;
			}
		} else if (secondsLeft > 1) {
			if (!twoPlayed) {
				Sounds.two.play();
				twoPlayed = true;
			}
		} else if (secondsLeft > 0) {
			if (!onePlayed) {
				Sounds.one.play();
				onePlayed = true;
			}
		}
	}
	

	private void drawPlayerInfo(Graphics g) {
		
		float 
		margin = screenWidth/160,
		fontHeight = Fonts.infoFont.getHeight(),
		startX =  screenWidth/40,
		infoBoxHeight = fontHeight*2 + margin*2,
		cornerRadius = 4f;

		for (int i = 0; i < players.size(); i++) {
			
			Color 
			carColor = players.get(i).getCarColor();
			
			String 
			username = players.get(i).getUsername(),
			laps = "Lap " + cars.get(i).getLaps() + "/" + maxLaps;
			
			int 
			usernameLength = Fonts.infoFont.getWidth(username),
			lapsLength = Fonts.infoFont.getWidth(laps);
			
			float 
		    infoBoxWidth = infoBoxWidth(usernameLength, lapsLength) + margin*2,
			startY = screenHeight/10 + (infoBoxHeight + margin*2)*i,
			
			usernameStartY = startY + margin,
			lapsStartY = usernameStartY + margin/2 + fontHeight;
			
			RoundedRectangle infoBox = new RoundedRectangle(startX, startY, infoBoxWidth, infoBoxHeight, cornerRadius);
			g.setColor(carColor);
			g.fill(infoBox);
			Fonts.infoFont.drawString(startX+margin, usernameStartY, username);
			
			Fonts.infoFont.drawString(startX+margin, lapsStartY, laps);
		}

	}
	
	private int infoBoxWidth(int usernameLength, int lapsLength ){
		if(usernameLength < lapsLength)
			return lapsLength;
		
		return usernameLength;
	}

	public void updateCars(GameContainer container, StateBasedGame game, int deltaTime) throws SlickException{

		int carsFinished = 0;

		for (Car car : cars) {
			try {
				
				car.update(container, game, deltaTime);
				missile.checkIntersection(car);
				
			} catch (IOException | EncodeException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			
			if (car.isRaceFinished())
				carsFinished++;
		}
		
		if(carsFinished == cars.size())
			raceFinished = true;
		
	}
	
	public static void slowDownCar(String carId){
		for(Car car : cars){
			if(car.getId() == carId)
				car.slowDown();
		}
	}
	
	public static void setMakeNewMissile(){
		makeNewMissile = true;
	}

	public static void returnToMenu(GameContainer container, StateBasedGame game) throws SlickException {
		
		for(Car car : cars){
			car.controlls.resetTopSpeed();
			car.controlls.resetDeAcceleration();
		}
		
		for(Player player : players){
			player.rumbleController(false);
			player.removeLaunchButton();
		}
		
		Application.closeConnection();
		Application.createGameSession();
		
		game.getState(menuID).init(container, game);
		game.enterState(menuID);
	}

	public void checkCountdown() {
		if (!raceStarted) {
			currentTimeCountDown = System.nanoTime();
			nanoSecondsElapsed = currentTimeCountDown - startTimeCountdown;
			secondsElapsed = TimeUnit.NANOSECONDS.toSeconds(nanoSecondsElapsed);
			secondsLeft = 3 - secondsElapsed;

			if (secondsLeft <= 0)
				startRace();
			
		}
	}

	public void startRace() {
		for (Car car : cars)
			car.startClock();
		
		raceStarted = true;
	}

	private void checkDistances() {
		
		Vector2f randmHighNum = new Vector2f(level.getMapWidthPixels(), level.getMapHeightPixels());

		Vector2f furthestFromMapStartPoint = new Vector2f();
		Vector2f closestToMapStartPoint = new Vector2f(randmHighNum.x, randmHighNum.y);
		

		for (Car car : cars) {
			if (car.getPosition().x > furthestFromMapStartPoint.x) {
				furthestFromMapStartPoint.x = car.getPosition().x;
			}
			if (car.getPosition().x < closestToMapStartPoint.x) {
				closestToMapStartPoint.x = car.getPosition().x;
			}
			if (car.getPosition().y > furthestFromMapStartPoint.y) {
				furthestFromMapStartPoint.y = car.getPosition().y;
			}
			if (car.getPosition().y < closestToMapStartPoint.y) {
				closestToMapStartPoint.y = car.getPosition().y;
			}
		}

		Vector2f deltaDistance = new Vector2f();

		deltaDistance.x = furthestFromMapStartPoint.x - closestToMapStartPoint.x;
		deltaDistance.y = furthestFromMapStartPoint.y - closestToMapStartPoint.y;

		camera.setDeltaDistances(deltaDistance);
		camera.setClosestEdge(closestToMapStartPoint);
	}

	public void checkForKeyboardInput(GameContainer container, StateBasedGame game) throws SlickException {
		
		Input input = container.getInput();

		if (input.isKeyPressed(Input.KEY_R)) {
			returnToMenu(container, game);
		}

	}

	public void createCar(int nr, String id, int playerChoice) throws SlickException {

		Properties stats = Properties.values()[playerChoice];
		
		Vector2f startArea = new Vector2f();

		startArea.x = level.getStartCoordinates().x - level.getTileWidth()*4;
		startArea.y = level.getStartCoordinates().y - level.getTileHeight()*4;
		
		cars.add(new Car(stats, id, nr, startArea, level));
	}

	private void firstCamRelocation(Graphics g) {
		camera.zoom(g, camera.getZoom());// Crasher om verdien <=0
		g.translate(camera.getX(), camera.getY()); // Start of camera
	}
	
	private void secondCamRelocation(Graphics g){
		
		g.translate(-camera.getX(), -camera.getY()); // End of camera
		camera.zoom(g, 1 / camera.getZoom());
	}

	public void initVariables() {
		
		threePlayed = twoPlayed = onePlayed = 
		goPlayed = finalRoundSaid = 
		crowdFinishedPlayed = raceFinished = 
		raceStarted = countdownStarted = itmEqpd
		= isHiScrUpdtd = false;
		
		startGoSignal = makeNewMissile = true;
		
		screenWidth = Application.screenSize.width;
		screenHeight = Application.screenSize.height;
		
		highScores = new ArrayList<Result>();
		
		zoom = 1;
		biggest = 0;
	}

	public void setIP(String ip) {
		IP = ip + "/furyracers";
	}

	public float getZoom() {
		return zoom;
	}

	public boolean isFinalRoundSaid() {
		return finalRoundSaid;
	}
	
	public static Car getCar(int playerNr){
		
		if(playerNr < 1)
			return null;
		
		return cars.get(playerNr-1);
	}
	
	public int getID() {
		return 1;
	}
}