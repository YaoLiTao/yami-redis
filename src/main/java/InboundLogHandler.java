import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;


public class InboundLogHandler extends ChannelInboundHandlerAdapter {

    Logger logger = Logger.getLogger(InboundLogHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("InboundLogHandler.channelRead");
        logger.info(((ByteBuf) msg).toString(Charset.forName("UTF-8")));
        ctx.fireChannelRead(msg);
    }
}
