package com.spriteapp.booklibrary.ui.activity;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.util.Constants;
import com.spriteapp.booklibrary.util.FileUtils;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.widget.ReadMoreSettingLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFontsActivity extends TitleActivity {
    private final int DOWNLOAD_COMPLETE = 0;
    private final int DOWNLOADS_ING = 1;
    private final int DOWNLOAD_FAIL = -1;
    // 文件存储
    private File updateFile = null;
    // 下载文件的存放路径
    private File updateDir;
    private String BaseUrl = "https://img2.hxdrive.net//uploads//font/";
    private String url;


    public static String hei = "hei.ttf";
    public static String siyuan = "siyuan.otf";
    public static String shu = "shu.ttf";
    public static String kai = "kai.ttf";
    public static String fangsong = "fangsong.ttf";
    private String fileName = null;
    private int font_num;

    private TextView download_font_default, download_font1, download_font2, download_font3, download_font4, download_font5;
    private TextView download_now;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOAD_COMPLETE:
                    ReadMoreSettingLayout.initTypeFace();
                    ToastUtil.showSingleToast("下载完成");
                    download_now.setText("立即使用");
                    fileName = null;
                    break;
                case DOWNLOADS_ING://下载中
                    int progress = (int) msg.obj;
                    download_now.setText("下载中 " + progress + "%");
                    break;
                case DOWNLOAD_FAIL:
                    updateFile.delete();
                    fileName = null;
                    break;
            }
        }
    };

    @Override
    public void initData() throws Exception {
        setTitle("字体下载");

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            updateDir = new File(Constants.DOWNLOAD_FONTS);
        } else {
            updateDir = getFilesDir();
        }

        initButtonStatus();
    }

    public void initButtonStatus() {
        font_num = SharedPreferencesUtil.getInstance().getInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_FONT_STYLE, 0);

        isDownLoad(download_font_default, null, 0);
        isDownLoad(download_font1, hei, 1);
        isDownLoad(download_font2, siyuan, 2);
        isDownLoad(download_font3, shu, 3);
        isDownLoad(download_font4, kai, 4);
        isDownLoad(download_font5, fangsong, 5);


    }

    public void isDownLoad(TextView tv, String font, int f) {
        tv.setEnabled(true);
        if (font_num == f) {
            if (f == 0 || FileUtils.isExists(Constants.DOWNLOAD_FONTS + "/" + font)) {
                tv.setText("正在使用");
                tv.setEnabled(false);
            }
        } else if (f == 0 || FileUtils.isExists(Constants.DOWNLOAD_FONTS + "/" + font)) {
            tv.setText("立即使用");
        } else {
            tv.setText("下载");
        }
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_download_fonts, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        download_font_default = (TextView) findViewById(R.id.download_font_default);
        download_font1 = (TextView) findViewById(R.id.download_font1);
        download_font2 = (TextView) findViewById(R.id.download_font2);
        download_font3 = (TextView) findViewById(R.id.download_font3);
        download_font4 = (TextView) findViewById(R.id.download_font4);
        download_font5 = (TextView) findViewById(R.id.download_font5);

        download_font_default.setOnClickListener(this);
        download_font1.setOnClickListener(this);
        download_font2.setOnClickListener(this);
        download_font3.setOnClickListener(this);
        download_font4.setOnClickListener(this);
        download_font5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == download_font_default) {
            downLoad(null, download_font_default, 0);
        } else if (v == download_font1) {
            downLoad(hei, download_font1, 1);
        } else if (v == download_font2) {
            downLoad(siyuan, download_font2, 2);
        } else if (v == download_font3) {
            downLoad(shu, download_font3, 3);
        } else if (v == download_font4) {
            downLoad(kai, download_font4, 4);
        } else if (v == download_font5) {
            downLoad(fangsong, download_font5, 5);
        } else if (v == download_font_default) {

        }
    }


    public void downLoad(String font, TextView tv, int font_num) {
        if (fileName != null) {
            ToastUtil.showSingleToast("请等待下载完");
            return;
        }
        if (tv.getText().toString().equals("立即使用")) {
            SharedPreferencesUtil.getInstance().putInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_FONT_STYLE, font_num);
            initButtonStatus();
            return;
        }

        fileName = font;
        download_now = tv;

        url = BaseUrl + fileName;
        updateFile = new File(updateDir.getPath(), fileName);
        if (updateFile.exists()) {
            fileName = null;
            return;
        }
        new Thread(new mRunnable()).start();
    }


    private class mRunnable implements Runnable {
        Message message = handler.obtainMessage();

        public void run() {
            message.what = DOWNLOAD_COMPLETE;
            try {
                // 下载成功
                if (downloadUpdateFile(url, updateFile)) {
                    handler.sendMessage(message);
                } else {// 下载失败
                    message.what = DOWNLOAD_FAIL;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {// 下载失败
                message.what = DOWNLOAD_FAIL;
                handler.sendMessage(message);
            }
        }
    }

    /**
     * 下载文件
     */
    public boolean downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {
        int downloadCount = 0;
        int currentSize = 0;
        long totalSize = 0;
        int updateTotalSize = 0;
        boolean result = false;
        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;
        // 文件目录是否存在
        if (!updateDir.exists()) {
            updateDir.mkdirs();
        }
        // 文件是否存在
        if (!updateFile.exists()) {
            updateFile.createNewFile();
        }
        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
            if (currentSize > 0) {
                httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
            }
            httpConnection.setConnectTimeout(20000);
            httpConnection.setReadTimeout(120000);
            updateTotalSize = httpConnection.getContentLength();
            if (httpConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }
            is = httpConnection.getInputStream();
            fos = new FileOutputStream(saveFile, false);
            byte buffer[] = new byte[4096];
            int readsize = 0;
            while ((readsize = is.read(buffer)) > 0) {
                fos.write(buffer, 0, readsize);
                totalSize += readsize;
                int progress = (int) (totalSize * 100 / updateTotalSize);
                if ((downloadCount == 0) || progress - 1 > downloadCount) {
                    downloadCount += 1;
                    Message ms = new Message();
                    ms.obj = progress;
                    ms.what = DOWNLOADS_ING;
                    handler.sendMessage(ms);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
            result = updateTotalSize > 0 && updateTotalSize == totalSize;
            if (!result) { //下载失败或者为下载完成
//				new File(filepath).delete();
            }
        }
        return result;
    }
}
