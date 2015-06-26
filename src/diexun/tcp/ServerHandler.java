package diexun.tcp;

import java.nio.charset.Charset;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ServerHandler extends SimpleChannelHandler {

    private static UserChannelGroup userChannelGroup; // 管道组,存放客户端管道

    public ServerHandler() {
        super();
        /* 获得客户端在服务器端注册的所有信息，用于向所有客户端分发消息 */
        userChannelGroup = new UserChannelGroup();
    }

    /**
     * 用于扑捉客户端退出的消息。 并将其从服务器端的注册表中删掉。
     *
     * @param ctx
     * @param e
     * @throws java.lang.Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Channel channel = e.getChannel();
        channel.close();
        if (userChannelGroup.contains(channel)) {
            userChannelGroup.remove(channel);
        }
    }

    /**
     * 读取客户端消息
     *
     * @param ctx
     * @param e
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        ChannelBuffer buffer = (ChannelBuffer) e.getMessage();
        String content = buffer.toString(Charset.defaultCharset());
        JSONObject obj = (JSONObject) JSONValue.parse(content);
        int website = ((Long) obj.get(MsgConstants.WEBSITE_TAG)).intValue();
        Channel c = e.getChannel();
        switch (website) {
            case MsgConstants.BASIS: //基础数据
                BasisSelector.selector(obj, c);
                break;
//            case MsgConstants.APPAREL://服装网
//                ApparelSelector.selector(obj, c);
//                break;
            case MsgConstants.SHOSE://鞋包网
            case MsgConstants.BAG://箱包网:
//                ShoseSelector.selector(obj, c);
                break;
        }

    }

    /**
     * 对新连接的用户进行注册
     *
     * @param ctx
     * @param e
     * @throws java.lang.Exception
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        
        UserChannel userChannel = new UserChannel();
        userChannel.setChannel(e.getChannel());
        userChannelGroup.addUserChannel(userChannel);
    }

    public static UserChannelGroup getUserChannelGroup() {
        return userChannelGroup;
    }

}
