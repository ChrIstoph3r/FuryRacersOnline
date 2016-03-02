package com.github.fredrikzkl.furyracers.game;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.naming.directory.DirContext;
import org.newdawn.slick.Color;
import javax.naming.ldap.Control;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.GameContainer;


public class Car implements Comparable<Car>,Runnable {

	
	int playerNr;

	private int laps, collisionSlowdownConstant = 4,  
			maxLaps = 3, passedChekpoints,time;
	
	private long startTime, nanoSecondsElapsed, 
	secondsElapsed,minutesElapsed, tenthsOfASecondElapsed,
	currentTime = 0;
	
	private boolean offRoad, raceStarted, finishedRace, startClock;
	private boolean paused;
	
	private float topSpeed, currentSpeed, radDeg, centerOfRotationYOffset;
	
	float[] collisionBoxPoints;
	
	private String tileType, timeElapsed;
	public String id;
	
	private Image sprite;
	private CarProperties stats;
	private Level level; 
	private Polygon collisionBox;
	Vector2f position;

	private Vector2f movementVector;
	private Controlls controlls;

	private float carLength;

	private float carWidth;

	private float centerOfRotationX;

	private float centerOfRotationY;

	private ArrayList<String> stoppingDirections = new ArrayList<String>();
	
	//-----------------------------//
	private Sound finalRound;
	private Sound crowdFinish;
	private Sound still;
	private Sound acceleratingSound;
	private Sound topSpeedSound;
	private Sound deAcceleratingSound;

	
	
	private boolean deAccelerating;
	
	public Car(CarProperties stats, String id, int playerNr, float startX, float startY, Level level){
		this.stats = stats;
		this.id = id;
		this.playerNr = playerNr;
		this.level = level;
		position = new Vector2f(startX,startY);
		controlls = new Controlls(this, stats);
		
		initVariables();
		getCarSprite();

		initSounds();
		
	}
	
	private void initVariables(){
		System.out.println("yes");
		time = 0;
		paused = true;
		passedChekpoints = 0;
		laps = 0;
		offRoad = false;
		raceStarted = false; 
		finishedRace = false; 
		startClock = false;
		topSpeed = stats.topSpeed;
		collisionBoxPoints  = new float[4];
		collisionBox = new Polygon(collisionBoxPoints);
		movementVector = new Vector2f();
		centerOfRotationYOffset = 64*stats.carSize;
	}
	
	private void getCarSprite(){
		
		try {
			sprite  = new Image(stats.imageFile);
		} catch (SlickException e) {
			System.out.println("Could not find image file " + stats.imageFile);
			e.printStackTrace();
		}
	}
	
	
	public void update(GameContainer container, StateBasedGame game, int deltaTime)throws SlickException{
		
		Input input = container.getInput();
		currentSpeed = controlls.getCurrentSpeed();
		controlls.reactToControlls(input, deltaTime,paused);
		rePositionCar(deltaTime);
		checkForEdgeOfMap();
		checkForCheckpoint();
		checkForCollision();
		checkForOffRoad();
		checkRaceTime();
		
		//sounds();
	}
	
	private void sounds() {
		if(currentSpeed < 10){
			topSpeedSound.stop();
			deAcceleratingSound.stop();
			acceleratingSound.stop();
			if(!still.playing())
			still.play();
			
		}else{
			if(currentSpeed > (stats.topSpeed*0.80)){
				if(!topSpeedSound.playing() && !acceleratingSound.playing())
				topSpeedSound.play();
				if(deAccelerating || !controlls.throttleKeyIsDown)
				topSpeedSound.stop();
			}else{
				if(controlls.throttleKeyIsDown && !deAccelerating){
					if(!acceleratingSound.playing() && !topSpeedSound.playing() && !deAcceleratingSound.playing())
					acceleratingSound.play();
				}else{
					if(!deAcceleratingSound.playing() && !topSpeedSound.playing() && !acceleratingSound.playing())
					deAcceleratingSound.play();
				}
			}
		}
		
	}

