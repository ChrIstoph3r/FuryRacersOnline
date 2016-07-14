package com.github.fredrikzkl.furyracers.game;

import java.io.IOException;

import javax.websocket.EncodeException;

import org.newdawn.slick.Color;

import com.github.fredrikzkl.furyracers.car.Car;
import com.github.fredrikzkl.furyracers.network.GameSession;

public class Player implements Comparable<Player>{
	
	private final String 
	RED = "#ffe6e6", GREEN = "#d6f5d6",   
	BLUE = "#e6f5ff", YELLOW = "#ffffcc";
	
	public final int AMOUNT_OF_CARTYPES = 3;
	public final int AMOUNT_OF_CARCOLORS = 4;
	
	private String id;
	private String username;
	private Color rgbRED, rgbGREEN, rgbBLUE, rgbYELLOW;
	private Color carColor;
	private int playerNr;
	
	private boolean ready = false;
	private boolean carChosen = false;
	
	private int score = 0;
	private int carSel = 0;
	private int colorSel = 0;
	private int carCombo = 0;
	
	public Player(String id, int playerNr) throws IOException, EncodeException{
		
		this.id = id;
		this.playerNr = playerNr;
		
		rgbRED = new Color(252f, 0f, 0f, 0.4f);
		rgbBLUE = new Color(0f, 0f, 250f, 0.3f);
		rgbGREEN = new Color(0f,255f, 0f, 0.5f);
		rgbYELLOW = new Color(255f,255f,0f,0.5f);
		
		colorSel = playerNr-1;
		carSel = playerNr-1;
		
		sendCarColorToController();
		sendCarModelToController();
	}

	public boolean equals(Player o){
		return o.getId().equals(getId());
	}
	
	public int hashCode(){
		return getId().hashCode();
	}
	
	public void selNextCar() throws IOException, EncodeException {
		carSel++;
		detCarModel();
	}
	
	public void selPrevCar() throws IOException, EncodeException {
		carSel--;
		detCarModel();
	}
	
	private void detCarModel() throws IOException, EncodeException{
		
		if(carSel > AMOUNT_OF_CARTYPES-1){
			carSel = 0;
		}else if(carSel < 0){
			carSel = AMOUNT_OF_CARTYPES-1;
		}
		
		sendCarModelToController();
	}

	public void selNextColor() throws IOException, EncodeException  {
		colorSel++;
		detColor();
	}
	
	public void selPrevColor() throws IOException, EncodeException  {
		colorSel--;
		detColor();
	}
	
	private void detColor() throws IOException, EncodeException{
		
		if(colorSel > AMOUNT_OF_CARCOLORS-1){
			colorSel = 0;
		}else if(colorSel < 0){
			colorSel = AMOUNT_OF_CARCOLORS-1;
		}
		
		sendCarColorToController();
	}

	public void sendCarColorToController() throws IOException, EncodeException{
		
		switch(colorSel){
			
			case 0: 
				GameSession.carColorToController(id, RED);
				carColor = rgbRED;
				break;
			case 1:
				GameSession.carColorToController(id, BLUE);
				carColor = rgbBLUE;
				break;
			case 2:
				GameSession.carColorToController(id, GREEN);
				carColor = rgbGREEN;
				break;
			case 3:
				GameSession.carColorToController(id, YELLOW);
				carColor = rgbYELLOW;
				break;
		}
	}
	
	public void sendCarModelToController() throws IOException, EncodeException{
		
		switch(carSel){
			
			case 0: 
				GameSession.carModelToController(id, "Mustang");
				break;
			case 1:
				GameSession.carModelToController(id, "Camaro");
				break;
			case 2:
				GameSession.carModelToController(id, "VelociRaptor");
				break;
		}
	}
	
	@Override
	public int compareTo(Player o) {
		return Integer.compare(this.getScore(), o.getScore());
	}
	public void buttonDown(String data) throws IOException, EncodeException {
		
		switch (data) {
		case "0":
			getCar().controlls.reverseKeyDown();
			break;
		case "1":
			getCar().controlls.throttleKeyDown();
			break;
		case "2":
			getCar().controlls.rightKeyDown();
			break;
		case "3":
			getCar().controlls.leftKeyDown();
			break;
		case "4":
			GameCore.activateMissile(id);
			System.out.println("Launch(Player.buttonDown)");
			break;
		}
	}

	public void buttonUp(String data) {
		switch (data) {
		case "0":
			getCar().controlls.reverseKeyUp();
			break;
		case "1":
			getCar().controlls.throttleKeyUp();
			break;
		case "2":
			getCar().controlls.rightKeyUp();
			break;
		case "3":
			getCar().controlls.leftKeyUp();
		}
	}
	
	Color getCarColor(){
		return carColor;
	}
	
	public boolean isCarChosen() {
		return carChosen;
	}
	
	public void rumbleController(boolean rumbleController) {
		
		try {
			if(rumbleController){
				GameSession.rumbleControllerOn(id);
			}else{
				GameSession.rumbleControllerOff(id);
			}
		} catch (IOException | EncodeException e) {
			e.printStackTrace();
		} 
	}
	
	public void removeLaunchButton(){
		try {
			GameSession.removeLaunchButton(id);
		} catch (IOException | EncodeException e) {
			e.printStackTrace();
		} 
	}

	public void setCarChosen(boolean carChosen) {
		this.carChosen = carChosen;
	}
	
	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public int getCarComboNr() {
		return carCombo;
	}

	public void setCarCombo(int comboNr) {
		carCombo = comboNr;
	}

	public int getCarSel() {
		return carSel;
	}

	public int getColorSel() {
		return colorSel;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getId() {
		return id;
	}

	public int getScore() {
		return score;
	}

	public int getPlayerNr() {
		return playerNr;
	}
	
	public String toString(){
		return "Player " + getPlayerNr() + " ID:'" + getId() +"'";
	}
	
	public Car getCar(){
		return GameCore.cars.get(playerNr-1);
	}
}
