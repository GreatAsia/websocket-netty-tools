package cn.creedon.client.header;


import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
/**
 * @author zhou
 * @date 2023/4/4
 */
public class MyIdleStatHandler  extends IdleStateHandler{

    private static final Logger log = LoggerFactory.getLogger(MyIdleStatHandler.class);
    private static final int READER_IDLE_TIME = 15;

    public MyIdleStatHandler()
    {
        super(15L, 0L, 0L, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt)
            throws Exception
    {
        log.info("15秒内未读取到数据,关闭连接");
        ctx.channel().close();
    }
}
