package my.shouheng.palmmarkdown;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import my.shouheng.palmmarkdown.async.MarkdownParser;
import my.shouheng.palmmarkdown.fastscroller.FastScrollWebView;
import my.shouheng.palmmarkdown.listener.MarkdownParseListener;
import my.shouheng.palmmarkdown.listener.OnAttachmentClickedListener;
import my.shouheng.palmmarkdown.listener.OnGetHtmlListener;
import my.shouheng.palmmarkdown.listener.OnImageClickedListener;
import my.shouheng.palmmarkdown.listener.OnLoadingFinishListener;
import my.shouheng.palmmarkdown.tools.Constant;
import my.shouheng.palmmarkdown.tools.MdWebViewClient;

/**
 * Created by wangshouheng on 2017/6/29. */
public class MarkdownViewer extends FastScrollWebView {

    private static final String TAG = "MarkdownViewer";

    private OnImageClickedListener onImageClickedListener;
    private OnGetHtmlListener onGetHtmlListener;
    private OnLoadingFinishListener onLoadingFinishListener;
    private OnAttachmentClickedListener onAttachmentClickedListener;
    private MarkdownParseListener markdownParseListener;

    private MdWebViewClient webViewClient;

    private String htmlResult;

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
    private void init() {
        if (Build.VERSION.SDK_INT >= 21) WebView.enableSlowWholeDocumentDraw();
        this.getSettings().setJavaScriptEnabled(true);
        this.setVerticalScrollBarEnabled(true);
        this.setHorizontalScrollBarEnabled(false);
        this.addJavascriptInterface(this, "jsCallback");
        this.webViewClient = new MdWebViewClient(onLoadingFinishListener, onAttachmentClickedListener);
        this.setWebViewClient(webViewClient);
    }

    public void setHtmlResource(boolean isDarkTheme) {
        this.loadUrl(isDarkTheme ? "file:///android_asset/markdown_night.html" : "file:///android_asset/markdown.html");
    }

    public final void parseMarkdown(String str) {
        if (markdownParseListener != null) {
            markdownParseListener.onStart();
        }
        new MarkdownParser(new MarkdownParser.OnGetResultListener() {
            @Override
            public void onGetResult(String html) {
                htmlResult = html;
                loadUrl("javascript:parseMarkdown()");
                loadUrl("javascript:(" + Constant.SHOW_PHOTO_JS + ")()");
                if (markdownParseListener != null) {
                    markdownParseListener.onStart();
                }
            }
        }).execute(str);
    }

    public final void outHtml(OnGetHtmlListener onGetHtmlListener) {
        this.onGetHtmlListener = onGetHtmlListener;
        this.loadUrl("javascript:jsCallback.processHTML(document.documentElement.outerHTML);");
    }

    public void setOnLoadingFinishListener(OnLoadingFinishListener finishListener) {
        this.onLoadingFinishListener = finishListener;
        if (webViewClient != null) {
            webViewClient.setOnLoadingFinishListener(onLoadingFinishListener);
        }
    }

    public void setOnImageClickedListener(OnImageClickedListener clickedListener) {
        this.onImageClickedListener = clickedListener;
    }

    public void setOnAttachmentClickedListener(OnAttachmentClickedListener clickedListener) {
        this.onAttachmentClickedListener = clickedListener;
        if (webViewClient != null) {
            webViewClient.setOnAttachmentClickedListener(clickedListener);
        }
    }

    public void setMarkdownParseListener(MarkdownParseListener markdownParseListener) {
        this.markdownParseListener = markdownParseListener;
    }

    @JavascriptInterface
    public void showPhotosInGallery(String url, String[] urls) {
        Log.d(TAG, "showPhotosInGallery: " + url);
        if (onImageClickedListener != null) {
            onImageClickedListener.onImageClicked(url, urls);
        }
    }

    @JavascriptInterface
    public void processHTML(String html) {
        if (onGetHtmlListener != null) {
            onGetHtmlListener.onGetHtml(html);
        }
    }

    @JavascriptInterface
    public String getContent() {
        return htmlResult;
    }
}
