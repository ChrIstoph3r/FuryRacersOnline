package com.github.fredrikzkl.furyracers.assets;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class Sounds {
	
	public static Music 
	menuMusic;
	
	public static Sound 
	car_select, select_car, spray,
	playerJoin, playerReady, deSelect, 
	peep, getReady,
	three, two, one, go, 
	finalRound, crowdFinish,
	checkpoint, lap, still,
	topSpeed, 
	scoreboardClose, scoreboardMove,
	explosion, missileLaunch, missilePickUp;
	
	public static void initialize(){
		
		try {
			String path = "games/furyracers/assets/Sound/";
			
			
			menuMusic = new Music(path + "menu.ogg");
			car_select = new Sound(path + "car_select.ogg");
			select_car = new Sound(path + "select_car.ogg");
			spray = new Sound(path + "spray.ogg");
			playerJoin = new Sound(path + "playerJoin.ogg");
			playerReady = new Sound(path + "ready.ogg");
			deSelect = new Sound(path + "deselect.ogg");
			peep = new Sound(path + "countdown.ogg");
			getReady = new Sound(path + "announcer/getReady.ogg");
			three = new Sound(path + "announcer/three.ogg");
			two = new Sound(path + "announcer/two.ogg");
			one = new Sound(path + "announcer/one.ogg");
			go = new Sound(path + "announcer/race!.ogg");
			finalRound = new Sound(path +"announcer/finalRound.ogg");
			crowdFinish = new Sound(path +"crowdFinish.ogg");
			checkpoint = new Sound(path +"checkpoint.ogg");
			lap = new Sound(path +"lap.ogg");
			still = new Sound(path + "carSounds/still.ogg");
			topSpeed = new Sound(path + "carSounds/speed.ogg");
			scoreboardClose = new Sound(path + "scoreBoard/closed.ogg");
			scoreboardMove = new Sound(path + "scoreBoard/move.ogg");
			explosion = new Sound(path + "Missile/missileExploding2.ogg");
			missileLaunch = new Sound(path + "Missile/missileLaunch.ogg");
			missilePickUp = new Sound(path + "Missile/missilePickUp.ogg");
			
		} catch (SlickException e) {
			System.out.println("Could not load sound file" + e);
			e.printStackTrace();
		}
	}

}
