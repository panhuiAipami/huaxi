package net.huaxi.reader.fragment;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by ZMW on 2016/5/19.
 */
public class BaseFragment extends Fragment {
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isResumed()){
            onVisibilityChangedToUser(isVisibleToUser, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint()){
            onVisibilityChangedToUser(true, false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getUserVisibleHint()){
            onVisibilityChangedToUser(false, false);
        }
    }

    /**
     * 当Fragment对用户的可见性发生了改变的时候就会回调此方法
     * @param isVisibleToUser true：用户能看见当前Fragment；false：用户看不见当前Fragment
     * @param isHappenedInSetUserVisibleHintMethod true：本次回调发生在setUserVisibleHintMethod方法里；false：发生在onResume或onPause方法里
     */
    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod){
        String pageName=getClass().getName();
        if(isVisibleToUser){
            if(pageName != null){
                MobclickAgent.onPageStart(pageName);
                Log.i("UmengPageTrack", pageName + " - display - "+(isHappenedInSetUserVisibleHintMethod?"setUserVisibleHint":"onResume"));
            }
        }else{
            if(pageName != null){
                MobclickAgent.onPageEnd(pageName);
                Log.w("UmengPageTrack", pageName + " - hidden - "+(isHappenedInSetUserVisibleHintMethod?"setUserVisibleHint":"onPause"));
            }
        }
    }
}
