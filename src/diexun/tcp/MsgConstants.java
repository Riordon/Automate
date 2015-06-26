/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diexun.tcp;

/**
 *
 * @author 罗远康
 */
public class MsgConstants {

    public static final String WEBSITE_TAG = "website";

    public static final String APPAREL_REFER = "fz";//服装网推送识别码
    public static final String SHOSE_REFER = "xy";//鞋业网推送识别码
    public static final String BAG_REFER = "xb";//箱包网推送识别码
    public static final String EPD_REFER = "epd_xb";//EPD鞋包网推送识别码

    public static final String NOWPOPULAR_REFER = "zzlx"; //正在流行推送简码
    public static final String FASHIONSTYLE_REFER = "ssks";//时尚款式推送简码

    public static final String SUCCESS = "1";
    public static final String FAILURE = "0";
    public static final String FAILURE_REPEAT = "-1"; //失败重名
    /**
     * website_key
     */
    public static final int BASIS = 0; //基础服务 key
    public static final int APPAREL = 1;//服装网 key
    public static final int SHOSE = 2;//鞋业网 key
    public static final int BAG = 3;//箱包网 key
    public static final int EPD = 4;//箱包网 key
    public static final int PUSHMSG = 100;//推送消息key

    public static final String KEY_TAG = "tag";
    public static final String COLUMN = "column";

    public static final int QUERY_LIST = 1; //查询列表
    public static final int QUERY_SORT = 2; //查询分类
    public static final int QUERY_COLUMN = 3; //查询栏目
    public static final int QUERY_SUBCOLUMN = 4;//查询子栏目
    public static final int QUERY_ERROR_LIST = 5;//查询错误信息列表
    public static final int DELETE = 6;
    public static final int DEL_ERROR_INFO = 11; //删除错误文件
    public static final int DEL_FILTER_INFO = 12; //删除错误信息
    public static final int QUERY_FILTER_LIST = 13; //查询过滤信息
    public static final long SHOE_EXIT = 100;
    public static final String VALUE = "value";
    public static final int SHOW_TIEM = 14; //设置显示时间tag
    public static final int AUTO_DEL_FILTER_INFO = 15;//自动删除过来信息

    /*以下是对用户的一些操作tag*/
    public static final int USER_LOGIN = 1; //用户登录
    public static final int USER_LIST = 2;// 获取用户列表
    public static final int USER_ADD = 3; // 用户添加
    public static final int USER_DEL = 4; //用户删除
    public static final int USER_UPDATE = 5; //用户修改
    public static final int USER_UPDATEPSW = 6;//修改用户密码
}
