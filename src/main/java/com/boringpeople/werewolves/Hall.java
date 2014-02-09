package com.boringpeople.werewolves;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

import com.boringpeople.werewolves.message.CreateRoomMessage;
import com.boringpeople.werewolves.message.JoinRoomMessage;
import com.boringpeople.werewolves.message.MessageType;
import com.boringpeople.werewolves.util.MessageUtil;
import com.boringpeople.werewolves.util.SocketChannelUtil;
import com.boringpeople.werewolves.processor.IMessageProcessor;
import com.boringpeople.werewolves.processor.ISignProcessor;

public class Hall extends TimerTask implements IDispose, IHall {

    public ArrayList<Player> players;

    private boolean disposed;
    private int capability;
    private final Selector selector;
    private final List<Session> sessions;
    private final HashMap<Integer, Room> rooms;
    private final Timer timer;

    public ISignProcessor iAuthProcessor;

    public final List<IMessageProcessor> iMessageProcessor;

    public Hall() throws IOException {
        this(-1);
    }

    public Hall(int capability) throws IOException {
        this.capability = capability;
        rooms = new HashMap<>();
        iMessageProcessor = new ArrayList<>();
        sessions = new ArrayList<>();
        timer = new Timer("SelectorServer Timer");
        selector = Selector.open();
    }

    public void addMessageProcessor(IMessageProcessor imp) {
        if (imp != null && !iMessageProcessor.contains(imp)) {
            iMessageProcessor.add(imp);
        }
    }

    @Override
    public void run() {
        try {
            selector.selectNow();
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isValid()) {
                    if (key.channel() != null) {
                        if (key.isReadable()) {
                            processMessage(key);
                        }
                        if (key.isWritable()) {
                            sendMessageTo(key);
                        }
                    } else {
                        key.cancel();
                    }
                }
                keyIterator.remove();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessageTo(SelectionKey key) throws IOException {
        Session session = (Session) key.attachment();
        session.sendMessage();
    }

    private void processMessage(SelectionKey key) {
        byte[] data = SocketChannelUtil.readData((SocketChannel) key.channel());
        if (data != null && data.length > 0) {
            MessageType mt = MessageUtil.getMessageType(data);
            switch (mt) {
                case CreateRoom:
                    CreateRoomMessage crm = MessageUtil.deSerializeMessage(data, new CreateRoomMessage());
                    createRoom(key, crm);
                case JoinRoom:
                    JoinRoomMessage jrm = MessageUtil.deSerializeMessage(data, new JoinRoomMessage());
                    joinRoom(key, jrm);
                    break;
            }
            try {
                System.out.println("New Message From :" + ((SocketChannel) key.channel()).getRemoteAddress() + " "
                        + new String(data));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void createRoom(SelectionKey key, CreateRoomMessage crm) {
        try {
            Room room = new Room();
            rooms.put(room.Id, room);
            joinRoom(key, room);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void joinRoom(SelectionKey key, JoinRoomMessage jrm) {
        if (rooms.containsKey(jrm.roomId)) {
            try {
                joinRoom(key, rooms.get(jrm.roomId));
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
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

    public int getCapability() {
        return capability;
    }

    public int getClientCount() {
        return selector.keys().size();
    }

    public boolean canAddMore() {
        return capability == -1 ? true : selector.keys().size() < capability;
    }

    public void startServer() throws Exception {
        if (disposed) {
            throw new Exception("Object Disposed");
        }
        timer.schedule(this, 10, 30);
    }

    @Override
    public void playerLeaveRoom(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void roomDissolve(Room room) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
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
