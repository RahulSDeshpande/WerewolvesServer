package com.boringpeople.werewolf.message;

/**
 * Created with IntelliJ IDEA.
 * User: Qiao
 * Date: 14-2-9
 * Time: 下午8:35
 * To change this template use File | Settings | File Templates.
 */
public class JoinRoomMessage extends Message {

    public int roomId;

    public JoinRoomMessage() {
        super(MessageType.JoinRoom);
    }

    public JoinRoomMessage(int roomId) {
        this();
        this.roomId=roomId;
    }
}
