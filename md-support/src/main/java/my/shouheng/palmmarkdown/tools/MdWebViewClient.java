package my.shouheng.palmmarkdown.tools;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import my.shouheng.palmmarkdown.listener.OnAttachmentClickedListener;
import my.shouheng.palmmarkdown.listener.OnLoadingFinishListener;

/**
 * Created by shouh on 2018/3/25. */
public class MdWebViewClient extends WebViewClient {

    private static final String TAG = "MdWebViewClient";

    private OnLoadingFinishListener onLoadingFinishListener;

    private OnAttachmentClickedListener onAttachmentClickedListener;

    public MdWebViewClient(OnLoadingFinishListener finishListener, OnAttachmentClickedListener clickedListener) {
        this.onLoadingFinishListener = finishListener;
        this.onAttachmentClickedListener = clickedListener;
    }

    @Override
    public final void onPageFinished(WebView webView, String str) {
        if (onLoadingFinishListener != null) {
            onLoadingFinishListener.onLoadingFinish();
        }
    }

    @Override
    public final void onReceivedError(WebView webView, int i, String str, String str2) {
        Log.e(TAG, "onReceivedError :errorCode:" + i + "description:" + str + "failingUrl" + str2);
    }

    @Override
    public final boolean shouldOverrideUrlLoading(WebView webView, String url) {
        if (onAttachmentClickedListener != null) {
            onAttachmentClickedListener.onAttachmentClicked(url);
        }
        return true;
    }

    public void setOnLoadingFinishListener(OnLoadingFinishListener onLoadingFinishListener) {
        this.onLoadingFinishListener = onLoadingFinishListener;
    }

    public void setOnAttachmentClickedListener(OnAttachmentClickedListener onAttachmentClickedListener) {
        this.onAttachmentClickedListener = onAttachmentClickedListener;
    }
}
