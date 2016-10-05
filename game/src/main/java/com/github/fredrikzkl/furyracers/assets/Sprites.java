package com.github.fredrikzkl.furyracers.assets;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Sprites {
	
	public static Image 
	icons, cars, controllerQR, nextLevelBorder, resultsBoard, highscoresBoard, 
	mustangR, mustangB, mustangG, mustangY, 
	camaroR, camaroB, camaroG, camaroY, 
	vRaptorR, vRaptorB, vRaptorG, vRaptorY,
	city1, city2, hills, moon, stars1, stars2,
	missile;
	
	private static String path = "games/furyracers/assets/";
	
	public static void initialize(){
		
		try {
			
			
			
			icons = new Image( path + "Sprites/menu/menu_sheet.png");
			cars = new Image( path + "Sprites/menu/carSheet.png");
			
			nextLevelBorder = new Image( path + "Sprites/UI/nextLevelBorder.png");
			resultsBoard = new Image( path + "Sprites/UI/border.png");
			highscoresBoard = new Image( path + "Sprites/UI/border.png");
			
			mustangR = new Image( path + "Sprites/cars/fr_mustang_red.png");
			mustangG = new Image( path + "Sprites/cars/fr_mustang_green.png");
			mustangB = new Image( path + "Sprites/cars/fr_mustang_blue.png");
			mustangY = new Image( path + "Sprites/cars/fr_mustang_yellow.png");
			
			camaroR = new Image( path + "Sprites/cars/fr_camaro_red.png");
			camaroG = new Image( path + "Sprites/cars/fr_camaro_green.png");
			camaroB = new Image( path + "Sprites/cars/fr_camaro_blue.png");
			camaroY = new Image( path + "Sprites/cars/fr_camaro_yellow.png");
			
			vRaptorR = new Image( path + "Sprites/cars/fr_pickup_red.png");
			vRaptorG = new Image( path + "Sprites/cars/fr_pickup_green.png");
			vRaptorB = new Image( path + "Sprites/cars/fr_pickup_blue.png");
			vRaptorY = new Image( path + "Sprites/cars/fr_pickup_yellow.png");
			
			missile = new Image( path + "Sprites/missile/spr_missile.png");
			
			city1 = new Image(path + "Sprites/background/city1.png");
			city2 = new Image(path + "Sprites/background/city2.png");
			hills = new Image(path + "Sprites/background/hills.png");
			moon = new Image(path + "Sprites/background/moon.png");
			stars1 = new Image(path + "Sprites/background/stars1.png");
			stars2 = new Image(path + "Sprites/background/stars2.png");
			
		} catch (RuntimeException e) {
			System.out.println("Sprite/image ERROR: " + e);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadQRimage(){
		
		try {
			controllerQR = new Image( path + "QRcode/controllerQR.JPG");
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
	}
}
