package com.hyena.framework.samples;

import android.os.Bundle;

import com.hyena.framework.app.activity.NavigateActivity;
import com.hyena.framework.app.fragment.BaseFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;
import com.hyena.framework.app.fragment.UIViewFactory;
import com.hyena.framework.samples.evn.ViewFactoryImpl;
import com.hyena.framework.samples.music.MusicPlayFragment;
import com.hyena.framework.samples.plugin.InstrumentationHook;

public class MainActivity extends NavigateActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InstrumentationHook.hook();
        UIViewFactory.getViewFactory().registViewBuilder(new ViewFactoryImpl());
        showFragment(BaseUIFragment.newFragment(this, MusicPlayFragment.class, null));
//        Object result = InvokeHelper.getFieldValue(this, "mBase");
//        LogUtil.v("yangzc", result.getClass().getName());

//        Intent intent = new Intent(this, PluginService.class);
//        startService(intent);

        //test network
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpProvider provider = new HttpProvider();
//                HttpResult ret = provider.doGet("http://knowapp.b0.upaiyun.com/ss/cityList/cityList16.json", 30, new DataHttpListener());
//                LogUtil.v("yangzc", ret.getResult());
//            }
//        }).start();

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

    @Override
    public BaseUIFragmentHelper getUIFragmentHelper(BaseFragment fragment) {
        if (fragment instanceof BaseUIFragment) {
            return new BaseUIFragmentHelper((BaseUIFragment)fragment);
        }
        return null;
    }

}
