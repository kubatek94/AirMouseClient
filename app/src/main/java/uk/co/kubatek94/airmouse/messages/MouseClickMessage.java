package uk.co.kubatek94.airmouse.messages;

import uk.co.kubatek94.airmouse.Server;
import uk.co.kubatek94.airmouse.view.TouchPadView;

/**
 * Created by kubatek94 on 16/03/16.
 */
public class MouseClickMessage extends Message {

    public MouseClickMessage() {
        super(Type.MOUSE_CLICK, 1);
    }

    public MouseClickMessage(TouchPadView.OnClickListener.Button button) {
        super(Type.MOUSE_CLICK, 1);
        body[0] = (byte) button.ordinal();
    }

    @Override
    public void action(Server server) {}
}
