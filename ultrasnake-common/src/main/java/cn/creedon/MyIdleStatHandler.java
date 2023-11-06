package cn.creedon;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class MyIdleStatHandler extends IdleStateHandler {

    private static final int READER_IDLE_TIME = 15;

    public MyIdleStatHandler() {
        super(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        log.info(READER_IDLE_TIME + "秒内未读取到数据，关闭连接");
        ctx.channel().close();
    }
}
