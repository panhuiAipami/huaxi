package com.nostra13.universalimageloader.core;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;

/**
 * Created by ZMW on 2015/12/10.
 */
public class UniImageLoader {

    public static void init (Context ctx, String cacheDirName){
        File cacheDir = new File(cacheDirName);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
                //
                .memoryCacheExtraOptions(480, 800)//每个缓存文件的最大长宽
                .threadPoolSize(3)//default线程池缓存数
                .threadPriority(Thread.NORM_PRIORITY - 1)// default线程优先级
                .denyCacheImageMultipleSizesInMemory()//
                        //				.memoryCache(new UsingFreqLimitedMemoryCache(5 * 1024 * 1024))//设置内存缓存 默认为一个当前应用可用内存的1/8大小的LruMemoryCache
                        //				.memoryCacheSize(5 * 1024 * 1024) //设置内存缓存的最大大小 默认为一个当前应用可用内存的1/8
                        //				.memoryCacheSizePercentage(13)//设置内存缓存最大大小占当前应用可用内存的百分比 默认为一个当前应用可用内存的1/8
                .diskCacheSize(100 * 1024 * 1024) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO)//先进先出
                .diskCacheFileCount(1000)//设置图片下载和显示的工作队列排序
                .diskCache(new UnlimitedDiscCache(cacheDir, null, new FileNameGenerator(){
                    @Override
                    public String generate(String imageUri) {
                        return imageUri;
                    }
                }))//default 磁盘自定义缓存路径
                .imageDownloader(new BaseImageDownloader(ctx))// default 图片下载
                .imageDecoder(new BaseImageDecoder(true))// default 图片解码
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())//default 图片显示
                        //				.writeDebugLogs()// Remove for release app

                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

    }
}
