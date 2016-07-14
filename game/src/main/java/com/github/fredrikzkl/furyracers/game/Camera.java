package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.github.fredrikzkl.furyracers.Application;

public class Camera {
	
	int 
	screenWidth, screenHeight;
	
	private float 
	x, y, initialZoom, 
	zoom, biggest,
	mapHeightPixels, mapWidthPixels;
	
	float 
	edgeX, edgeY;
	
	private Vector2f 
	deltaDistances,  closestEdge, visibleMapArea;
	
	public Camera(float startX, float startY, Level level){
		
		x = startX;
		y = startY;
		
		initialZoom = 1f;
		
		screenHeight = Application.screenSize.height;
		screenWidth = Application.screenSize.width;
		
		mapHeightPixels = level.getMapHeightPixels();
		mapWidthPixels = level.getMapWidthPixels();
		
		visibleMapArea = new Vector2f();
		deltaDistances = new Vector2f();
		closestEdge = new Vector2f();
		
		edgeX = (float) (mapWidthPixels - screenWidth);
		edgeY = (float) (mapHeightPixels - screenHeight);
	}
	
	public void update(float posX, float posY) {
		visibleMapArea.x = screenWidth/zoom;
		visibleMapArea.y = screenHeight/zoom;
		
		x = -posX;
		y = -posY;
		
		if(posX < 0)
			x = 0;
		
		if(posY < 0)
			y = 0;
		
		if(visibleMapArea.x + posX > mapWidthPixels){
			x = visibleMapArea.x - mapWidthPixels;
		}
		
		if(visibleMapArea.y + posY > mapHeightPixels){
			y = visibleMapArea.y - mapHeightPixels;
		}
	}
	
	public void zoomLogic() {
		
		float deltaX = (float) (deltaDistances.x/(screenWidth/1.98)); //Høyere deleverdi gir mindre margin
		float deltaY = (float) (deltaDistances.y/(screenHeight/1.92)); 
		
		boolean zoomLim = true;
		
		
		if(deltaY > deltaX)
			biggest = deltaY;
		else
			biggest = deltaX;
		
		float temp = initialZoom/biggest;
		
		if(getVisibleMapArea().x - getX() >= mapWidthPixels && zoom > temp && getX() >= 0 || 
		   getVisibleMapArea().y - getY() >= mapHeightPixels && zoom > temp && getY() >= 0)
			zoomLim = false;
		
		if(zoomLim){
			if(temp > initialZoom){
				zoom = (float) initialZoom;
			}else{
				zoom = temp;
			}
		}
	}
	
	public void updateCamCoordinates(){
		
		float x = ( (deltaDistances.x/2 + closestEdge.x) - screenWidth/2 ) * zoom;
		float y = ( (deltaDistances.y/2 + closestEdge.y) - screenHeight/2 ) * zoom;
		
		update(x,y);
	}


	public void zoom(Graphics g, float zoom) {
		g.scale(zoom, zoom);
	}

	public Vector2f getVisibleMapArea() {
		return visibleMapArea;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}
	
	public float getZoom(){
		return zoom;
	}
	
	public void setDeltaDistances(Vector2f deltaDistances){
		
		this.deltaDistances = deltaDistances;
	}
	
	public void setClosestEdge(Vector2f closestEdge){
		
		this.closestEdge = closestEdge;
	}
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
}

