package com.spriteapp.booklibrary.manager;

import com.spriteapp.booklibrary.util.FileUtils;

import java.io.File;

/**
 * Created by kuangxiaoguo on 2017/7/10.
 */

public class CacheManager {

    public static void saveNativeBookStore(String data) {
        File file = FileUtils.getNativeStoreFile();
        if (file.getAbsolutePath() != null)
            FileUtils.writeFile(file.getAbsolutePath(), data);
    }

    public static String readNativeBookStore() {
        return FileUtils.getFileContent(FileUtils.getNativeStorePath());
    }

}
