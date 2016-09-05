package com.hyena.framework.samples;

import android.os.Bundle;

import com.hyena.framework.app.activity.NavigateActivity;
import com.hyena.framework.app.fragment.BaseFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;
import com.hyena.framework.app.fragment.UIViewFactory;
import com.hyena.framework.samples.evn.ViewFactoryImpl;
import com.hyena.framework.samples.plugin.InstrumentationHook;
import com.hyena.framework.samples.webview.WebViewFragment;

public class MainActivity extends NavigateActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InstrumentationHook.hook();
        UIViewFactory.getViewFactory().registViewBuilder(new ViewFactoryImpl());

        showFragment(BaseUIFragment.newFragment(this, UIFragment.class, null));
    }

    @Override
    public BaseUIFragmentHelper getUIFragmentHelper(BaseFragment fragment) {
        if (fragment instanceof BaseUIFragment) {
            return new BaseUIFragmentHelper((BaseUIFragment)fragment);
        }
        return null;
    }

}
