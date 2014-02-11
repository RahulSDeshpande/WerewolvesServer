package com.boringpeople.werewolves;

import com.boringpeople.werewolves.message.CreateRoomMessage;
import com.boringpeople.werewolves.message.JoinRoomMessage;
import com.boringpeople.werewolves.message.MessageType;
import com.boringpeople.werewolves.message.SignInResultMessage;
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

public class Room extends AbstractMessageProcessor implements IDispose{

    private static int _id = 0;
    public int id;
    public String NickName;
    public IHall hall;
    public ArrayList<Player> players;

    public Room(IHall hall) throws IOException {
        super("Room_"+_id+" Timer");
        this.id = _id++;
        this.hall=hall;
    }

    @Override
    protected void onChannelReadable(SelectionKey key) throws IOException {
        byte[] data = SocketChannelUtil.readData((SocketChannel) key.channel());
        if (data != null && data.length > 0) {
            MessageType mt = MessageUtil.getMessageType(data);
            switch (mt) {
                case LeaveRoom:
                    key.cancel();
                    hall.playerLeaveRoom((Session)key.attachment());
                    if(selector.keys().isEmpty()){
                        hall.roomDissolve(this);
                    }
                    break;
            }
            try {
                System.out.println("[Room_"+id+"] New Message From :" + ((SocketChannel) key.channel()).getRemoteAddress() + " "
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
        assignRoles();
        play();
    }

    private void assignRoles() {

    }

    private void play() {

    }

    public void addNewPlayer(Session session) throws ClosedChannelException {
        session.channel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE,session);
        JoinRoomMessage jrm=new JoinRoomMessage(this.id);
        jrm.description="Welcome to Room "+id+".";
        session.scheduleMessage(jrm);
    }

    @Override
    public void dispose() {
        stop();
    }
}
