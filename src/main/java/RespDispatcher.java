import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class RespDispatcher extends ByteToMessageDecoder {

    private Logger logger = Logger.getLogger(RespDispatcher.class);
    private boolean isFirstFrame = true;
    private List<String> params = new ArrayList<>();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        logger.debug("RespDispatcher.decode");
        if (isFirstFrame && in.getChar(0) == '*') {
            CharSequence firstLine = in.readCharSequence(in.readableBytes(), Charset.forName("UTF-8"));
        } else {
            throw new Exception();
        }
    }
}
