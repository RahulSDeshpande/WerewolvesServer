package com.boringpeople;

import com.boringpeople.werewolf.Hall;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Main {

    public static Hall hall;

    public static void main(String[] args) {
        System.out.println("Hello Welcome To Werewolves.");
        new Thread() {
            public void run() {
                try {
                    waitInPort(19605);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.err.println("Server quited.");
            }
        }.start();
    }

    private static void waitInPort(int port) throws Exception {
        hall = new Hall();
        hall.startServer();
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server Started.");
            for (;;) {
                selector.select(10);
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isValid())
                        handle(key);
                    keyIterator.remove();
                }
            }

        } catch (IOException e) {
            System.out.printf("error %d ,%s\n", port, e.getMessage());
        }
        hall.dispose();
    }

    private static void handle(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssc.accept();
        try {
            System.out.println("New Client Connected.");
            hall.addNewClient(sc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
