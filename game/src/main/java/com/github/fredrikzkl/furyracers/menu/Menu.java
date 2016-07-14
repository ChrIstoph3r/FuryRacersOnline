package com.github.fredrikzkl.furyracers.menu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.websocket.EncodeException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.assets.Fonts;
import com.github.fredrikzkl.furyracers.assets.Sounds;
import com.github.fredrikzkl.furyracers.assets.Sprites;
import com.github.fredrikzkl.furyracers.game.CourseHandler;
import com.github.fredrikzkl.furyracers.game.GameCore;
import com.github.fredrikzkl.furyracers.game.Player;
import com.github.fredrikzkl.furyracers.network.GameSession;

public class Menu extends BasicGameState {

	private final int ICONSIZE = 128;
	
	private int 
	screenWidth, screenHeight,
	counter, secondsToNextGame,
	tick, mapSelected;
	
	private float 
	xPosCountdown, yPosCountdown, 
	QRmargin;

	private double 
	seconds, duration, last, 
	allReadyTimestamp;

	private boolean 
	getReadySaid;
	
	private String 
	controllerIP, version, countDown;

	private QRgenerator QR;
	private GameCore core;
	private CourseHandler course;
	public List<String> console;
	public List<Player> players;
	private ParallaxBackground background;

	public Menu(GameCore game) {
		core = game;
	}

	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		initVariables();
		QR.genQR(controllerIP);
		Sprites.loadQRimage();
		Application.setInMenu(true);
		GameSession.setGameState(getID());
	}

	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		setKeyboardCommands(container, game);
		setTime();
		readyCheck(container, game);
	}
	
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		
		background.draw();
		drawHeader();
		drawMenuInfo(g);
		drawBacksideInfo();
		drawPlayerIcons(container, g);
		drawQRcode(g);
		drawNextLevelInfo();
		drawIp(g);
	}
	
	public void initVariables() throws SlickException{
		
		QR = new QRgenerator();
		
		screenWidth = Application.screenSize.width;
		screenHeight = Application.screenSize.height;
		
		players = new ArrayList<Player>();
		console = new ArrayList<String>();
		console.add("Welcome to FuryRacers! Version: " + version);
		
		mapSelected = randomMap();
		course = new CourseHandler(mapSelected);
		
		QRmargin = screenWidth/32;
		
		xPosCountdown = screenWidth - screenHeight/10;
		yPosCountdown = screenHeight - screenHeight/15;
		allReadyTimestamp = 0;
		secondsToNextGame = 5;
		counter = secondsToNextGame;
		countDown = Integer.toString(counter);
		duration = last = System.nanoTime();
		
		background = new ParallaxBackground();
		Sounds.menuMusic.loop();
		Sounds.menuMusic.setVolume(0.4f);
		getReadySaid = false;
	}
	
	private void setKeyboardCommands(GameContainer container, StateBasedGame game) throws SlickException{
		
		Input input = container.getInput();
		if (input.isKeyPressed(Input.KEY_R)) {
			Application.closeConnection();
			Application.createGameSession();
			init(container, game);
		}
	}
	
	private void setTime(){
		
		tick++;
		duration = System.nanoTime() - last;
		seconds += duration / 1_000_000_000.0f;
		last = System.nanoTime();
	}

	private void drawQRcode(Graphics g){

		float yPosQR = QRmargin;
		float xPosQR  = screenWidth - Sprites.controllerQR.getWidth() - QRmargin;
		
		g.drawImage(Sprites.controllerQR, xPosQR, yPosQR);
		
		String infoTxt = "Scan for controller!";
		int margin = Fonts.consoleText.getHeight();
		float yPosInfoString = yPosQR + Sprites.controllerQR.getHeight() + margin;
		float xEndOfQR = xPosQR + Sprites.controllerQR.getWidth();
		float strLngthInfo = Fonts.consoleText.getWidth(infoTxt);
		
		float middleOfQr = (xPosQR + xEndOfQR)/2;
		float xPosInfoString =  middleOfQr - strLngthInfo/2;
		
		Fonts.consoleText.drawString(xPosInfoString, yPosInfoString, "Scan for controller!", Color.red);
	}
	
	private void drawIp(Graphics g) {
		
		String infoStr = "Write " + controllerIP + " in phone browser for controller!";
		float strLngthIp = Fonts.consoleText.getWidth(infoStr);
		float fontHeight = Fonts.consoleText.getHeight();
		
		float cornerRadius = 4f;
		float boxHeight = fontHeight;
		float boxWidth = strLngthIp;
		float boxTransparacy = 0.9f;
		
		float xPosBox = 0;
		float yPosBox = screenHeight - fontHeight;
		
		RoundedRectangle infoBox = new RoundedRectangle(xPosBox, yPosBox, boxWidth, boxHeight, cornerRadius);
		g.setColor(new Color(0f, 0f, 0f, boxTransparacy));
		g.fill(infoBox);
		
		float xPosIpString = xPosBox;
		float yPosIpString = yPosBox;
		 
		Fonts.consoleText.drawString(xPosIpString, yPosIpString, infoStr);
	}
	
	private void drawHeader(){
		
		String headerString = "Fury Racers";
		int stringLength = Fonts.header.getWidth(headerString);
		
		float xPos = screenWidth/2 - stringLength/2;
		Fonts.header.drawString(xPos, screenHeight/15, "Fury Racers", Fonts.headerColor);
	}

	private void drawPlayerIcons(GameContainer container, Graphics g) {

		float yPos = screenHeight / 4;
		int margin = 32; 
		int maxPlayers = 4;
		float playerIconsLength = maxPlayers * (ICONSIZE + margin);
		float startPlayerIconsX = screenWidth/2 - playerIconsLength/2;

		for (int i = 0; i < 4; i++) {
			float xPos = startPlayerIconsX + i * (ICONSIZE + margin);
			drawCarSprite(xPos, yPos, i, g);
		}
		
		drawBoarder(startPlayerIconsX, yPos, g);
	}
	
	private void drawCarSprite(float xPos, float yPos, int i, Graphics g){
		
		if (i < players.size()) {
			int carSelectNum = players.get(i).getCarSel();
			int colorSelectNum = players.get(i).getColorSel();
			
			g.drawImage( getCarImage(carSelectNum, colorSelectNum), xPos, yPos);
		}else{
			g.drawImage(getEmptyBlackImage(), xPos, yPos);
		}
	}
	
	private void drawBoarder(float startPlayerIcons, float yPos, Graphics g){
		
		int margin = 32; 
		
		for (int i = 0; i < 4; i++) {
			float xPos = startPlayerIcons + i * (ICONSIZE + margin);
			
			if (i < players.size() && players.get(i).isReady()) {
				g.drawImage(Sprites.icons.getSubImage(128, 0, 128, 128), xPos, yPos);
				continue;
			}
			g.drawImage(Sprites.icons.getSubImage(0, 0, 128, 128), xPos, yPos);
		}
	}
	
	private Image getCarImage(int carSelectNum, int colorSelectNum){
		
		Image carImage = Sprites.cars.getSubImage( carSelectNum * ICONSIZE, colorSelectNum * ICONSIZE, ICONSIZE, ICONSIZE); 
		
		return carImage;
	}
	
	private Image getEmptyBlackImage(){
		
		return Sprites.icons.getSubImage(256, 0, 128, 128);
	}

	private boolean allPlayersAreReady() {

		if(players.size() == 0)
			return false;
		
		for (Player player : players)
			if (!player.isReady())
				return false;
		 
		return true;
	}

	private void readyCheck(GameContainer container, StateBasedGame game) throws SlickException {

		if (allPlayersAreReady()) {

			if (allReadyTimestamp < 0) {
				allReadyTimestamp = seconds;
				printConsole("Everyone is ready, the game will begin shortly!");
				if(!getReadySaid){
					Sounds.getReady.play();
					getReadySaid = true;
				}
			}

			secondsToNextGame = (int) (allReadyTimestamp + counter - seconds);
			countDown = String.valueOf(secondsToNextGame);

			if (secondsToNextGame <= 0)
				startGame(container, game);

		} else {
			getReadySaid = false;
			allReadyTimestamp = -1;
			countDown = String.valueOf(counter);
		}
	}

	private void startGame(GameContainer container, StateBasedGame game) throws SlickException {
		Sounds.menuMusic.stop();
		core.init(container, game);
		core.mapInput(course, players);
		game.enterState(1);
	}

	public void updatePlayerList(ArrayList<Player> list) {
		players = list;
		Sounds.playerJoin.play();
	}

	public void printConsole(String text) {
		if (console.size() > 4)
			console.remove(0);
		console.add(text);
	}

	public void buttonDown(String data, int playerNr) throws IOException, EncodeException {
		switch (data) {
			
			case "1":
				selectButtonDown(playerNr);
				break;
			case "2":
				rightButtonDown(playerNr);
				break;
			case "3":
				leftButtonDown(playerNr);
				break;
		}
	}
	
	private void selectButtonDown(int playerNr) throws IOException, EncodeException{
		
		for (int i = 0; i < players.size(); i++) {
			if (playerNr == i + 1) {
				Player player = players.get(i);
				player.sendCarColorToController(); //Sometimes initial messages containing car model and color, . 
				player.sendCarModelToController(); //are never received by controller.
													//This provides added certainty.
				if (player.isCarChosen()) {			
					if (player.isReady()) {
						player.setReady(false);
						Sounds.deSelect.play();
					} else {
						detCarComboForSpriteSheet(player);
						player.setReady(true);
						Sounds.playerReady.play();
					}
				} else {
					player.setCarChosen(true);
					Sounds.select_car.play();
				}
			}
		}
	}
	
	private void rightButtonDown(int playerNr) throws IOException, EncodeException{
		
		for (int i = 0; i < players.size(); i++) {
			if (playerNr == i + 1) {
				Player player = players.get(i);
				if (!player.isReady()) {
					if (player.isCarChosen()) {
						player.selNextColor();;
						Sounds.spray.play();
					} else {
						player.selNextCar();
						Sounds.car_select.play();
					}
				}
			}
		}
	}
	
	private void leftButtonDown(int playerNr) throws IOException, EncodeException{
		
		for (int i = 0; i < players.size(); i++) {
			if (playerNr == i + 1) {
				Player player = players.get(i);
				if (!player.isReady()) {
					if (player.isCarChosen()) {
						player.selPrevColor();
						Sounds.spray.play();
					} else {
						player.selPrevCar();
						Sounds.car_select.play();
					}
				}
			}
		}
	}

	public void drawNextLevelInfo(){

		float scaleValue = (float) (screenWidth / (Sprites.nextLevelBorder.getWidth()*3.5));
		float realXvalue = Sprites.nextLevelBorder.getWidth()*scaleValue;
		float realYvalue = Sprites.nextLevelBorder.getHeight()*scaleValue;
		float margin = realYvalue/6;
		float posX = (screenWidth/2 - realXvalue/2);
		float posY = screenHeight - realYvalue - margin;
		
		Sprites.nextLevelBorder.draw(posX,posY,scaleValue);
		
		course.minimap.draw(posX,posY,realXvalue,realYvalue);
		
		//Draw nextLevelString
		int cnslTxtHeight = Fonts.consoleText.getHeight();
		int rglTxtHeight = Fonts.regularText.getHeight();
		int crsNmLngth = Fonts.regularText.getWidth(course.mapName);
		float mapNameYpos = screenWidth/2 - crsNmLngth/2;
		
 		Fonts.consoleText.drawString(posX, posY - cnslTxtHeight, "Next course: ");
		
		Fonts.regularText.drawString(mapNameYpos, posY + rglTxtHeight, course.mapName);
	}

	private void drawMenuInfo(Graphics g) {

		drawCountdown();
		
		drawBlinkingInfo();
		
	}
	
	private void drawCountdown(){
		
		Fonts.header.drawString(xPosCountdown, yPosCountdown, countDown);
	}
	
	private void drawBlinkingInfo(){
		
		String blinkingText;
		if (players.isEmpty())
			blinkingText = "Need at least 1 player to begin!";
		else
			blinkingText = "Ready up and the game will begin";
		if (allPlayersAreReady() && !players.isEmpty())
			blinkingText = "Game is starting";
		
		int stringLength = Fonts.regularText.getWidth(blinkingText);
		
		float xPos = screenWidth/2 - stringLength/2;
		
		if (Math.sin( tick/60) > 0) {
			Fonts.regularText.drawString( xPos, screenHeight / 2, blinkingText);
		}
	}

	public void drawBacksideInfo() {
		
		int fontHeight = Fonts.consoleText.getHeight();
		
		for (int i = console.size()+1; i > 1; i--) {
			Fonts.consoleText.drawString(0, screenHeight - (fontHeight * (console.size() - i + 3)),
					console.get(i - 2));// Draws the the bottom left info
		}
	}

	private void detCarComboForSpriteSheet(Player player) {
		int carTypeNr = player.getCarSel();
		int carColorNr = player.getColorSel();
		player.setCarCombo(carTypeNr * player.AMOUNT_OF_CARCOLORS + carColorNr);
	}
	
	private int randomMap(){
		
		String path = "games/furyracers/assets/";
		File dir = new File(path + "Maps/");
		int numberOfSubfolders = 0;
		File listDir[] = dir.listFiles();
		for (int i = 0; i < listDir.length; i++) {
		    if (listDir[i].isDirectory()) {
		    	numberOfSubfolders++;
		    }
		}
		return  1 + (int)(Math.random() * numberOfSubfolders);
	}
	
	public void setIP(String ip) {
		controllerIP = ip + "/furyracers";
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public int getID() {
		return 0;
	}
}
