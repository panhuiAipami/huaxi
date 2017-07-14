package net.huaxi.reader.common;

import com.tools.commonlibs.task.EasyTask;
import com.tools.commonlibs.tools.LogUtils;
import com.umeng.message.PushAgent;

/**
 * Created by ZMW on 2016/4/1.
 */
public class UmengHelper {
    private static UmengHelper umengHelper;
    public static UmengHelper getInstance(){
        if(umengHelper==null){
            umengHelper=new UmengHelper();
        }
        return umengHelper;
    }


    public void addAlias(){
            if(UserHelper.getInstance().getUserId()!=null){
                final String uid=UserHelper.getInstance().getUserId();
                EasyTask.addTask(new Runnable() {
                    @Override
                    public void run() {
                        PushAgent mPushAgent = PushAgent.getInstance(AppContext.getInstance());
                        mPushAgent.setDebugMode(true);
                        mPushAgent.enable();
                        try {
                            mPushAgent.addAlias(uid, "xs");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        LogUtils.debug("用户id===" + UserHelper.getInstance().getUserId() + "已经绑定");
                    }
                });
            }
    }

    public void removeAlias(){
        if(UserHelper.getInstance().getUserId()!=null){
            final String uid=UserHelper.getInstance().getUserId();
            EasyTask.addTask(new Runnable() {
                @Override
                public void run() {
                    PushAgent mPushAgent = PushAgent.getInstance(AppContext.getInstance());
                    mPushAgent.setDebugMode(true);
                    mPushAgent.enable();
                    try {
                        mPushAgent.removeAlias(uid, "xs");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    LogUtils.debug("用户id===" + UserHelper.getInstance().getUserId() + "已经绑定");
                }
            });
        }
    }
}
