# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/kuangxiaoguo/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# RxJava and RxAndroid
-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# OkHttp3
-dontwarn okhttp3.logging.**
-dontwarn okio.**
-keep class okhttp3.internal.**{*;}
-dontwarn com.switfpass.pay.**
-keep class com.switfpass.pay.** {*;}

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature

# Gson
-keep class com.google.gson.stream.** { *; }
-keepattributes EnclosingMethod

-keep class com.spriteapp.booklibrary.model.**{*;}
-keep class com.spriteapp.booklibrary.config.**{*;}
-keep class com.spriteapp.booklibrary.listener.**{*;}
-keep class com.spriteapp.booklibrary.ui.**{*;}
-keep class com.spriteapp.booklibrary.base.**{*;}
-keep class com.spriteapp.booklibrary.enumeration.**{*;}
-keep class com.spriteapp.booklibrary.widget.**{*;}
-keep class com.spriteapp.booklibrary.recyclerView.**{*;}
-keep class com.spriteapp.booklibrary.util.**{*;}

# Alipay
-keep class sun.misc.Unsafe { *; }

-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.alipay.** {*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.alipay.**

-keep class com.ut.** {*;}
-dontwarn com.ut.**

-keep class com.ta.** {*;}
-dontwarn com.ta.**

-keep class anet.**{*;}
-keep class org.android.spdy.**{*;}
-keep class org.android.agoo.**{*;}
-dontwarn anet.**
-dontwarn org.android.spdy.**
-dontwarn org.android.agoo.**

# Event Bus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#保留crash日志的行号
-keepattributes SourceFile,LineNumberTable

