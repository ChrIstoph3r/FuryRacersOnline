package com.github.fredrikzkl.furyracers.network;

import com.github.fredrikzkl.furyracers.Application;

import javax.websocket.*;

import org.newdawn.slick.SlickException;

import java.io.IOException;

@ClientEndpoint
public class WebsocketClient {
    private GameSession gameSession = Application.getGameSession();

    @OnOpen
    public void onOpen(Session session) throws IOException, EncodeException {
        gameSession.onOpen(session);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException, EncodeException, SlickException {
    	gameSession.onMessage(session, message);
    }
}
