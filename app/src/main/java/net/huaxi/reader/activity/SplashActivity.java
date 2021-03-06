package net.huaxi.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.util.FileHelper;
import com.spriteapp.booklibrary.util.GlideUtils;

import net.huaxi.reader.MainActivity;
import net.huaxi.reader.R;
import net.huaxi.reader.utils.PreferenceHelper;

import static com.spriteapp.booklibrary.ui.activity.HomeActivity.ADVERTISEMENT;
import static net.huaxi.reader.MainActivity.SEXTIME;

public class SplashActivity extends AppCompatActivity {
    private ImageView splash;
    private TextView skip_ad;
    private int num = 3;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    handler.removeCallbacksAndMessages(null);
                    isGotoSex(0);
                    break;
                case 1:
                    if (num > 1) {
                        num--;
                        skip_ad.setText("跳过" + num);
                        handler.sendEmptyMessageDelayed(1, 1000);
                    } else {
                        handler.sendEmptyMessageDelayed(0, 1000);
                    }
                    break;
            }
        }
    };

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        if (intent != null) {
//            String app = intent.getStringExtra("app");
//            if (!TextUtils.isEmpty(app)) {
//                Log.d("protected", "app===" + app);
//                if (app.equals("mi")) {
//                    //请求赠送花瓣的接口
//                }
//            } else {
//                Log.d("protected", "app");
//            }
//        } else {
//            Log.d("protected", "intent===null");
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.activity_splash);
//        Intent intent = getIntent();
//        if (intent != null) {
//            String app = intent.getStringExtra("app");
//            if (!TextUtils.isEmpty(app)) {
//                Log.d("savedInstanceState", "app===" + app);
//            } else {
//                Log.d("savedInstanceState", "app");
//            }
//        } else {
//            Log.d("savedInstanceState", "intent===null");
//        }
        splash = (ImageView) findViewById(R.id.splash);
        skip_ad = (TextView) findViewById(R.id.skip_ad);
        skip_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(0);
            }
        });
        try {
            Log.d("loadImage", "url==");
            String url = FileHelper.readObjectFromJsonFile(this, "start_page", String.class);

            if (url != null && !url.isEmpty() && net.huaxi.reader.utils.Util.isNetAvailable(this)) {
                Log.d("loadImage", "url===" + url);
                GlideUtils.loadImage(splash, url, this);
                skip_ad.setVisibility(View.VISIBLE);
                splash.setOnClickListener(new View.OnClickListener() {//广告页跳转
                    @Override
                    public void onClick(View v) {
                        String link = FileHelper.readObjectFromJsonFile(SplashActivity.this, "start_page_url", String.class);
                        if (link != null && !link.isEmpty() && net.huaxi.reader.utils.Util.isNetAvailable(SplashActivity.this)) {
                            handler.removeCallbacksAndMessages(null);
                            isGotoSex(1);
                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void isGotoSex(int type) {
        Intent intent;
        boolean sex_first = PreferenceHelper.getBoolean(SEXTIME, true);
        if (type == 1) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra(ADVERTISEMENT, 1);
        } else {
            if (sex_first) {
                intent = new Intent(SplashActivity.this, SexActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, MainActivity.class);

            }
        }

        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sendHandler();
    }

    private void sendHandler() {
        if (skip_ad.getVisibility() == View.GONE)
            handler.sendEmptyMessageDelayed(0, 3000);
        else
            handler.sendEmptyMessageDelayed(1, 1000);
    }

}
