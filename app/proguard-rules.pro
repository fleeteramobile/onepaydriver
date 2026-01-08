## Add project specific ProGuard rules here.
## By default, the flags in this file are appended to flags specified
## in /Users/RP/Library/Android/sdk/tools/proguard/proguard-android.txt
## You can edit the include path and order by changing the proguardFiles
## directive in build.gradle.
##
## For more details, see
##   http://developer.android.com/guide/developing/tools/proguard.html
#
## Add any project specific keep options here:
#
## If your project uses WebView with JS, uncomment the following
## and specify the fully qualified class name to the JavaScript interface
## class:
##-keepclassmembers class fqcn.of.javascript.interface.for.webview {
##   public *;
##}
#
## Retrofit
## Platform calls Class.forName on types which do not exist on Android to determine platform.
#-dontnote retrofit2.Platform
## Platform used when running on RoboVM on iOS. Will not be used at runtime.
#-dontnote retrofit2.Platform$IOS$MainThreadExecutor
## Platform used when running on Java 8 VMs. Will not be used at runtime.
#-dontwarn retrofit2.Platform$Java8
## Retain generic type information for use by reflection by converters and adapters.
#-keepattributes Signature
## Retain declared checked exceptions for use by a Proxy instance.
#-keepattributes Exceptions
#
###RXJAVA
##-keep class rx.schedulers.Schedulers {
##    public static <methods>;
##}
##-keep class rx.schedulers.ImmediateScheduler {
##    public <methods>;
##}
##-keep class rx.schedulers.TestScheduler {
##    public <methods>;
##}
##-keep class rx.schedulers.Schedulers {
##    public static ** test();
##}
##-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
##    long producerIndex;
##    long consumerIndex;
##}
##-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
##    long producerNode;
##    long consumerNode;
##}
#
## Retrofit 2.X
## https://square.github.io/retrofit/ ##
#-dontwarn retrofit.**
#-keep class retrofit.** { *; }
#-dontwarn sun.misc.Unsafe
#-dontwarn com.octo.android.robospice.retrofit.RetrofitJackson**
#-dontwarn retrofit.appengine.UrlFetchClient
#-keepattributes Signature
#-keepattributes Exceptions
#-keepclasseswithmembers class * {
#    @retrofit.http.* <methods>;
#}
#-keep class com.google.gson.** { *; }
#-keep class com.google.inject.** { *; }
#-keep class org.apache.http.** { *; }
#-keep class org.apache.james.mime4j.** { *; }
#-keep class javax.inject.** { *; }
#-keep class retrofit.** { *; }
#-dontwarn org.apache.http.**
#-dontwarn android.net.http.AndroidHttpClient
#-dontwarn retrofit.**
#
#-dontwarn sun.misc.**
#
#-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
#   long producerIndex;
#   long consumerIndex;
#}
#
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
#   long producerNode;
#   long consumerNode;
#}
#
#-dontwarn retrofit2.Platform$Java8
#
#-keep class com.yappily.model.** { *; }
#
## OkHttp
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class okhttp3.** { *; }
#-keep interface okhttp3.** { *; }
#-dontwarn okhttp3.**
#
## Proguard flags for consumers of the Google Play services SDK
# # https://developers.google.com/android/guides/setup#add_google_play_services_to_your_project
#
# # Keep SafeParcelable value, needed for reflection. This is required to support backwards
# # compatibility of some classes.
# -keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
#     public static final *** NULL;
# }
#
# # Needed for Parcelable/SafeParcelable classes & their creators to not get renamed, as they are
# # found via reflection.
# -keepnames class * implements android.os.Parcelable
# -keepclassmembers class * implements android.os.Parcelable {
#   public static final *** CREATOR;
# }
#
# # Keep the classes/members we need for client functionality.
# -keep @interface android.support.annotation.Keep
# -keep @android.support.annotation.Keep class *
# -keepclasseswithmembers class * {
#   @android.support.annotation.Keep <fields>;
# }
# -keepclasseswithmembers class * {
#   @android.support.annotation.Keep <methods>;
# }
#
# # Keep the names of classes/members we need for client functionality.
# -keep @interface com.google.android.gms.common.annotation.KeepName
# -keepnames @com.google.android.gms.common.annotation.KeepName class *
# -keepclassmembernames class * {
#   @com.google.android.gms.common.annotation.KeepName *;
# }
#
# # Needed when building against pre-Marshmallow SDK.
# -dontwarn android.security.NetworkSecurityPolicy
#
# -dontwarn com.google.android.gms.clearcut.**
# -dontwarn com.google.android.gms.internal.**
#
## OkHttp
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class com.squareup.okhttp.** { *; }
#-keep interface com.squareup.okhttp.** { *; }
#-dontwarn com.squareup.okhttp.**
#
#
#-dontwarn sun.misc.**
#
#-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
#   long producerIndex;
#   long consumerIndex;
#}
#
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
#    rx.internal.util.atomic.LinkedQueueNode producerNode;
#}
#
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
#    rx.internal.util.atomic.LinkedQueueNode consumerNode;
#}
#
# -keep class com.google.android.gms.ads.** { *; }
# -dontwarn okio.**
#
##
###BUTTERKNIFE
### Retain generated class which implement Unbinder.
##-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }
##
### Prevent obfuscation of types which use ButterKnife annotations since the simple name
### is used to reflectively look up the generated ViewBinding.
##-keep class butterknife.*
##-keepclasseswithmembernames class * { @butterknife.* <methods>; }
##-keepclasseswithmembernames class * { @butterknife.* <fields>; }
##
###VOLLEY
#-keep class org.apache.commons.logging.**
#
#-keep class android.support.v7.widget.SearchView { *; }
#-dontwarn okio.**
#-dontwarn retrofit2.Platform$Java8
#-dontwarn java.lang.invoke**
#
### Platform calls Class.forName on types which do not exist on Android to determine platform.
##-dontnote retrofit2.Platform
### Platform used when running on RoboVM on iOS. Will not be used at runtime.
##-dontnote retrofit2.Platform$IOS$MainThreadExecutor
### Platform used when running on Java 8 VMs. Will not be used at runtime.
##-dontwarn retrofit2.Platform$Java8
### Retain generic type information for use by reflection by converters and adapters.
##-keepattributes Signature
### Retain declared checked exceptions for use by a Proxy instance.
##-keepattributes Exceptions
#
#########--------Retrofit + RxJava--------#########
#-dontwarn retrofit.**
#-keep class retrofit.** { *; }
#-dontwarn sun.misc.Unsafe
#-dontwarn com.octo.android.robospice.retrofit.RetrofitJackson**
#-dontwarn retrofit.appengine.UrlFetchClient
#-keepattributes Signature
#-keepattributes Exceptions
#-keepclasseswithmembers class * {
#    @retrofit.http.* <methods>;
#}
#-keep class com.google.gson.** { *; }
#-keep class com.google.inject.** { *; }
#-keep class org.apache.http.** { *; }
#-keep class org.apache.james.mime4j.** { *; }
#-keep class javax.inject.** { *; }
#-keep class retrofit.** { *; }
#-dontwarn org.apache.http.**
#-dontwarn android.net.http.AndroidHttpClient
#-dontwarn retrofit.**
#
#-dontwarn sun.misc.**
#
#-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
#   long producerIndex;
#   long consumerIndex;
#}
#
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
#   long producerNode;
#   long consumerNode;
#}
#
#
## ALSO REMEMBER KEEPING YOUR MODEL CLASSES
#-keep class com.your.package.model.** { *; }
#
#-keepattributes Signature
#-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.examples.android.model.** { *; }

