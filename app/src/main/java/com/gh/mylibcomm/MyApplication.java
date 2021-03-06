package com.gh.mylibcomm;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import androidx.multidex.MultiDexApplication;

import com.gh.libbase.utils.RxTool;
import com.jeremyliao.liveeventbus.LiveEventBus;


public class MyApplication extends MultiDexApplication {
    private static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        LiveEventBus.config().lifecycleObserverAlwaysActive(true).autoClear(true);
        instance = this;

        RxTool.init(instance);

    }
    //设置app字体不允许随系统调节而发生大小变化
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1) {
            //非默认值
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        Configuration newConfig = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (newConfig.fontScale != 1) {
            newConfig.fontScale = 1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Context configurationContext = createConfigurationContext(newConfig);
                resources = configurationContext.getResources();
                displayMetrics.scaledDensity = displayMetrics.density * newConfig.fontScale;
            } else {
                //updateConfiguration 在 API 25(7.0以上系统)之后，被方法 createConfigurationContext 替代
                resources.updateConfiguration(newConfig, displayMetrics);
            }
        }
        return resources;
    }

    public static MyApplication getThisApplication() {
        return instance;
    }
}
