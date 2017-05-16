package com.example.yun.filesss;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Yun on 2016-05-07.
 */
public class NameChange extends Service {
    private static final String downpath = "storage/emulated/0/Download"; // 파일이 다운로드 되는 경로
    private static final String TAG = "FileObserverActivity";
    private FileObserver observer, observer2;
    private NotificationManager mNM;
    private Notification mNoti;
    SQLiteDatabase namedb;
    String tablename = "namechange";
    Cursor cursor, cursor2, cursor3;
    String sql = "select * from " + tablename;
    String name, Cname2;
    int count, count2, count3;
    boolean ncrun = false;
    boolean acrun = false;
    BroadcastReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        receiver = new BootReceiver();
        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        namedb = openOrCreateDatabase("FilenameChange.db", MODE_WORLD_WRITEABLE, null); // db생성, db 이미 있을시에 오픈
        try {
            namedb.execSQL("create table " + tablename + " (name text)"); // namechange 테이블 생성
        } catch (Exception e) {
        }
        try {
            namedb.execSQL("create table changeset (sett integer)");
        } catch (Exception e) {
        }
        cursor2 = namedb.rawQuery("select * from changeset", null);
        count2 = cursor2.getCount();
        for (int i = 0; i < count2; i++) {
            cursor2.moveToNext();
            if (cursor2.getInt(0) == 1) {
                ncrun = true;
            } else {
                ncrun = false;
            }
        }

