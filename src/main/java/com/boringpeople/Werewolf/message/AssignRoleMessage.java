package com.boringpeople.werewolf.message;

import com.boringpeople.werewolf.Role;

/**
 * Created with IntelliJ IDEA.
 * User: Qiao
 * Date: 14-2-15
 * Time: 下午4:32
 * To change this template use File | Settings | File Templates.
 */
public class AssignRoleMessage extends Message {
    public Role role;

    public AssignRoleMessage(Role role) {
        super(MessageType.AssignRoles);
        this.role = role;
    }
}
