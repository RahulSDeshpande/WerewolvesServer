package com.boringpeople.werewolves;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;

public class Room {

    private static int _id = 0;
    public int Id;
    public String NickName;
    public IHall hall;
    public ArrayList<Player> players;
    private final Selector selector;

    public Room() throws IOException {
        Id = _id++;
        selector=Selector.open();
    }

    public void startGame() {
        assignRoles();
        play();
    }

    private void assignRoles() {

    }

    private void play() {

    }

    public void addNewPlayer(Session session) throws ClosedChannelException {
        session.channel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE,session);
    }
}
