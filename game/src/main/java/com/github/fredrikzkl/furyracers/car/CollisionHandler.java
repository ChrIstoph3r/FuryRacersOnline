package com.github.fredrikzkl.furyracers.car;

import java.util.ArrayList;

import org.newdawn.slick.geom.Vector2f;

import com.github.fredrikzkl.furyracers.game.CourseHandler;
import com.github.fredrikzkl.furyracers.game.Level;

public class CollisionHandler {
	
	CourseHandler course = Level.course;
	
	private static boolean 
	isLeftTileLineCrossed, isRightTileLineCrossed, 
	isTopTileLineCrossed, isBottomTileLineCrossed;
	
	private static float 
	yOfTileEndX, yOfTileStartX, 
	xOfTileEndY, xOfTileStartY;
	
	private Car car;
	private Controlls controlls;
	
	public CollisionHandler(Car car){
		this.car = car;
		controlls = car.controlls;
	}
	
	void checkForCollision() {

		ArrayList<String> directionsToStop;
		ArrayList<String> stopTurningDirections;
		
		float[] colBoxPoints = car.getCollisionBox().getPoints();
		float xPos;
		float yPos;
		int stoppedDirections = 0;
		Vector2f turningVector = car.getTurningDirectionVector();
		Vector2f movementVector = car.getMovementVector();
		
		for(int i = 0; i < colBoxPoints.length; i+=2){
			
			xPos = colBoxPoints[i];
			yPos = colBoxPoints[i + 1];

			if (collisionTile(xPos, yPos) && stoppedDirections != 2) {
				directionsToStop = whichDirectionToStop(xPos, yPos, movementVector.x, movementVector.y);
				stopTurningDirections = whichDirectionToStop(xPos, yPos,turningVector.y, turningVector.y);
				stopCarDirection(directionsToStop, movementVector);
				resetCarRotation(stopTurningDirections, movementVector);

				if (directionsToStop.size() == 2)
					break;
				stoppedDirections++;
			}
		}
	}
	
	void stopCarDirection(ArrayList<String> directionsToStop, Vector2f movementVector){
		
		Vector2f position = car.getPosition();

		for (String directionToStop : directionsToStop) {

			switch (directionToStop) {
				case "positiveX":
					position.x -= movementVector.x;
					break;
				case "negativeX":
					position.x -= movementVector.x;
					break;
				case "positiveY":
					position.y -= movementVector.y;
					break;
				case "negativeY":
					position.y -= movementVector.y;
					break;
			}
			
		}
	}
	
	private void resetCarRotation(ArrayList<String> stopTurningDirections, Vector2f movementVector){
		
		for(String directionToStop : stopTurningDirections){
			
			switch(directionToStop){
				case "positiveX": stopTurningInPositiveXdirection(movementVector);break;
				case "negativeX": stopTurningInNegativeXdirection(movementVector); break;
				case "positiveY": stopTurningInPositiveYdirection(movementVector);break;
				case "negativeY": stopTurningInNegativeYdirection(movementVector);break;
			}
		}
	}
	
	private void stopTurningInPositiveXdirection(Vector2f movementVector){
		
		float deltaAngleChange = controlls.getDeltaAngleChange();
		float movementDegrees = controlls.getMovementDegrees();
		
	
			if(movementVector.y > 0)
				movementDegrees += deltaAngleChange;
			else
				movementDegrees -= deltaAngleChange;
		
		
		controlls.setMovementDegrees(movementDegrees);
	}
	
	private void stopTurningInNegativeXdirection(Vector2f movementVector){
		
		float deltaAngleChange = controlls.getDeltaAngleChange();
		float movementDegrees = controlls.getMovementDegrees();
		
		if(movementVector.y < 0)
			movementDegrees += deltaAngleChange;
		else
			movementDegrees -= deltaAngleChange;
		
		controlls.setMovementDegrees(movementDegrees);
	}
	
	private void stopTurningInPositiveYdirection(Vector2f movementVector){
		
		float deltaAngleChange = controlls.getDeltaAngleChange();
		float movementDegrees = controlls.getMovementDegrees();
		
		if(movementVector.x > 0)
			movementDegrees += deltaAngleChange;
		else
			movementDegrees -= deltaAngleChange;
		
		controlls.setMovementDegrees(movementDegrees);
	}
	
	private void stopTurningInNegativeYdirection(Vector2f movementVector){
		
		float deltaAngleChange = controlls.getDeltaAngleChange();
		float movementDegrees = controlls.getMovementDegrees();
		
		if(movementVector.x < 0)
			movementDegrees += deltaAngleChange*1.001;
		else
			movementDegrees -= deltaAngleChange;
		
		controlls.setMovementDegrees(movementDegrees);
	}
	
	
	 boolean collisionTile(float xPos, float yPos) {

		int tileX = (int) (xPos / Level.tileWidth), tileY = (int) (yPos / Level.tileHeight);
		
		int tileIDprops = course.data.getTileId(tileX, tileY, Level.propsLayer);

		boolean isColliding = course.data.getTileProperty(tileIDprops, "collision", "-1").equals("1");

		return isColliding;
	}

