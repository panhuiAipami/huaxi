package net.huaxi.reader.appinterface;

/**
 * Created by panhui on 2017/8/16.
 */

public class ListenerManager {
    public  static ListenerManager instance;

    public static ListenerManager getInstance() {
        if (instance == null) {
            instance = new ListenerManager();
        }
        return instance;
    }

    ShareResult result;
    GoToShuJia goToShuJia;
    CallBackHuaweiPayInfo backHuaweiPayInfo;

    public CallBackHuaweiPayInfo getBackHuaweiPayInfo() {
        return backHuaweiPayInfo;
    }

    public void setBackHuaweiPayInfo(CallBackHuaweiPayInfo backHuaweiPayInfo) {
        this.backHuaweiPayInfo = backHuaweiPayInfo;
    }

    public ShareResult getResult() {
        return result;
    }

    public void setResult(ShareResult result) {
        this.result = result;
    }

    public GoToShuJia getGoToShuJia() {
        return goToShuJia;
    }

    public void setGoToShuJia(GoToShuJia goToShuJia) {
        this.goToShuJia = goToShuJia;
    }
}
