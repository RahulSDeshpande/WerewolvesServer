package com.boringpeople.werewolf.message;

import java.io.Serializable;
import java.util.Date;

import com.boringpeople.werewolf.util.MessageUtil;

public abstract class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	public boolean bubble;
	public String from;
	public String to;
	public Date when;
    public int code;
    public String description;
	
	public final MessageType type;

	public Message(MessageType type) {
		this.type = type;
	}

	public String toJson() {
		return MessageUtil.serializeMessage(this);
	}
}
