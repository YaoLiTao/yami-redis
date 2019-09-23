import handler.InboundLogHandler;
import handler.OutboundLogHandler;
import handler.RespDispatcher;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Application {

    // 日志
    private static final Logger logger = Logger.getLogger(Application.class);

    // 端口号
    private final static int port = 6379;

    // 入站日志Handler
    private static final InboundLogHandler inboundLogHandler = new InboundLogHandler();

    // 出站日志Handler
    private static final OutboundLogHandler outboundLogHandler = new OutboundLogHandler();

    static {
        // 配置log4j
        BasicConfigurator.configure();
    }

    public static void main(String[] args) throws InterruptedException {

        logger.debug("服务器开始初始化 。。。");

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            logger.debug("一个新客户端连接到服务器");

                            socketChannel.pipeline()
                                    .addLast(inboundLogHandler)
                                    .addLast(outboundLogHandler)
                                    .addLast(new RespDispatcher());

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind().sync();
            logger.debug("服务器初始化完成");
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
            logger.info("服务器关闭");
        }
    }
}
