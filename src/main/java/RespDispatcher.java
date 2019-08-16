import io.netty.handler.codec.LineBasedFrameDecoder;

public class RespDispatcher extends LineBasedFrameDecoder {


    public RespDispatcher(int maxLength) {
        super(maxLength);
    }
}
