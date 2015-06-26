package diexun.tcp;

import diexun.main.Main;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;

public class NettyServer {

    private final int port = 9096;
    private ServerBootstrap bootstrap;
    private ServerHandler handler;

    /**
     * 初始化服务器端
     */
    public void init() {
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(), // boss 监听请求，并分派给slave进行处理
                Executors.newCachedThreadPool()// slave 处理请求，将其丢到线程池中处理
        ));
        handler = new ServerHandler();
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                /* 典型的过滤式处理 */
                pipeline.addLast("encoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("decoder", new LengthFieldPrepender(4, false));
                /* 添加自定义的handler，对请求进行处理 */
                pipeline.addLast("handler", handler);
                return pipeline;
            }
        });
        /* 使用tcp长连接 */
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.setOption("reuseAddress", true);
    }

    /**
     * 绑定端口，启动netty服务
     */
    public void start() {
        if (Main.PORT.isEmpty() || Main.PORT.equals(""))
        {
            bootstrap.bind(new InetSocketAddress(port));
            System.out.println("Netty(Tcp)Server start,Port:" + port);
        }
        else
        {
            bootstrap.bind(new InetSocketAddress(Integer.parseInt(Main.PORT)));
            System.out.println("Netty(Tcp)Server start,Port:" + Main.PORT);
        }
        
    }

    /**
     * 关闭netty，释放资源。
     */
    public void stop() {
        bootstrap.releaseExternalResources();
    }

}
