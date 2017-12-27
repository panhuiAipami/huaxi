package net.huaxi.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.spriteapp.booklibrary.util.FileHelper;
import com.spriteapp.booklibrary.util.GlideUtils;

import net.huaxi.reader.MainActivity;
import net.huaxi.reader.R;
import net.huaxi.reader.utils.PreferenceHelper;

import static net.huaxi.reader.MainActivity.SEXTIME;

public class SplashActivity extends AppCompatActivity {
    private ImageView splash;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent;
            switch (msg.what) {
                case 0:
                    boolean sex_first = PreferenceHelper.getBoolean(SEXTIME, true);
                    if (sex_first) {
                        intent = new Intent(SplashActivity.this, SexActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, MainActivity.class);

                    }
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.activity_splash);
        splash = (ImageView) findViewById(R.id.splash);
        try {
            Log.d("loadImage", "url==");
            String url = FileHelper.readObjectFromJsonFile(this, "start_page", String.class);
            if (url != null && !url.isEmpty() && net.huaxi.reader.utils.Util.isNetAvailable(this)) {
                Log.d("loadImage", "url===" + url);
                GlideUtils.loadImage(splash, url, this);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        sendHandler();
    }

    private void sendHandler() {
        handler.sendEmptyMessageDelayed(0, 3000);
    }

}