	private ArrayList<String> whichDirectionToStop(float xCarPos, float yCarPos, float xVector, float yVector) {

		ArrayList<String> stopCarMovement = new ArrayList<String>();
		
		if(xCarPos < 0 || yCarPos < 0)return stopCarMovement;

		int 
		tileX = (int) (xCarPos / Level.tileWidth), 
		tileY = (int) (yCarPos / Level.tileHeight);

		boolean 
		leftTileIsObstacle = isCollisionObstacle(tileX - 1, tileY),
		rightTileIsObstacle = isCollisionObstacle(tileX + 1, tileY),
		topTileIsObstacle = isCollisionObstacle(tileX, tileY - 1),
		bottomTileIsObstacle = isCollisionObstacle(tileX, tileY + 1);

		boolean 
		carMovingLeft = (xVector < 0), carMovingRight = (xVector > 0),
		carMovingUp = (yVector < 0), carMovingDown = (yVector > 0);

		int 
		tileStartX = tileX * Level.tileWidth, 
		tileStartY = tileY * Level.tileHeight,
	 
		tileEndX = tileStartX + Level.tileWidth - 1, 
		tileEndY = tileStartY + Level.tileHeight - 1;

		float 
		slope = yVector / xVector,
		constant = yCarPos - slope * xCarPos; // c = y - ax

		intersectionPointsOfLineWithTile(slope, constant, tileStartX, tileStartY, tileEndX, tileEndY);
		checkIntersectionsWithTile(tileStartX, tileStartY, tileEndX, tileEndY);

		if (isRightTileLineCrossed && carMovingLeft) {
			if (rightTileIsObstacle) {
				if (carMovingUp) {
					if (bottomTileIsObstacle) {
						stopCarMovement.add("negativeY");
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					} else {
						stopCarMovement.add("negativeY");
						return stopCarMovement;
					}
				} else if (carMovingDown) {
					if (topTileIsObstacle) {
						stopCarMovement.add("positiveY");
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					}else{
						stopCarMovement.add("positiveY");
						return stopCarMovement;
					}
				}
			}else{
				stopCarMovement.add("negativeX");
				return stopCarMovement;
			}
		}
		
		if(isLeftTileLineCrossed && carMovingRight){
			if(leftTileIsObstacle) {
				if(carMovingUp){
					if(bottomTileIsObstacle){
						stopCarMovement.add("negativeY");
						stopCarMovement.add("positiveX");
						return stopCarMovement;
					}else{
						stopCarMovement.add("negativeY");
						return stopCarMovement;
					}
				}else if(carMovingDown){
					if(topTileIsObstacle){
						stopCarMovement.add("positiveY");
						stopCarMovement.add("positiveX");
						return stopCarMovement;
					}
				}
			}else{
				stopCarMovement.add("positiveX");
				return stopCarMovement;
			}
		}
		
		if(isBottomTileLineCrossed && carMovingUp){
			if(bottomTileIsObstacle){
				if(carMovingLeft){
					if(rightTileIsObstacle){
						stopCarMovement.add("negativeY");
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					}else{
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					}
				}else if(carMovingRight){
					if(topTileIsObstacle){
						stopCarMovement.add("negativeY");
						stopCarMovement.add("positiveX");
						return stopCarMovement;
					}
				}
			}else{
				stopCarMovement.add("negativeY");
				return stopCarMovement;
			}
		}
		
		if(isTopTileLineCrossed && carMovingDown){
			if(topTileIsObstacle) {
				if(carMovingRight){
					if(rightTileIsObstacle){
						stopCarMovement.add("positiveY");
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					}else{
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					}
				}else if(carMovingLeft){
					if(topTileIsObstacle){
						stopCarMovement.add("positiveY");
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					}
				}
			}else{
				stopCarMovement.add("positiveY");
				return stopCarMovement;
			}
		}
		
		return stopCarMovement;
	}
	
	
	private void intersectionPointsOfLineWithTile(float slope, float constant, int tileStartX, int tileStartY, int tileEndX, int tileEndY){
		/*The line is a representation of the cars vector direction*/
		/*This method finds the intersection points between the line and the tile(The tile the car has landed on).*/
		
		yOfTileEndX = slope*tileEndX + constant; // When the line crosses the x-value of the tile's right side, yOfTileEndX is the lines y-value (y = ax + c)
		yOfTileStartX = slope*tileStartX + constant;
												
		xOfTileEndY = (tileEndY - constant)/slope; // x = (y-c)/a
		xOfTileStartY = (tileStartY - constant)/slope;
	}
	
	
	 private void checkIntersectionsWithTile(int tileStartX, int tileStartY,int tileEndX, int tileEndY){

		isLeftTileLineCrossed = isTileLineCrossed(tileStartY, tileEndY, yOfTileStartX);
		isRightTileLineCrossed = isTileLineCrossed(tileStartY, tileEndY, yOfTileEndX);
		isTopTileLineCrossed = isTileLineCrossed(tileStartX, tileEndX, xOfTileStartY);
		isBottomTileLineCrossed = isTileLineCrossed(tileStartX, tileEndX, xOfTileEndY);
	}

	 boolean isCollisionObstacle(int tileX, int tileY) {

		int tileId = course.data.getTileId(tileX, tileY, Level.propsLayer);
		return course.data.getTileProperty(tileId, "collision", "-1").equals("1");
	}

	static boolean isTileLineCrossed(float tileStart, float tileEnd, float carCrossedAt) {
		
		int errorMargin = 2;
		return (tileStart - errorMargin <= carCrossedAt && carCrossedAt <= tileEnd + errorMargin);
	}

}
