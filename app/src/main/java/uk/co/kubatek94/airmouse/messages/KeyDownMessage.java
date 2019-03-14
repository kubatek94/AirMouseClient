package uk.co.kubatek94.airmouse.messages;

import uk.co.kubatek94.airmouse.Server;

/**
 * Created by kubatek94 on 22/03/16.
 */
public class KeyDownMessage extends Message {

    public KeyDownMessage() {
        super(Type.KEYDOWN, 1);
    }

    public KeyDownMessage(int keyCode) {
        super(Type.KEYDOWN, 1);
        setKeyCode(keyCode);
    }

    public void setKeyCode(int keyCode) {
        body[0] = (byte) keyCode;
    }

    @Override
    public void action(Server server) {}
}