	public void rePositionCar(int deltaTime){
		
		radDeg = (float) Math.toRadians(controlls.getMovementDegrees());
		float currentSpeed = controlls.getCurrentSpeed();
		
		movementVector.x = (float) Math.cos(radDeg)*currentSpeed*deltaTime/1000;
		movementVector.y = (float) Math.sin(radDeg)*currentSpeed*deltaTime/1000;
		
		position.x += movementVector.x;
		position.y += movementVector.y;	
	}
	
	
	public void checkForEdgeOfMap(){
		
		float[] colBoxPoints = collisionBox.getPoints();
		
		for(int i = 0; i < colBoxPoints.length; i+=2){
			
			if(colBoxPoints[i] < 5 || colBoxPoints[i] > level.getMapWidthPixels()-5)
				position.x -= movementVector.x;
			
			if(colBoxPoints[i+1] < 5 || colBoxPoints[i+1] > level.getMapHeightPixels()-5)
				position.y -= movementVector.y;
		}
	}
	
	public void checkForCheckpoint(){

		int tilePosX = (int) (position.x/level.getTileWidth());
		int tilePosY = (int) (position.y/level.getTileHeight());
		
		tileType = level.getTileType(tilePosX, tilePosY, passedChekpoints);
		
		switch(tileType){
			case "checkpoint1": passedChekpoints++; break;
			case "checkpoint2": passedChekpoints++; break;
			case "checkpoint3": passedChekpoints++; break;
			case "lap": laps++; passedChekpoints = 0;	
		}	
		if(laps == 2){
			if(!GameCore.finalRoundSaid){
				finalRound.play();
				GameCore.finalRoundSaid = true;
			}
		}
		
		if(laps == 3){
			if(!GameCore.crowdFinishedPlayed){
				crowdFinish.play();
				GameCore.crowdFinishedPlayed = true;
			}
			finishedRace = true;
			stats.deAcceleration = 250;
			controlls.throttleKeyUp();
			controlls.leftKeyUp();
			controlls.rightKeyUp();
			setTime((int) (minutesElapsed+ secondsElapsed + tenthsOfASecondElapsed));
		}
	}
	
	public void checkForOffRoad(){

		float[] colBoxPoints = collisionBox.getPoints();
		float xPos;
		float yPos;
		int pointsNotOffRoad = 0;
		
		int amountOfPoints = colBoxPoints.length;
		
		for(int i = 0; i < amountOfPoints; i+=2){
			
			
			xPos = colBoxPoints[i];
			yPos = colBoxPoints[i+1];
			
			if(level.offRoad(xPos, yPos)){
				if(!offRoad){
					controlls.changeTopSpeed(0.5f);
					controlls.changeCurrentSpeed(0.5f);
					offRoad = true;
					break;
				}
			}else {
				pointsNotOffRoad++;
			}
		}
		
		if(offRoad && pointsNotOffRoad == amountOfPoints/2){
			controlls.changeTopSpeed(2);
			offRoad = false;
		}
	}
	
	public void checkForCollision(){

		ArrayList<String> directionsToStop;
		ArrayList<String> stopTurningDirections;
		
		float[] colBoxPoints = collisionBox.getPoints();
		float xPos;
		float yPos;
		int stoppedDirections = 0;
		Vector2f turningVector = getTurningDirectionVector();
		
		for(int i = 0; i < colBoxPoints.length; i+=2){
			
			xPos = colBoxPoints[i];
			yPos = colBoxPoints[i+1];
			
			if(level.collision(xPos, yPos) && stoppedDirections !=2){
				directionsToStop = level.whichDirectionToStop(xPos, yPos, movementVector.x, movementVector.y);
				stopTurningDirections = level.whichDirectionToStop(xPos, yPos,turningVector.y, turningVector.y);
				stopCarDirection(directionsToStop);
				resetCarRotation(stopTurningDirections);
				if(directionsToStop.size() == 2)
					break;
				stoppedDirections++;
			}
		}
	}
	
