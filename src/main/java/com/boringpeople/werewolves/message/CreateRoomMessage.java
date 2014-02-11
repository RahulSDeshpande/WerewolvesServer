package com.boringpeople.werewolves.message;

/**
 * Created with IntelliJ IDEA.
 * User: Qiao
 * Date: 14-2-9
 * Time: 下午8:39
 * To change this template use File | Settings | File Templates.
 */
public class CreateRoomMessage extends Message {
    public int roomId;
    public  CreateRoomMessage(){
        super(MessageType.CreateRoom);
    }
}
