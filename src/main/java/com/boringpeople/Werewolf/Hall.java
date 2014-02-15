package com.boringpeople.werewolf;

import com.boringpeople.werewolf.message.*;
import com.boringpeople.werewolf.util.MessageUtil;
import com.boringpeople.werewolf.util.SocketChannelUtil;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Hall extends AbstractMessageProcessor implements IDispose, IHall {

    private static int _id=0;
    private int id;
    public ArrayList<Player> players;

    private boolean disposed;
    private int capability;

    private List<Session> sessions;
    private HashMap<Integer, Room> rooms;

    public Hall() throws IOException {
        this(-1);
    }

    public Hall(int capability) throws IOException {
        super("Hall "+_id+" Timer");
        id=_id++;
        this.capability = capability;
        rooms = new HashMap<>();
        sessions = new ArrayList<>();
    }

    @Override
    protected void onChannelWritable(SelectionKey key) throws IOException {
        Session session = (Session) key.attachment();
        session.sendMessage();
    }

    @Override
    protected void onChannelReadable(SelectionKey key) throws IOException {
        byte[] data = SocketChannelUtil.readData((SocketChannel) key.channel());
        if (data != null && data.length > 0) {
            MessageType mt = MessageUtil.getMessageType(data);
            Session session=(Session)key.attachment();
            switch (mt) {
                case SignIn:
                    SignInMessage sim=MessageUtil.deSerializeMessage(data,new SignInMessage());
                    session.player.nickName = sim.nickName;
                    session.scheduleMessage(new SimpleMessage(0));
                    break;
                case CreateRoom:
                    CreateRoomMessage crm = MessageUtil.deSerializeMessage(data, new CreateRoomMessage());
                    createRoom(key, crm);
                    break;
                case JoinRoom:
                    JoinRoomMessage jrm = MessageUtil.deSerializeMessage(data, new JoinRoomMessage());
                    joinRoom(key, jrm);
                    break;
            }
            try {
                System.out.println("[Hall_"+id+"] New Message From :" + ((SocketChannel) key.channel()).getRemoteAddress() + " "
                        + new String(data));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void createRoom(SelectionKey key, CreateRoomMessage crm) {
        Session session = (Session) key.attachment();
        try {
            Room room = new Room(this);
            room.start();
            rooms.put(room.id, room);
            joinRoom(key, room);
            session.scheduleMessage(new SimpleMessage(StateCode.Success));
        } catch (IOException e) {
            e.printStackTrace();
            session.scheduleMessage(new SimpleMessage(StateCode.CreateRoomError,e.getMessage()));
        }
    }

    private void joinRoom(SelectionKey key, JoinRoomMessage jrm) {
        if (rooms.containsKey(jrm.roomId)) {
            try {
                joinRoom(key, rooms.get(jrm.roomId));
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }else {
            Session session = (Session) key.attachment();
            session.scheduleMessage(new SimpleMessage(StateCode.RoomNotExists,"Room "+jrm.roomId+" Not Exists."));
        }
    }

    private void joinRoom(SelectionKey key, Room room) throws ClosedChannelException {
        Session session = (Session) key.attachment();
        key.cancel();
        room.addNewPlayer(session);
    }


    public void addNewClient(SocketChannel channel) throws Exception {
        if (disposed) {
            throw new Exception("The Hall Disposed");
        }
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.configureBlocking(false);
        Session session = new Session(channel);
        channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, session);
        sessions.add(session);
    }


    public void startServer() throws Exception {
        if (disposed) {
            throw new Exception("Object Disposed");
        }
        start();
    }

    @Override
    public void playerLeaveRoom(Session session) {
        try {
            session.channel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE,session);
        }catch (Exception exp)
        {
            exp.printStackTrace();
        }
    }

    @Override
    public void roomDissolve(Room room) {
        room.dispose();
        rooms.remove(room.id);
        System.out.println("Room Auto Dissolve.");
    }

    @Override
    public void dispose() {
        stop();
        disposed = true;
        try {
            for (SelectionKey sk : selector.keys()) {
                sk.channel().close();
            }
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
