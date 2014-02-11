package com.boringpeople.werewolves;

import com.boringpeople.werewolves.message.*;
import com.boringpeople.werewolves.util.MessageUtil;
import com.boringpeople.werewolves.util.SocketChannelUtil;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Room extends AbstractMessageProcessor implements IDispose {

    enum GameState {
        Waiting,
        Running,
    }

    private static int _id = 0;
    public int id;
    public String NickName;
    public IHall hall;
    public ArrayList<Session> sessions;

    private GameState gameState;

    public Room(IHall hall) throws IOException {
        super("Room_" + _id + " Timer");
        this.id = _id++;
        this.hall = hall;
        sessions = new ArrayList<>();
        gameState = GameState.Waiting;
    }

    @Override
    protected void onChannelReadable(SelectionKey key) throws IOException {
        byte[] data = SocketChannelUtil.readData((SocketChannel) key.channel());
        if (data != null && data.length > 0) {
            MessageType mt = MessageUtil.getMessageType(data);
            switch (mt) {
                case LeaveRoom:
                    if (gameState == GameState.Waiting) {
                        sessions.remove((Session) key.attachment());
                        key.cancel();
                        hall.playerLeaveRoom((Session) key.attachment());
                        if (sessions.isEmpty()) {
                            hall.roomDissolve(this);
                        }
                    } else {
                        ((Session) key.attachment()).scheduleMessage(new LeaveRoomMessage(-11,"Game is Runing."));
                    }
                    break;
            }
            try {
                System.out.println("[Room_" + id + "] New Message From :" + ((SocketChannel) key.channel()).getRemoteAddress() + " "
                        + new String(data));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    protected void onChannelWritable(SelectionKey key) throws IOException {
        Session session = (Session) key.attachment();
        session.sendMessage();
    }

    public void startGame() {
        gameState = GameState.Running;
        assignRoles();
        play();
    }

    private void assignRoles() {

    }

    private void play() {

    }

    public void addNewPlayer(Session session) throws ClosedChannelException {
        sessions.add(session);
        session.channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, session);
        JoinRoomMessage jrm = new JoinRoomMessage(this.id);
        jrm.description = "Welcome to Room " + id + ".";
        session.scheduleMessage(jrm);
    }

    @Override
    public void dispose() {
        stop();
        try {
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
