package handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ByteProcessor;
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

    private enum State {
        DECODE_PARAM_COUNT,// 参数数量
        DECODE_PARAM_LENGTH,// 参数长度
        DECODE_PARAM_DATA// 参数具体数据
    }

    private State state = State.DECODE_PARAM_COUNT;
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
        try {
            for (; ; ) {
                switch (state) {
                    case DECODE_PARAM_COUNT:
                        if (!decodeParamCount(in)) return;
                        break;
                    case DECODE_PARAM_LENGTH:
                        if (!decodeParamLength(in, out)) return;
                        break;
                    case DECODE_PARAM_DATA:
                        if (!decodeParamData(in, out)) return;
                        break;
                    default:
                        throw new Exception("Unknown state: " + state);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean decodeParamCount(ByteBuf in) {
        logger.debug("参数数量：");
        int lineEndPos = in.forEachByte(ByteProcessor.FIND_CRLF);
        if (lineEndPos == -1) return false;
        // 获取协议 <参数数量>
        paramCount = Integer.parseInt(
                in.readCharSequence(lineEndPos - 3,
                        StandardCharsets.US_ASCII).toString());
        logger.debug(" * 参数数量: " + paramCount);
        // 去掉'\r\n'
        in.readerIndex(lineEndPos + 1);
        // 设置状态为参数长度
        state = State.DECODE_PARAM_LENGTH;
        return true;
    }

    private boolean decodeParamLength(ByteBuf in, List<Object> out) {
        logger.debug("参数长度：");

        int lineEndPos = in.forEachByte(ByteProcessor.FIND_CRLF);
        if (lineEndPos == -1) return false;
        // 获取参数长度
        nextParamLength = Integer.parseInt(
                in.readCharSequence(lineEndPos - 3,
                        StandardCharsets.US_ASCII).toString());
        logger.debug(" $ 参数长度: " + nextParamLength);
        // 去掉'\r\n'
        in.readerIndex(lineEndPos + 1);
        // 设置状态为参数具体数据
        state = State.DECODE_PARAM_DATA;
        return true;
    }

    private boolean decodeParamData(ByteBuf in, List<Object> out) {
        logger.debug("参数具体数据：");

        paramCountIndex++;
        return true;
    }
}
