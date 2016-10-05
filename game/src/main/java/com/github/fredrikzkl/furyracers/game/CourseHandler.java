package com.github.fredrikzkl.furyracers.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

public class CourseHandler {
	
	private String directory;

	private int level;
	
	public Image subLayer, topLayer, minimap;
	public Music soundTrack;
	public TiledMap data;
	public String mapName;
	
	private File info;
	
	public CourseHandler(int level){
		this.level = level;
		String path = "games/furyracers/assets/";
		directory  = path + "Maps/course" + level + "/";
		importAssets();
		
		mapName = readTxtFile(info);
		
		if(subLayer != null && topLayer != null && soundTrack != null &&  data != null){
			System.out.println("Course" + level + " sucsessfully loaded!");
		}
		
	}

	private void importAssets() {
		try {
			subLayer = new Image(directory + "1.png");
			topLayer = new Image(directory + "2.png");
			soundTrack = new Music(directory + "soundTrack.ogg");
			data = new TiledMap(directory + "data.tmx");
			info = new File (directory + "info.txt");
			minimap = new Image(directory + "minimap.png");
		} catch (SlickException e) {
			
			String error = "Could not load level " + level + " properly! Jumping to next map..." + e;
			JOptionPane.showMessageDialog(null, error);
			subLayer = null;
			topLayer = null;
			soundTrack = null;
			data = null;
			info = null;
			minimap = null;
		}
		
	}

	private String readTxtFile(File file){
		
		String name = "";
		
		try {
			
			Scanner txtFile = new Scanner(file);
			
			while(txtFile.hasNext()){
				name += txtFile.next() + " ";
			}
			
			if(name.length() > 0){
				name = name.substring(0, name.length()-1);
			}
			
			txtFile.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		
		return name;
	}
}
