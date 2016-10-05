package com.github.fredrikzkl.furyracers.car;

import org.newdawn.slick.geom.Vector2f;

import com.github.fredrikzkl.furyracers.game.Movement;


public class Controlls extends Movement{
	
	private int
	preventLeftLock, preventRightLock;
	
	private boolean 
	ignoreNextRight, ignoreNextLeft;	

	public Controlls(Properties stats, Vector2f startPos) {

		super(stats, startPos); 
		preventLeftLock = 0;
		preventRightLock = 0;
	}
	
	public String getTurningDirection(){
		
		/*if(turnLeft)
			return "positive";
		
		if(turnRight)
			return "negative";*/
		
		return "neutral";
	}

	public void throttleKeyDown() {
		accelerate = true;
	}
	

	public void rightKeyDown() {
		
		if(ignoreNextRight){
			ignoreNextRight = false;
			return;
		}
		
		ignoreNextLeft = false;
		turnRight = true;
		turnLeft = false;
	}

	public void rightKeyUp() {
		
		if(!turnRight && preventRightLock < 2){
			ignoreNextRight = true;
			preventRightLock++;
			return;
		}
		
		preventRightLock = 0;
		turnRight = false;
	}
	
	public void leftKeyDown() {
		
		if(ignoreNextLeft){
			ignoreNextLeft = false;
			return;
		}
		
		ignoreNextRight = false;
		turnLeft = true;
		turnRight = false;
	}

	public void leftKeyUp() {
		
		if(!turnLeft && preventLeftLock < 2){
			ignoreNextLeft = true;
			preventLeftLock++;
			return;
		}
		
		preventLeftLock = 0;
		turnLeft = false;
	}
	
	public void reverseKeyUp() {
		reverse = false;
	}

	public void throttleKeyUp() {
		accelerate = false;
	}
}
