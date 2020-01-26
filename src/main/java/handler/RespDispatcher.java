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
        DECODE_PARAM_DATA,// 参数具体数据
        COMMAND_INTEGRATION//把参数整合成命令
    }

    private State state = State.DECODE_PARAM_COUNT;
    // 参数数量
    private int paramCount = 0;
    // 参数index
    private int paramCountIndex = 0;
    // 参数值长度
    private int paramContentLength = 0;
    // 实际数据
    private LinkedList<ByteBuf> params = new LinkedList<>();

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
                        if (!decodeParamLength(in)) return;
                        break;
                    case DECODE_PARAM_DATA:
                        if (!decodeParamData(in)) return;
                        break;
                    case COMMAND_INTEGRATION:
                        commandIntegration();
                        resetDecoder();
                        return;
                    default:
                        throw new Exception("Unknown state: " + state);
                }
            }
        } catch (Exception e) {
            resetDecoder();
            throw e;
        }
    }

    private void resetDecoder() {
        state = State.DECODE_PARAM_COUNT;
        paramCount = 0;
        paramCountIndex = 0;
        paramContentLength = 0;
        params = new LinkedList<>();
    }

    private boolean decodeParamCount(ByteBuf in) {
        if (!in.isReadable()) return false;
        // 获取行类型
        final int initialIndex = in.readerIndex();
        final byte lineType = in.readByte();
        if (lineType != '*') {
            in.readerIndex(initialIndex);
            resetDecoder();
            return false;
        }

        // 获取行尾的下标
        final int lineEndIndex = in.forEachByte(ByteProcessor.FIND_CRLF);
        if (lineEndIndex == -1) return false;

        // 获取协议 <参数数量>
        paramCount = Integer.parseInt(
                in.readCharSequence(lineEndIndex - in.readerIndex(),
                        StandardCharsets.US_ASCII).toString());
        logger.debug(" * 参数数量: " + paramCount);

        // 去掉'\r\n'，切换为DECODE_PARAM_LENGTH状态
        in.readerIndex(lineEndIndex + 2);
        state = State.DECODE_PARAM_LENGTH;
        return true;
    }

    private boolean decodeParamLength(ByteBuf in) {

        // 获取行类型
        final int initialIndex = in.readerIndex();
        final byte lineType = in.readByte();
        if (lineType != '$') {
            in.readerIndex(initialIndex);
            resetDecoder();
            return false;
        }

        // 获取行尾的下标
        final int lineEndIndex = in.forEachByte(ByteProcessor.FIND_CRLF);
        if (lineEndIndex == -1) return false;

        // 获取参数长度
        paramContentLength = Integer.parseInt(
                in.readCharSequence(lineEndIndex - in.readerIndex(),
                        StandardCharsets.US_ASCII).toString());
        logger.debug(" $ 参数长度: " + paramContentLength);

        // 去掉'\r\n'，切换为DECODE_PARAM_DATA状态
        in.readerIndex(lineEndIndex + 2);
        state = State.DECODE_PARAM_DATA;
        return true;
    }

    private boolean decodeParamData(ByteBuf in) {
        final int initialIndex = in.readerIndex();
        if (!in.isReadable(paramContentLength + 2)) return false;

        //读取具体参数
        ByteBuf param = in.copy(initialIndex, paramContentLength);
        in.readerIndex(initialIndex + paramContentLength + 2);
        logger.debug("具体参数: " + param.toString(StandardCharsets.US_ASCII));

        params.add(param);
        paramCountIndex++;
        state = paramCountIndex >= paramCount ? State.COMMAND_INTEGRATION : State.DECODE_PARAM_LENGTH;
        return true;
    }

    private void commandIntegration() {
        final String cmd = params.get(0).toString(StandardCharsets.US_ASCII);
        logger.debug("命令: " + cmd);
    }
}
