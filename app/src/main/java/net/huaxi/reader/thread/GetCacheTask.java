package net.huaxi.reader.thread;

import android.widget.TextView;

import com.tools.commonlibs.task.EasyTask;
import com.tools.commonlibs.tools.FileUtils;
import com.tools.commonlibs.tools.LogUtils;
import net.huaxi.reader.activity.SettingActivity;
import net.huaxi.reader.common.Constants;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by ZMW on 2016/1/20.
 */
public class GetCacheTask extends EasyTask<SettingActivity, Void, Void, Long> {
    private TextView tvsize;
    public GetCacheTask(SettingActivity activity,TextView tvsize) {
        super(activity);
        this.tvsize=tvsize;
    }

    @Override
    public Long doInBackground(Void... params) {

        long size = 0;
        //1,获取app缓存
//        size += FileUtils.getFileSize(caller.getCacheDir());
//        LogUtils.debug("app缓存=="+FileUtils.getFileSize(caller.getCacheDir()));
        //2,获取章节缓存
        size += FileUtils.getFileSize(new File(Constants.XSREADER_BOOK));
        LogUtils.debug("获取章节缓存==" + FileUtils.getFileSize(new File(Constants.XSREADER_BOOK)));
        //3,获取图片缓存
        size += FileUtils.getFileSize(new File(Constants.XSREADER_SPLASH_IMGCACHE));
        LogUtils.debug("获取闪屏图片缓存=="+FileUtils.getFileSize(new File(Constants.XSREADER_SPLASH_IMGCACHE)));
        return size;
    }

    @Override
    public void onPostExecute(Long size) {
        super.onPostExecute(size);
        if(size==0){
            tvsize.setText("0MB");
            return ;
        }
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        String result="0.00MB";

        if(size>1024 && 1024*1024 >size){
            double d=size/1024D;
            result=dcmFmt.format(d)+"KB";
        }
        if(1024*1024 <size){
            double d=size/(1024*1024D);
            result = dcmFmt.format(d)+"MB";
        }
        tvsize.setText(result);
    }
}
