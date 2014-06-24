package me.dreamand.blog.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/chat")
public class ChatServer {
    
    private static final Logger LOGGER = 
            Logger.getLogger(ChatServer.class.getName());

    private static Set<Session> clients = 
            Collections.synchronizedSet(new HashSet<Session>());
    
    
    @OnOpen
    public void onOpen(Session session) {
    	clients.add(session);
        LOGGER.log(Level.INFO, "New connection with client: {0}", 
                session.getId());
    }
    
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        LOGGER.log(Level.INFO, "New message from Client [{0}]: {1}", 
                new Object[] {session.getId(), message});
        
        synchronized(clients){
            // Iterate over the connected sessions
            // and broadcast the received message
            for(Session client : clients){
              if (!client.equals(session)){
                client.getBasicRemote().sendText(session.getId()+"#$!"+message);
              }
            }
          }

        
        //return "Server received [" + message + "]";
    }
    
    @OnClose
    public void onClose(Session session) {
    	clients.remove(session);
        LOGGER.log(Level.INFO, "Close connection for client: {0}", 
                session.getId());
    }
    
    @OnError
    public void onError(Throwable exception, Session session) {
        LOGGER.log(Level.INFO, "Error for client: {0}", session.getId());
    }
}
