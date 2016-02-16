package com.github.fredrikzkl.furyracers;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class Menu extends BasicGameState{

	public Menu(int state){
		
	}
	
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		
	}

	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.drawString("Are you ready?!", 50, 50);
	}

	
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		Input mouse = container.getInput();
		if(mouse.isMouseButtonDown(0)){
			game.enterState(1);
		}
	}

	public int getID() {
		return 0;
	}
}