	private Vector2f getTurningDirectionVector(){
		
		String turningDirection = controlls.getTurningDirection();
		float deltaAngleChange = controlls.getDeltaAngleChange();
		float movementDegrees = controlls.getMovementDegrees();
		float angleBeforeCollision;
		Vector2f turningVector = new Vector2f();
		
		if(turningDirection == "positive"){
			angleBeforeCollision = movementDegrees - deltaAngleChange;
			double toRad = Math.toRadians(angleBeforeCollision);
			turningVector.x = (float) Math.cos(toRad-Math.PI/2);
			turningVector.x = (float) Math.sin(toRad-Math.PI/2);
			
			return turningVector;
		}
		
		if(turningDirection == "negative"){
			angleBeforeCollision = movementDegrees + deltaAngleChange;
			double toRad = Math.toRadians(angleBeforeCollision);
			turningVector.x = (float) Math.cos(toRad+Math.PI/2);
			turningVector.x = (float) Math.sin(toRad+Math.PI/2);
			
			return turningVector;
		}
		
		return turningVector;
	}
	
	private void resetCarRotation(ArrayList<String> stopTurningDirections){
		
		for(String directionToStop : stopTurningDirections){
			
			switch(directionToStop){
				case "positiveX": stopTurningInPositiveXdirection();break;
				case "negativeX": stopTurningInNegativeXdirection(); break;
				case "positiveY": stopTurningInPositiveYdirection();break;
				case "negativeY": stopTurningInNegativeYdirection();break;
			}
			
		
		}
	}
	
	public void stopCarDirection(ArrayList<String> directionsToStop){

		/*if(leftKeyIsDown){
			movementDegrees += deltaAngleChange*1.1;
		}else if(rightKeyIsDown){
			movementDegrees -= deltaAngleChange*1.1;
		}*/
		
		//deAccelerate(collisionSlowdownConstant);
		
		for(String directionToStop : directionsToStop){
			
			switch(directionToStop){
				case "positiveX": position.x -= movementVector.x; break;
				case "negativeX": position.x -= movementVector.x; break;
				case "positiveY": position.y -= movementVector.y; break;
				case "negativeY": position.y -= movementVector.y; break;
			}
			
		
		}
		stoppingDirections  = directionsToStop;
	}
	
	private void stopTurningInPositiveXdirection(){
		
		float deltaAngleChange = controlls.getDeltaAngleChange();
		float movementDegrees = controlls.getMovementDegrees();
		
	
			if(movementVector.y > 0)
				movementDegrees += deltaAngleChange;
			else
				movementDegrees -= deltaAngleChange;
		
		
		controlls.setMovementDegrees(movementDegrees);
	}
	
	private void stopTurningInNegativeXdirection(){
		
		float deltaAngleChange = controlls.getDeltaAngleChange();
		float movementDegrees = controlls.getMovementDegrees();
		
		if(movementVector.y < 0)
			movementDegrees += deltaAngleChange;
		else
			movementDegrees -= deltaAngleChange;
		
		controlls.setMovementDegrees(movementDegrees);
	}
	
	private void stopTurningInPositiveYdirection(){
		
		float deltaAngleChange = controlls.getDeltaAngleChange();
		float movementDegrees = controlls.getMovementDegrees();
		
		if(movementVector.x > 0)
			movementDegrees -= deltaAngleChange;
		else
			movementDegrees += deltaAngleChange;
		
		controlls.setMovementDegrees(movementDegrees);
	}
	
	private void stopTurningInNegativeYdirection(){
		
		float deltaAngleChange = controlls.getDeltaAngleChange();
		float movementDegrees = controlls.getMovementDegrees();
		
		if(movementVector.x < 0)
			movementDegrees -= deltaAngleChange*1.001;
		else
			movementDegrees += deltaAngleChange;
		
		controlls.setMovementDegrees(movementDegrees);
	}
	
