package com.github.fredrikzkl.furyracers.car;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.websocket.EncodeException;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.GameContainer;

import com.github.fredrikzkl.furyracers.Box;
import com.github.fredrikzkl.furyracers.assets.Sounds;
import com.github.fredrikzkl.furyracers.game.GameCore;
import com.github.fredrikzkl.furyracers.game.Level;
import com.github.fredrikzkl.furyracers.network.GameSession;

public class Car implements Comparable<Car>, Box {
	
	private static final int
	maxLaps = GameCore.maxLaps, fiveSecsInMillis = 5000, twoSecsInMillis = 2000;
	
	private int 
	playerNr, laps, passedChekpoints, 
	time, originalCarWidth, originalCarLength,
	secsAfctedBySlowDown;
	
	private long 
	startTime, nanoSecondsElapsed, 
	secondsElapsed, minutesElapsed, 
	tenthsOfASecondElapsed, currentTime,
	totalTenthsOfSeconds;
	
	private boolean 
	offRoad, isRaceFinished, 
	startClock, preventMovement,
	explSlowDown;
	
	private float 
	currentSpeed, centerOfRotationYOffset,
	carLength, carWidth, centerOfRotationY;
	
	float[] collisionBoxPoints;

	private String 
	tileType, timeElapsed, 
	id, username;
	
	private Vector2f 
	startPos;

	private ArrayList<String> 
	stoppingDirections;
	
	private Sound topSpeed;
	
	public boolean deAcceleratingSoundPlayed = false;
	
	private Properties stats;
	private Level level;
	private CollisionBox colBox;
	private CollisionHandler collision;
	public Controlls controlls;

