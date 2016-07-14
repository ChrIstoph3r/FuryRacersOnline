package com.github.fredrikzkl.furyracers.car;

import org.newdawn.slick.Image;

import com.github.fredrikzkl.furyracers.assets.Sprites;

public enum Properties {
	
	mustangR("mustang", "medium", Sprites.mustangR,0.4f, 300,480,100, 105, 75,110,1), // 0
	mustangB("mustang", "medium", Sprites.mustangB,0.4f, 300,480,100, 105, 75,110,1), // 1
	mustangG("mustang", "medium", Sprites.mustangG,0.4f, 300,480,100, 105, 75,110,1), // 2
	mustangY("mustang", "medium", Sprites.mustangY,0.4f, 300,480,100, 105, 75,110,1), // 3
	
	CamaroR("camaro", "medium", Sprites.camaroR,0.45f, 300,480,100, 105, 75,110,1), // 4
	CamaroB("camaro", "medium", Sprites.camaroB,0.45f, 300,480,100, 105, 75,110,1), // 5
	CamaroG("camaro", "medium", Sprites.camaroG,0.45f, 300,480,100, 105, 75,110,1), // 6
	CamaroY("camaro", "medium", Sprites.camaroY,0.45f, 300,480,100, 105, 75,110,1), // 7
	
	VRaptorR("velociRapor", "large", Sprites.vRaptorR,0.65f, 350,480,70, 100, 80,110,1), //8
	VRaptorB("velociRapor", "large", Sprites.vRaptorB,0.65f, 350,480,70, 100, 80,110,1), //9
	VRaptorG("velociRapor", "large", Sprites.vRaptorG,0.65f, 350,480,70, 100, 80,110,1), //10
	VRaptorY("velociRapor", "large", Sprites.vRaptorY,0.65f, 350,480,70, 100, 80,110,1), //11
	
	Missile("HeatSeaker", "small", Sprites.missile, 1f, 350, 900, 250, 100, 80, 120, 1); //12
	
	public String name;
	public String type;
	public Image image;
	
	public float size;
	public float topSpeed;
	public float reverseTopSpeed;
	public float acceleration;
	public float reverseAcceleration;
	public float deAcceleration;
	public float handling;
	public float weight;
	
	Properties(String name, String type, Image image, float size, float reverseTopSpeed,float topSpeed,
			float acceleration, float reverseAcceleration, float deAcceleration, float handling, float weight){
		
		this.type = type;
		this.size = size;
		this.image = image;
		this.reverseTopSpeed = reverseTopSpeed;
		this.reverseAcceleration = reverseAcceleration;
		this.deAcceleration = deAcceleration;
		this.acceleration = acceleration;
		this.topSpeed = topSpeed;
		this.handling = handling;
		this.weight = weight;
		this.name = name;
	}
}
