package cc.dobot.crtcpdemo;

import android.app.Application;

public class AppDemo extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
    }

    private static AppDemo sInstance;

    public static AppDemo getInstance() {
        return sInstance;
    }
}
