package uk.co.kubatek94.airmouse.messages;

import com.annimon.stream.Stream;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import uk.co.kubatek94.airmouse.Server;

/**
 * Created by kubatek94 on 16/03/16.
 */
public abstract class Message {
    public enum Type {
        KEEP_ALIVE, AUTHENTICATE, MOUSE_MOVE, MOUSE_CLICK, MOUSE_SCROLL, KEYDOWN, KEYUP, OTHER
    }

    /* buffer that is send to the server
    *  buffer[0] = message type
    *  buffer[1] = message length
    *  buffer[x] -> type specific
    * */
    //protected ByteBuffer bufferWriter;

    protected byte[] header = new byte[2];
    protected byte[] body;

    protected Type type;

    public Message(Type type, int dataLength) {
        this.type = type;

        header[0] = (byte) type.ordinal();
        header[1] = (byte) (header.length + dataLength);

        if (dataLength > 0) {
            body = new byte[dataLength];
        }
        //bufferWriter = ByteBuffer.wrap(body);

        //bufferWriter.position(0);
        //bufferWriter.order(ByteOrder.LITTLE_ENDIAN);
    }

    public Type getType() { return type; }

    public byte[] getHeader() { return header; }
    public int getHeaderLength() { return header.length; }

    public byte[] getBody() { return body; }
    public int getBodyLength() { return header[1] - header.length; }

    //public ByteBuffer getBufferWriter() { return bufferWriter; }
    public int getMessageLength() { return header[1]; }

    @Override
    public String toString() {
        return Arrays.toString(header) + (body == null ? "" : Arrays.toString(body));
    }

    public abstract void action(Server server);
}
