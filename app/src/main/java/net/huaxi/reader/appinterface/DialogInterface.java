package net.huaxi.reader.appinterface;

/**
 * Created by Saud on 15/12/30.
 */
public interface DialogInterface {

    /**
     * 点击dialog中的某个控件后，关闭dialog的回调
     */
      void closeSettingWindow();


      void openSettingWindow();

    /**
     * 点击dialog中的目录点击，
     */
      void openDirectoryWindow();

    /**
     * 夜间模式回调，
     */
      void isNight(boolean isnight);


    /**
     * 关掉底部的窗口
     */
    void closeBottomWindow();
}