        cursor3 = namedb.rawQuery("select * from classifyset", null);
        count3 = cursor3.getCount();
        for (int i = 0; i < count3; i++) {
            cursor3.moveToNext();
            if (cursor3.getInt(0) == 1) {
                acrun = true;
            } else {
                acrun = false;
            }
        }

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service가 중지되었습니다.", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestory()");
        if (ncrun) {
            observer.stopWatching();
        }
        if (acrun) {
            if(!ncrun)
            observer2.stopWatching();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ncrun) {
            Toast.makeText(this, "Service가 시작되었습니다.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "onStart()");
            observer = new FileObserver(downpath) {
                @Override
                public void onEvent(int event, String path) {
                    if (event == 256) { // 디렉토리에 새로운 파일이 생성 되었을 때
                        Log.v(path, path+"");
                        cursor = namedb.rawQuery(sql, null);
                        count = cursor.getCount(); // db안의 데이터 총 갯수를 확인
                        String spath = "storage/emulated/0/Download";
                        String fileet = "";
                        if(acrun) {
                            String Et = getExtension(path).toUpperCase().toString();
                            if (Et.equals("JPG") || Et.equals("PNG") || Et.equals("JPEG") || Et.equals("GIF") || Et.equals("AI") || Et.equals("PSD") || Et.equals("EPS")){
                                fileet = "이미지 파일";
                            } else if (Et.equals("AAC") || Et.equals("MP3") || Et.equals("WAV") || Et.equals("WMA")) { //음악파일
                                fileet= "음악 파일";
                            } else if (Et.equals("AVI") || Et.equals("FLV") || Et.equals("MKV") || Et.equals("WMV") || Et.equals("MP4")) { //동영상
                                fileet = "동영상 파일";
                            } else if (Et.equals("TXT")) { //텍스트파일
                                fileet = "문서 파일";
                            } else if (Et.equals("ZIP")) { //알집
                                fileet = "압축 파일";
                            }
                            else{
                                fileet = "기타 파일";
                            }

                            spath = "storage/emulated/0/Download/" + fileet;
                            File dir = new File("storage/emulated/0/Download");
                            File[] files = dir.listFiles();
                            for (int i = 0; i < files.length; i++) {
                                if (files[i].getName().equals(fileet)) {
                                    break;
                                } else {
                                    File newdir = new File("storage/emulated/0/Download/" + fileet);
                                    newdir.mkdir();
                                    break;
                                }

                            }
                        }
                        for (int i = 0; i < count; i++) {
                            cursor.moveToNext();//
                            name = cursor.getString(0);
                        } //다시 select문을 사용하여서
                        File dir = new File("storage/emulated/0/Download/");
                        File newFile = new File(dir, path);
                        int num = 1;
                        while (true) {
                            String Cname = name + num + "." + getExtension(path); // 바꿀 이름을 문자열에 저장한다.
                            File filecheck = new File(spath+"/"+ Cname); //이미 있는 파일 명인지 탐색하기 위해 파일 생성
                            if (filecheck.exists()) { // 파일명이 존재할 때
                                if (filecheck.length() != 0) { // 파일안의 내용이 0이 아닐 때
                                    num++; // num 값을 1 증가시킨다.
                                } else { // 파일의 내용이 0일 때
                                    File newFile2 = new File(spath, Cname); // 파일의 이름을 바꾸기 위해 새로운 파일 newFile2의 이름을 Cname에 저장된 문자열로 생성
                                    // filedown(path, Cname); // 파일의 이름을 바꾸었다는 노티피케이션 출력
                                    Cname2 = Cname;
                                    //moveFile(path,"storage/emulated/0/Download/"+path,spath+"/"+path);
                                    reNameFile(newFile, newFile2); // 다운로드 받은 파일의 이름을 newFile2에 저장된 Cname의 값으로 변경한다.
                                    break;
                                }
                            } else {
                                File newFile2 = new File(spath, Cname);
                                // filedown(path, Cname);
                                Cname2 = Cname;
                                reNameFile(newFile, newFile2);
                                break;
                            }
                        }

                        filedown(path, Cname2);
                        Toast.makeText(getApplicationContext(),path+"를 "+ Cname2 + "로 변경하였습니다.",Toast.LENGTH_LONG).show();
                        // mNM.notify(7777, mNoti); // 노티피케이션 출력

                    }
                }
            };
            observer.startWatching();
        }
        else if (acrun) {
            Toast.makeText(this, "Service가 시작되었습니다.", Toast.LENGTH_LONG).show();
            observer2 = new FileObserver(downpath) {

                @Override
                public void onEvent(int event, String path) {
                    if (event == 256) { // 디렉토리에 새로운 파일이 생성 되었을 때
                        File dir = new File("storage/emulated/0/Download");
                        //File newFile = new File(dir, path);
                        int num = 1;
                        String fileet = getExtension(path).toUpperCase().toString();
                        File[] files = dir.listFiles();
                        for (int i = 0; i < files.length; i++) {
                            if (files[i].getName().equals(fileet)) {
                                moveFile(path, "storage/emulated/0/Download/" + path, "storage/emulated/0/Download/" + fileet);
                                break;
                            } else {
                                File newdir = new File("storage/emulated/0/Download/" + fileet);
                                newdir.mkdir();
                                moveFile(path, "storage/emulated/0/Download/" + path, "storage/emulated/0/Download/" + fileet);
                                break;
                            }

                        }


                    }
                }
            };
            observer2.startWatching();
        }


        return START_STICKY;

    }

    void filedown(String filename, String changename) { // 노티피케이션부분
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.fileicon) //상태표시줄에 보이는 아이콘 모양
                .setTicker("파일이름 자동 변경")                                    //알림이 발생될 때 잠시 보이는 글씨
                .setContentTitle("파일이름 자동 변경")                              //알림창에서의 제목
                .setContentText(filename + " 의 이름을 " + changename + "으로 변경 하였습니다.");                  //알림창에서의 글씨

       /* mNoti = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("파일이름 자동 변경")
                .setContentText(filename + "파일의 이름을 " + changename + "으로 변경 하였습니다.")
                .setSmallIcon(R.drawable.fileicon)
                .setTicker("알림!!!")
                .setAutoCancel(true)
                .build();*/
        builder.setVibrate(new long[]{0, 1000});
        Intent intent = new Intent(this, NameChange.class);
        intent.putExtra("Cname2", Cname2);
        PendingIntent pending = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pending);   //PendingIntent 설정
        builder.setAutoCancel(true);         //클릭하면 자동으로 알림 삭제
        Notification notification = builder.build();    //Notification 객체 생성
        manager.notify(0, notification);    //NotificationManager가 알림(Notification)을 표시, id는 알림구분용


    }

    private boolean reNameFile(File file, File new_name) { // 파일이름 변경 부분
        boolean result;
        if (file != null && file.exists() && file.renameTo(new_name)) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public static String getExtension(String fileStr) { // 확장자 뽑기
        return fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
    }

    public String moveFile(String fileName, String beforeFilePath, String afterFilePath) { //파일 이동
        String filePath = afterFilePath + "/" + fileName;
        File dir = new File(filePath);
        try {
            File file = new File(beforeFilePath);
            if (file.renameTo(new File(filePath))) { //파일 이동
                return filePath; //성공시 성공 파일 경로 return
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
