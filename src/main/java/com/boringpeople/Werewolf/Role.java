package com.boringpeople.werewolf;

/**
 * Created with IntelliJ IDEA.
 * User: Qiao
 * Date: 14-2-15
 * Time: 下午4:19
 * To change this template use File | Settings | File Templates.
 */
public enum Role {
    /**
     *上帝
     */
    God("god"),
    /**
     * 狼人
     */
    Werewolf("werewolf"),
    /**
     * 村民
     */
    Villager("villager"),
    /**
     *先知
     */
    Prophet("prophet"),
    /**
     * 女巫
     */
    Witch("witch"),
    /**
     * 丘比特
     */
    Cupid("cupid");

    private String _type;

    private Role(String type) {
        this._type = type;
    }

    @Override
    public String toString() {
        return this._type;
    }

    public static Role transform(String type) {
        switch (type) {
            case "werewolf":
                return Role.Werewolf;
            case "villager":
                return Role.Villager;
            case "prophet":
                return Role.Prophet;
            case "witch":
                return Role.Witch;
            case "cupid":
                return Role.Cupid;
            default:
                return  Role.God;
        }
    }
}
