package com.spriteapp.booklibrary.util;

import android.os.Environment;

/**
 * Created by panhui on 2017/8/7.
 */

public class Constants {
    /**
     * SD卡根目录
     */
    public static final String SDROOTPath = Environment.getExternalStorageDirectory().getPath();
    /**
     * 外部存储设备的根路径下面的huaxi文件夹
     */
    public static final String SDPath = SDROOTPath + "/huaxi";
    /**
     * APP updata
     */
    public static final String SDPath_updata_app = SDPath + "/app_update/";
    /**
     * 缓存目录
     */
    public static final String SDPath_cache = SDPath + "/cache";
    /**
     * 实体类
     */
    public static final String SDPath_bean = SDPath + "/bean";
    /**
     * 下载APP action
     */
    public static final String ACTION_DOWNLOAD = "ACTION_XS_APK_DOWNLOAD";
    /**
     * 用户信息
     */
    public final static String USERINFO_FILE = "huaxi_user_info.txt";
    /**
     * APP updata
     */
    public static final String IMG = "/huaxi/image/";


}
