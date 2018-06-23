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
import my.shouheng.palmmarkdown.tools.FileHelper;
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

    private String themeCss;
    private String noteHtml;

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

    public void setHtmlResource(final boolean isDarkTheme) {
        themeCss = FileHelper.readAssetsContent(getContext(), isDarkTheme ? "dark_theme.txt" : "light_theme.txt");
    }

    public final void parseMarkdown(String str) {
        if (markdownParseListener != null) markdownParseListener.onStart();
        new MarkdownParser(new MarkdownParser.OnGetResultListener() {
            @Override
            public void onGetResult(String html) {
                // You must use loadDataWithBaseURL method
//                Log.d(TAG, "onGetResult: " + html);
                loadDataWithBaseURL("", noteHtml =
                                "<html>\n" +
                                "<head>\n" +
                                "    <meta charset=\"utf-8\"/>\n" +
                                themeCss +
                                "    <script type=\"text/x-mathjax-config\">\n" +
                                "        MathJax.Hub.Config({\n" +
                                "            showProcessingMessages: false,\n" +
                                "            messageStyle: 'none',\n" +
                                "            showMathMenu: false,\n" +
                                "            tex2jax: {\n" +
                                "                inlineMath: [ ['$','$'], [\"\\\\(\",\"\\\\)\"] ],\n" +
                                "                displayMath: [ ['$$','$$'], [\"\\\\[\",\"\\\\]\"] ]\n" +
                                "            }\n" +
                                "        });\n" +
                                "    </script>\n" +
                                "    <script type=\"text/javascript\" async\n" +
                                "        src=\"https://cdn.bootcss.com/mathjax/2.7.3/MathJax.js?config=TeX-MML-AM_CHTML\">\n" +
                                "    </script>\n" +
                                "    <link rel=\"dns-prefetch\" href=\"//cdn.mathjax.org\" />\n" +
                                "</head>\n" +
                                "<body>\n" +
                                "    <article id=\"content\" class=\"markdown-body\">"
                                + html +
                                "</article>\n" +
                                "<script>" +
                                "    var imgs = document.getElementsByTagName(\"img\");\n" +
                                "    var list = new Array();\n" +
                                "    for(var i = 0; i < imgs.length; i++){\n" +
                                "        list[i] = imgs[i].src;\n" +
                                "    }\n" +
                                "    for(var i = 0; i < imgs.length; i++){\n" +
                                "        imgs[i].onclick = function() {\n" +
                                "            jsCallback.showPhotosInGallery(this.src, list);\n" +
                                "        }\n" +
                                "    }\n" +
                                "</script>" +
                                "</body>\n" +
                                "</html>",
                        "text/html",
                        "UTF-8",
                        "");
                if (markdownParseListener != null) markdownParseListener.onEnd();
            }
        }).execute(str);
    }

    public final void outHtml(OnGetHtmlListener onGetHtmlListener) {
        this.onGetHtmlListener = onGetHtmlListener;
//        this.loadUrl("javascript:jsCallback.processHTML(document.documentElement.outerHTML);");
        if (onGetHtmlListener != null) {
            onGetHtmlListener.onGetHtml(noteHtml);
        }
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
}
