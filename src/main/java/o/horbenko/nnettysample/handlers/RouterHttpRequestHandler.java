package o.horbenko.nnettysample.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;

@Log4j2
public class RouterHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                FullHttpRequest msg) throws Exception {
        log.debug("Consumed {}", msg.toString());

        ByteBuf content = msg.content();
        log.debug("Content is {}", content.toString(StandardCharsets.UTF_8));

        ByteBuf responseBody = Unpooled.copiedBuffer("Response", StandardCharsets.UTF_8);

        ctx
                .writeAndFlush(prepareOkHttpResponse(responseBody))
                .addListener(ChannelFutureListener.CLOSE);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
        log.error("Exception caught. exception.", cause);

        ctx
                .channel()
                .writeAndFlush(new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.INTERNAL_SERVER_ERROR))
                .addListener(ChannelFutureListener.CLOSE);
    }


    private FullHttpResponse prepareOkHttpResponse(ByteBuf responseBody) {

        if (responseBody == null) {
            return new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK);
        } else {
            return new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    responseBody);
        }
    }
}
