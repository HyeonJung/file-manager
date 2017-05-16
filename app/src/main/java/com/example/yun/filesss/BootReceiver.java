package com.example.yun.filesss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Yun on 2016-05-10.
 */
public class BootReceiver extends BroadcastReceiver {
    SQLiteDatabase namedb;
    int count2;
    Cursor cursor2;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

                    Intent i = new Intent(context, NameChange.class);
                    context.startService(i);

            }
        if(intent.getAction().equals("ACTION.RESTART.PersistentService")){
              Log.d("RestartService", "ACTION_RESTART_PERSISTENTSERVICE");
             Intent i = new Intent(context,NameChange.class);
             context.startService(i);
        }


    }
}

