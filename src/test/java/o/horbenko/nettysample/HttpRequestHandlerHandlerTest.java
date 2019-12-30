package o.horbenko.nettysample;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ResourceLeakDetector;
import o.horbenko.nnettysample.handlers.HttpRequestHandlerHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HttpRequestHandlerHandlerTest {

    private EmbeddedChannel testChannel;

    @Before
    public void initEmbeddedChannelWithPipeline() {
        this.testChannel = new EmbeddedChannel();
        testChannel
                .pipeline()
                .addLast(new LoggingHandler(LogLevel.TRACE))
                .addLast(new HttpRequestHandlerHandler());
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
