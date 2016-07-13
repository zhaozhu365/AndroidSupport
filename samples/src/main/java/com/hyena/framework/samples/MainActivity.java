package com.hyena.framework.samples;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.debug.InvokeHelper;
import com.hyena.framework.network.HttpListener;
import com.hyena.framework.network.HttpProvider;
import com.hyena.framework.network.HttpResult;
import com.hyena.framework.network.listener.DataHttpListener;
import com.hyena.framework.samples.plugin.InstrumentationHook;
import com.hyena.framework.samples.plugin.PluginService;
import com.hyena.framework.samples.widgets.ChartFragment;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InstrumentationHook.hook();
        //去除标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //强制设置为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //窗口自适应键盘
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_main);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, new ChartFragment());
        transaction.commitAllowingStateLoss();

        Object result = InvokeHelper.getFieldValue(this, "mBase");
        LogUtil.v("yangzc", result.getClass().getName());

        Intent intent = new Intent(this, PluginService.class);
        startService(intent);

        //test network
        HttpProvider provider = new HttpProvider();
        HttpResult ret = provider.doGet("http://www.baidu.com", 30, new DataHttpListener());
        LogUtil.v("yangzc", ret.getResult());

//        try {
//            Field instrumentationField = Activity.class.getDeclaredField("mInstrumentation");
//            instrumentationField.setAccessible(true);
//            Object instrumentation = instrumentationField.get(this);
//            LogUtil.v("yangzc", instrumentation.getClass().getName());
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        Intent intent = new Intent(this, StubActivity.StubStandActivity.class);
//        intent.putExtra(InstrumentationHook.ARGS_BUNDLE_PLUGIN_CLZ, PluginActivity.class.getName());
//        startActivity(intent);
    }

}
