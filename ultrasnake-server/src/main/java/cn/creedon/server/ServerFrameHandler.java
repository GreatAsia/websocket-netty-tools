package cn.creedon.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            String request = ((TextWebSocketFrame) frame).text();
            if ("OK".equalsIgnoreCase(request)) {
                ctx.channel().writeAndFlush(new TextWebSocketFrame("o.k"));
            } else {
                ctx.channel().writeAndFlush(new TextWebSocketFrame(request));
            }
        } else {
            String message = "不支持的Frame类型: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }
}
