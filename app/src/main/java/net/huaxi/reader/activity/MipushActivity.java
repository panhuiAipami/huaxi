package net.huaxi.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.spriteapp.booklibrary.ui.activity.WebViewActivity;
import com.umeng.message.UmengNotifyClickActivity;
import com.umeng.message.entity.UMessage;

import net.huaxi.reader.MainActivity;
import net.huaxi.reader.R;

import org.android.agoo.common.AgooConstants;

import java.util.Map;

import static com.spriteapp.booklibrary.ui.activity.HomeActivity.ADVERTISEMENT;

public class MipushActivity extends UmengNotifyClickActivity {
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Intent intent = new Intent();
            intent.setClass(MipushActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            addMessageToIntent(intent, (String) msg.obj);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mipush_test);
    }


    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        Message message = Message.obtain();
        message.obj = body;
        handler.sendMessage(message);
    }

    /**
     * 用于将UMessage中自定义参数的值放到intent中传到SplashActivity中，SplashActivity中对友盟推送时自定义消息作了专门处理
     * @param intent 需要增加值得intent
     * @param msg    需要增加到intent中的msg
     */
    private void addMessageToIntent(Intent intent, String msg) {
        UMessage uMessage = new Gson().fromJson(msg, UMessage.class);
        if (intent == null || uMessage == null || uMessage.extra == null) {
            return;
        }
//        try {
//            JSONObject jsonObject = new JSONObject(msg.toString());
//            JSONObject jsonObject2 = jsonObject.getJSONObject("body");
//            String act = jsonObject2.getString("activity");
//
//            if(!TextUtils.isEmpty(act)){
//                activityPath = act;
//                String packgeName = activityPath.substring(0, activityPath.lastIndexOf("."));
//                String activity = activityPath;
//                intent.setComponent(new ComponentName(packgeName, activity));
//                Log.e("MipushTestActivity", packgeName + "------>uMessage：" + activity);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        int from = 0;
        for (Map.Entry<String, String> entry : uMessage.extra.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null) {
                intent.putExtra(key, value);
                if (key.equals(WebViewActivity.LOAD_URL_TAG) && !TextUtils.isEmpty(value)) {
                    from = 2;
                }
            }
        }
        intent.putExtra(ADVERTISEMENT, from);
    }
}
