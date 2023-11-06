package cn.creedon.client;

import cn.creedon.HeartBeatTimerHandler;
import cn.creedon.MyIdleStatHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.URI;
import java.net.URISyntaxException;

public class ClientStartup {

    private final EventLoopGroup group;

    private final String ip;

    private final Integer port;

    private Channel channel;

    public ClientStartup(EventLoopGroup group, String ip, Integer port) {
        this.group = group;
        this.ip = ip;
        this.port = port;
    }

    public void startup() throws URISyntaxException, InterruptedException {
        URI uri = new URI("ws://" + this.ip + ":" + this.port + "/websocket");

        ClientFrameHandler handler =
                new ClientFrameHandler(
                        WebSocketClientHandshakerFactory.newHandshaker(
                                uri,
                                WebSocketVersion.V13,
                                null,
                                true,
                                new DefaultHttpHeaders()
                        )
                );
        Bootstrap b = new Bootstrap();
        b.group(this.group)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new MyIdleStatHandler());
                        p.addLast(new HttpClientCodec());
                        p.addLast(new HttpObjectAggregator(65536));
                        p.addLast(new HeartBeatTimerHandler());
                        p.addLast(WebSocketClientCompressionHandler.INSTANCE);
                        p.addLast(handler);
                    }
                });
        synchronized (b) {
            ChannelFuture future = b.connect(this.ip, this.port).sync();
            this.channel = future.channel();
        }
    }

    public void close() {
        this.channel.close();
    }

}
