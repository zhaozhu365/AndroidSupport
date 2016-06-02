package com.hyena.framework.samples.webview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.hyena.framework.app.fragment.BaseWebFragment;
import com.hyena.framework.app.widget.HybirdWebView;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.FileUtils;
import com.hyena.framework.utils.MsgCenter;

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
//        loadWebView();
//        setWebView(mWebView);

        MsgCenter.registerLocalReceiver(mReceiver, new IntentFilter("ssssssssssssss"));
        MsgCenter.registerLocalReceiver(mReceiver, new IntentFilter("ssssssssssssss"));
        MsgCenter.registerLocalReceiver(mReceiver, new IntentFilter("ssssssssssssss"));
        MsgCenter.registerLocalReceiver(mReceiver, new IntentFilter("ssssssssssssss"));
        MsgCenter.registerLocalReceiver(mReceiver, new IntentFilter("ssssssssssssss"));
        MsgCenter.registerLocalReceiver(mReceiver, new IntentFilter("ssssssssssssss"));
        MsgCenter.registerLocalReceiver(mReceiver, new IntentFilter("ssssssssssssss"));
        MsgCenter.sendLocalBroadcast(new Intent("ssssssssssssss"));
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.v("yangzc", "onReceive: " + intent.getAction());
        }
    };

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
    }

    private static String mTemplate = "";
    private void loadWebView() {
        try {
            //重用模板
            if(TextUtils.isEmpty(mTemplate)) {
//                File remoteFile = DirContext.getTemplateFile();
                byte data[];
//                if (remoteFile != null && remoteFile.exists()) {
//                    data = FileUtils.getBytes(remoteFile);
//                    mTemplate = new String(data);
//                }
                if (TextUtils.isEmpty(mTemplate)) {
                    AssetManager manager = BaseApp.getAppContext().getResources().getAssets();
                    data = FileUtils.getBytes(manager.open("newest.html"));
                    mTemplate = new String(data);
                }
            }

//            if (mQuestionIf != null) {
//                JSONObject question = new JSONObject();
//                question.put("question", mQuestionIf.mQuestion.replaceAll("\n", "").replaceAll("\r", ""));
//
                String html = mTemplate;
//                html = html.replace("${isChoice}", mPlayHelper.isChoice(mQuestionIf) + "");
//                html = html.replace("${questionIndex}", mQuestionIndex + "");
//                html = html.replace("${questionId}", mQuestionIf.mQuestionId);
//                html = html.replace("${question}", question.toString());

                mWebView.loadDataWithBaseURL("file:///android_asset/", html,
                        "text/html", "UTF-8", "newest.html");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
