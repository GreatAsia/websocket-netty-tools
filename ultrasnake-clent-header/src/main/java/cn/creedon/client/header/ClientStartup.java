package cn.creedon.client.header;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;
/**
 * @author zhou
 * @date 2023/4/4
 */
public class ClientStartup {

    private final EventLoopGroup group;
    private final String ip;
    private final Integer port;
    private Channel channel;
    private final String ws;

    public ClientStartup(EventLoopGroup group, String ip, Integer port,String ws)
    {
        this.group = group;
        this.ip = ip;
        this.port = port;
        this.ws = ws;
    }

    public void startup()
            throws URISyntaxException, InterruptedException, SSLException {
        startup(null);
    }

    public void startup(String data)
            throws URISyntaxException, InterruptedException, SSLException {
         URI uri = null;
        if ("wss".equals(ws)){
            uri = new URI(this.ws + "://" + this.ip  + "/websocket");

        }else {
            uri = new URI(this.ws + "://" + this.ip + ":" + this.port + "/websocket");

        }
        final SslContext sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        if ((data != null) && (!"".equals(data)))
        {
            httpHeaders.set("uid", data);

        }
        final ClientFrameHandler handler = new ClientFrameHandler(WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, httpHeaders));

        Bootstrap b = new Bootstrap();

        ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)b.group(this.group)).channel(NioSocketChannel.class)).handler(new LoggingHandler(LogLevel.INFO))).option(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true)))
                .handler(new ChannelInitializer<SocketChannel>() {


                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception
                    {
                        if ("wss".equals(ws)){
                            ch.pipeline().addLast("ssl-handler", sslCtx.newHandler(ch.alloc(), ip, port));

                        }
                          //ws
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new ChannelHandler[] { new MyIdleStatHandler() });
                        p.addLast(new ChannelHandler[] { new HttpClientCodec() });
                        p.addLast(new ChannelHandler[] { new HttpObjectAggregator(65536) });
                        p.addLast(new ChannelHandler[] { new HeartBeatTimerHandler() });
                        p.addLast(new ChannelHandler[] { WebSocketClientCompressionHandler.INSTANCE });
                        p.addLast(new ChannelHandler[] { handler });



                    }
                });
        synchronized (b)
        {
            ChannelFuture future = b.connect(this.ip, this.port.intValue()).sync();
            this.channel = future.channel();
        }
    }

    public void close()
    {
        this.channel.close();
    }
}
