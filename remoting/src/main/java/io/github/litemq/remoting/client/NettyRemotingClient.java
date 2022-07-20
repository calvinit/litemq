package io.github.litemq.remoting.client;

import io.github.litemq.remoting.config.NettyClientConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Netty 远程访问客户端
 *
 * @author calvinit
 * @since 0.0.1
 */
public class NettyRemotingClient implements RemotingClient {
    // private static final InternalLogger log = InternalLoggerFactory.getInstance(NettyRemotingClient.class);

    private final NettyClientConfig nettyClientConfig;
    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup eventLoopGroupWorker;

    private EventExecutorGroup defaultEventExecutorGroup;

    public NettyRemotingClient(NettyClientConfig nettyClientConfig) {
        this(nettyClientConfig, null, null);
    }

    public NettyRemotingClient(NettyClientConfig nettyClientConfig,
                               EventLoopGroup eventLoopGroup,
                               EventExecutorGroup eventExecutorGroup) {
        this.nettyClientConfig = nettyClientConfig;
        this.eventLoopGroupWorker = Objects.requireNonNullElseGet(eventLoopGroup,
                () -> new NioEventLoopGroup(1, new ThreadFactory() {
                    private final AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, String.format("NettyClientSelector_%d", this.threadIndex.incrementAndGet()));
                    }
                }));
        this.defaultEventExecutorGroup = Objects.requireNonNullElseGet(eventExecutorGroup,
                () -> new DefaultEventExecutorGroup(
                        20,
                        new ThreadFactory() {
                            private final AtomicInteger threadIndex = new AtomicInteger(0);

                            @Override
                            public Thread newThread(Runnable r) {
                                return new Thread(r, "NettyClientWorkerThread_" + this.threadIndex.incrementAndGet());
                            }
                        }));
    }

    @Override
    public void start() {
        bootstrap.group(eventLoopGroupWorker).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                defaultEventExecutorGroup,
                                new IdleStateHandler(0, 0, 10));
                    }
                });
    }

    @Override
    public void shutdown() {
        eventLoopGroupWorker.shutdownGracefully();
        defaultEventExecutorGroup.shutdownGracefully();
    }

    private Channel createChannel(String host, int port) {
        return bootstrap.connect(host, port).channel();
    }
}
