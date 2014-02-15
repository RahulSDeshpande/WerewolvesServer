package com.boringpeople.werewolf;

/**
 * Created with IntelliJ IDEA.
 * User: Qiao
 * Date: 14-2-9
 * Time: 下午7:54
 * To change this template use File | Settings | File Templates.
 */
public interface IHall {
    public void playerLeaveRoom(Session session);
    public void roomDissolve(Room room);
}
