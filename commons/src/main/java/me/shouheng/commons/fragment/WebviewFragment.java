package me.shouheng.commons.fragment;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.facebook.stetho.common.LogUtil;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

import me.shouheng.commons.R;
import me.shouheng.commons.activity.interaction.FragmentKeyDown;
import me.shouheng.commons.databinding.FragmentWebviewBinding;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.*;
import me.shouheng.commons.utils.IntentUtils;
import me.shouheng.commons.utils.PalmUtils;

/**
 * @author shouh
 * @version $Id: WebviewFragment, v 0.1 2018/11/17 20:40 shouh Exp$
 */
@PageName(name = UMEvent.PAGE_WEBVIEW)
public class WebviewFragment extends CommonFragment<FragmentWebviewBinding> implements FragmentKeyDown {

    public final static String ARGUMENT_KEY_URL = "__extra_key_url";
    public final static String ARGUMENT_KEY_TITLE = "__extra_key_title";
    public final static String ARGUMENT_KEY_USE_PAGE_TITLE = "__extra_use_page_title";

    private AgentWeb mAgentWeb;
    private boolean usePageTitle;
    private String url;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_webview;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        getBinding().setIsDarkTheme(isDarkTheme());

        Bundle arguments = getArguments();
        assert arguments != null;
        url = arguments.getString(ARGUMENT_KEY_URL);
        String title = arguments.getString(ARGUMENT_KEY_TITLE);
        if (getActivity() != null) {
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) ab.setTitle(title);
        }
        usePageTitle = arguments.getBoolean(ARGUMENT_KEY_USE_PAGE_TITLE);

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(getBinding().llContainer, -1, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator(accentColor(), 3)
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(new WebViewClient(){
                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        super.onReceivedError(view, errorCode, description, failingUrl);
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                    }

                    @Override
                    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                        super.onReceivedHttpError(view, request, errorResponse);
                    }

                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        super.onReceivedSslError(view, handler, error);
                    }

                    @Override
                    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                        super.onReceivedClientCertRequest(view, request);
                    }

                    @Override
                    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                        super.onReceivedHttpAuthRequest(view, handler, host, realm);
                    }
                })
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setMainFrameErrorView(R.layout.layout_network_error_page, R.id.btn_retry)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .interceptUnkownUrl()
                .createAgentWeb()
                .ready()
                .go(url);
    }

    protected WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            LogUtil.d("onProgressChanged:" + newProgress + "  view:" + view);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (usePageTitle) {
                if (!TextUtils.isEmpty(title)) {
                    /* The max length used to get the title form the web page.
                     * If the title is longer than this value, the longer part will be replaced with '...' */
                    int maxWebPageTitleLength = 15;
                    if (title.length() > maxWebPageTitleLength) {
                        title = title.substring(0, maxWebPageTitleLength).concat("...");
                    }
                }
                if (getActivity() != null) {
                    ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
                    if (ab != null) ab.setTitle(title);
                }
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_web_items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_copy) {
            PalmUtils.copy(getActivity(), url);
            return true;
        } else if (item.getItemId() == R.id.item_open) {
            IntentUtils.openWebPage(getContext(), url);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();// 恢复
        super.onResume();
    }

    @Override
    public void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    public boolean onFragmentKeyDown(int keyCode, KeyEvent event) {
        return mAgentWeb.handleKeyEvent(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroyView();
    }
}
