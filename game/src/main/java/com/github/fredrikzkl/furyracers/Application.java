package com.github.fredrikzkl.furyracers;

import com.github.fredrikzkl.furyracers.game.GameCore;
import com.github.fredrikzkl.furyracers.game.Level;
import com.github.fredrikzkl.furyracers.network.GameSession;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;

import java.awt.*;

public class Application {
    private static GameCore game;
    private static GameSession gameSession;
    
    public static final int HEIGHT = 1280;
    public static final int WIDTH = 720;
    
    public static double oY = 0, oX = 0;
    public static int dir;
  

    private Application() {
        game = new GameCore("Fury");
        createGameSession();
        startGame();
    }

    public static GameSession getGameSession() {
        return gameSession;
    }

    public static void fatalError(String error) {
        System.out.println(error);
        exit();
    }

    private static void exit() {
        System.out.println("Unable to recover; exiting...");
        System.exit(1);
    }

    public static void main(String[] args) {
        new Application();
    }

    private void createGameSession() {
        try {
            gameSession = new GameSession(game);
            gameSession.connect();
        } catch (Exception e) {
            fatalError("Could not start websocket server: " + e.getMessage());
        }
    }

    private void startGame() {
        /*GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");*/

        try {
            Display.setResizable(true);

            AppGameContainer app = new AppGameContainer(new ScalableGame(game, 1280, 720));
            app.setDisplayMode(HEIGHT, WIDTH, false);
            app.setTargetFrameRate(60);
            //app.setMouseGrabbed(true);
            app.setAlwaysRender(true);
            app.start();
        } catch (SlickException e) {
            fatalError("Could not start pong: " + e.getMessage());
        }
    }
}