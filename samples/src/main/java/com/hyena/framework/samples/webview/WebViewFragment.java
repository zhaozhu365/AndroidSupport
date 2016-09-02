package com.hyena.framework.samples.webview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.hyena.framework.app.fragment.BaseWebFragment;
import com.hyena.framework.app.widget.HybirdWebView;
import com.hyena.framework.app.widget.WebViewClientWrapper;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.ImageFetcher;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;

/**
 * Created by yangzc on 16/5/30.
 */
public class WebViewFragment extends BaseWebFragment {

    private HybirdWebView mWebView;

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        mWebView = new HybirdWebView(getActivity());
        return super.onCreateViewImpl(savedInstanceState);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        loadWebView();
        setWebView(mWebView);
        mWebView.setWebViewClient(new WebViewClientWrapper(mWebViewClient) {
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                mWebView.handleUrlLoading(url);
            }
        });
    }

    @Override
    protected boolean onCallMethodImpl(String methodName, Hashtable paramsMap) {
        if ("image_load".equalsIgnoreCase(methodName)) {
            try {
                String url = URLDecoder.decode((String)paramsMap.get("url"), "utf-8");
                LogUtil.v("yangzc", "resource: " + url);
                ImageFetcher.getImageFetcher().loadImage(url, url, new ImageFetcher.ImageFetcherListener() {
                    @Override
                    public void onLoadComplete(String imageUrl, Bitmap bitmap, Object object) {
                        if (bitmap != null) {
                            File file = ImageFetcher.getImageFetcher().getCacheFilePath(imageUrl);
                            runJs("showImage", imageUrl, file.toURI().getPath());
                        }
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onCallMethodImpl(methodName, paramsMap);
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
    }

    private void loadWebView() {
        try {
            mWebView.loadUrl("file:///android_asset/newest.html");
//            mWebView.loadUrl("http://www.baidu.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
