package com.github.fredrikzkl.furyracers.car;

import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;

import com.github.fredrikzkl.furyracers.Box;

public class CollisionBox {
	
	private Box boxedFigure;
	private float boxWidth;
	private float boxLength;
	private Vector2f backLeft;
	private Polygon collisionBox;
	private int amntPntsLngth, amntPntsWdth;
	
	
	public CollisionBox(Box boxedFigure, int amntPntsLngth, int amntPntsWdth){
		this.boxedFigure = boxedFigure;
		boxWidth = boxedFigure.getWidth();
		boxLength = boxedFigure.getLength();
		collisionBox = new Polygon();
		
		this.amntPntsLngth = amntPntsLngth;
		this.amntPntsWdth = amntPntsWdth;
	}
	
	public void generatePoints(){
		
		float centerOfRotationX = boxedFigure.getPosition().x;
		float centerOfRotationY  = boxedFigure.getPosition().y + boxedFigure.getCenterOfRotationYOffset();
		float radDeg = boxedFigure.getRadDeg();
		collisionBox = new Polygon();
		collisionBox.setClosed(true);

		float backLeftX = centerOfRotationX + (float)(Math.cos(radDeg+Math.PI/2)*boxWidth/2),
			  backLeftY = centerOfRotationY + (float)(Math.sin(radDeg+Math.PI/2)*boxWidth/2);
		
		backLeft = new Vector2f(backLeftX, backLeftY);
		
		Vector2f frontLeft = createPointsLeftOfCar(backLeft, radDeg);
		Vector2f frontRight = createPointsTopOfCar(frontLeft, radDeg);
		Vector2f backRight = createPointsRightOfCar(frontRight, radDeg);
		
		pointsBackOfCar(backRight, radDeg);
	}
	
	private Vector2f createPointsLeftOfCar(Vector2f backLeft, float radDeg){
		
		Vector2f newPoint = new Vector2f(0,0);
		
		for(int i = 0; i < amntPntsLngth; i++){
			
			newPoint.x = (float) (backLeft.x + Math.cos(radDeg)*boxLength*i / (amntPntsLngth-1));
			newPoint.y = (float) (backLeft.y + Math.sin(radDeg)*boxLength*i / (amntPntsLngth-1));
			
			collisionBox.addPoint(newPoint.x, newPoint.y);
		}
		
		return newPoint;
	}
	
	private Vector2f createPointsTopOfCar(Vector2f frontLeft, float radDeg){
		
		Vector2f newPoint = new Vector2f(0,0);
		
		for(int i = 1; i < amntPntsWdth; i++){
			
			 newPoint.x = (float) (frontLeft.x +  Math.cos(radDeg-Math.PI/2)*boxWidth*i / (amntPntsWdth-1));
			 newPoint.y = (float) (frontLeft.y +  Math.sin(radDeg-Math.PI/2)*boxWidth*i / (amntPntsWdth-1));
			
			collisionBox.addPoint(newPoint.x, newPoint.y);
		}
		
		return newPoint;
	}
	
	
	
	private Vector2f createPointsRightOfCar(Vector2f frontRight, float radDeg){
		
		Vector2f newPoint = new Vector2f(0,0); 
		
		for(int i = 1; i < amntPntsLngth; i++){
						
			newPoint.x = (float) (frontRight.x +  Math.cos(radDeg+Math.PI)*boxLength*i / (amntPntsLngth-1));
			newPoint.y = (float) (frontRight.y +  Math.sin(radDeg+Math.PI)*boxLength*i / (amntPntsLngth-1));
			
			collisionBox.addPoint(newPoint.x, newPoint.y);
		}
		
		return newPoint;
	}
	
	private void pointsBackOfCar(Vector2f backRight, float radDeg){
		
		Vector2f newPoint = new Vector2f(0,0);

		for(int i = 1; i < amntPntsWdth-1; i++){
			
			newPoint.x = (float) (backRight.x +  Math.cos(radDeg+Math.PI/2)*boxWidth*i / (amntPntsWdth-1));
			newPoint.y = (float) (backRight.y +  Math.sin(radDeg+Math.PI/2)*boxWidth*i / (amntPntsWdth-1));
				
			collisionBox.addPoint(newPoint.x, newPoint.y);
		}
	}
	
	public Vector2f getMiddleOfBoxPoint(){
		
		float radDeg = boxedFigure.getRadDeg();
		
		Vector2f middleLeftOfBox = new Vector2f(0,0);
		
		middleLeftOfBox.x = (float) (backLeft.x + Math.cos(radDeg)*boxLength/2);
		middleLeftOfBox.y = (float) (backLeft.y + Math.sin(radDeg)*boxLength/2);
		
		Vector2f middleOfBox = new Vector2f(0,0);
		
		middleOfBox.x = (float) (middleLeftOfBox.x + Math.cos(radDeg-Math.PI/2)*boxWidth/2);
		middleOfBox.y = (float) (middleLeftOfBox.y + Math.sin(radDeg-Math.PI/2)*boxWidth/2);
		
		return middleOfBox;
	}
	
	
	float[] getPoints(){
		
		return collisionBox.getPoints();
	}
	
	public Polygon getBox(){
		return collisionBox;
	}
	
	public void resetCollisionBox(){
		collisionBox = new Polygon();
	}

}
