package com.hyena.framework.samples;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;

import com.hyena.framework.app.activity.NavigateActivity;
import com.hyena.framework.app.fragment.BaseFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;
import com.hyena.framework.app.fragment.UIViewFactory;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.network.HttpProvider;
import com.hyena.framework.samples.evn.ViewFactoryImpl;
import com.hyena.framework.samples.plugin.InstrumentationHook;
import com.hyena.framework.samples.webview.WebViewBrowser;
import com.hyena.framework.samples.webview.WebViewFragment;
import com.hyena.framework.utils.HttpHelper;

import java.io.File;

public class MainActivity extends NavigateActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InstrumentationHook.hook();
        UIViewFactory.getViewFactory().registViewBuilder(new ViewFactoryImpl());
//        showFragment(BaseUIFragment.newFragment(this, WebViewBrowser.class, null));
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                boolean result = HttpHelper.storeFile("http://knowapp.b0.upaiyun.com/ss/cityList/cityList20.json",
//                        new File(Environment.getExternalStorageDirectory(), "ss.json").getAbsolutePath(), null);
//                LogUtil.v("yangzc", result + "");
//            }
//        }).start();
        showFragment(BaseUIFragment.newFragment(this, Pull2RefreshFragment.class, null));
    }

    @Override
    public void onPreCreate() {
        super.onPreCreate();
        setTranslucentStatus(true);
    }

    @Override
    public BaseUIFragmentHelper getUIFragmentHelper(BaseFragment fragment) {
        if (fragment instanceof BaseUIFragment) {
            return new BaseUIFragmentHelper((BaseUIFragment)fragment);
        }
        return null;
    }

    @Override
    public void showFragment(BaseFragment fragment) {
        super.showFragment(fragment);
        if (fragment instanceof BaseUIFragment) {
            ((BaseUIFragment) fragment).setStatusTintBarColor(Color.BLUE);
            ((BaseUIFragment) fragment).setStatusTintBarEnable(true);
        }
    }
}
