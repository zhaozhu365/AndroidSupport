package com.hyena.framework.samples.webview;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.hyena.framework.app.fragment.BaseWebFragment;
import com.hyena.framework.app.widget.HybirdWebView;
import com.hyena.framework.app.widget.WebViewClientWrapper;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.FileUtils;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.MathUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
        return mWebView;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        loadWebView();
        setWebView(mWebView);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.setWebViewClient(new WebViewClientWrapper(mWebViewClient) {
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                mWebView.handleUrlLoading(url);
                LogUtil.v("yangzc", url);
            }
        });
    }

    @Override
    public void onPageFinished() {
        super.onPageFinished();
        try {
            InputStream is = BaseApp.getAppContext().getResources().getAssets().open("js/support_inject.js");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileUtils.copyStream(is, baos);
            is.close();
            String data = new String(baos.toByteArray()).replaceAll("[\\t\\n\\r]", "");
            LogUtil.v("yangzc", data);
            mWebView.loadUrl("javascript:" + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean onCallMethodImpl(String methodName, Hashtable paramsMap) {
        if ("image_load".equalsIgnoreCase(methodName)) {
            try {
                String url = URLDecoder.decode((String) paramsMap.get("url"), "utf-8");
                LogUtil.v("yangzc", "resource: " + url);
                ImageFetcher.getImageFetcher().loadImage(url, url, new ImageFetcher.ImageFetcherListener() {
                    @Override
                    public void onLoadComplete(String imageUrl, Bitmap bitmap, Object object) {
                        if (bitmap != null) {
                            File file = ImageFetcher.getImageFetcher().getCacheFilePath(imageUrl);
                            runJs("showImage", imageUrl, Uri.fromFile(file).toString());
                            LogUtil.v("yangzc", "onLoadComplete --> " + Uri.fromFile(file).toString());
                        }
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return true;
        } else if ("image_show".equalsIgnoreCase(methodName)) {
            try {
                int x = MathUtils.valueOfInt((String) paramsMap.get("l"));
                int y = MathUtils.valueOfInt((String) paramsMap.get("t"));
                int w = MathUtils.valueOfInt((String) paramsMap.get("w"));
                int h = MathUtils.valueOfInt((String) paramsMap.get("h"));

                String url = URLDecoder.decode((String) paramsMap.get("url"), "utf-8");
                Rect rect = new Rect(x, y, x + w, y + h);
                getUIFragmentHelper().showPicture(rect, url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return super.onCallMethodImpl(methodName, paramsMap);
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
    }

    private void loadWebView() {
        try {
//            mWebView.loadUrl("file:///android_asset/newest.html");
            mWebView.loadUrl("http://www.sina.com.cn");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
