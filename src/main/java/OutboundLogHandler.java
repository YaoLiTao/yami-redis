import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.apache.log4j.Logger;


public class OutboundLogHandler extends ChannelOutboundHandlerAdapter {

    Logger logger = Logger.getLogger(OutboundLogHandler.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.info(msg.toString());
    }
}
