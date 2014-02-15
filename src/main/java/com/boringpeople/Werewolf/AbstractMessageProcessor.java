package com.boringpeople.werewolf;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jianxingqiao on 2/11/14.
 */
public abstract class AbstractMessageProcessor extends TimerTask {

    private  String name;
    protected Timer timer;
    protected Selector selector;

    public AbstractMessageProcessor(String name) throws IOException {
        this.name=name;
        timer = new Timer(name);
        selector = Selector.open();
    }

    @Override
    public void run() {
        try {
            selector.selectNow();
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isValid()) {
                    if (key.channel() != null) {
                        if (key.isWritable()) {
                            try {
                                onChannelWritable(key);
                            } catch (IOException exp) {
                                exp.printStackTrace();
                                key.cancel();
                            }
                        }
                        if (key.isReadable()) {
                            try {
                                onChannelReadable(key);
                            } catch (IOException exp) {
                                exp.printStackTrace();
                                key.cancel();
                            }
                        }

                    } else {
                        key.cancel();
                    }
                }
                keyIterator.remove();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void onChannelReadable(SelectionKey key) throws IOException;

    protected abstract void onChannelWritable(SelectionKey key) throws IOException;


    public void start() {
        timer.schedule(this, 10, 30);
    }

    public  void stop(){
        timer.cancel();
    }

}
