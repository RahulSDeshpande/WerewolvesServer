package com.boringpeople.werewolves.message;

/**
 * Created with IntelliJ IDEA.
 * User: Qiao
 * Date: 14-2-11
 * Time: 下午9:46
 */
public class SimpleMessage extends Message {
    public SimpleMessage(int code){
        this(code,"");
    }
    public SimpleMessage(int code, String description) {
        super(MessageType.DefaultResponse);
        this.code = code;
        this.description = description;
    }
}
