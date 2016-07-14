package com.github.fredrikzkl.furyracers.assets;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Animations {
	
	/*int columns = 5;
	int rows = 5;
	
	Image[] exploImges = new Image[rows*columns];
	
	for(int i = 0; i < rows; i++)
		for(int j = 0; j < columns; j++)
			exploImges[j + columns*i] = explosionSheet.getSprite(i,j);*/
	
	private static String path = "games/furyracers/assets/";
	
	public static SpriteSheet 
	explosionSheet;
	
	public static Animation 
	explosion;
	
	public static int
	widthExplSprites = 64,
	hghtExplSprites = 64,
	explosionShowEachSprtFr = 200;
	
	public static void initialize(){
		
		try {
			explosionSheet = new SpriteSheet(path + "Sprites/missile/explosion17.png", widthExplSprites, hghtExplSprites);
			explosion = new Animation(explosionSheet, explosionShowEachSprtFr);
			explosion.setLooping(false);
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
		
	}

}
