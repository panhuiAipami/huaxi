package net.huaxi.reader.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.BitmapWriteTool;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.User;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.dialog.PhotoDialog;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.thread.UploadPhotoTask;
import net.huaxi.reader.util.ImageUtil;
import net.huaxi.reader.view.CircleImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyInfoActivity extends BaseActivity implements View.OnClickListener {
    public static final String REGULAR_USERNAEM = "^[_＿a-zA-Z0-9\\u4e00-\\u9fa5]{2,10}$";
    public static final int IMAGE_REQUEST_CODE = 1002;
    public static final int SELECT_PIC_KITKAT = 1003;
    public static final int RESULT_REQUEST_CODE = 1004;
    private static final int CAMERA_REQUEST_CODE = 1001;
    Uri myimageuri;
    private ImageView ivBack;
    private CircleImageView ivHeadImage;
    private RelativeLayout rlHeadImage, rlNikeName, rlBandPhone, rlChangePassword, rlBandQQ,
            rlBandWeixin, rlBandWeibo;
    private TextView tvNikename, tvBandPhone, tvBandQQ, tvBandWeixin, tvBandWeibo, tvUserid, tvMan, tvWoman;
    private RadioGroup rgGender;
    private RadioButton rbMan, rbWomen;
    private PhotoDialog photoDialog;
    private Uri avatarUri;

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //得到view视图窗口
        Window window = getActivity().getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(getResources().getColor(R.color.c01_themes_color));
        setContentView(R.layout.activity_my_info);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //修改昵称完成之后加载数据
        initData();
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.myinfo_back_imageview);
        ivHeadImage = (CircleImageView) findViewById(R.id.myinfo_head_imageview);
        rlHeadImage = (RelativeLayout) findViewById(R.id.myinfo_headicon_layout);
        rlNikeName = (RelativeLayout) findViewById(R.id.myinfo_nikename_layout);
        rlBandPhone = (RelativeLayout) findViewById(R.id.myinfo_bandphone_layout);
        rlChangePassword = (RelativeLayout) findViewById(R.id.myinfo_change_password_layout);
        rlBandQQ = (RelativeLayout) findViewById(R.id.myinfo_bandQQ_layout);
        rlBandWeixin = (RelativeLayout) findViewById(R.id.myinfo_bandweixin_layout);
        rlBandWeibo = (RelativeLayout) findViewById(R.id.myinfo_bandweibo_layout);
        tvNikename = (TextView) findViewById(R.id.myinfo_nikename_textview);
        tvBandPhone = (TextView) findViewById(R.id.myinfo_bandphone_textview);
        tvBandQQ = (TextView) findViewById(R.id.myinfo_bandQQ_textview);
        tvBandWeixin = (TextView) findViewById(R.id.myinfo_bandweixin_textview);
        tvBandWeibo = (TextView) findViewById(R.id.myinfo_bandweibo_textview);
        rgGender = (RadioGroup) findViewById(R.id.myinfo_radiogroup);
        rbMan = (RadioButton) findViewById(R.id.myinfo_man_radiobutton);
        rbWomen = (RadioButton) findViewById(R.id.myinfo_women_radiobutton);
        tvUserid = (TextView) findViewById(R.id.myinfo_userid_textview);
        tvMan = (TextView) findViewById(R.id.myinfo_man_radio_textview);
        tvWoman = (TextView) findViewById(R.id.myinfo_woman_radio_textview);
    }

    private void initEvent() {
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rbMan.getId()) {
                    LogUtils.debug("rbman..click......");
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("u_gender", "1");
                    changeUserInfo(map);
                } else if (checkedId == rbWomen.getId()) {
                    LogUtils.debug("rbwomen..click.....");
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("u_gender", "2");
                    changeUserInfo(map);
                }
            }
        });
        tvMan.setOnClickListener(this);
        tvWoman.setOnClickListener(this);

        rlHeadImage.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        rlNikeName.setOnClickListener(this);
        rlBandPhone.setOnClickListener(this);
        rlChangePassword.setOnClickListener(this);
        tvUserid.setOnClickListener(this);
        tvUserid.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                        getSystemService(CLIPBOARD_SERVICE);
                ClipData textCd = ClipData.newPlainText("userid", tvUserid.getText());
                clipboard.setPrimaryClip(textCd);
                ViewUtils.toastShort(getString(R.string.myinfo_id_is_copy));
                return true;
            }
        });
    }

    public void initData() {


        setUserdesc();
        final GetRequest request = new GetRequest(URLConstants.USER_DETAIL + "?1=1" + CommonUtils.getPublicGetArgs(), new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                LogUtils.debug("response==" + response.toString());
                int errorid = ResponseHelper.getErrorId(response);
                if (!ResponseHelper.isSuccess(response)) {
                    LogUtils.debug("获取用户信息失败：Errorid==" + errorid);
                    return;
                }
                User user = null;
                user = new Gson().fromJson(ResponseHelper.getVdata(response).toString(), User
                        .class);
                UserHelper.getInstance().setUser(user);
                setUserdesc();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setUserdesc();
                LogUtils.debug("网络错误");
            }
        });
        RequestQueueManager.addRequest(request);
    }

    private void setUserdesc() {
        rgGender.setOnCheckedChangeListener(null);
        final User user = UserHelper.getInstance().getUser();
        String username = user.getNickname();
        if (StringUtils.isNotEmpty(username)) {
//            tvNikename.setText(username.matches(REGULAR_USERNAEM) ?
//                    username : "");
            tvNikename.setText(username);

        }
        if ("1".equals(user.getGender())) {
            rbMan.setChecked(true);
        } else if (("2").equals(user.getGender())) {
            rbWomen.setChecked(true);
        }
        tvBandPhone.setText(user.getMobile() == null ? "" : user.getMobile());
        if (user.getImgid() != null) {
            LogUtils.debug(user.getImgid());
            setHeadIcon(user);
        }
        if (user.getUmid() != null) {
            tvUserid.setText(getString(R.string.myinfo_user_id) + user.getUmid());
        }
        initEvent();
        //第三方登录不能修改手机号


        if (User.USER_TYPE_AUTH_ALI == user.getType() || User.USER_TYPE_AUTH_QQ == user.getType()
                || User.USER_TYPE_AUTH_WECHAT == user.getType() || User.USER_TYPE_AUTH_WEIBO ==
                user.getType()) {
            rlBandPhone.setVisibility(View.GONE);
            rlChangePassword.setVisibility(View.GONE);
        } else {
            rlBandPhone.setVisibility(View.VISIBLE);
            rlChangePassword.setVisibility(View.VISIBLE);

        }
    }

    private void setHeadIcon(final User user) {
        ImageUtil.getImageListener(MyInfoActivity.this, user.getImgid(), new ImageUtil.BitmapCallBackListener() {
            @Override
            public void callBack(Bitmap bitmap) {
                if (bitmap != null) {
                    String path = String.format(BitmapWriteTool.ROOTPATH + File.separator + "%s" +
                            ".jpg", user.getUmid());
                    LogUtils.debug("icon path:"+path);
                    Log.i("rrr", "callBack: 图片路径"+path);


                    Bitmap map = getLoacalBitmap(path);


                    myimageuri = Uri.parse(path);
                    Bitmap bitmap1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    BitmapWriteTool.writeToSDcard(bitmap1, new File(path), Bitmap.CompressFormat.JPEG);
                    ivHeadImage.setImageBitmap(map);
                    ivHeadImage.setOnClickListener(MyInfoActivity.this);

                } else {
                    ivHeadImage.setImageResource(R.mipmap.usercenter_default_icon);
                    LogUtils.debug("icon path is null");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ivBack.getId()) {
            finish();
        } else if (v.getId() == rlHeadImage.getId()) {
            uploadImageview();
        } else if (rlNikeName.getId() == v.getId()) {
            Intent intent = new Intent(MyInfoActivity.this, MyInfoChangeNikeNameActivity.class);
            intent.putExtra("name", tvNikename.getText().toString());
            startActivity(intent);
        } else if (rlBandPhone.getId() == v.getId()) {
            Intent intent = new Intent(MyInfoActivity.this, BandPhoneSendActivity.class);
            String phonenum = tvBandPhone.getText().toString();
            intent.putExtra("phonenum", phonenum);
            if (StringUtils.isBlank(phonenum)) {
                intent.putExtra("usetype", "5");
            } else {
                intent.putExtra("usetype", "6");
            }
            startActivity(intent);
        } else if (rlChangePassword.getId() == v.getId()) {
            String phonenum = tvBandPhone.getText().toString();
            if (StringUtils.isBlank(phonenum)) {
                ViewUtils.toastShort(getString(R.string.myinfo_please_band_phone_num));
            } else {
                Intent intent = new Intent(MyInfoActivity.this, ChangePasswordSendActivity.class);
                intent.putExtra("phonenum", phonenum);
                startActivity(intent);
            }
        } else if (ivHeadImage.getId() == v.getId()) {
            lookHeadImage();
        } else if (tvMan.getId() == v.getId()) {
            rbMan.setChecked(true);
        } else if (tvWoman.getId() == v.getId()) {
            rbWomen.setChecked(true);
        }
    }

    public void lookHeadImage() {
        Uri uri = myimageuri;
        LogUtils.debug("uri==" + uri.getPath());
        Intent intent = new Intent("android.intent.action.VIEW");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String url = uri.getPath();
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        } else {
            intent.setDataAndType(uri, "image/*");
        }
        startActivity(intent);
    }

    private void changeUserInfo(Map<String, String> map) {
        if (StringUtils.isBlank(SharePrefHelper.getCookie()) || "-1".equals(SharePrefHelper
                .getCookie())) {
            return;
        }
        map.putAll(CommonUtils.getPublicPostArgs());
        PostRequest request = new PostRequest(URLConstants.UPDATE_USER, new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (!ResponseHelper.isSuccess(response)) {
                    ViewUtils.toastShort(getString(R.string.myinfo_update_fail));
                    return;
                }
                LogUtils.debug("response===" + response.toString());
                ViewUtils.toastShort(getString(R.string.myinfo_update_success));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ViewUtils.toastShort(getString(R.string.network_server_error));
            }
        }, map);
        RequestQueueManager.addRequest(request);
    }

    private void uploadImageview() {
        if (photoDialog == null) {
            initPhotoDialog();
        }

        photoDialog.show();


    }

    private Uri getTempUri() {
        if (avatarUri == null) {
            String path = String.format(BitmapWriteTool.ROOTPATH + File.separator + "%d.jpg", new
                    Date().getTime());
            File imageFile = new File(path);
            if (!imageFile.getParentFile().exists()) {
                imageFile.getParentFile().mkdirs();
            }
            avatarUri = Uri.fromFile(imageFile);
        }
        return avatarUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
//            myimageuri
//            ivHeadImage

            return;
        }
        switch (requestCode) {
            //本地图片
            case IMAGE_REQUEST_CODE:
            case SELECT_PIC_KITKAT:
                startPhotoZoom(data.getData());
                break;
            //照相
            case CAMERA_REQUEST_CODE:
                Uri uri = getTempUri();
                startPhotoZoom(uri);
                Intent intent = new Intent(); // 新建一个Intent，用来发广播
                intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(uri); // 指定存入资源库的数据，就是我们照片文件的地址
                sendBroadcast(intent);
                break;
            case RESULT_REQUEST_CODE:
                String urlpath = getTempUri().getPath();
                if (urlpath != null) {
                    LogUtils.debug(getString(R.string.myinfo_upload_start));
                    new UploadPhotoTask(MyInfoActivity.this, urlpath).execute();
                } else {
                    LogUtils.debug(getString(R.string.myinfo_can_not_upload));
                }
                break;
        }


    }

    /**
     * 从相册得到的url转换为SD卡中图片路径
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPath(Uri uri) {
//        if (TextUtils.isEmpty(uri.getAuthority())) {
//            return null;
//        }
//        String[] projection = {MediaStore.Images.Media.DATA};
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        String path = cursor.getString(column_index);
//        return path;

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
            // ExternalStorageProvider
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(this, contentUri, null, null);
            }
            // MediaProvider
            else if ("com.android.providers.media.documents".equals(uri
                    .getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(this, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if ("com.google.android.apps.photos.content".equals(uri.getAuthority()))
                return uri.getLastPathSegment();
            return getDataColumn(this, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private void initPhotoDialog() {
        photoDialog = new PhotoDialog(MyInfoActivity.this);
        photoDialog.getDialog().findViewById(R.id.dialog_getphoto_fromalbum_textview)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES
                                .KITKAT) {
                            startActivityForResult(intent, SELECT_PIC_KITKAT);
                        } else {
                            startActivityForResult(intent, IMAGE_REQUEST_CODE);
                        }

                        photoDialog.cancel();
                    }
                });
        photoDialog.getDialog().findViewById(R.id.dialog_getphoto_fromtakephoto)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Environment.getExternalStorageState().equals(Environment
                                .MEDIA_MOUNTED)) {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
                            startActivityForResult(cameraIntent, MyInfoActivity
                                    .CAMERA_REQUEST_CODE);
                        } else {
                            ViewUtils.toastShort(getString(R.string.myinfo_please_input_sdcard));
                        }
                        photoDialog.cancel();
                    }
                });

    }


    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        if (uri == null) {
            return;
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String url = getPath(uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        } else {
            intent.setDataAndType(uri, "image/*");
        }
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("scaleUpIfNeeded", true);//黑边
        startActivityForResult(intent, RESULT_REQUEST_CODE);


    }
    /**
     * 加载本地图片
     * http://bbs.3gstdy.com
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
