package uk.co.kubatek94.airmouse.messages;

import uk.co.kubatek94.airmouse.Server;

/**
 * Created by kubatek94 on 21/03/16.
 */
public class MouseScrollMessage extends Message {

    public MouseScrollMessage() {
        super(Type.MOUSE_SCROLL, 1);
    }

    public MouseScrollMessage(float value) {
        super(Type.MOUSE_SCROLL, 1);
        setValue(value);
    }

    public void setValue(float value) {
        body[0] = (byte) value;
    }

    @Override
    public void action(Server server) {}
}
