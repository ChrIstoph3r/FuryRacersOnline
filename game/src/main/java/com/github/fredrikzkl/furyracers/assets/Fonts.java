package com.github.fredrikzkl.furyracers.assets;

import java.awt.Font;
import java.io.InputStream;

import javax.swing.JOptionPane;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

public class Fonts {
	
	private static float 
	headerSize = 28f,textSize = 17f,
	consoleSize = 15f;
	
	public static TrueTypeFont 
	infoFont, countdown, 
	scoreBoardHeader, scoreBoardText,
	ip,header, regularText, consoleText,
	ipTextMenu;
	
	public static Color headerColor = new Color(221, 0, 0);
	public static Font regularFont;
	
	
	public static void initialize(){
		regularFont = new Font("Verdana", Font.BOLD, 20);
		InputStream inputStream;
		String path = "games/furyracers/assets/";

		try {
			inputStream = ResourceLoader.getResourceAsStream(path + "Font/Orbitron-Regular.ttf");
			Font awtFont1 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			
			awtFont1 = awtFont1.deriveFont(60f); // set font size
			Font awtFont2 = awtFont1.deriveFont(24f);
			Font awtFont3 = awtFont1.deriveFont(consoleSize);
			Font awtFont4 = awtFont1.deriveFont(headerSize);
			Font awtFont5 = awtFont1.deriveFont(textSize);
			Font awtFont6 = awtFont1.deriveFont(50f);
			Font awtFont7 = awtFont1.deriveFont(24f);

			header = new TrueTypeFont(awtFont1, true);
			regularText = new TrueTypeFont(awtFont2, true);
			consoleText = new TrueTypeFont(awtFont3, true);
			scoreBoardHeader = new TrueTypeFont(awtFont4, true);
			scoreBoardText = new TrueTypeFont(awtFont5, true);
			countdown = new TrueTypeFont(awtFont6, true);
			infoFont = new TrueTypeFont(awtFont7, true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

}
