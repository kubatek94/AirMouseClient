package uk.co.kubatek94.airmouse.messages;

import uk.co.kubatek94.airmouse.Server;

/**
 * Created by kubatek94 on 19/03/16.
 */
public class KeepAliveMessage extends Message {

    public KeepAliveMessage() {
        super(Type.KEEP_ALIVE, 0);
    }

    @Override
    public void action(Server server) {
        System.out.println("Keep alive from server: " + server);
    }
}
