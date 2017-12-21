package my.shouheng.palmmarkdown;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsIntent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangshouheng on 2017/6/29. */
public class MarkdownViewer extends WebView{

    private static final String TAG = "MarkdownViewer";

    private int primaryColor = Color.parseColor("#4CAF50");
    private int primaryDark = Color.parseColor("#388E3C");

    private OnLoadingFinishListener mLoadingFinishListener;
    private OnImageClickedListener onImageClickedListener;


    public MarkdownViewer(Context context) {
        super(context);
        init();
    }

    public MarkdownViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarkdownViewer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void init(){
        if (Build.VERSION.SDK_INT >= 21) {
            WebView.enableSlowWholeDocumentDraw();
        }
        this.getSettings().setJavaScriptEnabled(true);
        this.setVerticalScrollBarEnabled(true);
        this.setHorizontalScrollBarEnabled(false);
        this.addJavascriptInterface(new JavaScriptInterface(), "showPhotos");
        this.setWebViewClient(new MdWebViewClient(this));
    }


    public void setWebViewTheme(boolean isDarkTheme) {
        this.loadUrl(isDarkTheme ?
                "file:///android_asset/markdown_night.html" :
                "file:///android_asset/markdown.html");
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setPrimaryDark(int primaryDark) {
        this.primaryDark = primaryDark;
    }


    public final void parseMarkdown(String str, boolean z) {
        this.loadUrl("javascript:parseMarkdown(\"" + str
                .replace("\n", "\\n")
                .replace("\"", "\\\"")
                .replace("'", "\\'") + "\", " + z + ")");
        this.loadUrl("javascript:(" + readJS("showPhotos.js") + ")()");
    }


    public void setOnLoadingFinishListener(OnLoadingFinishListener loadingFinishListener) {
        this.mLoadingFinishListener = loadingFinishListener;
    }

    public void setOnImageClickedListener(OnImageClickedListener onImageClickedListener) {
        this.onImageClickedListener = onImageClickedListener;
    }


    private String readJS(String fileName) {
        try {
            InputStream inStream = getResources().getAssets().open(fileName);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inStream.read(bytes)) > 0) {
                outStream.write(bytes, 0, len);
            }
            return outStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private final class MdWebViewClient extends WebViewClient {
        final MarkdownViewer markdowmWebView;

        private MdWebViewClient(MarkdownViewer markdownPreviewView) {
            this.markdowmWebView = markdownPreviewView;
        }

        @Override
        public final void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            if (this.markdowmWebView.mLoadingFinishListener != null) {
                this.markdowmWebView.mLoadingFinishListener.onLoadingFinish();
            }
        }

        @Override
        public final void onReceivedError(WebView webView, int i, String str, String str2) {
            new StringBuilder("onReceivedError :errorCode:")
                    .append(i)
                    .append("description:")
                    .append(str)
                    .append("failingUrl")
                    .append(str2);
        }

        @Override
        public final boolean shouldOverrideUrlLoading(WebView webView, String url) {
            if (!TextUtils.isEmpty(url)){
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder
                        .setToolbarColor(primaryColor)
                        .setSecondaryToolbarColor(primaryDark)
                        .build();
                customTabsIntent.launchUrl(webView.getContext(), Uri.parse(url));
            }
            return true;
        }
    }

    private final class JavaScriptInterface {

        /**
         * 当点击了图片的时候使用图片浏览活动展示图片大图
         *
         * @param url 当前显示的图片的url */
        @JavascriptInterface
        public void showPhotosInGallery(String url, String[] urls) {
            Log.d(TAG, "showPhotosInGallery: " + url);
            if (onImageClickedListener != null) {
                onImageClickedListener.onImageClicked(url, urls);
            }
        }
    }

    public interface OnImageClickedListener {
        void onImageClicked(String url, String[] urls);
    }

    public interface OnLoadingFinishListener {
        void onLoadingFinish();
    }
}
