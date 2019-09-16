import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LineBasedFrameDecoder;
import org.apache.log4j.Logger;

import java.util.List;

public class RespDispatcher extends LineBasedFrameDecoder {

    private Logger logger = Logger.getLogger(RespDispatcher.class);

    public RespDispatcher(int maxLength) {
        super(maxLength);
    }

    @Override
    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        logger.info("decodeLast");
        for (Object line : out) {
            logger.info(line);
        }
    }
}
