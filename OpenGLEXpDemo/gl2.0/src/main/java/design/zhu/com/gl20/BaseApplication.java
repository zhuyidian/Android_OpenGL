package design.zhu.com.gl20;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application { ;
    private static BaseApplication instance;

    private static Context mContextApp;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化全局上下文
        mContextApp = getApplicationContext();
    }

    public synchronized static BaseApplication getInstance() {
        if (null == instance) {
            instance = new BaseApplication();
        }
        return instance;
    }

    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        return mContextApp;
    }
}
