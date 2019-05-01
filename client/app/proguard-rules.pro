# 指定压缩级别
-optimizationpasses 5
# 不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses
# 混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 把混淆类中的方法名也混淆了
-useuniqueclassmembernames
# 优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
# 将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
# 保留行号
-keepattributes SourceFile,LineNumberTable
# 是否使用大小写混合
-dontusemixedcaseclassnames
# 混淆时是否记录日志
-verbose
# 忽略警告，避免打包时某些警告出现
-ignorewarnings
# 预校验
-dontpreverify
# 保护注解
-keepattributes *Annotation*
# 保护JS回调接口
-keepattributes *JavascriptInterface*
# 保留JavascriptInterface中的方法
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# 记录生成的日志数据,gradle build时在本项目根目录输出
# Apk包内所有class的内部结构
-dump class_files.txt
# 未混淆的类和成员
-printseeds seeds.txt
# 列出从Apk中删除的代码
-printusage unused.txt
# 混淆前后的映射
-printmapping mapping.txt

# 保持所有实现Serializable接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保持Parcelable不被混淆
-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}

# 保持枚举类不被混淆
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}

# Fragment需要额外保护
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

# Prettytime
-keep class org.ocpsoft.prettytime.i18n.**

# 与v4和v7包相关的混淆
-keep public class * extends android.support.*

# 解决ActionBar上面的搜索按钮的空指针问题
-keep class android.support.v7.widget.SearchView { *; }

# 过滤泛型
-keepattributes Signature

# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**

#:Matisse
-dontwarn com.squareup.picasso.**

# FingerPrint
-ignorewarnings
# MeiZuFingerprint
-keep class com.fingerprints.service.** { *; }
# SmsungFingerprint
-keep class com.samsung.android.sdk.** { *; }

# UMeng
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# AgentWeb
-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**

# Hotfix
# 基线包使用，生成mapping.txt
-printmapping mapping.txt
# 生成的mapping.txt在app/build/outputs/mapping/release路径下，移动到/app路径下
# 修复后的项目使用，保证混淆结果一致
# -applymapping mapping.txt
# hotfix
-keep class com.taobao.sophix.**{*;}
-keep class com.ta.utdid2.device.**{*;}
# 防止inline
-dontoptimize

# Cipher
-keep class net.idik.lib.cipher.so.** {*;}