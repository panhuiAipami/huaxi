package net.huaxi.reader.callback;


/**
 * Created by panhui on 2017/8/2.
 */

public class ListenerManager {
    public static ListenerManager instance;

    public static ListenerManager getInstance() {
        if (instance == null) {
            instance = new ListenerManager();
        }
        return instance;
    }
    private ShareResult result;
    private ShareBeanCallBack shareBeanCallBack;
    private FinishWebActivity finishWebActivity;
    private FinishActivity finishActivity;
    public FinishActivity getFinishActivity() {
        return finishActivity;
    }

    public void setFinishActivity(FinishActivity finishActivity) {
        this.finishActivity = finishActivity;
    }

    public ShareResult getResult() {
        return result;
    }

    public void setResult(ShareResult result) {
        this.result = result;
    }

    public ShareBeanCallBack getShareBeanCallBack() {
        return shareBeanCallBack;
    }

    public void setShareBeanCallBack(ShareBeanCallBack shareBeanCallBack) {
        this.shareBeanCallBack = shareBeanCallBack;
    }



    public FinishWebActivity getFinishWebActivity() {
        return finishWebActivity;
    }

    public void setFinishWebActivity(FinishWebActivity finishWebActivity) {
        this.finishWebActivity = finishWebActivity;
    }
}
