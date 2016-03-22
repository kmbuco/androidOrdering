package com.copia.copiasalesmobile.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.copia.copiasalesmobile.SQLite.DatabaseConnectorSqlite;

/**
 * Created by mbuco on 3/22/16.
 */
public class ServiceSyncOrders extends Service {
    DatabaseConnectorSqlite dbConn;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("~~~~~~~~``oo``~~~~~~~~", "Service Created");
    }
}
