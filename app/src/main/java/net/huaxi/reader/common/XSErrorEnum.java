package net.huaxi.reader.common;

/**
 * 阅读客户端的CODE枚举;
 * Created by taoyingfeng
 * 2015/12/15.
 */
public enum XSErrorEnum {

    /**
     * 错误编码格式:错误编码有4-5位长度的整数组成
     */
    //章节内容
    CHAPTER_NOT_EXIST(-4000, "章节不存在(-4000)"),
    CHAPTER_DOWNLOAD_FAILED(-4001, "章节下载失败(-4001)"),
    CHAPTER_USER_HAS_SUBSCRIBE(10170, "用户已经订阅，可以阅读(10170)"),
    CHAPTER_NOT_SUBSCRIBE(10172, "章节未订阅(10172)"),
    CHAPTER_SUBSCRIBE_SUCCESS(10173, "章节订阅成功(10173)"),
    CHAPTER_SUBSCRIBE_FAILED(10174, "章节订阅失败(10174)"),
    CHAPTER_NEED_LOGIN(-100005, "需要登录(-100005)"),
    CHAPTER_VIP_READ(10180, "包月用户阅读包月作品，可以阅读(10180)"),
    CHAPTER_AUTO_SUBSCRIBE_FAILED(-4003, "章节自动订阅失败(-4003)"),
    CHAPTER_SHORT_BALANCE(10263, "余额不足(10263)"),
    CHAPTER_FILE_NOT_EXIST(-4005, "章节文件不存在(4005)"),
    CHAPTER_FILE_PARSE_FILED(-4006, "章节文件解析失败(-4006)"),
    CHAPTER_FILE_VERIGY_FILED(-4007, "文件内容校验失败(-4007)"),
    CHAPTER_FIRST(-4008, "已经是第一页"),
    CHAPTER_LAST(-4009, "已经是最后一页(-4009)"),
    CHAPTER_COPYRIGHT(-4010, "版权页面(-4010)"),

    //网络错误(!!!!来源自TaskErrorEnum,请同步修改!!!!!!)
    NETWORK_UNAVAILABLE(-3001, "当前网络不可用(-3001)"),
    NETWORK_CONNECTIONTIMEOUT(-3002, "网络连接超时(-3002)"),
    NETWORK_PARSE_URL_FAILED(-3003, "解析URL失败(-3003)"),
    NETWORK_NUKONWN(-3004, "未知错误(-3004)"),
    NETWORK_FILE_NOT_FOUNDED(-3005, "网络文件找不到(-3005)"),

    NETWORK_LOCAL_FILE_DELETED(-10273, "本地文件已被删除(-10273)"),

    CHAPTER_NOVER_UPDATE(10340, "目录不需要更新(10340)"),
    CHAPTER_UPDATE_FAILED(10341, "目录获取失败(10341)"),

    ERRORCODE_ERROR_SERVER_DATA(001, "当前网络异常，请重试(001)"),
    ERRORCODE_ERROR_NET(002, "当前网络异常，请重试(002)"),

    /****************************************************************/
    /*  请把错误代码写到分界线上部 */

    JSON_NO_FEILD(-2, "没有找到JSON字段(-2)"),
    JSON_NULL_POINT_EXCEPTION(-1, "JSON对象为空(-1)"),
    CANCEL(1, "用户取消(1)"),
    SUCCESS(0, "操作成功(0)"),
    TEMP_SUCCESS(2, "缓存数据获取成功(2)");


    private int code;
    private String msg;

    private XSErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

}

