package cmd;

import cache.InnerCache;
import exception.NoSuchCommandException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.util.List;

public class CmdDispatcher {

    public static ByteBuf dispatch(String cmd, List<ByteBuf> params) throws Exception {
        switch (cmd) {
            case "COMMAND":
                return command(params.get(1), params.get(2));
            default:
                throw new NoSuchCommandException();
        }
    }

    public static ByteBuf command(ByteBuf key, ByteBuf value) {
        return Unpooled.wrappedBuffer("ok".getBytes(Charset.forName("UTF-8")));
    }

    public static ByteBuf set(ByteBuf key, ByteBuf value) {
        InnerCache.hash.put(key, value);
        return Unpooled.wrappedBuffer("1".getBytes(Charset.forName("UTF-8")));
    }

    public static ByteBuf get(ByteBuf key) {
        return InnerCache.hash.get(key);
    }
}
