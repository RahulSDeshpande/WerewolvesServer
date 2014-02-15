package com.boringpeople.werewolf;

import com.boringpeople.werewolf.message.*;
import com.boringpeople.werewolf.util.MessageUtil;
import com.boringpeople.werewolf.util.SocketChannelUtil;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Room extends AbstractMessageProcessor implements IDispose {

    private static int _id = 0;
    public int id;
    public String NickName;
    public IHall hall;
    public ArrayList<Session> sessions;

    private final int playerCount;
    private GameState gameState;

    public Room(IHall hall) throws IOException {
        super("Room_" + _id + " Timer");
        this.id = _id++;
        this.hall = hall;
        this.playerCount = 8;
        sessions = new ArrayList<>();
        gameState = GameState.Waiting;
    }

    @Override
    protected void onChannelReadable(SelectionKey key) throws IOException {
        byte[] data = SocketChannelUtil.readData((SocketChannel) key.channel());
        if (data != null && data.length > 0) {
            MessageType mt = MessageUtil.getMessageType(data);
            Session session = (Session) key.attachment();
            switch (mt) {
                case LeaveRoom:
                    if (gameState == GameState.Waiting) {
                        sessions.remove(session);
                        key.cancel();
                        hall.playerLeaveRoom(session);
                        if (sessions.isEmpty()) {
                            hall.roomDissolve(this);
                        }
                    } else {
                        session.scheduleMessage(new LeaveRoomMessage(StateCode.GameIsStarting, "Game is Runing."));
                    }
                    break;
                case Ready:
                    session.state = GameState.Ready;
                    session.scheduleMessage(new SimpleMessage(StateCode.Success));
                    if (sessions.size() == playerCount && isAllReady()) {
                        startGame();
                    }
                    break;
                case Disready:
                    session.state = GameState.NotReady;
                    session.scheduleMessage(new SimpleMessage(StateCode.Success));
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
        Random random=new Random();
        switch (playerCount) {
            case 8:
                LinkedList<Role> roles = new LinkedList<>();
                roles.add(Role.Werewolf);
                roles.add(Role.Werewolf);
                roles.add(Role.Prophet);
                roles.add(Role.Witch);
                roles.add(Role.Villager);
                roles.add(Role.Villager);
                roles.add(Role.Villager);
                roles.add(Role.Villager);
                for (Session session: sessions){
                    Role r=roles.remove(random.nextInt(roles.size()));
                    session.scheduleMessage(new AssignRoleMessage(r));
                }
        }
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

    private boolean isAllReady() {
        for (Session session : sessions) {
            if (session.state == GameState.NotReady) {
                return false;
            }
        }
        return true;
    }
}
