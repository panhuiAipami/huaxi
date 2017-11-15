package com.spriteapp.booklibrary.util;

import android.content.Context;
import android.os.Environment;

import com.spriteapp.booklibrary.constant.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileUtils {

    public static String getNativeStorePath() {
        return Constant.PATH_TXT + "nativeBookStore" + ".txt";
    }

    public static String getFileContent(String fileName) {
        String content = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                content += line;
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static File getNativeStoreFile() {
        File file = new File(getNativeStorePath());
        if (!file.exists())
            createFile(file);
        return file;
    }

    /**
     * 创建根缓存目录
     */
    public static String createRootPath(Context context) {
        String cacheRootPath = "";
        if (isSdCardAvailable() && context.getExternalCacheDir() != null) {
            // /sdcard/Android/data/<application package>/cache
            cacheRootPath = context.getExternalCacheDir().getPath();
        } else {
            // /data/data/<application package>/cache
            cacheRootPath = context.getCacheDir().getPath();
        }
        return cacheRootPath;
    }

    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 递归创建文件夹
     *
     * @param dirPath
     * @return 创建失败返回""
     */
    public static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {
                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }

    /**
     * 递归创建文件夹
     *
     * @param file
     * @return 创建失败返回""
     */
    public static String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {
                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void writeFile(String filePathAndName, String fileContent) {
        try {
            OutputStream outputStream = new FileOutputStream(filePathAndName);
            OutputStreamWriter out = new OutputStreamWriter(outputStream);
            out.write(fileContent);
            out.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

}