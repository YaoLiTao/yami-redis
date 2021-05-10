package handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;

/**
 * 出站数据日志处理器
 */
@ChannelHandler.Sharable
public class OutboundLogHandler extends ChannelOutboundHandlerAdapter {

    Logger logger = Logger.getLogger(OutboundLogHandler.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.debug("出站数据:\n" + ((ByteBuf) msg).toString(Charset.forName("UTF-8")));
        ctx.writeAndFlush(msg);
        ReferenceCountUtil.release(msg);
    }
}
