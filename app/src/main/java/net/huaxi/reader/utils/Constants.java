package net.huaxi.reader.utils;

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
     * 外部存储设备的根路径下面的gazhi文件夹
     */
    public static final String SDPath = SDROOTPath + "/huaxi";
    /**
     * APP updata
     */
    public static final String SDPath_updata_app = SDPath + "/app_updata/";
    /**
     * 缓存目录
     */
    public static final String SDPath_cache = SDPath + "/cache";
    /**
     * 实体类
     */
    public static final String SDPath_bean = SDPath + "/bean";



}
