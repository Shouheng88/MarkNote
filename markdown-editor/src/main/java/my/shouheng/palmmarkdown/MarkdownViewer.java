package my.shouheng.palmmarkdown;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import my.shouheng.palmmarkdown.fastscroller.FastScrollWebView;
import my.shouheng.palmmarkdown.tools.GenieUtils;

/**
 * Created by wangshouheng on 2017/6/29. */
public class MarkdownViewer extends FastScrollWebView {

    private static final String TAG = "MarkdownViewer";

    private int primaryColor = Color.parseColor("#4CAF50");
    private int primaryDark = Color.parseColor("#388E3C");

    private OnLoadingFinishListener mLoadingFinishListener;
    private OnImageClickedListener onImageClickedListener;
    private OnAttachmentClickedListener onAttachmentClickedListener;

    private static final String VIDEO_MIME_TYPE = "video/*";
    private static final String SCHEME_HTTPS = "https";
    private static final String SCHEME_HTTP = "http";
    private static final String PDF_MIME_TYPE = "application/pdf";
    private static final String _3GP = ".3gp";
    private static final String _MP4 = ".mp4";
    private static final String _PDF = ".pdf";

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

    public void setOnAttachmentClickedListener(OnAttachmentClickedListener onAttachmentClickedListener) {
        this.onAttachmentClickedListener = onAttachmentClickedListener;
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
            if (this.markdowmWebView.mLoadingFinishListener != null) {
                this.markdowmWebView.mLoadingFinishListener.onLoadingFinish();
            }
        }

        @Override
        public final void onReceivedError(WebView webView, int i, String str, String str2) {
            Log.e(TAG, "onReceivedError :errorCode:" + i + "description:" + str + "failingUrl" + str2);
        }

        @Override
        public final boolean shouldOverrideUrlLoading(WebView webView, String url) {
            Log.d(TAG, "shouldOverrideUrlLoading: " + url);
            if (!TextUtils.isEmpty(url)){
                Uri uri = Uri.parse(url);

                /*
                 * Open the http or https link from chrome tab. */
                if (SCHEME_HTTPS.equalsIgnoreCase(uri.getScheme())
                        || SCHEME_HTTP.equalsIgnoreCase(uri.getScheme())) {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder
                            .setToolbarColor(primaryColor)
                            .setSecondaryToolbarColor(primaryDark)
                            .build();
                    customTabsIntent.launchUrl(webView.getContext(), Uri.parse(url));
                    // Consume the intent here.
                    return true;
                }

                /*
                 * Open the files of given format. */
                if (url.endsWith(_3GP) || url.endsWith(_MP4)) {
                    startActivity(uri, VIDEO_MIME_TYPE);
                    return true;
                } else if (url.endsWith(_PDF)) {
                    startActivity(uri, PDF_MIME_TYPE);
                    return true;
                } else {
                    /*
                     * Let the callback resolve the intent. */
                    if (onAttachmentClickedListener != null) {
                        onAttachmentClickedListener.onAttachmentClicked(uri);
                    }
                    return true;
                }
            }
            return true;
        }
    }

    private void startActivity(Uri uri, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, mimeType);
        GenieUtils.startActivityFailSafe(getContext(), intent);
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

    public interface OnAttachmentClickedListener {
        void onAttachmentClicked(Uri uri);
    }
}
