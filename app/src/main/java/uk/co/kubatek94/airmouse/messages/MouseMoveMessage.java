package uk.co.kubatek94.airmouse.messages;

import uk.co.kubatek94.airmouse.Server;

/**
 * Created by kubatek94 on 16/03/16.
 */
public class MouseMoveMessage extends Message {

    public MouseMoveMessage() {
        super(Type.MOUSE_MOVE, 2);
    }

    public MouseMoveMessage(float dx, float dy) {
        super(Type.MOUSE_MOVE, 2);
        setDelta((byte)dx, (byte)dy);
    }

    public void setDelta(byte dx, byte dy) {
        body[0] = dx;
        body[1] = dy;
    }

    @Override
    public void action(Server server) {}
}
