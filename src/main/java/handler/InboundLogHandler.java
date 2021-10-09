package handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;


/**
 * 入站数据日志处理器
 */
@ChannelHandler.Sharable
public class InboundLogHandler extends ChannelInboundHandlerAdapter {

    Logger logger = Logger.getLogger(InboundLogHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("入站数据:\n" + ((ByteBuf) msg).toString(Charset.forName("UTF-8")));
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.debug("读取数据完成");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
