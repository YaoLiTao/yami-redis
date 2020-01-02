package handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * Redis协议分发器
 */
public class RespDispatcher extends ByteToMessageDecoder {

    // 日志
    private Logger logger = Logger.getLogger(RespDispatcher.class);
    // 参数数量状态
    private static final byte PARAM_COUNT_FRAME_STATE = 0;
    // 参数长度值状态
    private static final byte PARAM_LENGTH_FRAME_STATE = 1;
    // 实际数据状态
    private static final byte DATA_FRAME_STATE = 2;
    // 协议步骤
    private byte parseState = PARAM_COUNT_FRAME_STATE;
    // 协议命令
    private String cmd = null;
    // 参数数量
    private long paramCount = 0;
    // 参数index
    private long paramCountIndex = 0;
    // 参数值长度
    private long nextParamLength = 0;
    // 实际参数
    private LinkedList<String> params = new LinkedList<>();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        logger.debug("解析数据：" + in.toString(Charset.defaultCharset()));
        if (parseState == PARAM_COUNT_FRAME_STATE && in.readChar() == '*') {

            // 找到协议"参数个数"行的结尾位置
            int lineEndPos = in.forEachByte(ByteProcessor.FIND_CRLF);
            if (lineEndPos == -1) return;
            // 获取协议 <参数数量>
            paramCount = Integer.parseInt(
                    in.readCharSequence(lineEndPos - 3,
                            StandardCharsets.US_ASCII).toString());
            logger.debug(" * 参数数量: " + paramCount);

            // 抛除'\r\n'
            in.readerIndex(lineEndPos + 1);
            // 设置状态为参数长度状态
            parseState = PARAM_LENGTH_FRAME_STATE;
        } else if (parseState == PARAM_LENGTH_FRAME_STATE && in.readChar() == '$' && paramCountIndex < paramCount) {

            // 找到"参数长度值"行的结尾位置
            int lineEndPos = in.forEachByte(ByteProcessor.FIND_CRLF);
            if (lineEndPos == -1) return;
            // 获取协议参数长度
            nextParamLength = Integer.parseInt(
                    in.readCharSequence(lineEndPos - 3,
                            StandardCharsets.US_ASCII).toString());

            logger.debug(" $ 参数长度: " + nextParamLength);

            // 抛除'\r\n'
            in.readerIndex(lineEndPos + 1);
            // 设置状态为实际数据状态
            parseState = DATA_FRAME_STATE;
        } else if (parseState == DATA_FRAME_STATE && paramCountIndex < paramCount) {
            logger.debug(" 实际数据");

        } else {
            logger.error("协议解析错误 buff:\n" + in.toString(Charset.defaultCharset()));
            ReferenceCountUtil.release(in);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

}
