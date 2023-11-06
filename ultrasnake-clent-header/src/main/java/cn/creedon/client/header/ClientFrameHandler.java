package cn.creedon.client.header;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhou
 * @date 2023/4/4
 */
public class ClientFrameHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger log = LoggerFactory.getLogger(ClientFrameHandler.class);
    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    public ClientFrameHandler(WebSocketClientHandshaker handshaker)
    {
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture()
    {
        return this.handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
    {
        this.handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
        this.handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)
    {
        log.info("Channel Inactive!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg)
            throws Exception
    {
        if (!this.handshaker.isHandshakeComplete())
        {
            try
            {
                this.handshaker.finishHandshake(ctx.channel(), (FullHttpResponse)msg);
                this.handshakeFuture.setSuccess();
                log.info("websocket Handshake 完成!");
            }
            catch (WebSocketHandshakeException e)
            {
                this.handshakeFuture.setFailure(e);
                log.error("websocket 连接失败!", e);
            }
            return;
        }
        if ((msg instanceof FullHttpResponse))
        {
            FullHttpResponse response = (FullHttpResponse)msg;

            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }
        WebSocketFrame frame = (WebSocketFrame)msg;
        if ((frame instanceof TextWebSocketFrame))
        {
            TextWebSocketFrame textFrame = (TextWebSocketFrame)frame;
            if (!"o.k".equalsIgnoreCase(textFrame.text())) {
                log.info("msg: " + textFrame.text());
            }
        }
        else if ((frame instanceof PongWebSocketFrame))
        {
            log.info("接收pong");
        }
        else if ((frame instanceof CloseWebSocketFrame))
        {
            log.info("接收close");
            ctx.channel().close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        log.error("出现异常", cause);
        if (!this.handshakeFuture.isDone()) {
            this.handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
}
