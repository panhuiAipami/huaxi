package net.huaxi.reader.util;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import com.tools.commonlibs.tools.PhoneUtils;
import net.huaxi.reader.book.SharedPreferenceUtil;

/**
 * Created by Saud on 16/1/9.
 */
public class ScreenLightUtils {


    /**
     * 保存当前的屏幕亮度值，并使之生效
     */
    public static void setScreenBrightness(Activity activity, int paramInt) {
        if (paramInt != -1) {//获取到亮度值不为0，说明用户单独设置过屏幕亮度，还原上次设置的亮度
            if ("YQ601".equals(PhoneUtils.getPhoneModel())) {//对锤子手机进行单独配置
                if (paramInt < 23) {
                    paramInt = 23;//锥子手机设置亮度在23
                }
            } else {
                if (paramInt < 10) {
                    paramInt = 10;
                }
            }
            Window localWindow = activity.getWindow();
            WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
            float f = paramInt / 255.0F;
            localLayoutParams.screenBrightness = f;
            localWindow.setAttributes(localLayoutParams);
        }
    }

    /**
     * 初始化屏幕亮度到上次设置的值
     *
     * @param activity
     */
    public static void initScreenBright(Activity activity) {
        setScreenBrightness(activity, getSaveBrightness());
    }

    /**
     * 保存屏幕亮度
     *
     * @param progress
     */
    public static void savaScreenBrightness(int progress) {
        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveScreenBrightness(progress);
    }

    /**
     * 获的上次设置的屏幕亮度
     *
     * @return
     */
    public static int getSaveBrightness() {
        return SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getScreenBrightness();
    }


}
