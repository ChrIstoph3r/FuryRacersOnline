package com.github.fredrikzkl.furyracers.game;

import java.io.IOException;

import javax.websocket.EncodeException;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;

import com.github.fredrikzkl.furyracers.Box;
import com.github.fredrikzkl.furyracers.assets.Animations;
import com.github.fredrikzkl.furyracers.assets.Sounds;
import com.github.fredrikzkl.furyracers.car.Car;
import com.github.fredrikzkl.furyracers.car.CollisionBox;
import com.github.fredrikzkl.furyracers.car.Properties;
import com.github.fredrikzkl.furyracers.network.GameSession;

public class Item extends Movement implements Box{
	
	float centerOfRotationYOffset;
	boolean isLaunched;
	float[] itemBoxPoints;
	boolean drawExpl, explActive;
	
	Car eqpdToCar, targetedCar;
	CollisionBox colBox;
	String itemType;
	Vector2f dimensions;
	Polygon itemBox;

	public Item(Properties stats, Vector2f startCoords) {
		 
		super(stats, startCoords);
		
		dimensions = new Vector2f();
		
		itemType = stats.name;
		
		dimensions.x = stats.image.getWidth()*stats.size;
		dimensions.y = stats.image.getHeight()*stats.size/2;
		
		centerOfRotationYOffset = dimensions.y;
		
		colBox = new CollisionBox(this, 2, 2);
	}
	
	public void update(int deltaTime, boolean preventMovement){
		
		if(!isLaunched && eqpdToCar != null){
			
			movementDegrees = eqpdToCar.controlls.getMovementDegrees();
			position = new Vector2f(eqpdToCar.getPosition());
			centerOfRotationYOffset = eqpdToCar.getCenterOfRotationYOffset();
		}else if(eqpdToCar != null){
			
			accelerate = true;
			
			float thtaAnglTwrdsTargtCar = thtaAnglForVctr(vctrTwrdsCar(targetedCar));  
			
			movementDegrees = thtaAnglTwrdsTargtCar;
			
			centerOfRotationYOffset = dimensions.y;
			
			updateSpeed(deltaTime, preventMovement);
			rePosition(deltaTime);
		}
		
		if(drawExpl)
			updateExplosion(deltaTime);
		
		colBox.generatePoints();
	}
	
	
	public void render(Graphics g) {

		if( eqpdToCar != null)
			stats.image.setCenterOfRotation(0, eqpdToCar.getCenterOfRotationYOffset());
		else
			stats.image.setCenterOfRotation(0, dimensions.y);
		
		stats.image.setRotation(getMovementDegrees());
		
		if(drawExpl)
			drawExplosion();
		else 
			stats.image.draw(position.x, position.y, stats.size);
	}
	
	private void updateExplosion(int deltaTime){
		
		Animations.explosion.update(deltaTime);
		
		if(Animations.explosion.isStopped() && !explActive){
			Animations.explosion.restart();
			explActive = true;
			
		}else if(Animations.explosion.isStopped()){
			drawExpl = false;
			GameCore.setMakeNewMissile();
			resetMissile();
		}
	}
	
	public void playExplSound(){
		Sounds.explosion.play();
	}
	
	private void drawExplosion(){
		
		float explWidth = Animations.widthExplSprites*2;
		float explHeight = Animations.hghtExplSprites*2;
		
		Vector2f middlePointOfCar = targetedCar.getMiddlePoint(); 
		
		float xPos = middlePointOfCar.x - explWidth/2;
		float yPos = middlePointOfCar.y - explHeight/2;
	
		Animations.explosion.draw(xPos, yPos, explWidth, explHeight);
	}
	
	/*private void turnTwrdsTargetAngle(float targetAngle){
		
		System.out.println("movement " + movementDegrees + " target " + targetAngle);
		float diff = targetAngle - movementDegrees;
		
		if(diff < 0){
			turnLeft = true;
			turnRight = false;
		}else if(diff > 0){
			turnLeft = false;
			turnRight = true;
		}else{
			turnLeft = false;
			turnRight = false;
		}
	}*/
	
	private float thtaAnglForVctr(Vector2f vector){
		
		float thetaAngle = (float) vector.getTheta();
		
		return thetaAngle;
	}
	
	public Vector2f vctrTwrdsCar(Car car){
		
		Vector2f carPoint = new Vector2f(car.getMiddlePoint());
		
		Vector2f itemToCarVctr = new Vector2f(carPoint.sub(getMiddleOfItemPoint()));
		
		return itemToCarVctr;
	}
	
	public void equipTo(Car car) throws IOException, EncodeException{
		
		GameSession.itemEquipped(car.getId(), "");
		eqpdToCar = car;
		Sounds.missilePickUp.play();
		System.out.println("equipping");
	}
	
	public Vector2f getMiddleOfItemPoint(){
		Vector2f middlePoint = new Vector2f(position.x+dimensions.x/2, position.y+centerOfRotationYOffset+dimensions.y/2);
		return middlePoint;
	}
	
	public void launchMissile(Car targetedCar) throws IOException, EncodeException{
		
		if(eqpdToCar == null)return; 
		System.out.println("Missile Launch");
		GameSession.removeLaunchButton(targetedCar.getId());
		isLaunched = true;
		this.targetedCar = targetedCar;
		Sounds.missileLaunch.play();
	}
	
	public void resetMissile(){
		eqpdToCar = null;
		isLaunched = false;
		accelerate = false;
		turnLeft = false;
		turnRight = false;
		targetedCar  = null;
	}
	
	private boolean itmIntrscts(Car car){
		
		return car.getBoxShape().intersects(getItemBox());
	}
	
	public void checkIntersection(Car car) throws IOException, EncodeException{
		
		if(targetedCar == null && eqpdToCar == null){
			if(itmIntrscts(car)){
				System.out.println("Should be equipping");
				equipTo(car);
			}
		}else if(!drawExpl && eqpdToCar.getId() != car.getId()){
			if(itmIntrscts(car)){
				hitCar(car.getId());
			}
		}
	}
	
	public void hitCar(String carId){
		drawExpl = true;
		playExplSound();
		GameCore.slowDownCar(carId);
	}

	public Polygon getItemBox() {
		
		/*
		itemBoxPoints[0] = position.x;
		itemBoxPoints[1] = position.y + centerOfRotationYOffset;
		
		itemBoxPoints[2] = itemBoxPoints[0] + dimensions.x;
		itemBoxPoints[3] = itemBoxPoints[1];
		
		itemBoxPoints[4] = itemBoxPoints[2];
		itemBoxPoints[5] = itemBoxPoints[1] + dimensions.y;
		
		itemBoxPoints[6] = itemBoxPoints[0];
		itemBoxPoints[7] = itemBoxPoints[5];
		
		itemBox = new Polygon(itemBoxPoints);
		
		System.out.println(radDeg);
		
		if(eqpdToCar != null)
			itemBox = (Polygon) itemBox.transform(Transform.createRotateTransform(getRadDeg(), eqpdToCar.getPosition().x, eqpdToCar.getPosition().y + eqpdToCar.getCenterOfRotationYOffset()));
		else
			itemBox = (Polygon) itemBox.transform(Transform.createRotateTransform(getRadDeg(), position.x, position.y));
		return itemBox;
		*/
		
		return colBox.getBox();
		
	}

	public float getWidth() {
		return dimensions.y;
	}

	public float getLength() {
		return dimensions.x;
	}

	public float getCenterOfRotationYOffset() {
		return centerOfRotationYOffset;
	}
	
	public void setDrawExpl(){
		drawExpl = true;
	}
}
