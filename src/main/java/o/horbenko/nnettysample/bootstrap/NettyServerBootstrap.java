package o.horbenko.nnettysample.bootstrap;

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
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.log4j.Log4j2;
import o.horbenko.nnettysample.handlers.RouterHttpRequestHandler;
import o.horbenko.nnettysample.utils.OsCheck;

import java.util.concurrent.ThreadFactory;

@Log4j2
public class NettyServerBootstrap {


    /**
     * The maximum queue length for incoming connection indications (a request to connect)
     * is set to the backlog parameter.
     * If a connection indication arrives when the queue is full, the connection is refused.
     */
    private static final int SO_BACKLOG_OPTION_VALUE = 1024;
    private static final boolean SO_KEEPALIVE_OPTION_VALUE = true;

    /**
     * {@see https://netty.io/4.1/api/io/netty/channel/ChannelPipeline.html}
     * How many threads will be created for blocking operations via {@link #createNewEventExecutorGroup()}
     */
    private static final int BLOCKING_OPERATION_EXECUTION_THREADS_COUNT =
            Runtime.getRuntime().availableProcessors() + 1;


    private final int serverPort;

    public NettyServerBootstrap(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * option() "is for the NioServerSocketChannel that accepts incoming connections."
     * Link: https://netty.io/wiki/user-guide-for-4.x.html#wiki-h2-2
     * <p>
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
                        .addLast("httpServerCodec", new HttpServerCodec())
                        .addLast("httpAggregator", new HttpObjectAggregator(Integer.MAX_VALUE))
                        .addLast(createNewEventExecutorGroup(), "requestRouterHandler", new RouterHttpRequestHandler())
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

        int threadsCount = 0;
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


    private EventExecutorGroup createNewEventExecutorGroup() {
        ThreadFactory thf = new DefaultThreadFactory("blockingOps");
        return new DefaultEventExecutorGroup(BLOCKING_OPERATION_EXECUTION_THREADS_COUNT, thf);
    }


}
