# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/bestjoy/Downloads/adt-bundle-mac-x86_64-20140702/sdk/tools/proguard/proguard-android.txt
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

#保证条码解析处理程序不被删除或重命名
#-keep class com.bestjoy.app.common.qrcode.result.ParsedResult
#-keep class com.bestjoy.app.common.qrcode.result.ResultParser
#
#-keep class * extends com.bestjoy.app.common.qrcode.result.ParsedResult
#
#-keep class * extends com.bestjoy.app.common.qrcode.result.ResultParser
#
#-keep class * extends com.bestjoy.app.common.qrcode.result.ResultHandler {
#    public <init>(android.app.Activity, com.bestjoy.app.common.qrcode.result.ParsedResult);
#}
#
#-keep class com.bestjoy.library.scan.utils.QRGenerater {
#    public *;
#}
#
#-keep class com.bestjoy.library.scan.utils.DebugUtils {
#    public *;
#}
#
#-keep class com.bestjoy.** {
#    public *;
#}