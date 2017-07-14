package net.huaxi.reader.util;

import android.content.Context;

import com.tools.commonlibs.tools.LogUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by ZMW on 2016/5/16.
 * 定义事件
 */
public class UMEventAnalyze {

    //充值页面
    public static final String RECHARGE_PAGE = "XS_H001";//充值页
    public static final String RECHARGE_WEIXIN = "XS_H003";//微信充值
    public static final String RECHARGE_ALIPAY = "XS_H004";//支付宝充值
    public static final String RECHARGE_HISTORY = "XS_H005";//充值-充值记录
    public static final String RECHARGE_CANCEL = "XS_H006";//取消
    public static final String RECHARGE_QQ = "XS_H007";//QQ充值
    //会员
    public static final String VIP_PAGE = "XS_I001";//会员页
    public static final String VIP_WEIXIN = "XS_I003";//会员-微信支付（Android)
    public static final String VIP_ALIPAY = "XS_I004";//会员-支付宝支付（Android)
    public static final String VIP_CANCEL = "XS_I005";//取消
    public static final String VIP_QQ = "XS_I006";//会员-QQ支付（Android)
    //注册
    public static final String REGISTER_SEND_MESSAGE = "register_send_message";
    public static final String REGISTER_COMMIT = "register_commit";
    //登录
    public static final String LOGIN_READPAGE_TO_LOGIN = "login_readpage_to_login";
    public static final String LOGIN_SUCCESS = "login_success";
    //搜索
    public static final String SEARCH_PAGE = "XS_D001";//搜索页
    public static final String SEARCH_SHOW_DATA = "XS_D002";//搜索结果页
    public static final String SEARCH_SHOW_NULL = "XS_D003";//搜索无结果页
    public static final String SEARCH_HOT = "XS_D004";//搜索-热门搜索点击
    public static final String SEARCH_HISTORY = "XS_D005";//搜索-搜索记录点击
    public static final String SEARCH_RESULT = "XS_D007";//搜索结果-点击
    public static final String SEARCH_ABOUT = "XS_D008";//搜搜-无结果-相关推荐点击
    public static final String SEARCH_SMART = "XS_D009";//搜索-联想结果点击
    //详情
    public static final String BOOKINFO_PAGE = "XS_F001";//介绍页
    public static final String BOOKINFO_ABOUT = "XS_F002";//介绍页-简介
    public static final String BOOKINFO_CATALOGUE = "XS_F003";//介绍页-目录
    public static final String BOOKINFO_COMMENT = "XS_F004";//介绍页-评论
    public static final String BOOKINFO_OTHER = "XS_F005";//介绍页-作者其他作品
    public static final String BOOKINFO_SAME = "XS_F006";//介绍页-同类作品
    public static final String BOOKINFO_CATALOGUE_START = "XS_F007";//介绍页-目录-阅读页
    public static final String BOOKINFO_ADDSHELF = "XS_F008";//介绍页-加入书架
    public static final String BOOKINFO_READ = "XS_F009";//介绍页-开始阅读
    public static final String BOOKINFO_DOWNLOAD = "XS_F010";//介绍页-下载
    public static final String BOOKINFO_SHARE = "XS_F016";//介绍页-分享
    //下载
    public static final String DOWNLOAD_CHILD_SELECT = "XS_F011";//介绍页-下载-章节选择
    public static final String DOWNLOAD_SELECT_ALL = "XS_F012";//介绍页-下载-章节全选
    public static final String BOOKINFO_SELECT_NULL = "XS_F013";//介绍页-下载-章节全选-取消
    public static final String DOWNLOAD_BUY = "XS_F014";//介绍页-下载-确认下载
    public static final String DOWNLOAD_RECHARGE = "XS_F015";//介绍页-下载-充值
    //书城
    public static final String BOOKCITY_BOY = "XS_C0001";//男
    public static final String BOOKCITY_GIRL = "XS_C0002";//女
    public static final String BOOKCITY_SEACHER = "XS_C0003";//书城-搜索
    public static final String BOOKCITY_START_BOOKINFO = "XS_C0004";//书城-搜索
    //书架
    public static final String BOOK_SHELF_PAGE = "XS_B001";//书架-最近阅读-触发
    public static final String BOOK_SHELF_SHOW_LAST = "XS_B002";//书架-最近阅读-触发
    public static final String BOOK_SHELF_LAST_START = "XS_B003";//书架-最近阅读-作品点击
    public static final String BOOK_SHELF_AD = "XS_B004";//书架-最近阅读-活动位点击
    public static final String BOOK_SHELF_HIDDEN_LAST = "XS_B005";//书架-最近阅读-收起
    public static final String BOOK_SHELF_TO_DELETE = "XS_B006";//书架-启动编辑
    public static final String BOOK_SHELF_DELETE = "XS_B007";//书架-启动编辑-删除
    public static final String BOOK_SHELF_START = "XS_B008";//书架-作品点击
    public static final String BOOK_SHELF_READED_RECORD_CLICK = "XS_B009"; //书架-最近阅读-书籍-点击
    public static final String BOOK_SHELF_ORDER_RECORD_CLICK = "XS_B010"; //书架-订阅记录-书籍-点击
    public static final String BOOK_SHELF_READED_RECORD = "XS_B011"; //书架-最近阅读
    public static final String BOOK_SHELF_ORDER_RECORD = "XS_B012"; //书架-订阅记录


