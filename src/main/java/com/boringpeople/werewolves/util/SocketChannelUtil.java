package com.boringpeople.werewolves.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.boringpeople.werewolves.message.Message;

public class SocketChannelUtil {
	
	public static byte[] readData(SocketChannel channel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(2);

        int size = 0;
        channel.read(buffer);
        buffer.flip();
        int msgLength = buffer.getChar() - 2;
        System.out.println("Message Length:" + msgLength);
        buffer = ByteBuffer.allocate(msgLength);
        while ((size += channel.read(buffer)) < msgLength);
        buffer.flip();
        return buffer.array();

	}
	
	public static void writeMessage(Message msg, SocketChannel channel) throws IOException {
		byte[] buf = msg.toJson().getBytes("UTF-8");
		byte[] data = new byte[buf.length + 2];
		System.arraycopy(buf, 0, data, 2, buf.length);
		buf = null;
		data[0] = (byte) ((data.length >>> 8) & 0xFF);
		data[1] = (byte) ((data.length >>> 0) & 0xFF);
		ByteBuffer bbf = ByteBuffer.wrap(data);
		channel.write(bbf);
	}
	
}
