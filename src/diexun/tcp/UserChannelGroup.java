/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diexun.tcp;

import java.util.ArrayList;
import java.util.List;
import org.jboss.netty.channel.Channel;

/**
 *
 * @author luoyuankang
 */
public class UserChannelGroup {

    private List<UserChannel> userChannels;

    public UserChannelGroup() {
        userChannels = new ArrayList<UserChannel>();
    }

    /**
     * 寻找是否有这个管道
     *
     * @param channel
     * @return
     */
    public boolean contains(Channel channel) {
        if (null != userChannels && !userChannels.isEmpty()) {
            for (UserChannel userChannel : userChannels) {
                if (userChannel.getChannel() == channel) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取所有连接句柄
     *
     * @return
     */
    public List<UserChannel> getUserChannelList() {
        return userChannels;
    }

    public UserChannel queryUserChannel(Channel channel) {
        for (UserChannel userChannel : userChannels) {
            if (userChannel.getChannel() == channel) {
                return userChannel;
            }
        }
        return null;
    }

    /**
     * 从组里删除这个管道
     *
     * @param channel
     */
    public void remove(Channel channel) {
        if (null != userChannels && !userChannels.isEmpty()) {
            for (UserChannel userChannel : userChannels) {
                if (userChannel.getChannel() == channel) {
                    userChannels.remove(userChannel);
                    break;
                }
            }
        }
    }

    /**
     * 添加用户管道
     *
     * @param userChannel
     */
    public void addUserChannel(UserChannel userChannel) {
        userChannels.add(userChannel);
    }
}
