package com.example.yun.filesss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Yun on 2016-03-21.
 */
public class MyBroadcast extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_MOUNTED == intent.getAction()) {
            Toast.makeText(context, "sd카드가 마운트되었습니다.", Toast.LENGTH_SHORT).show();
        }
        else if (Intent.ACTION_MEDIA_UNMOUNTED  == intent.getAction()) {
            Toast.makeText(context, "sd카드가 제거되었습니다.", Toast.LENGTH_SHORT).show();
        }



    }
}
