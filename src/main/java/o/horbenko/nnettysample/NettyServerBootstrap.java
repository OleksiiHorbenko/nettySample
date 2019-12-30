package o.horbenko.nnettysample;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.log4j.Log4j2;
import o.horbenko.nnettysample.handlers.HttpRequestHandlerHandler;
import o.horbenko.nnettysample.utils.OsCheck;

import java.util.concurrent.ThreadFactory;

@Log4j2
public class NettyServerBootstrap {


    /**
     * The maximum queue length for incoming connection indications (a request to connect) is set to the backlog parameter.
     * If a connection indication arrives when the queue is full, the connection is refused.
     */
    private static final int SO_BACKLOG_OPTION_VALUE = 1024;
    private static final boolean SO_KEEPALIVE_OPTION_VALUE = true;


    private final int serverPort;

    public NettyServerBootstrap(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * option() "is for the NioServerSocketChannel that accepts incoming connections." Link: https://netty.io/wiki/user-guide-for-4.x.html#wiki-h2-2
     * childOption() is for the Channels accepted by the parent ServerChannel
     */
    public void runServer() {

        EventLoopGroup bossEventLoopGroup = createNewEventLoopGroup("boss");        // boss threads handle connections and pass processing to worker threads
        EventLoopGroup workerEventLoopGroup = createNewEventLoopGroup("worker");    // worker threads group

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(bossEventLoopGroup, workerEventLoopGroup)
                    .channel(getServerChannelClass())
                    .localAddress(serverPort)
                    .handler(new LoggingHandler(LogLevel.WARN))
                    .childHandler(createChannelInitializer())
                    .option(ChannelOption.SO_BACKLOG, SO_BACKLOG_OPTION_VALUE)
                    .childOption(ChannelOption.SO_KEEPALIVE, SO_KEEPALIVE_OPTION_VALUE);


            ChannelFuture channelFuture = serverBootstrap
                    .bind()
                    .sync();

            log.debug("Server starts listening on port = {}", serverPort);

            channelFuture
                    .channel()
                    .closeFuture()
                    .sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerEventLoopGroup.shutdownGracefully();
            bossEventLoopGroup.shutdownGracefully();
        }
    }

    private ChannelInitializer createChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel
                        .pipeline()
                        .addLast(new HttpServerCodec())
                        .addLast(new HttpObjectAggregator(Integer.MAX_VALUE))
                        .addLast(new LoggingHandler(LogLevel.DEBUG))
                        .addLast(new HttpRequestHandlerHandler())
                ;
            }
        };
    }

    private Class<? extends ServerChannel> getServerChannelClass() {
        switch (OsCheck.getOperatingSystemType()) {
            case Linux:
                return EpollServerSocketChannel.class;
            case MacOS:
                return KQueueServerSocketChannel.class;
            default:
                return NioServerSocketChannel.class;
        }
    }

    private EventLoopGroup createNewEventLoopGroup(String eventLoopGroupName) {

        int threadsCount = 0; // for AUTO CONFIGURATION
        ThreadFactory thf = new DefaultThreadFactory(eventLoopGroupName);

        switch (OsCheck.getOperatingSystemType()) {
            case Linux:
                return new EpollEventLoopGroup(threadsCount, thf);
            case MacOS:
                return new KQueueEventLoopGroup(threadsCount, thf);
            default:
                return new NioEventLoopGroup(threadsCount, thf);
        }
    }


}
