package com.copia.copiasalesmobile.utilities;

import android.content.Context;

/**
 * Created by mbuco on 3/21/16.
 */
public class MyApp extends android.app.Application {
    private static MyApp instance;
    public MyApp() {
        instance = this;
    }
    public static Context getContext() {
        return instance;
    }
}