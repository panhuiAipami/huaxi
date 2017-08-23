package net.huaxi.reader.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.ResponseHelper;

import org.json.JSONObject;

import java.util.Map;

public class MyInfoChangeNikeNameActivity extends BaseActivity {
    private TextView tvSave;
    private ImageView ivBack;
    private EditText etNewName;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info_change_nike_name);
        tvSave = (TextView) findViewById(R.id.myinfo_changenikename_save_textview);
        ivBack = (ImageView) findViewById(R.id.myinfo_changenikename_back_imageview);
        etNewName = (EditText) findViewById(R.id.myinfo_changenikename_edittext);
        etNewName.setText(getIntent().getStringExtra("name"));
        
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nikename = etNewName.getText().toString();
                if (StringUtils.isBlank(nikename))
                        //|| !nikename.matches(MyInfoActivity.REGULAR_USERNAEM))
                {
                    ViewUtils.toastShort("请输入正确的昵称");
                    return;
                }
                Map<String, String> map = CommonUtils.getPublicPostArgs();
                map.put("u_nickname", nikename);
                PostRequest request = new PostRequest(URLConstants.UPDATE_USER, new Response
                        .Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (!ResponseHelper.isSuccess(response)) {
                            LogUtils.debug("response===nickname1" + response.toString());
                            ViewUtils.toastShort(getString(R.string.myinfo_change_nikename));
                            return;
                        }
                        LogUtils.debug("response===nickname2" + response.toString());
                        LogUtils.debug("nickname 1");
                        ViewUtils.toastShort("修改成功");
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ViewUtils.toastShort(getString(R.string.network_server_error));
                    }
                }, map);
                RequestQueueManager.addRequest(request);
            }
        });
    }
}
