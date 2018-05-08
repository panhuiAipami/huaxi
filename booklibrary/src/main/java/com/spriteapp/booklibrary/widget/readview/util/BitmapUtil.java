package com.spriteapp.booklibrary.widget.readview.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.spriteapp.booklibrary.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by panhui on 2018/4/27.
 */

public class BitmapUtil {
    public static String loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
//        Bitmap screenshot;
//        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_4444);
//        Canvas canvas = new Canvas(screenshot);
//        canvas.translate(-v.getScrollX(), -v.getScrollY());//我们在用滑动View获得它的Bitmap时候，获得的是整个View的区域（包括隐藏的），如果想得到当前区域，需要重新定位到当前可显示的区域
//        v.draw(canvas);// 将 view 画到画布上

        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();       //启用DrawingCache并创建位图
        //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);     //禁用DrawingCahce否则会影响性能

        return saveBitmap(bitmap);
    }

    public static byte[] getBytes(Bitmap bitmap){
        //实例化字节数组输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);//压缩位图
        return baos.toByteArray();//创建分配字节数组
    }

    public static Bitmap getBitmap(byte[] data){
        return BitmapFactory.decodeByteArray(data, 0, data.length);//从字节数组解码位图
    }

    /**
     * 保存bitmap到本地
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Bitmap mBitmap) {
        File filePic;
        try {
            filePic = new File(Constants.SDPath_cache  + "/share_image.jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }
}