    //分类
    public static final String CLASSIFY_PAGE = "XS_E001";//分类页
    public static final String CLASSIFY_CHILD_PAGE = "XS_E002";//分类-列表页
    public static final String CLASSIFY_PARENT_BOY = "XS_E003";//男生分类点击
    public static final String CLASSIFY_PARENT_GIRL = "XS_E004";//女生分类点击
    public static final String CLASSIFY_PAY_FREE = "XS_E005";//分类筛选-免费
    public static final String CLASSIFY_PAY_YES = "XS_E006";//分类筛选-收费
    public static final String CLASSIFY_PAY_VIP = "XS_E007";//分类筛选-会员
    public static final String CLASSIFY_PAY_SENTIMENT = "XS_E008";//分类筛选-人气
    public static final String CLASSIFY_PAY_NUM = "XS_E009";//分类筛选-字数
    public static final String CLASSIFY_PAY_COLLECT = "XS_E010";//分类筛选-畅销
    public static final String CLASSIFY_PARENT_SEARCH = "XS_E011";//分类-搜索
    public static final String CLASSIFY_CHILD_SEARCH = "XS_E012";//分类-列表-搜索


    //阅读模块统计
    public static final String READPAGE_SHOW_TOOLBAR = "XS_G002";  //阅读页-工具栏呼出
    public static final String READPAGE_DOWNLOAD = "XS_G003"; //阅读页-下载
    public static final String READPAGE_CATELOG = "XS_G006";  //阅读页-目录；
    public static final String READPAGE_COMMENT = "XS_G007";  //阅读页-评论
    public static final String READPAGE_SETTINGS = "XS_G008";  //阅读页-设置
    public static final String READPAGE_MORE = "XS_G016";  //阅读页-更多
    public static final String READPAGE_NIGHT_MODEL = "XS_G026";  //阅读页-夜晚模式
    public static final String READPAGE_DAY_MODEL = "XS_G027";  //阅读页-白天模式
    public static final String READPAGE_LAST_PAGE = "XS_G032";  //阅读页-最后一页
    public static final String READPAGE_DETAIL = "XS_G004";  //阅读页-书籍详情
    public static final String READPAGE_SHARE = "XS_G005";  //阅读页-分享
    public static final String READPAGE_PRE_PAGE = "XS_G024";  //阅读页-上一章
    public static final String READPAGE_NEXT_PAGE = "XS_G025";  //阅读页-下一章
    public static final String READPAGE_LIGHT_CHANGE = "XS_G009";  //阅读页-设置-亮度调节
    public static final String READPAGE_TEXTSIZE_MAX = "XS_G010";  //阅读页-设置-字号加
    public static final String READPAGE_TEXTSIZE_MIN = "XS_G011";  //阅读页-设置-字号减
    public static final String READPAGE_BACKGROUND_YELLOW = "XS_G012";  //阅读页-设置-黄色背景
    public static final String READPAGE_BACKGROUND_WHITE = "XS_G013";  //阅读页-设置-白色背景
    public static final String READPAGE_BACKGROUND_PINK = "XS_G014";  //阅读页-设置-粉色背景
    public static final String READPAGE_BACKGROUND_GREEN = "XS_G015";  //阅读页-设置-绿色背景
    public static final String READPAGE_LIFT_NEXTPAGE_OPEN = "XS_G017";  //阅读页-更多-屏幕左侧翻页-开
    public static final String READPAGE_LIFT_NEXTPAGE_CLOSE = "XS_G018";  //阅读页-更多-屏幕左侧翻页-关
    public static final String READPAGE_AUTOORDER_OPEN = "XS_G021";  //阅读页-更多-自动订购-开
    public static final String READPAGE_AUTOORDER_CLOSE = "XS_G022";  //阅读页-更多-自动订购-关
    public static final String READPAGE_READ_PROGRESS = "XS_G023";  //阅读页-阅读进度条
    public static final String READPAGE_BACK = "XS_G031";  //阅读页-返回
    public static final String READPAGE_LASTPAGE_COMMENT = "XS_G033";  //阅读页-最后一页-发表评论
    public static final String READPAGE_LASTPAGE_LIKE = "XS_G034";  //阅读页-最后一页-猜你喜欢
    public static final String READPAGE_LASTPAGE_SHARE = "XS_G035";  //阅读页-最后一页-分享
    public static final String READPAGE_ORDER = "XS_G028";  //阅读页-订购提醒页面
    public static final String READPAGE_ORDER_OK = "XS_G029";  //阅读页-订购提醒页面-确认订购
    public static final String READPAGE_AUTOORDER_SELECT = "XS_G030";  //阅读页-订购提醒页面-勾选自动订购