	public void deAccelerate(int slowdownConstant){
		
		float deltaDeAcceleration = controlls.getDeltaDeAcceleration();
		deAccelerating = true;
		if(currentSpeed < -stats.deAcceleration) {
			
			currentSpeed += deltaDeAcceleration*slowdownConstant;
		}else if(currentSpeed > -stats.deAcceleration && currentSpeed < 0){
		
			currentSpeed = 0;
		}else if(currentSpeed > stats.deAcceleration) {
		
			currentSpeed -= deltaDeAcceleration*slowdownConstant;
		}else if(currentSpeed > 0 && currentSpeed < stats.deAcceleration){
		
			currentSpeed = 0;
		}
	}
	
	public void render(Graphics g) {
		
		float carRotation = controlls.getMovementDegrees(); 
		sprite.setCenterOfRotation(0, centerOfRotationYOffset);
		sprite.draw(position.x, position.y, stats.carSize);
		sprite.setRotation(carRotation);
		collisionBox = new Polygon();
		collisionBox.setClosed(true);
		generateCollisionBoxPoints();
	}
	
	public ArrayList<String> getDirectionsToStop(){
		
		return stoppingDirections;
	}
	
	public void generateCollisionBoxPoints(){
		
		carLength = 128*stats.carSize;
		carWidth = 64*stats.carSize;
		
		centerOfRotationX = position.x;
		centerOfRotationY = position.y + centerOfRotationYOffset;

		float backRightX = (float)(centerOfRotationX + Math.cos(radDeg-Math.PI/2)*carWidth/2),
			  backRightY = (float)(centerOfRotationY + Math.sin(radDeg-Math.PI/2)*carWidth/2);
		
		float backLeftX = (float)(centerOfRotationX+ Math.cos(radDeg+Math.PI/2)*carWidth/2),
			  backLeftY = (float)(centerOfRotationY + Math.sin(radDeg+Math.PI/2)*carWidth/2);
		
		colBoxPointsLeftOfCar(5);
		colBoxPointsTopOfCar(backLeftX, backLeftY, 3);
		
		colBoxPointsRightOfCar(backRightX, backRightY, 5);
		colBoxPointsBackOfCar(3);
	}
	
	public void colBoxPointsLeftOfCar(int amountOfPoints){
		
		float backLeftX = (float)(centerOfRotationX + Math.cos(radDeg+Math.PI/2)*carWidth/2),
			  backLeftY = (float)(centerOfRotationY + Math.sin(radDeg+Math.PI/2)*carWidth/2);
		
		float newPointX, newPointY; 
		
		for(int i = 0; i < amountOfPoints; i++){
			
			newPointX = (float) (backLeftX +  Math.cos(radDeg)*carLength*i/(amountOfPoints-1));
			newPointY = (float) (backLeftY +  Math.sin(radDeg)*carLength*i/(amountOfPoints-1));
			
			collisionBox.addPoint(newPointX, newPointY);
		}
	}
	
	public void colBoxPointsTopOfCar(float startPointX, float startPointY, int amountOfPoints){
		
		float frontLeftX = (float)(startPointX + Math.cos(radDeg)*carLength),
			  frontLeftY = (float)(startPointY + Math.sin(radDeg)*carLength);
		
		for(int i = 1; i < amountOfPoints; i++){
			
			float newPointX = (float) (frontLeftX +  Math.cos(radDeg-Math.PI/2)*carWidth*i/(amountOfPoints-1)),
				  newPointY = (float) (frontLeftY +  Math.sin(radDeg-Math.PI/2)*carWidth*i/(amountOfPoints-1));
			
			collisionBox.addPoint(newPointX, newPointY);
		}
	}
	
	public void colBoxPointsRightOfCar(float startPointX, float startPointY, int amountOfPoints){
		
		float frontRightX = (float)(startPointX + Math.cos(radDeg)*carLength);
		float frontRightY = (float)(startPointY + Math.sin(radDeg)*carLength);
		
		float newPointX, newPointY; 
		
		for(int i = 1; i > amountOfPoints; i--){
			
			newPointX = (float) (frontRightX +  Math.cos(radDeg+Math.PI)*carLength*i/(amountOfPoints-1));
			newPointY = (float) (frontRightY +  Math.sin(radDeg+Math.PI)*carLength*i/(amountOfPoints-1));
			
			collisionBox.addPoint(newPointX, newPointY);
		}
	}
	
