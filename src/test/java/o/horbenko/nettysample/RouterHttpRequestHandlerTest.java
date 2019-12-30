package o.horbenko.nettysample;

import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import o.horbenko.nnettysample.handlers.RouterHttpRequestHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RouterHttpRequestHandlerTest {

    private EmbeddedChannel testChannel;

    @Before
    public void initEmbeddedChannelWithPipeline() {
        this.testChannel = new EmbeddedChannel();
        testChannel
                .pipeline()
                .addLast(new LoggingHandler(LogLevel.TRACE))
                .addLast(new RouterHttpRequestHandler());
    }

    @Test
    public void test_ok() throws Exception {

// ARRANGE
        // prepare request
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.POST,
                "/",
                Unpooled.copiedBuffer("Hello netty!".getBytes())
        );

        // expected
        FullHttpResponse expectedHttpResponse = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );

// ACT
        testChannel
                .writeInbound(request);

// ASSERT
        FullHttpResponse actualHttpResponse = testChannel.readOutbound();
        Assert.assertEquals(expectedHttpResponse, actualHttpResponse);
    }

}
