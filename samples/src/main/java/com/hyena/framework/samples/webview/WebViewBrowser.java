package com.hyena.framework.samples.webview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyena.framework.app.fragment.BaseWebFragment;
import com.hyena.framework.app.widget.HybirdWebView;
import com.hyena.framework.samples.R;
import com.hyena.framework.utils.VersionUtils;

/**
 * Created by yangzc on 16/9/5.
 */
public class WebViewBrowser extends BaseWebFragment {

    private EditText mEditText;
    private HybirdWebView mWebview;
    private Button mBtnRefresh;

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_webview_browser, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mEditText = (EditText) view.findViewById(R.id.et_url);
        mWebview = (HybirdWebView) view.findViewById(R.id.hwv_web);

        setWebView(mWebview);
        String userAgent = mWebview.getSettings().getUserAgentString();
        mWebview.getSettings().setUserAgentString(userAgent
                + " AppOS/android"
                + " AppFrom/knowBox"
                + " AppVersion/" + VersionUtils.getVersionCode(getActivity()));
        mWebview.setHorizontalScrollBarEnabled(false);
        mWebview.setVerticalScrollBarEnabled(false);

        mBtnRefresh = (Button) view.findViewById(R.id.btn_refresh);
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebview.loadUrl("http://" + mEditText.getText().toString());
            }
        });
    }
}
