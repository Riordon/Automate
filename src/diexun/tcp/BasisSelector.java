/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diexun.tcp;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import diexun.user.User;
import diexun.user.UserManager;

/**
 *
 * @author luoyuankang
 */
public class BasisSelector {

    /**
     * 基础服务选择器
     *
     * @param obj
     * @param channel
     */
    public static void selector(JSONObject obj, Channel channel) {
        int tag = ((Long) obj.get(MsgConstants.KEY_TAG)).intValue();
        String result = "";
        switch (tag) {
            case MsgConstants.USER_LOGIN:
                result = userLogin(obj, channel);
                break;
            case MsgConstants.USER_LIST:
                result = getUserList(obj);
                break;
            case MsgConstants.USER_ADD:
                result = addUser(obj);
                break;
            case MsgConstants.USER_DEL:
                result = delUser(obj);
                break;
            case MsgConstants.USER_UPDATE:
                result = updateUser(obj);
                break;
        }

        byte[] bytes = result.getBytes();
        ChannelBuffer buffer2 = ChannelBuffers.buffer(bytes.length);
        buffer2.writeBytes(bytes);
        channel.write(buffer2);
    }

    /**
     * 用户登录
     *
     * @param obj
     * @param channel
     * @return
     */
    public static String userLogin(JSONObject obj, Channel channel) {
        String username = (String) obj.get("username");
        String password = (String) obj.get("password");
        JSONObject result = new JSONObject();
        result.put(MsgConstants.WEBSITE_TAG, MsgConstants.BASIS);
        result.put(MsgConstants.KEY_TAG, (Long) obj.get(MsgConstants.KEY_TAG));
        boolean isSuc = UserManager.userLogin(username, password);
        result.put("isSuc", isSuc ? MsgConstants.SUCCESS : MsgConstants.FAILURE);
        User user = UserManager.getUserByName(username);
        result.put("username", user.getUsername());
        result.put("password", user.getPassword());
        result.put("role", user.getRole());
        result.put("websitestr", user.getWebsite());
        result.put("push_website", user.getPush_website());
        result.put("push_sort", user.getPush_sort());
        result.put("push_column", user.getPush_column());
        result.put("push_column_type", user.getPush_column_type());
        UserChannel userChannel = ServerHandler.getUserChannelGroup().queryUserChannel(channel);
        if (null != userChannel) { //登陆成功，绑定用户到这个管道里
            userChannel.setUser(user);
        }
        return result.toString();
    }

    /**
     * 获取所有用户列表
     *
     * @param obj
     * @return
     */
    public static String getUserList(JSONObject obj) {
        JSONObject result = new JSONObject();
        result.put(MsgConstants.WEBSITE_TAG, MsgConstants.BASIS);
        result.put(MsgConstants.KEY_TAG, (Long) obj.get(MsgConstants.KEY_TAG));
        List<User> list = UserManager.getUserListByCache();
        if (null != list && !list.isEmpty()) {
            JSONArray array = new JSONArray();
            for (User user : list) {
                JSONObject temp = new JSONObject();
                temp.put("username", user.getUsername());
                temp.put("password", user.getPassword());
                temp.put("role", user.getRole());
                temp.put("websitestr", user.getWebsite());
                temp.put("push_website", user.getPush_website());
                temp.put("push_sort", user.getPush_sort());
                temp.put("push_column", user.getPush_column());
                temp.put("push_column_type", user.getPush_column_type());
                array.add(temp);
            }
            result.put("datas", array);
        }
        return result.toString();
    }

    /**
     * 添加用户
     *
     * @param obj
     * @return
     */
    public static String addUser(JSONObject obj) {
        JSONObject result = new JSONObject();
        result.put(MsgConstants.WEBSITE_TAG, MsgConstants.BASIS);
        result.put(MsgConstants.KEY_TAG, (Long) obj.get(MsgConstants.KEY_TAG));
        String username = (String) obj.get("username");
        String password = (String) obj.get("password");
        String role = (String) obj.get("role");
        String website = (String) obj.get("websitestr");
        User user = new User();
        user.setUsername(username);
        List<User> list = UserManager.getUserListByCache();
        if (null != list && !list.isEmpty()) {
            for (User user1 : list) {
                if (user1.getUsername().trim().equals(user.getUsername().trim())) {
                    result.put("isSuc", MsgConstants.FAILURE_REPEAT);
                    return result.toString();
                }
            }
        }
        user.setPassword(password);
        user.setRole(role);
        user.setWebsite(website);
        boolean isSuc = UserManager.addUser(user);
        result.put("isSuc", isSuc ? MsgConstants.SUCCESS : MsgConstants.FAILURE);
        return result.toString();
    }

    /**
     * 更新用户
     *
     * @param obj
     * @return
     */
    public static String updateUser(JSONObject obj) {
        JSONObject result = new JSONObject();
        result.put(MsgConstants.WEBSITE_TAG, MsgConstants.BASIS);
        result.put(MsgConstants.KEY_TAG, (Long) obj.get(MsgConstants.KEY_TAG));
        User user = new User();
        System.out.println(obj.toString());
        if (null != obj.get("username")) {
            user.setUsername((String) obj.get("username"));
        }
        if (null != obj.get("password")) {
            user.setPassword((String) obj.get("password"));
        }
        if (null != obj.get("role")) {
            user.setRole((String) obj.get("role"));
        }
        if (null != obj.get("websitestr")) {
            user.setWebsite((String) obj.get("websitestr"));
        }
        if (null != obj.get("push_website")) {
            user.setPush_website(((JSONObject) obj.get("push_website")).toJSONString());
        }
        if (null != obj.get("push_sort")) {
            user.setPush_sort(((JSONArray) obj.get("push_sort")).toJSONString());
        }
        if (null != obj.get("push_column")) {
            user.setPush_column(((JSONArray) obj.get("push_column")).toJSONString());
        }
        if (null != obj.get("push_column_type")) {
            user.setPush_column_type(((JSONObject) obj.get("push_column_type")).toJSONString());
        }
        boolean isSuc = UserManager.updateUser(user);
        result.put("isSuc", isSuc ? MsgConstants.SUCCESS : MsgConstants.FAILURE);
        return result.toString();
    }

    /**
     * 删除用户
     *
     * @param obj
     * @return
     */
    public static String delUser(JSONObject obj) {
        JSONObject result = new JSONObject();
        result.put(MsgConstants.WEBSITE_TAG, MsgConstants.BASIS);
        result.put(MsgConstants.KEY_TAG, (Long) obj.get(MsgConstants.KEY_TAG));
        String username = (String) obj.get("username");
        boolean isSuc = UserManager.delUser(username);
        result.put("isSuc", isSuc ? MsgConstants.SUCCESS : MsgConstants.FAILURE);
        return result.toString();
    }

}
