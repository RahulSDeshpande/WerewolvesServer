package com.boringpeople.werewolf.message;

/**
 * Created with IntelliJ IDEA.
 * User: Qiao
 * Date: 14-2-11
 * Time: 下午9:10
 * To change this template use File | Settings | File Templates.
 */
public class LeaveRoomMessage extends Message {

    public  LeaveRoomMessage(int code,String description){
        super(MessageType.LeaveRoom);
        this.code=code;
        this.description =description;
    }
}
