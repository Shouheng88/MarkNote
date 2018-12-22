#指定压缩级别
-optimizationpasses 5
#不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses
#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#把混淆类中的方法名也混淆了
-useuniqueclassmembernames
#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
#保留行号
-keepattributes SourceFile,LineNumberTable
# 是否使用大小写混合
-dontusemixedcaseclassnames
#混淆时是否记录日志
-verbose
#忽略警告，避免打包时某些警告出现
-ignorewarnings
#预校验
-dontpreverify
#保护注解
-keepattributes *Annotation*
#保护JS回调接口
-keepattributes *JavascriptInterface*
#保留JavascriptInterface中的方法
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

#记录生成的日志数据,gradle build时在本项目根目录输出
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt

#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}

# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

#Social Style Date and Time Formatting for Java
-keep class org.ocpsoft.prettytime.i18n.**

#如果有引用v4、v7包可以添加下面这行
-keep public class * extends android.support.*

#解决ActionBar上面的搜索按钮的空指针问题
-keep class android.support.v7.widget.SearchView { *; }

#过滤泛型
-keepattributes Signature

# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**

# proguard rules for rx-java
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
-dontwarn sun.misc.Unsafe

# Umeng 混淆配置
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

# Retrofit 混淆配置
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions
# okhttp 混淆配置
-dontwarn okio.**
# Gson 混淆配置
# TODO 自定义数据模型的bean目录，如果使用了 Retrofit，必须增加该配置


# SwipeRecyclerView
-keepclasseswithmembers class android.support.v7.widget.RecyclerView$ViewHolder {
   public final View *;
}
-dontwarn com.yanzhenjie.recyclerview.swipe.**
-keep class com.yanzhenjie.recyclerview.swipe.** {*;}

# 弹出菜单不混淆，需要反射调用其中的字段
-keep class android.support.v7.widget.PopupMenu { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# sensor
-dontwarn com.sensorsdata.analytics.android.**
-keep class com.sensorsdata.analytics.android.** {
  *;
}
-keep class **.R$* {
  <fields>;
}
-keepnames class * implements android.view.View$OnClickListener
-keep public class * extends android.content.ContentProvider
-keepnames class * extends android.view.View

-keep class * extends android.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}
-keep class android.support.v4.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}
-keep class * extends android.support.v4.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}
# DataBinding
-dontwarn android.databinding.**
-keep class android.databinding.** { *; }
# TODO 包下的 databinding 包的目录
# MultiDex
-keep class com.sensorsdata.analytics.android.** { *; }
