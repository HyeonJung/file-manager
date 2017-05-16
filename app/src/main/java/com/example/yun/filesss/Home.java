package com.example.yun.filesss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class Home extends AppCompatActivity {
    Button btmemory, btdocument, btmusic, btmovie, btimage, btbookmark, btlately, btdownload;
    Button  btdocumentsd, btmusicsd, btmoviesd, btimagesd, btbookmarksd, btlatelysd, btdownloadsd;
    Button btsettings;
    Button test;
    Animation anim,anim2,anim3;
    String sdcard;
    TextView memory;
    ImageView fakebutton;
    int flag=0;

    private MyBroadcast myb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        myb = new MyBroadcast();
        String check = android.os.Environment.getExternalStorageState();
        long size = folderMemoryCheck("/storage/external_SD/새 폴더");
        //Toast.makeText(this, sdcard+size, Toast.LENGTH_SHORT).show();
        btmemory = (Button)findViewById(R.id.btmemory);
        memory = (TextView)findViewById(R.id.textView2);
        fakebutton = (ImageView)findViewById(R.id.fakebutton);
        btsettings = (Button)findViewById(R.id.btsettings);

        //-----------------------------------------------------------
        // 내장 메모리
        btdocument = (Button)findViewById(R.id.btdocument);
        btdocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,FIleManager.class);
                intent.putExtra("type","non");
                //intent.putExtra("current","non");
                startActivity(intent);
            }
        });
        btmusic = (Button)findViewById(R.id.btmusic);
        btmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,Filekind.class);
                intent.putExtra("category","music");
                startActivity(intent);
            }
        });
        btmovie = (Button)findViewById(R.id.btmovie);
        btmovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,Filekind.class);
                intent.putExtra("category","video");
                startActivity(intent);
            }
        });
        btimage = (Button)findViewById(R.id.btimage);
        btimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,Filekind.class);
                intent.putExtra("category","image");
                startActivity(intent);
            }
        });
        btbookmark = (Button)findViewById(R.id.btbookmark);
        btbookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,BatchChange.class);
                startActivity(intent);
            }
        });
        btlately = (Button)findViewById(R.id.btlatelyfile);
        btlately.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,Filekind.class);
                intent.putExtra("category","lately");
                startActivity(intent);
            }
        });
        btdownload = (Button)findViewById(R.id.btdownload);
        btdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,FIleManager.class);
                intent.putExtra("type","download");
                //intent.putExtra("current","non");
                startActivity(intent);
            }
        });
       /* test = (Button)findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,BatchChange.class);
                startActivity(intent);
            }
        });*/
        //-----------------------------------------------------------
        btmemory.setOnClickListener(mClickListener);
        //-----------------------------------------------------------
        // 외장 메모리
        btdocumentsd = (Button)findViewById(R.id.btdocumentsd);
        btdocumentsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,FIleManager.class);
                //intent.putExtra("type","download");
                //intent.putExtra("current","non");
                intent.putExtra("type","sd");
                startActivity(intent);
            }
        });
        btmusicsd = (Button)findViewById(R.id.btmusicsd);
        btmoviesd = (Button)findViewById(R.id.btmoviesd);
        btimagesd = (Button)findViewById(R.id.btimagesd);
        btbookmarksd = (Button)findViewById(R.id.btbookmarksd);
        btlatelysd = (Button)findViewById(R.id.btlatelyfilesd);
        btdownloadsd = (Button)findViewById(R.id.btdownloadsd);
        anim= AnimationUtils.loadAnimation(this,R.anim.alpha);
        anim2=AnimationUtils.loadAnimation(this,R.anim.alpha2);
        anim3=AnimationUtils.loadAnimation(this,R.anim.alpha3);
        //-----------------------------------------------------------
        btdocumentsd.startAnimation(anim3);
        btmusicsd.startAnimation(anim3);
        btmoviesd.startAnimation(anim3);
        btbookmarksd.startAnimation(anim3);             // 처음에 외장 메모리 버튼 안보이게 숨김
        btimagesd.startAnimation(anim3);
        btdownloadsd.startAnimation(anim3);
        btlatelysd.startAnimation(anim3);
        //-----------------------------------------------------------
        btdocument.setClickable(true);
        btmusic.setClickable(true);
        btmovie.setClickable(true);
        btbookmark.setClickable(true);                  // 내장 메모리 버튼 사용 ok
        btimage.setClickable(true);
        btdownload.setClickable(true);
        btlately.setClickable(true);
    }

    public void onClick(View button){
        PopupMenu popup = new PopupMenu(this,button);
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
        popup.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.settings:
                                Intent i = new Intent(Home.this, Settings.class);
                                startActivity(i);
                                return true;
                        }
                        return true;
                    }
                }
        );
        popup.show();
    }


    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btmemory:
                    String check = android.os.Environment.getExternalStorageState();
                    Log.v("머냐", check);
                    if((check.equals("mounted"))||(check.equals("mounted_read_only"))){  //SD카드가 제거된 상태
                        if (flag==0) {
                            btdocument.startAnimation(anim);
                            btmusic.startAnimation(anim);                   // 내장 메모리 버튼 투명도 애니메이션 실행
                            btmovie.startAnimation(anim);
                            btbookmark.startAnimation(anim);
                            btimage.startAnimation(anim);
                            btdownload.startAnimation(anim);
                            btlately.startAnimation(anim);

                            btdocument.setClickable(false);
                            btmusic.setClickable(false);
                            btmovie.setClickable(false);
                            btbookmark.setClickable(false);                 // 내장 메모리 버튼 클릭 x
                            btimage.setClickable(false);
                            btdownload.setClickable(false);
                            btlately.setClickable(false);

                            btdocumentsd.startAnimation(anim2);
                            btmusicsd.startAnimation(anim2);
                            btmoviesd.startAnimation(anim2);
                            btbookmarksd.startAnimation(anim2);             // 외장 메모리 버튼 보여줌
                            btimagesd.startAnimation(anim2);
                            btdownloadsd.startAnimation(anim2);
                            btlatelysd.startAnimation(anim2);

                            btdocumentsd.setClickable(true);
                            btmusicsd.setClickable(true);
                            btmoviesd.setClickable(true);
                            btbookmarksd.setClickable(true);
                            btimagesd.setClickable(true);                   // 외장 메모리 버튼 사용 ok
                            btdownloadsd.setClickable(true);
                            btlatelysd.setClickable(true);
                            fakebutton.setImageResource(R.drawable.sd);

                            memory.setText("외부저장소");
                            flag=1;
                        }
                        else if(flag==1) {
                            btdocumentsd.startAnimation(anim);
                            btmusicsd.startAnimation(anim);
                            btmoviesd.startAnimation(anim);
                            btbookmarksd.startAnimation(anim);               // 외장 메모리 버튼 투명도 애니메이션 실행
                            btimagesd.startAnimation(anim);
                            btdownloadsd.startAnimation(anim);
                            btlatelysd.startAnimation(anim);

                            btdocumentsd.setClickable(false);
                            btmusicsd.setClickable(false);
                            btmoviesd.setClickable(false);
                            btbookmarksd.setClickable(false);               // 외장 메모리 버튼 사용 x
                            btimagesd.setClickable(false);
                            btdownloadsd.setClickable(false);
                            btlatelysd.setClickable(false);

                            btdocument.startAnimation(anim2);
                            btmusic.startAnimation(anim2);
                            btmovie.startAnimation(anim2);
                            btbookmark.startAnimation(anim2);
                            btimage.startAnimation(anim2);                  // 내장 메모리 버튼 보여줌
                            btdownload.startAnimation(anim2);
                            btlately.startAnimation(anim2);

                            btdocument.setClickable(true);
                            btmusic.setClickable(true);
                            btmovie.setClickable(true);
                            btbookmark.setClickable(true);                  // 내장 메모리 버튼 사용 ok
                            btimage.setClickable(true);
                            btdownload.setClickable(true);
                            btlately.setClickable(true);

                            fakebutton.setImageResource(R.drawable.android
                            );

                            memory.setText("내부저장소");

                            flag=0;
                        }}
                    break;

            }
        }
    };

    @Override
    protected void onPause() {
        //--------------------------------------------------------------------
        // Unregister broadcast receiver(s).

        unregisterReceiver(myb);

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //--------------------------------------------------------------------
        // Register broadcast receiver(s).

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        registerReceiver(myb, intentFilter);
    }

    public long folderMemoryCheck(String a_path){

        long totalMemory = 0;
        File file = new File(a_path);
        File[] childFileList = file.listFiles();

        if(childFileList == null){
            return 0;
        }

        for(File childFile : childFileList){
            if(childFile.isDirectory()){
                totalMemory += folderMemoryCheck(childFile.getAbsolutePath());
            }
            else{
                totalMemory += childFile.length();
            }
        }
        return totalMemory;


    }







}
