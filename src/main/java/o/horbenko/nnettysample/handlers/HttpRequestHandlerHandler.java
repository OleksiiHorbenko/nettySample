package o.horbenko.nnettysample.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;

@Log4j2
public class HttpRequestHandlerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                FullHttpRequest msg) throws Exception {

        log.debug("Consumed {}", msg.toString());

        ByteBuf content = msg.content();

        // LOGIC
        // LOGIC
        // LOGIC
        // LOGIC

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );

        ChannelFuture writeFlushFuture = ctx
                .write(response)
                .addListener(ChannelFutureListener.CLOSE);

//        if (!isKeep)
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        super.channelReadComplete(ctx);
        ctx.flush();
    }
}
