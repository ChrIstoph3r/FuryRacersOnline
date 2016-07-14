package com.github.fredrikzkl.furyracers.car;

import org.newdawn.slick.geom.Vector2f;

import com.github.fredrikzkl.furyracers.game.Movement;


public class Controlls extends Movement{

	private int
	stopLockLeft = 0, stopLockRight = 0;
	
	boolean 
	ignoreNextRight, ignoreNextLeft;

	public Controlls(Properties stats, Vector2f startPos) {

		super(stats, startPos);
		stopLockLeft = 0;
		stopLockRight = 0; 
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
		reverseKeyUp();
	}

	public void rightKeyDown() {
		if(!ignoreNextRight){
			turnRight = true;
		}
		ignoreNextRight = false;
	}

	public void rightKeyUp() {
		
		if(!turnRight){
			stopLockRight++;
			if(stopLockRight < 1){
				
				ignoreNextRight = true;
			}else{
				stopLockRight = 0;
			}
		}else{
			turnRight = false;
			ignoreNextRight = false;
		}
	}

	public void reverseKeyDown() {
		reverse = true;
		throttleKeyUp();
	}
	
	public void leftKeyDown() {
		if(!ignoreNextLeft){
			turnLeft = true;
		}
		ignoreNextLeft = false;
	}

	public void leftKeyUp() {
		
		
		if(!turnLeft){
			stopLockLeft++;
			if(stopLockLeft < 1){
				ignoreNextLeft = true;
			}else{
				stopLockLeft = 0;
			}
		}else{
			turnLeft = false;
			ignoreNextLeft = false;
		}
	}

	public void reverseKeyUp() {
		reverse = false;
	}

	public void throttleKeyUp() {
		accelerate = false;
	}
}
