package net.huaxi.reader.book.render;

/**
 * 页面渲染状态
 * taoyingfeng
 * 2016/1/9.
 */
public class ReadPageState {
    //阅读状态
    public static final int BOOKTYPE_NORMAL = 0;
    //加载中状态
    public static final int BOOKTYPE_LOADING = 1;
    //无网络
    public static final int BOOKTYPE_NONE_NETWORK = 2;
    //加载失败
    public static final int BOOKTYPE_ERROR = 3;
    //进入购买订单
    public static final int BOOKTYPE_ORDER_PAY = 4;
    //充值状态
    public static final int BOOKTYPE_RECHARGE = 5;
    //登录阅读
    public static final int BOOKTYPE_UNLOGIN = 6;
    //首页版权
    public static final int BOOKTYPE_COPYRIGHT = 7;

}
