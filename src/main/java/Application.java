import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Application {
    private final static int port = 6379;
    private static Logger logger = Logger.getLogger(Application.class);

    static {
        BasicConfigurator.configure();
    }

    public static void main(String[] args) throws InterruptedException {

        logger.debug("server initial ...");

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                        logger.debug("a client connected ...");

                        socketChannel.pipeline()
                                .addLast(new InboundLogHandler())
                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new RespDispatcher());

                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture future = bootstrap.bind().sync();
        future.channel().closeFuture().sync();
    }
}
