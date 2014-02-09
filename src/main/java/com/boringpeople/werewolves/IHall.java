package com.boringpeople.werewolves;

/**
 * Created with IntelliJ IDEA.
 * User: Qiao
 * Date: 14-2-9
 * Time: 下午7:54
 * To change this template use File | Settings | File Templates.
 */
public interface IHall {
    public void playerLeaveRoom(Player player);
    public void roomDissolve(Room room);
}