# RETROFIT START
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
#-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
-keepclasseswithmembers class com.taximobility.data.apiData.** { *; }
-dontwarn okio.**

# OKHTTP# START
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.* { *;}

# PICASSO# START
-dontwarn com.squareup.okhttp.**

# FB# START
-keep class com.facebook.** {
   *;
}

# GLIDE# START
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep public class org.jsoup.** {
public *;
}
-keep class com.github.mikephil.charting.** { *; }

########--------Retrofit + RxJava--------#########
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-dontwarn sun.misc.Unsafe
-dontwarn com.octo.android.robospice.retrofit.RetrofitJackson**
-dontwarn retrofit.appengine.UrlFetchClient
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn retrofit.**

-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   long producerNode;
   long consumerNode;
}


# ALSO REMEMBER KEEPING YOUR MODEL CLASSES
-keep class com.your.package.model.** { *; }

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule


-keep public class org.jsoup.** {
public *;
}


-keepclasseswithmembers class com.taximobility.driver.data.** {
    *;   }

-keepclasseswithmembers class com.mayan.sospluginmodlue.**{
*;}

-keepclasseswithmembers class com.mayan.sospluginmodlue.model.status.apiData.** { *; }


-keepnames class com.taximobility.driver.fragments.*
-keep class com.taximobility.driver.fragments.*

# Data Binding
-dontwarn android.databinding.**
-keep class android.databinding.** { *; }

-dontobfuscate
-dontoptimize
-dontpreverify
-ignorewarnings

## Android architecture components: Lifecycle
# LifecycleObserver's empty constructor is considered to be unused by proguard
-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}
# ViewModel's empty constructor is considered to be unused by proguard
-keepclassmembers class * extends android.arch.lifecycle.ViewModel {
    <init>(...);
}
# keep Lifecycle State and Event enums values
-keepclassmembers class android.arch.lifecycle.Lifecycle$State { *; }
-keepclassmembers class android.arch.lifecycle.Lifecycle$Event { *; }
# keep methods annotated with @OnLifecycleEvent even if they seem to be unused
# (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)
-keepclassmembers class * {
    @android.arch.lifecycle.OnLifecycleEvent *;
}

#############
# Retrofit
#############
-dontnote okio.**


#############
# HttpClient Legacy (Ignore) - org.apache.http legacy
#############
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**
##########
# Glide
##########
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-dontnote com.bumptech.glide.**
#############
# Stetho
#############
-dontnote com.facebook.stetho.**


##Crashlytics
-keep class com.crashlytics.** { *; }
-keepattributes SourceFile,LineNumberTable

#-keep class com.firebase.** { *; }

-keep public class * implements androidx.versionedparcelable.VersionedParcelable
