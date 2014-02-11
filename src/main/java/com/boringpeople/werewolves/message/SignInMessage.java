package com.boringpeople.werewolves.message;

/**
 * Created with IntelliJ IDEA.
 * User: Qiao
 * Date: 14-2-11
 * Time: 下午9:14
 * To change this template use File | Settings | File Templates.
 */
public class SignInMessage extends Message {
    public String nickName;

    public SignInMessage(String nickName) {
        super(MessageType.SignIn);
        this.nickName = nickName;
    }
}
