import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;


public class InboundLogHandler extends ChannelInboundHandlerAdapter {

    Logger logger = Logger.getLogger(InboundLogHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        int size = in.readableBytes();
        logger.info(in.readCharSequence(size, Charset.forName("UTF-8")));
        ByteBuf out = ctx.alloc().buffer().writeBytes(("+ok\r\n").getBytes());
        ctx.writeAndFlush(out);
    }
}
