package net.huaxi.reader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.spriteapp.booklibrary.config.HuaXiConfig;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.listener.ChannelListener;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.activity.HomeActivity;
import com.spriteapp.huaxireader.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String SIGN_SECRET = "jm6j32avpdkfd1s3o5gnnqs9my5vuujco2zv37";
    public static final int CLIENT_ID = 8;
    private static final int CHANNEL_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HuaXiConfig config = new HuaXiConfig.Builder().setContext(this)
                .setChannelListener(new ChannelListener() {
                    @Override
                    public void toLoginPage(Context context) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void showShareDialog(Context context, BookDetailResponse shareDetail, boolean isNightMode) {
                        Toast.makeText(context, "分享", Toast.LENGTH_SHORT).show();
                    }
                })
                .setChannelId(CHANNEL_ID)
                .setClientId(CLIENT_ID)
                .setSignSecret(SIGN_SECRET).build();
        HuaXiSDK.getInstance().init(config);
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

}
