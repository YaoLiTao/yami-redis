package cmd;

import exception.NoSuchCommandException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class CmdDispatcher {

    public static ByteBuf dispatch(String cmd, List<ByteBuf> params) throws Exception {
        switch (cmd) {
            case "COMMAND":
                return command();
            default:
                throw new NoSuchCommandException();
        }
    }

    public static ByteBuf command() {
        return Unpooled.wrappedBuffer("ok".getBytes(StandardCharsets.US_ASCII));
    }

    public static ByteBuf set(ByteBuf key, ByteBuf value) {
        return Unpooled.wrappedBuffer("1".getBytes(StandardCharsets.US_ASCII));
    }

    public static ByteBuf get(ByteBuf key) {
        return null;
    }
}
