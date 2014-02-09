package com.boringpeople.werewolves;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

import com.boringpeople.werewolves.util.SocketChannelUtil;
import com.boringpeople.werewolves.message.Message;

public class Session {

	public long Id;
	public Player player;
	public LinkedList<Message> messages;
	public SocketChannel channel;

	public Session(SocketChannel channel) {
		Id = System.nanoTime();
		messages = new LinkedList<>();
		this.channel = channel;
	}

	public void scheduleMessage(Message msg) {
		messages.add(msg);
	}

	public void sendMessage() throws IOException {
		if (messages.size() > 0) {
			Message msg = messages.removeFirst();
			SocketChannelUtil.writeMessage(msg, channel);
		}
	}

	public byte[] readMessage() {
		return SocketChannelUtil.readData(channel);
	}

}
