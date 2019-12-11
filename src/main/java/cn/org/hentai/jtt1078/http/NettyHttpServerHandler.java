package cn.org.hentai.jtt1078.http;

import cn.org.hentai.jtt1078.publisher.PublishManager;
import cn.org.hentai.jtt1078.util.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by matrixy on 2019/8/13.
 */
public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter
{
    static Logger logger = LoggerFactory.getLogger(NettyHttpServerHandler.class);
    static final byte[] HTTP_403_DATA = "<h1>403 Forbidden</h1><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding--><!--padding-->".getBytes();
    static final String HEADER_ENCODING = "ISO-8859-1";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        FullHttpRequest fhr = (FullHttpRequest) msg;
        String uri = fhr.uri();
        Packet resp = Packet.create(1024);
        // uri的第二段，就是通道标签
        if (uri.startsWith("/video/"))
        {
            String tag = uri.substring(7);
        }
        else if (uri.startsWith("/audio/"))
        {
            String tag = uri.substring(7);

            resp.addBytes("HTTP/1.1 200 OK\r\n".getBytes(HEADER_ENCODING));
            resp.addBytes("Connection: keep-alive\r\n".getBytes(HEADER_ENCODING));
            resp.addBytes("Content-Type: application/json\r\n".getBytes(HEADER_ENCODING));
            resp.addBytes("Transfer-Encoding: chunked\r\n".getBytes(HEADER_ENCODING));
            resp.addBytes("Cache-Control: no-cache\r\n".getBytes(HEADER_ENCODING));
            resp.addBytes("\r\n".getBytes(HEADER_ENCODING));

            ctx.writeAndFlush(resp.getBytes());

            // 订阅音频数据
            PublishManager.getInstance().subscribe(tag, ctx);
        }
        else
        {

            ByteBuf body = Unpooled.buffer(HTTP_403_DATA.length);
            body.writeBytes(HTTP_403_DATA);
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(403), body);
            response.headers().add("Content-Length", HTTP_403_DATA.length);
            ctx.write(response);
            ctx.flush();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        ctx.close();
        cause.printStackTrace();
    }
}