	public void colBoxPointsBackOfCar(int amountOfPoints){
		
		float backRightX = (float)(centerOfRotationX + Math.cos(radDeg-Math.PI/2)*carWidth/2),
			  backRightY = (float)(centerOfRotationY + Math.sin(radDeg-Math.PI/2)*carWidth/2);
		
		for(int i = 0; i < amountOfPoints; i++){
			
			float newPointX = (float) (backRightX +  Math.cos(radDeg+Math.PI/2)*carWidth*i/(amountOfPoints-1)),
				  newPointY = (float) (backRightY +  Math.sin(radDeg+Math.PI/2)*carWidth*i/(amountOfPoints-1));
			
			collisionBox.addPoint(newPointX, newPointY);
		}
	}

	public void checkRaceTime(){
		
		if(startClock){
			startTime = System.nanoTime();
			startClock = false;
			raceStarted = true;
			paused = false;
		}
		
		if(startTime != 0 && !finishedRace){
			currentTime = System.nanoTime();
			nanoSecondsElapsed = currentTime - startTime;
			minutesElapsed = TimeUnit.NANOSECONDS.toMinutes(nanoSecondsElapsed);
			secondsElapsed = TimeUnit.NANOSECONDS.toSeconds(nanoSecondsElapsed) - 60*minutesElapsed;
			tenthsOfASecondElapsed = TimeUnit.NANOSECONDS.toMillis(nanoSecondsElapsed)/100 - TimeUnit.NANOSECONDS.toSeconds(nanoSecondsElapsed)*10;
			
			timeElapsed = minutesElapsed + ":" + secondsElapsed + ":" + tenthsOfASecondElapsed;
		}
	}
	
	public void buttonDown(String data){
		controlls.disableKeyboardInput();
        switch(data){
        	case "0": controlls.reverseKeyDown();break;
        	case "1": controlls.throttleKeyDown();break;
        	case "2": controlls.rightKeyDown();break;
        	case "3": controlls.leftKeyDown();
        }
	}
	
	public void buttonUp(String data){
		switch(data){
			case "0": controlls.reverseKeyUp();break;
			case "1": controlls.throttleKeyUp();break;
			case "2": controlls.rightKeyUp();break;
			case "3": controlls.leftKeyUp();
		}
	}
	
	
	
	public String getTimeElapsed(){
		return timeElapsed;
	}
	
	public int getPlayerNr() {
		return playerNr;
	}
	
	public Image getImage(){
		return sprite;
	}
	
	public void startClock(){
		startClock = true;
	}
	
	public int getLaps(){
		if(laps < maxLaps)
			return laps+1;
		return maxLaps;
	}
	
	public boolean finishedRace(){
		return finishedRace;
	}
	
	public Vector2f getPosition() {
		return position;
	}

	public void run(GameContainer container, StateBasedGame game, int deltaTime)throws SlickException{
		
		try{
			Input input = container.getInput();
			currentSpeed = controlls.getCurrentSpeed();
			controlls.reactToControlls(input, deltaTime, paused);
			rePositionCar(deltaTime);
			checkForEdgeOfMap();
			checkForCheckpoint();
			//checkForCollision();
			checkForOffRoad();
			checkRaceTime();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
	
	
	@Override
	public int compareTo(Car o) {
		return -(Integer.compare(this.getTime(), o.getTime()));
	}
	
	public void initSounds(){
		String path = "/Sound/carSounds/";
		String type = ".ogg";
		try {
			finalRound = new Sound("/Sound/announcer/finalRound" + type);
			crowdFinish = new Sound("/Sound/crowdFinish" + type);
			still = new Sound(path + "still" + type);
			acceleratingSound = new Sound(path + "accelerating" + type);
			topSpeedSound = new Sound(path + "topSpeed" + type);
			deAcceleratingSound = new Sound(path + "deAccelerate" + type);
		} catch (SlickException e) {
			System.out.println("ERROR! Could not load car sounds!");
		}
	}
}
