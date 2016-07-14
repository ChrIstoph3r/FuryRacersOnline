package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.geom.Vector2f;

import com.github.fredrikzkl.furyracers.car.Properties;

public abstract class Movement {
	
	private float 
	deltaAngleChange, deltaDeAcceleration, 
	topSpeed, acceleration, deAcceleration;
	
	float 
	currentSpeed, movementDegrees, radDeg = 0;
	
	public boolean accelerate, reverse, turnLeft, turnRight;
	
	public Properties stats;
	
	public Vector2f 
	movementVector, position; 
	
	public Movement(Properties stats, Vector2f startPos){
		
		this.stats = stats;
		topSpeed = stats.topSpeed;
		acceleration = stats.acceleration;
		deAcceleration = stats.deAcceleration;
		
		movementVector = new Vector2f();
		position = new Vector2f(startPos);
	}
	
	public void rePosition(int deltaTime) {

		radDeg = (float) Math.toRadians(movementDegrees);

		movementVector.x = (float) Math.cos(radDeg) * currentSpeed * deltaTime/1000;
		movementVector.y = (float) Math.sin(radDeg) * currentSpeed * deltaTime/1000;

		position.x += movementVector.x;
		position.y += movementVector.y;
		
	}
	
	public void updateSpeed(int deltaTime, boolean preventMovement) {
		if (!preventMovement) {

			deltaDeAcceleration = deAcceleration * deltaTime/1000;
			if (accelerate && currentSpeed < topSpeed) {

				currentSpeed += acceleration * deltaTime/1000;
			} else if (reverse && currentSpeed > -stats.reverseTopSpeed) {

				currentSpeed -= stats.reverseAcceleration * deltaTime/1000;
			} else if (currentSpeed < -deAcceleration) {

				currentSpeed += deltaDeAcceleration;
			} else if (currentSpeed > -deAcceleration && currentSpeed < 0) {

				currentSpeed = 0;
			} else if (currentSpeed > deAcceleration) {

				currentSpeed -= deltaDeAcceleration;
			} else if (currentSpeed > 0 && currentSpeed < deAcceleration) {

				currentSpeed = 0;
			} else {
				deltaDeAcceleration = 0;
			}
			
			if (currentSpeed != 0) {
				deltaAngleChange = 0;
				if (turnLeft) {
					deltaAngleChange = stats.handling * deltaTime/1000;
					movementDegrees -= deltaAngleChange;
				} else if (turnRight) {
					deltaAngleChange = stats.handling * deltaTime/1000;
					movementDegrees += deltaAngleChange;
				}
			}
			
			if(movementDegrees > 360) movementDegrees -= 360;
			else if(movementDegrees < 0) movementDegrees += 360;
		}
	}
	
	public float getCurrentSpeed() {

		return currentSpeed;
	}

	public float getDeltaDeAcceleration() {
		return deltaDeAcceleration;
	}
	
	public float getDeltaAngleChange(){
		return deltaAngleChange;
	}
	
	public float getMovementDegrees(){
		return movementDegrees;
	}
	
	public void setMovementDegrees(float movementDegrees){
		this.movementDegrees = movementDegrees;
	}
	
	public void changeDeAcceleration(float constant){
		
		deAcceleration *= constant;
	}
	
	public void resetDeAcceleration(){
		deAcceleration = stats.deAcceleration;
	}
	
	public void changeCurrentSpeed(float changeConstant){
		currentSpeed *= changeConstant;
	}
	
	public void changeTopSpeed(float changeConstant){
		topSpeed *= changeConstant;
	}
	
	public void resetTopSpeed(){
		
		topSpeed = stats.topSpeed;
	}

	public Vector2f getPosition(){
		return position;
	}
	
	public float getRadDeg(){
		return radDeg = (float) Math.toRadians(movementDegrees);
	}
}
