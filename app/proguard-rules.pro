# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-studio-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

    #===============基本配置=================================
    #指定代码的压缩级别
    -optimizationpasses 5

    #包明不混合大小写
    -dontusemixedcaseclassnames

    #不去忽略非公共的库类
    -dontskipnonpubliclibraryclasses

     #优化  不优化输入的类文件
    -dontoptimize

     #预校验
    -dontpreverify

     #混淆时是否记录日志
    -verbose

     # 混淆时所采用的算法
    -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
    -keepattributes SourceFile,LineNumberTable
    #保护注解
    -keepattributes *Annotation*
    #屏蔽警告
    -ignorewarnings

    # =====================不混淆的类==============================
    -keep class android.** {*; }
    -keep public class * extends android.view
    -keep public class * extends android.app.Fragment
    -keep public class * extends android.support.v4.app.Fragment
    -keep public class * extends com.hxgc.jiuyuu.fragment.BaseFragment
    -keep public class * extends android.app.Activity
    -keep public class * extends android.app.Application
    -keep public class * extends android.app.Service
    -keep public class * extends android.content.BroadcastReceiver
    -keep public class * extends android.content.ContentProvider
    -keep public class * extends android.app.backup.BackupAgentHelper
    -keep public class * extends android.preference.Preference
    -keep public class com.android.vending.licensing.ILicensingService


    #==========buil中输出的配置文件==============
    #apk 包内所有 class 的内部结构
    -dump class_files.txt
    #未混淆的类和成员
    -printseeds seeds.txt
    #列出从 apk 中删除的代码
    -printusage unused.txt
    #混淆前后的映射
    -printmapping mapping.txt

    #=======================第三方SDK库===========================================
    #图片加载
    -keep class com.nostra13.universalimageloader.** { *; }

    #友盟
    -keep class com.umeng.**{*;}

    #支付宝
#    -keep class com.alipay.android.app.IAliPay{*;}
#    -keep class com.alipay.android.app.IAlixPay{*;}
#    -keep class com.alipay.android.app.IRemoteServiceCallback{*;}
#    -keep class com.alipay.android.app.lib.ResourceMap{*;}
    -keep class com.alipay.android.app.IAlixPay{*;}
    -keep class com.alipay.android.app.IAlixPay$Stub{*;}
    -keep class com.alipay.android.app.IRemoteServiceCallback{*;}
    -keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
    -keep class com.alipay.sdk.app.PayTask{ public *;}
    -keep class com.alipay.sdk.app.AuthTask{ public *;}

    #微信
    -keep class com.tencent.mm.sdk.** {*;}

    #v4或者v7包
    -dontwarn android.support.**


    #========================类==================================
    #自定义的view类
    -keep public class * extends android.view.View {
        public <init>(android.content.Context);
        public <init>(android.content.Context, android.util.AttributeSet);
        public <init>(android.content.Context, android.util.AttributeSet, int);
        public void set*(...);
    }

 # 保持自定义控件类不被混淆
    -keepclasseswithmembers class * {
          public <init>(android.content.Context, android.util.AttributeSet);
        }

    -keepclasseswithmembers class * {
           public <init>(android.content.Context, android.util.AttributeSet, int);
        }

    -keepclasseswithmembers class * {
        void onClick*(...);
    }
    -keepclasseswithmembers class * {
        *** *Callback(...);
    }

    #保持 native 方法不被混淆
    -keepclasseswithmembernames class * {
        native <methods>;
    }

    #保持自定义控件类不被混淆
    -keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet);
    }

    #保持 Parcelable 不被混淆
    -keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }

    #保持 Serializable 不被混淆
    -keepnames class * implements java.io.Serializable

    #保持 Serializable 不被混淆并且enum 类也不被混淆
    -keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private static final java.io.ObjectStreamField[] serialPersistentFields;
        !static !transient <fields>;
        !private <fields>;
        !private <methods>;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
    }

    #保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
    -keepclassmembers enum * {
      public static **[] values();
      public static ** valueOf(java.lang.String);
    }

    -keepclassmembers class * {
        public <init> (org.json.JSONObject);
        public void *ButtonClicked(android.view.View);
    }

    #不混淆资源类
    -keepclassmembers class **.R$* {
        public static <fields>;
    }

    -keep public class com.hxgc.jiuyuu.R$*{
    public static final int *;
    }

    #避免混淆泛型 如果混淆报错建议关掉
    # keep 泛型
    -keepattributes Signature

    # ====================Gson============================
#    -libraryjars libs/gson-2.2.4.jar
    # Gson specific classes
    -keep class sun.misc.Unsafe { *; }
    # Application classes that will be serialized/deserialized over Gson
    -keep class com.google.gson.examples.android.model.** { *; }
    -keep class com.google.gson.** {*;}
    -keep class com.google.**{*;}
    -keep class sun.misc.Unsafe { *; }
    -keep class com.google.gson.stream.** { *; }
    -keep class com.google.gson.examples.android.model.** { *; }
    -keep class com.google.** {
        <fields>;
        <methods>;
    }
    -dontwarn com.google.gson.**

    #========================JavaScript=============================
    #webview + js
    -keepattributes *JavascriptInterface*
    #自定义view
    -keep class com.hxgc.jiuyuu.view.** {*;}

    -keep class com.hxgc.jiuyuu.common.JavaScript {*;}

    #bugly代码混淆
    -keep public class com.tencent.bugly.**{*;}


    #友盟推送
    -dontwarn com.ut.mini.**
    -dontwarn okio.**
    -dontwarn com.xiaomi.**
    -dontwarn com.squareup.wire.**
    -dontwarn android.support.v4.**

    -keep class android.support.v4.** { *; }
    -keep interface android.support.v4.app.** { *; }

    -keep class okio.** {*;}
    -keep class com.squareup.wire.** {*;}

    -keep class com.umeng.message.protobuffer.* {
    	 public <fields>;
             public <methods>;
    }

    -keep class com.umeng.message.* {
    	 public <fields>;
             public <methods>;
    }

    -keep class org.android.agoo.impl.* {
    	 public <fields>;
             public <methods>;
    }

    -keep class org.android.agoo.service.* {*;}

    -keep class org.android.spdy.**{*;}
    #glide代码混淆
    -keep public class * implements com.bumptech.glide.module.GlideModule
    -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
    }
    #支付宝混淆规则
    -keep class com.alipay.android.app.IAlixPay{*;}
    -keep class com.alipay.android.app.IAlixPay$Stub{*;}
    -keep class com.alipay.android.app.IRemoteServiceCallback{*;}
    -keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
    -keep class com.alipay.sdk.app.PayTask{ public *;}
    -keep class com.alipay.sdk.app.AuthTask{ public *;}
    #eventbus混淆
    -keep class de.greenrobot.event.** {*;}
    -keepclassmembers class ** {
        public void onEvent*(**);
        void onEvent*(**);
    }

   #ButterKnife 7.0 第三方库的配置
    -keep class butterknife.** { *; }
    -dontwarn butterknife.internal.**
    -keep class **$$ViewBinder { *; }
    -keepclasseswithmembernames class * {
       @butterknife.* <fields>;
    }
    -keepclasseswithmembernames class * {
     @butterknife.* <methods>;
    }