    //阅读统计
    public static final String ONLINE_READING_COST = "DEV_001";        //在线阅读协议耗时时长
    public static final String OPEN_DETAIL_COST = "DEV_002";       //详情页打开耗时，
    public static final String RECHARGE_RESULT = "DEV_003";        //充值成功、失败、取消统计

    //底部导航
    public static final java.lang.String MAINACTIVITY_BOOKSHELF = "XS_A010";//书架
    public static final java.lang.String MAINACTIVITY_BOOKSTORE = "XS_A011";//书城
    public static final java.lang.String MAINACTIVITY_CLASSIFY = "XS_A012";//分类
    public static final java.lang.String MAINACTIVITY_USER = "XS_A013";//我的
    //评论
    public static final java.lang.String COMMENT_PAGE = "XS_J001";//评论页
    public static final java.lang.String COMMENT_WRITE = "XS_J002";//评论-发起评论
    public static final java.lang.String COMMENT_SEND = "XS_J003";//评论-确认发表

    //启动页
    public static final java.lang.String SPLASH_PAGE = "XS_A001";   //打开APP
    public static final java.lang.String SPLASH_SHOW = "XS_A002";   //闪屏展示
    public static final java.lang.String SPLASH_SELECT = "XS_A004";   //男女偏好页面展示
    public static final java.lang.String SPLASH_BOY = "XS_A005";   //男生偏好选择
    public static final java.lang.String SPLASH_GIRL = "XS_A006";  //女生偏好选择
    public static final java.lang.String SPLASH_TO_READ = "XS_A009";//启动-直接进入阅读页
    //用户中心
    public static final java.lang.String USER_PAGE = "XS_K001";//账户页访问
    public static final java.lang.String USER_TO_LOGIN = "XS_K003";//账户页-跳转登录
    public static final java.lang.String LOGIN_SUBMIT = "XS_K004";//登录页-登录
    public static final java.lang.String LOGIN_REGISTER = "XS_K005";//登录页-注册
    public static final java.lang.String LOGIN_BACK = "XS_K006";//登录页-返回
    public static final java.lang.String LOGIN_FORGET = "XS_K007";//登录页-忘记密码
    public static final java.lang.String LOGIN_QQ = "XS_K008";//登录页-QQ
    public static final java.lang.String LOGIN_WX = "XS_K009";//登录页-微信
    public static final java.lang.String LOGIN_REGISTER_SEND = "XS_K010";//注册页-发送验证码
    public static final java.lang.String LOGIN_REGISTER_PERMISSION = "XS_K011";//注册页-用户许可
    public static final java.lang.String LOGIN_REGISTER_SEECCES = "XS_K012";//注册页-完成注册
    public static final java.lang.String USER_CHARGE = "XS_K013";//账户页-充值
    public static final java.lang.String USER_VIP = "XS_K014";// 账户页-会员
    public static final java.lang.String USER_COMSUME = "XS_K015";// 账户页-消费记录
    public static final java.lang.String USER_SETTING = "XS_K016";// 账户页-设置
    public static final java.lang.String SETTING_MSG_OPEN = "XS_K017";// 账户页-设置-通知提醒
    public static final java.lang.String USER_REPORT = "XS_K018";// 账户页-设置-问题反馈
    public static final java.lang.String SETTING_ABOUT = "XS_K019";// 账户页-设置-关于我们
    public static final java.lang.String SETTING_PASIE = "XS_K020";// 账户页-设置-赞一个
    public static final java.lang.String USER_HELP = "XS_K021";// 账户页-设置-使用帮助
    public static final java.lang.String SETTING_CACHE = "XS_K022";// 账户页-设置-清空缓存
    public static final java.lang.String SETTING_LOGOUT = "XS_K024";// 账户页-设置-退出登录

    public static void countEvent(Context context, String id) {
        MobclickAgent.onEvent(context, id);
        LogUtils.debug("eventId==" + id);
    }


    public static void intervalEvent(Context context, String id, long interval) {
        MobclickAgent.onEvent(context, id, String.valueOf(interval));
        LogUtils.debug("eventId==" + id + " value =" + interval + " ms");
    }

    public static void resultEvent(Context context, String id, int result) {
        MobclickAgent.onEvent(context, id, String.valueOf(result));
        LogUtils.debug("eventId==" + id + " result =" + result);
    }

    public static void countEvent(Context context, String id, String type) {
        MobclickAgent.onEvent(context, id, type);
        LogUtils.debug("eventId==" + id + " type =" + type);
    }


}
