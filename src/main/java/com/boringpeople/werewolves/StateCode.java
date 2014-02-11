package com.boringpeople.werewolves;

/**
 * Created with IntelliJ IDEA.
 * User: Qiao
 * Date: 14-2-11
 * Time: 下午9:53
 */
public interface StateCode {
    public static final int Success=0;
    public static final int AccountOrPasswordError = -10;
    public static final int CreateRoomError = -20;
    public static final int RoomNotExists = -21;
    public static final int GameIsStarting=-30;
}
