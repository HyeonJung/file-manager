package com.example.yun.filesss;

import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import android.widget.Toast;


/**
 * Created by Yun on 2016-05-20.
 */
public class FileThread extends Thread {
    FileThread ft;
    File f;
    String s;
    FIleManager.ListViewAdapter la;
    public FileThread(File file, String save_file, FIleManager.ListViewAdapter la) {
        f=file;
        s=save_file;
        this.la = la;
    }

    public void run(){
        copyFile(f,s);
        while (la.getCount() != 0)
            la.remove(0);
    }

    private boolean copyFile(File file, String save_file) {
        boolean result;
        if (file != null && file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream newfos = new FileOutputStream(save_file);
                int readcount = 0;
                byte[] buffer = new byte[1024];
                while ((readcount = fis.read(buffer, 0, 1024)) != -1) {
                    newfos.write(buffer, 0, readcount);
                }
                newfos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = true;
        } else {
            result = false;
        }
        return result;
    }
}

