package net.huaxi.reader.util;

import android.app.Activity;

import com.google.gson.Gson;
import net.huaxi.reader.book.SharedPreferenceUtil;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Saud on 16/3/25.
 */
public class TempUtils {

    /**
     * 保持list
     */
    public static <T extends Object> void savaTemp(Activity activity, Gson gson, String key, List<T> list) {
        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getShaPreferencesInstance(activity);
        String prefStr = gson.toJson(list);
        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveString(key, prefStr);
    }


    /**
     * 多去保持的list
     *
     * @return
     */
    public static <T extends Object> List<T> getTemp(Activity activity, Gson gson, String key , Type type) {
        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getShaPreferencesInstance(activity);
        String str = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getString(key);
        List<T> list = gson.fromJson(str, type);
        return list;
    }


}