	public Car(Properties stats, String id, int playerNr, Vector2f startArea, Level level) {
		
		originalCarLength = stats.image.getWidth();
		originalCarWidth = stats.image.getHeight()/2;
		
		carLength = originalCarLength * stats.size;
		carWidth = originalCarWidth * stats.size;
		
		centerOfRotationYOffset = stats.image.getHeight()/2 * stats.size;
		
		this.stats = stats;
		this.id = id;
		this.playerNr = playerNr;
		this.level = level;
		
		initVariables();
		detStartPos(startArea);
		
		controlls = new Controlls(stats, startPos);
		collision = new CollisionHandler(this);
		colBox = new CollisionBox(this, 5, 3);
		
		try {
			topSpeed= new Sound("games/furyracers/assets/Sound/carSounds/speed.ogg");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	private void detStartPos(Vector2f carStartPos){
		
		Car previousCar = GameCore.getCar(playerNr-1);
		float spaceBetweenCars = originalCarWidth/4;
		carStartPos.x -= carLength;

		if(previousCar != null){
			float prevCarStartY = previousCar.getStartPos().y;
			float prevCarEndY = prevCarStartY + previousCar.getWidth();
			carStartPos.y = prevCarEndY + spaceBetweenCars;
		}
		
		startPos = new Vector2f(carStartPos);
	}

	private void initVariables() {
		time = passedChekpoints = 
		laps = secsAfctedBySlowDown = 0;
		currentTime = 0;
		preventMovement = true;
		offRoad = false;
		username = "";
		isRaceFinished = false;
		startClock = false;
		stoppingDirections = new ArrayList<String>();
	}

	public void update(GameContainer container, StateBasedGame game, int deltaTime) throws SlickException, IOException, EncodeException {

		if(explSlowDown)
			checkIfStillSlowDown(deltaTime);
			
		controlls.updateSpeed(deltaTime, preventMovement);
		controlls.rePosition(deltaTime);
		checkForEdgeOfMap();
		checkForCheckpoint();
		collision.checkForCollision();
		checkForOffRoad();
		checkRaceTime();
		carSounds();
	}

	private void checkIfStillSlowDown(int deltaTime){
		
		secsAfctedBySlowDown += deltaTime;
		
		if(secsAfctedBySlowDown < twoSecsInMillis){
			controlls.changeCurrentSpeed(0);
		}
		
		if(secsAfctedBySlowDown < fiveSecsInMillis){
			explSlowDown = false;
			secsAfctedBySlowDown = 0;
			controlls.changeTopSpeed(2);
		}
	}
	
	public void checkForEdgeOfMap() {

		float[] 
		colBoxPoints = colBox.getPoints();
		int 
		safetyMargin = 7,
		startOfMapX = safetyMargin, 
		startOfMapY = safetyMargin;

		for (int i = 0; i < colBoxPoints.length; i += 2) {

			if (colBoxPoints[i] < startOfMapX || colBoxPoints[i] > level.getMapWidthPixels() - startOfMapX)
				controlls.position.x -= controlls.movementVector.x;

			if (colBoxPoints[i + 1] < startOfMapY || colBoxPoints[i + 1] > level.getMapHeightPixels() - startOfMapY)
				controlls.position.y -= controlls.movementVector.y;
		}
	}
	
	public String getId(){
		return id;
	}

	public void checkForCheckpoint() {

		int tilePosX = (int) (controlls.position.x / level.getTileWidth());
		int tilePosY = (int) (controlls.position.y / level.getTileHeight());

		tileType = level.getTileType(tilePosX, tilePosY, passedChekpoints);
		
		switch (tileType) {
			case "checkpoint1":
				checkpoint();
				break;
			case "checkpoint2":
				checkpoint();
				break;
			case "checkpoint3":
				checkpoint();
				break;
			case "lap":
				lap();
		}
		
		if (laps == maxLaps-1) 
			finalLap();
		
		else if (laps == maxLaps) 
			finishedRace();
	}
	
	private void finalLap(){
		
		if (!GameCore.finalRoundSaid) {
			Sounds.finalRound.play();
			GameCore.finalRoundSaid = true;
		}
	}
	
	private void finishedRace(){
		
		if (!GameCore.crowdFinishedPlayed) {
			Sounds.crowdFinish.play();
			GameCore.crowdFinishedPlayed = true;
		}
		
		isRaceFinished = true;
		controlls.throttleKeyUp();
		controlls.leftKeyUp();
		controlls.rightKeyUp();
		controlls.changeDeAcceleration(1.04f);
		setTime((int) totalTenthsOfSeconds);
	}
	
	private void lap(){
		
		laps++;
		passedChekpoints = 0;
		
		if(!Sounds.lap.playing() && laps != 3)
			Sounds.lap.play();
	}
	
	private void checkpoint(){
		
		passedChekpoints++;
		
		if (!Sounds.checkpoint.playing())
			Sounds.checkpoint.play();
	}

	public void checkForOffRoad() {

		float[] 
		colBoxPoints = colBox.getPoints();
		
		float 
		xPos, yPos;
		
		int 
		pointsNotOffRoad = 0,
		amountOfXpointsAndYpoints = colBoxPoints.length;
		
		for(int i = 0; i < amountOfXpointsAndYpoints; i+=2){
			
			xPos = colBoxPoints[i];
			yPos = colBoxPoints[i + 1];

			if (level.offRoad(xPos, yPos)) {
				if (!offRoad) {
					controlls.changeTopSpeed(0.5f);
					controlls.changeCurrentSpeed(0.5f);
					offRoad = true;
					rumbleController(true);
					break;
				}
			} else {
				pointsNotOffRoad++;
			}
		}
		
		if(offRoad && pointsNotOffRoad == amountOfXpointsAndYpoints/2){
			controlls.changeTopSpeed(2);
			offRoad = false;
			rumbleController(false);
		}
	}
	
	public void rumbleController(boolean rumbleController) {
		
		try {
			if(rumbleController){
				GameSession.rumbleControllerOn(id);
			}else{
				GameSession.rumbleControllerOff(id);
			}
		}catch(IOException | EncodeException e) {
			e.printStackTrace();
		} 
	}
	
	Vector2f getTurningDirectionVector(){
		
		String turningDirection = controlls.getTurningDirection();
		float deltaAngleChange = controlls.getDeltaAngleChange();
		float degreesRotated = controlls.getMovementDegrees();
		float angleBeforeCollision;
		Vector2f turningVector = new Vector2f();
		
		if(turningDirection == "positive"){
			angleBeforeCollision = degreesRotated - deltaAngleChange;
			double toRad = Math.toRadians(angleBeforeCollision);
			turningVector.x = (float) Math.cos(toRad+Math.PI/2);
			turningVector.y = (float) Math.sin(toRad+Math.PI/2);
			
			return turningVector;
		}
		
		if(turningDirection == "negative"){
			angleBeforeCollision = degreesRotated + deltaAngleChange;
			double toRad = Math.toRadians(angleBeforeCollision);
			turningVector.x = (float) Math.cos(toRad-Math.PI/2);
			turningVector.y = (float) Math.sin(toRad-Math.PI/2);
			
			return turningVector;
		}
		
		return turningVector;
	}

	public void render(Graphics g) {

		float carRotation = controlls.getMovementDegrees();
		stats.image.setCenterOfRotation(0, centerOfRotationYOffset);
		stats.image.setRotation(carRotation);
		stats.image.draw(controlls.position.x, controlls.position.y, stats.size);
		colBox.generatePoints();
	}
	
	public ArrayList<String> getDirectionsToStop(){
		
		return stoppingDirections;
	}
	
	private void checkRaceTime() {

		if (startClock) {
			startTime = System.nanoTime();
			startClock = false;
			preventMovement = false;
		}

		if (startTime != 0 && !isRaceFinished) {
			currentTime = System.nanoTime();
			nanoSecondsElapsed = currentTime - startTime;
			minutesElapsed = TimeUnit.NANOSECONDS.toMinutes(nanoSecondsElapsed);
			secondsElapsed = TimeUnit.NANOSECONDS.toSeconds(nanoSecondsElapsed) - 60*minutesElapsed;
			totalTenthsOfSeconds = TimeUnit.NANOSECONDS.toMillis(nanoSecondsElapsed)/100;
			tenthsOfASecondElapsed = totalTenthsOfSeconds
					- TimeUnit.NANOSECONDS.toSeconds(nanoSecondsElapsed)*10;

			timeElapsed = minutesElapsed + ":" + secondsElapsed + ":" + tenthsOfASecondElapsed;
		}
	}

	public String getTimeElapsed() {
		return timeElapsed;
	}

	public int getPlayerNr() {
		return playerNr;
	}

	public Image getImage() {
		return stats.image;
	}

	public void startClock() {
		startClock = true;
	}

	public int getLaps() {
		if (laps < maxLaps)
			return laps + 1;
		return maxLaps;
	}

	public boolean isRaceFinished() {
		return isRaceFinished;
	}

	public Vector2f getPosition() {
		return controlls.getPosition();
	}

	public int getTime() {
		return time;
	}

	public void setTime(int totalTime) {
		this.time = totalTime;
	}

	@Override
	public int compareTo(Car o) {
		return -(Integer.compare(getTime(), o.getTime()));
	}
	
	
	private void carSounds() {

		if(currentSpeed < 1 && !topSpeed.playing()){
			if(!Sounds.still.playing())
				Sounds.still.play();
		}
		if(controlls.accelerate){
			if(!topSpeed.playing()){
				Sounds.still.stop();
				topSpeed.play();
				deAcceleratingSoundPlayed = false;
			}
		}else{
			topSpeed.stop();
			if(!Sounds.still.playing())
				Sounds.still.play();
		}
	}
	
	public void slowDown(){
		controlls.changeCurrentSpeed(0);
		controlls.changeTopSpeed(0.5f);
		rumbleController(true);
		explSlowDown = true;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getUsername(){
		return username;
	}
	
	public Vector2f getMovementVector(){
		return controlls.movementVector;
	}
	
	public Vector2f getStartPos(){
		return startPos;
	}
	
	public float getCenterOfRotationYOffset(){
		return centerOfRotationYOffset;
	}
	
	float getCenterOfRotationY(){
		return centerOfRotationY;
	}
	
	public CollisionBox getCollisionBox(){
		return colBox;
	}
	
	public Polygon getBoxShape(){
		return colBox.getBox();
	}
	
	public void setOffroad(boolean isOffroad){
		offRoad = isOffroad;
	}

	public float getWidth() {
		
		return carWidth;
	}

	public float getLength() {
		return carLength;
	}
	
	public Vector2f getMiddlePoint(){
		return colBox.getMiddleOfBoxPoint();
	}

	@Override
	public float getRadDeg() {
		return controlls.getRadDeg();
	}
}
