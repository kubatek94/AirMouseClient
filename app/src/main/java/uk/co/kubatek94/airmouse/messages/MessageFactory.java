package uk.co.kubatek94.airmouse.messages;

/**
 * Created by kubatek94 on 19/03/16.
 */
public class MessageFactory {
    public static Message.Type[] messageTypes = Message.Type.values();

    public static Message getMessage(Message.Type type, int dataLength) {
        switch (type) {
            case KEEP_ALIVE: return new KeepAliveMessage();
            case MOUSE_MOVE: return new MouseMoveMessage();
            case MOUSE_SCROLL: return new MouseScrollMessage();
            case MOUSE_CLICK: return new MouseClickMessage();
            case KEYUP: return new KeyUpMessage();
            case KEYDOWN: return new KeyDownMessage();

            default:
                return null;
        }
    }

}
