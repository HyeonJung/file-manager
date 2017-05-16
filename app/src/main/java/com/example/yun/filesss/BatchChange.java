package com.example.yun.filesss;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BatchChange extends AppCompatActivity {
    private List<String> item = null;
    private List<String> path = null;
    private List<String> itemalert = null;
    private List<String> pathalert = null;
    private List<String> copypath = new ArrayList<String>();
    private List<String> changeitem;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd"); // 파일 최근 수정 날짜 형식
    ViewHolder2 holder;
    private ListView mListView = null;
    private ListView mListView2 = null;
    private ListViewAdapter mAdapter = null;
    private ListViewAdapter2 mAdapter2 = null;
    private ListViewAdapter2 mAdapter3 = null;
    private String root = "/storage/sdcard0";
    private String thiscurrentroot = "";
    private String alertcurrentroot = "";
    private String rename;
    private List<Boolean> box = new ArrayList<Boolean>();
    boolean Ckable = true;
    ImageView Nofile;
    Button Add, Change, Clear, PathChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_change);
        mListView = (ListView) findViewById(R.id.listView1);
        mListView2 = (ListView) findViewById(R.id.listView2);
        mAdapter = new ListViewAdapter(this);
        mAdapter2 = new ListViewAdapter2(this);
        mAdapter3 = new ListViewAdapter2(this);
        mListView.setAdapter(mAdapter);
        mListView2.setAdapter(mAdapter2);
        Nofile = (ImageView) findViewById(R.id.nofile);
        Add = (Button) findViewById(R.id.addbt);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                for (int i = 0; i < box.size(); i++) {
                    if (box.get(i)) {
                        if (copypath.size() < 1) {
                            File f = new File(path.get(i));
                            copypath.add(path.get(i));
                            String fname = f.getName();
                            String Et = getExtension(fname);
                            if (Et.equals("jpg") || Et.equals("png") || Et.equals("jpeg") || Et.equals("gif") || Et.equals("ai") || Et.equals("psd") || Et.equals("eps")) { //그림파일
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(f.getPath(),options);
                                int imageHeight = options.outHeight;
                                int imageWidth = options.outWidth;
                                String imageType = options.outMimeType;
                                // Get the dimensions of the View
                                int targetW = holder.mIcon.getWidth();
                                int targetH = holder.mIcon.getHeight();
                                // Get the dimensions of the bitmap
                                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                bmOptions.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(f.getPath(), bmOptions);
                                int photoW = bmOptions.outWidth;
                                int photoH = bmOptions.outHeight;

// Determine how much to scale down the image
                                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

// Decode the image file into a Bitmap sized to fill the View
                                bmOptions.inJustDecodeBounds = false;
                                bmOptions.inSampleSize = scaleFactor;
                                bmOptions.inPurgeable = true;

                                Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(), bmOptions);
                                //mImageView.setImageBitmap(bitmap);
                                //Bitmap mybitmap = BitmapFactory.decodeFile(file.getPath());
                                Drawable image = new BitmapDrawable(bitmap);
                                mAdapter2.addItem(image,
                                        fname,                                       // 파일 이름
                                        false);                                     // 파일일 경우에는 파일의 크기
                            } else if (Et.equals("aac") || Et.equals("mp3") || Et.equals("wav") || Et.equals("wma")) { //음악파일
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.musicfile),
                                        fname,                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            } else if (Et.equals("avi") || Et.equals("flv") || Et.equals("mkv") || Et.equals("wmv") || Et.equals("mp4")) { //동영상
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.play),
                                        fname,                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            }else if (Et.equals("txt")) { //텍스트파일
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.txtfile),
                                        fname,                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            }
                            else if (Et.equals("zip")) { //알집
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.zipfile),
                                        fname,                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            }
                            else {
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.defaultfile),
                                        fname,                                         // 파일 이름
                                        false);                                // 파일일 경우에는 파일의 크기
                            }

                            Toast.makeText(getApplicationContext(), "추가 되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            boolean exist = false;
                            for (int n = 0; n < copypath.size(); n++) {
                                if (path.get(i).equals(copypath.get(n))) {
                                    exist = true;
                                }
                            }
                            if (exist == false) {
                                File f = new File(path.get(i));
                                copypath.add(path.get(i));
                                String fname = f.getName();
                                String Et = getExtension(fname);
                                if (Et.equals("jpg") || Et.equals("png") || Et.equals("jpeg") || Et.equals("gif") || Et.equals("ai") || Et.equals("psd") || Et.equals("eps")) { //그림파일
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inJustDecodeBounds = true;
                                    BitmapFactory.decodeFile(f.getPath(),options);
                                    int imageHeight = options.outHeight;
                                    int imageWidth = options.outWidth;
                                    String imageType = options.outMimeType;
                                    // Get the dimensions of the View
                                    int targetW = holder.mIcon.getWidth();
                                    int targetH = holder.mIcon.getHeight();
                                    // Get the dimensions of the bitmap
                                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                    bmOptions.inJustDecodeBounds = true;
                                    BitmapFactory.decodeFile(f.getPath(), bmOptions);
                                    int photoW = bmOptions.outWidth;
                                    int photoH = bmOptions.outHeight;

// Determine how much to scale down the image
                                    int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

// Decode the image file into a Bitmap sized to fill the View
                                    bmOptions.inJustDecodeBounds = false;
                                    bmOptions.inSampleSize = scaleFactor;
                                    bmOptions.inPurgeable = true;

                                    Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(), bmOptions);
                                    //mImageView.setImageBitmap(bitmap);
                                    //Bitmap mybitmap = BitmapFactory.decodeFile(file.getPath());
                                    Drawable image = new BitmapDrawable(bitmap);
                                    mAdapter2.addItem(image,
                                            fname,                                       // 파일 이름
                                            false);                                     // 파일일 경우에는 파일의 크기
                                } else if (Et.equals("aac") || Et.equals("mp3") || Et.equals("wav") || Et.equals("wma")) { //음악파일
                                    mAdapter2.addItem(getResources().getDrawable(R.drawable.musicfile),
                                            fname,                                         // 파일 이름
                                            false);                                    // 파일일 경우에는 파일의 크기
                                } else if (Et.equals("avi") || Et.equals("flv") || Et.equals("mkv") || Et.equals("wmv") || Et.equals("mp4")) { //동영상
                                    mAdapter2.addItem(getResources().getDrawable(R.drawable.play),
                                            fname,                                         // 파일 이름
                                            false);                                    // 파일일 경우에는 파일의 크기
                                }else if (Et.equals("txt")) { //텍스트파일
                                    mAdapter2.addItem(getResources().getDrawable(R.drawable.txtfile),
                                            fname,                                         // 파일 이름
                                            false);                                    // 파일일 경우에는 파일의 크기
                                }
                                else if (Et.equals("zip")) { //알집
                                    mAdapter2.addItem(getResources().getDrawable(R.drawable.zipfile),
                                            fname,                                         // 파일 이름
                                            false);                                    // 파일일 경우에는 파일의 크기
                                }
                                else {
                                    mAdapter2.addItem(getResources().getDrawable(R.drawable.defaultfile),
                                            fname,                                         // 파일 이름
                                            false);                                // 파일일 경우에는 파일의 크기
                                }


                            }
                        }

                    }

                }
                for (int i = 0; i < box.size(); i++) {
                    box.set(i, false);
                }
                if (copypath.size() > 0)
                    Nofile.setVisibility(View.INVISIBLE);
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "추가 되었습니다.", Toast.LENGTH_SHORT).show();
                mAdapter2.notifyDataSetChanged(); // 어댑터 갱신위해 사용

            }
        });
        PathChange = (Button) findViewById(R.id.btfilepath);
        PathChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                while (mAdapter3.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                    mAdapter3.remove(0);
                getDirpath(root);
                AlertDialog.Builder alt_bld;
                final AlertDialog alert2;
                alt_bld = new AlertDialog.Builder(BatchChange.this);
                LayoutInflater inflater = BatchChange.this.getLayoutInflater();
                final View dialogView= inflater.inflate(R.layout.alert_listview, null);
                alt_bld.setView(dialogView);
                alert2 = alt_bld.create();
                alert2.setTitle("경로 선택");
                alert2.setMessage("경로를 선택해 주세요.");
                ListView list = new ListView(BatchChange.this);


                ListView listv = (ListView)dialogView.findViewById(R.id.alertlistView);
                Button btok = (Button)dialogView.findViewById(R.id.alertbutton);
                Button btcancel = (Button)dialogView.findViewById(R.id.alertbutton2);
                listv.setAdapter(mAdapter3);

                listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        File file = new File(pathalert.get(position));
                        if (file.isDirectory()) { //디렉토리이면
                            if (file.canRead()) { //읽을 수 있는 파일이면
                                // ListData mData = mAdapter.mListData.get(position);
                                // Toast.makeText(MainActivity.this, mAdapter.getCount() + "", Toast.LENGTH_SHORT).show();
                                while (mAdapter3.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                    mAdapter3.remove(0);
                                getDirpath(pathalert.get(position));
                                mAdapter3.notifyDataSetChanged(); // 어댑터 갱신위해 사용
                            }
                        }
                    }
                });
                btok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeitem = new ArrayList<>();
                        for (int i = 0; i < copypath.size(); i++) {
                            File f = new File(copypath.get(i));
                            Log.v("f : ", f.getName());
                            String afterpath = alertcurrentroot + "/" + f.getName();
                            Log.v("에프터 : " + afterpath, "  alert : " + alertcurrentroot);
                            File filecheck = new File(f.getPath()); //이미 있는 파일 명인지 탐색하기 위해 파일 생성
                            Log.v("f.getname", f.getName() + "  f.getpath() " + f.getPath() + "  root : " + alertcurrentroot);
                            moveFile(f.getName(), f.getPath(), alertcurrentroot);
                            changeitem.add(f.getName());
                            copypath.set(i, afterpath);

                        }


                        while (mAdapter2.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                            mAdapter2.remove(0);
                        for (int n = 0; n < changeitem.size(); n++) {
                            Log.v("얍 :", changeitem.get(n) + "\ncopypath : " + copypath.get(n));
                            String Et = getExtension(changeitem.get(n));
                            if (Et.equals("jpg") || Et.equals("png") || Et.equals("jpeg") || Et.equals("gif") || Et.equals("ai") || Et.equals("psd") || Et.equals("eps")) { //그림파일
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(copypath.get(n),options);
                                int imageHeight = options.outHeight;
                                int imageWidth = options.outWidth;
                                String imageType = options.outMimeType;
                                // Get the dimensions of the View
                                int targetW = holder.mIcon.getWidth();
                                int targetH = holder.mIcon.getHeight();
                                // Get the dimensions of the bitmap
                                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                bmOptions.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(copypath.get(n), bmOptions);
                                int photoW = bmOptions.outWidth;
                                int photoH = bmOptions.outHeight;

// Determine how much to scale down the image
                                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

// Decode the image file into a Bitmap sized to fill the View
                                bmOptions.inJustDecodeBounds = false;
                                bmOptions.inSampleSize = scaleFactor;
                                bmOptions.inPurgeable = true;

                                Bitmap bitmap = BitmapFactory.decodeFile(copypath.get(n), bmOptions);
                                //mImageView.setImageBitmap(bitmap);
                                //Bitmap mybitmap = BitmapFactory.decodeFile(file.getPath());
                                Drawable image = new BitmapDrawable(bitmap);
                                mAdapter2.addItem(image,
                                        changeitem.get(n),                                       // 파일 이름
                                        false);                                     // 파일일 경우에는 파일의 크기
                            } else if (Et.equals("aac") || Et.equals("mp3") || Et.equals("wav") || Et.equals("wma")) { //음악파일
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.musicfile),
                                        changeitem.get(n),                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            } else if (Et.equals("avi") || Et.equals("flv") || Et.equals("mkv") || Et.equals("wmv") || Et.equals("mp4")) { //동영상
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.play),
                                        changeitem.get(n),                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            }else if (Et.equals("txt")) { //텍스트파일
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.txtfile),
                                        changeitem.get(n),                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            }
                            else if (Et.equals("zip")) { //알집
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.zipfile),
                                        changeitem.get(n),                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            }
                            else {
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.defaultfile),
                                        changeitem.get(n),                                         // 파일 이름
                                        false);                                // 파일일 경우에는 파일의 크기
                            }
                        }
                        while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                            mAdapter.remove(0);
                        getDir(alertcurrentroot);
                        Toast.makeText(getApplicationContext(), "위치가 변경 되었습니다.", Toast.LENGTH_SHORT).show();
                        alert2.dismiss();
                    }
                });
                btcancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert2.dismiss();
                    }
                });
                alert2.show();
            }
        });


        Change = (Button) findViewById(R.id.change); // 이름 일괄변경 버튼
        Change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 클릭했을 때
                AlertDialog.Builder alert = new AlertDialog.Builder(BatchChange.this);
                alert.setTitle("파일 이름 일괄 변경");
                alert.setMessage("바꿀 이름을 입력해 주세요.");
                final EditText name = new EditText(BatchChange.this);
                alert.setView(name);
                alert.setPositiveButton("바꾸기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int num = 1; // 파일 뒤에 붙일 숫자
                        changeitem = new ArrayList<>();
                        rename = name.getText().toString(); // 바꿀 이름
                        for (int i = 0; i < copypath.size(); i++) {
                            File f = new File(copypath.get(i));
                            String Extension = getExtension(f.getName());

                            String afterpath = (f.getPath().substring(0, f.getPath().length() - (f.getName().length() + 1)));
                            while (true) {
                                File filecheck = new File(afterpath + "/" + rename + (num) + "." + Extension); //이미 있는 파일 명인지 탐색하기 위해 파일 생성
                                Log.v("파일 있냐 " + afterpath + rename + (num) + "." + Extension + ":", filecheck.exists() + "   " + copypath.size());
                                if (filecheck.exists() == true) { // 파일명이 존재할 때
                                    if (filecheck.length() != 0) { // 파일안의 내용이 0이 아닐 때
                                        num++; // num 값을 1 증가시킨다.
                                    } else { // 파일의 내용이 0일 때
                                        String name = rename + (num++) + "." + Extension;
                                        moveFile(name, f.getPath(), afterpath);
                                        changeitem.add(name);
                                        copypath.set(i, afterpath + "/" + name);
                                        break;
                                    }
                                } else {
                                    String name = rename + (num++) + "." + Extension;
                                    moveFile(name, f.getPath(), afterpath);
                                    changeitem.add(name);
                                    copypath.set(i, afterpath + "/" + name);
                                    break;
                                }
                            }


                        }
                        while (mAdapter2.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                            mAdapter2.remove(0);
                        for (int n = 0; n < changeitem.size(); n++) {
                            Log.v("얍 :", changeitem.get(n) + "\ncopypath : " + copypath.get(n));
                            String Et = getExtension(changeitem.get(n));
                            if (Et.equals("jpg") || Et.equals("png") || Et.equals("jpeg") || Et.equals("gif") || Et.equals("ai") || Et.equals("psd") || Et.equals("eps")) { //그림파일
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(copypath.get(n),options);
                                int imageHeight = options.outHeight;
                                int imageWidth = options.outWidth;
                                String imageType = options.outMimeType;
                                // Get the dimensions of the View
                                int targetW = holder.mIcon.getWidth();
                                int targetH = holder.mIcon.getHeight();
                                // Get the dimensions of the bitmap
                                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                bmOptions.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(copypath.get(n), bmOptions);
                                int photoW = bmOptions.outWidth;
                                int photoH = bmOptions.outHeight;

// Determine how much to scale down the image
                                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

// Decode the image file into a Bitmap sized to fill the View
                                bmOptions.inJustDecodeBounds = false;
                                bmOptions.inSampleSize = scaleFactor;
                                bmOptions.inPurgeable = true;

                                Bitmap bitmap = BitmapFactory.decodeFile(copypath.get(n), bmOptions);
                                //mImageView.setImageBitmap(bitmap);
                                //Bitmap mybitmap = BitmapFactory.decodeFile(file.getPath());
                                Drawable image = new BitmapDrawable(bitmap);
                                mAdapter2.addItem(image,
                                        changeitem.get(n),                                       // 파일 이름
                                        false);                                     // 파일일 경우에는 파일의 크기
                            } else if (Et.equals("aac") || Et.equals("mp3") || Et.equals("wav") || Et.equals("wma")) { //음악파일
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.musicfile),
                                        changeitem.get(n),                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            } else if (Et.equals("avi") || Et.equals("flv") || Et.equals("mkv") || Et.equals("wmv") || Et.equals("mp4")) { //동영상
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.play),
                                        changeitem.get(n),                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            }
                            else if (Et.equals("txt")) { //텍스트파일
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.txtfile),
                                        changeitem.get(n),                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            }
                            else if (Et.equals("zip")) { //알집
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.zipfile),
                                        changeitem.get(n),                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            }else {
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.defaultfile),
                                        changeitem.get(n),                                         // 파일 이름
                                        false);                                // 파일일 경우에는 파일의 크기
                            }
                        }
                        while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                            mAdapter.remove(0);
                        getDir(thiscurrentroot);
                        num = 0;
                        Toast.makeText(getApplicationContext(), rename + " 으로 변경 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();

            }
        });

        Clear = (Button) findViewById(R.id.btclear);
        Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copypath = new ArrayList<String>();
                while (mAdapter2.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                    mAdapter2.remove(0);
                mAdapter2.notifyDataSetChanged();
                if (copypath.size() < 1)
                    Nofile.setVisibility(View.VISIBLE);
            }
        });
        registerForContextMenu(mListView);
        getDir(root);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (Ckable) {
                    File file = new File(path.get(position));
                    if (file.isDirectory()) { //디렉토리이면
                        if (file.canRead()) { //읽을 수 있는 파일이면
                            box.clear();
                            while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                mAdapter.remove(0);
                            getDir(path.get(position));
                            mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용
                        }
                    } else {
                        if (box.get(position)) { //체크되어있으면
                            box.set(position, false); //체크 해제
                        } else {                   //체크되어있지 않으면
                            box.set(position, true); // 체크
                        }
                        Log.v(box.get(position) + "", position + "");
                        Log.v(box.size() + "", "왜이래이거");

                        mAdapter.remove(position);
                        String Et = getExtension(path.get(position));
                        if (Et.equals("jpg") || Et.equals("png") || Et.equals("jpeg") || Et.equals("gif") || Et.equals("ai") || Et.equals("psd") || Et.equals("eps")) { //그림파일
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(path.get(position),options);
                            int imageHeight = options.outHeight;
                            int imageWidth = options.outWidth;
                            String imageType = options.outMimeType;
                            // Get the dimensions of the View
                            int targetW = holder.mIcon.getWidth();
                            int targetH = holder.mIcon.getHeight();
                            // Get the dimensions of the bitmap
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            bmOptions.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(path.get(position), bmOptions);
                            int photoW = bmOptions.outWidth;
                            int photoH = bmOptions.outHeight;

// Determine how much to scale down the image
                            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

// Decode the image file into a Bitmap sized to fill the View
                            bmOptions.inJustDecodeBounds = false;
                            bmOptions.inSampleSize = scaleFactor;
                            bmOptions.inPurgeable = true;

                            Bitmap bitmap = BitmapFactory.decodeFile(path.get(position), bmOptions);
                            //mImageView.setImageBitmap(bitmap);
                            //Bitmap mybitmap = BitmapFactory.decodeFile(file.getPath());
                            Drawable image = new BitmapDrawable(bitmap);
                            mAdapter.addItemposition(image,
                                    item.get(position),                                       // 파일 이름
                                    false,position);                                     // 파일일 경우에는 파일의 크기
                        } else if (Et.equals("aac") || Et.equals("mp3") || Et.equals("wav") || Et.equals("wma")) { //음악파일
                            mAdapter.addItemposition(getResources().getDrawable(R.drawable.musicfile),
                                    item.get(position),                                         // 파일 이름
                                    false,position);                                    // 파일일 경우에는 파일의 크기
                        } else if (Et.equals("avi") || Et.equals("flv") || Et.equals("mkv") || Et.equals("wmv") || Et.equals("mp4")) { //동영상
                            mAdapter.addItemposition(getResources().getDrawable(R.drawable.play),
                                    item.get(position),                                         // 파일 이름
                                    false, position);                                    // 파일일 경우에는 파일의 크기
                        }else if (Et.equals("txt")) { //텍스트파일
                            mAdapter.addItemposition(getResources().getDrawable(R.drawable.txtfile),
                                    item.get(position),                                         // 파일 이름
                                    false, position);                                    // 파일일 경우에는 파일의 크기
                        }
                        else if (Et.equals("zip")) { //알집
                            mAdapter.addItemposition(getResources().getDrawable(R.drawable.zipfile),
                                    item.get(position),                                         // 파일 이름
                                    false, position);                                    // 파일일 경우에는 파일의 크기
                        }
                        else {
                            mAdapter.addItemposition((getResources().getDrawable(R.drawable.defaultfile)),
                                    item.get(position),                                         // 파일 이름
                                    false,position);                                // 파일일 경우에는 파일의 크기
                        }
                    }

                    }
                }

        });
        mListView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(BatchChange.this);
                alert.setTitle("제거");
                alert.setMessage("제거 하시겠습니까?");

                alert.setPositiveButton("제거", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        List<String> copypath2 = new ArrayList<String>();
                        String deletef = copypath.get(position);
                        Log.v("제거할꺼 : " + deletef, "야양ㅇㄹㅇㅎ" + copypath.size());
                        for (int i = 0; i < copypath.size(); i++) {
                            if (!(copypath.get(i).equals(deletef))) {
                                copypath2.add(copypath.get(i));
                                Log.v("추가했땅", copypath.get(i));
                            }
                        }
                        copypath.clear();
                        for (int i = 0; i < copypath2.size(); i++) {
                            copypath.add(copypath2.get(i));
                        }
                        while (mAdapter2.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                            mAdapter2.remove(0);
                        for (int i = 0; i < copypath.size(); i++) {
                            File f = new File(copypath.get(i));
                            String fname = f.getName();
                            String Et = getExtension(fname);
                            if (Et.equals("jpg") || Et.equals("png") || Et.equals("jpeg") || Et.equals("gif") || Et.equals("ai") || Et.equals("psd") || Et.equals("eps")) { //그림파일
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(f.getPath(),options);
                                int imageHeight = options.outHeight;
                                int imageWidth = options.outWidth;
                                String imageType = options.outMimeType;
                                // Get the dimensions of the View
                                int targetW = holder.mIcon.getWidth();
                                int targetH = holder.mIcon.getHeight();
                                // Get the dimensions of the bitmap
                                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                bmOptions.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(f.getPath(), bmOptions);
                                int photoW = bmOptions.outWidth;
                                int photoH = bmOptions.outHeight;

// Determine how much to scale down the image
                                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

// Decode the image file into a Bitmap sized to fill the View
                                bmOptions.inJustDecodeBounds = false;
                                bmOptions.inSampleSize = scaleFactor;
                                bmOptions.inPurgeable = true;

                                Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(), bmOptions);
                                //mImageView.setImageBitmap(bitmap);
                                //Bitmap mybitmap = BitmapFactory.decodeFile(file.getPath());
                                Drawable image = new BitmapDrawable(bitmap);
                                mAdapter2.addItem(image,
                                        fname,                                       // 파일 이름
                                        false);                                     // 파일일 경우에는 파일의 크기
                            } else if (Et.equals("aac") || Et.equals("mp3") || Et.equals("wav") || Et.equals("wma")) { //음악파일
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.musicfile),
                                        fname,                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            } else if (Et.equals("avi") || Et.equals("flv") || Et.equals("mkv") || Et.equals("wmv") || Et.equals("mp4")) { //동영상
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.play),
                                        fname,                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            }else if (Et.equals("txt")) { //텍스트파일
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.txtfile),
                                        fname,                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            }
                            else if (Et.equals("zip")) { //알집
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.zipfile),
                                        fname,                                         // 파일 이름
                                        false);                                    // 파일일 경우에는 파일의 크기
                            }
                            else {
                                mAdapter2.addItem(getResources().getDrawable(R.drawable.defaultfile),
                                        fname,                                         // 파일 이름
                                        false);                                // 파일일 경우에는 파일의 크기
                            }
                        }
                        Toast.makeText(getApplicationContext(), "목록에서 제거 되었습니다.", Toast.LENGTH_SHORT).show();
                        if (copypath.size() < 1)
                            Nofile.setVisibility(View.VISIBLE);
                        mAdapter2.notifyDataSetChanged();

                    }
                });
                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();
                return false;
            }
        });

    }


    private void getDir(String dirPath) {
        item = new ArrayList<String>(); //폴더 or 파일 이름
        path = new ArrayList<String>(); //경로
        thiscurrentroot = dirPath;
        Log.v("파일경로", thiscurrentroot);
        File f = new File(dirPath); // 인자로 받은 경로로 파일 지정
        File[] files = f.listFiles();

        if (!dirPath.equals("/storage/sdcard0")) { // 현재 파일이 루트가 아닐 때
            //root2 = f.getParent();
        } else {
        }
        //root2 = "";

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            path.add(file.getPath());
            box.add(false);
            if (file.isDirectory()) {                                           // 디렉토리일 경우
                item.add(file.getName());
                mAdapter.addItem(getResources().getDrawable(R.drawable.folder),
                        file.getName(),                                         // 파일 이름
                        false);                       // 디렉토리 내의 파일 or 디렉토리 수

            } else {                                                            // 디렉토리가 아닐 경우
                String Et = getExtension(file.getName());
                item.add(file.getName());
                box.add(false);
                if (Et.equals("jpg") || Et.equals("png") || Et.equals("jpeg") || Et.equals("gif") || Et.equals("ai") || Et.equals("psd") || Et.equals("eps")) { //그림파일
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getPath(),options);
                    int imageHeight = options.outHeight;
                    int imageWidth = options.outWidth;
                    String imageType = options.outMimeType;
                    // Get the dimensions of the View
                    int targetW = holder.mIcon.getWidth();
                    int targetH = holder.mIcon.getHeight();
                    // Get the dimensions of the bitmap
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getPath(), bmOptions);
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;

// Determine how much to scale down the image
                    int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

// Decode the image file into a Bitmap sized to fill the View
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inPurgeable = true;

                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), bmOptions);
                    //mImageView.setImageBitmap(bitmap);
                    //Bitmap mybitmap = BitmapFactory.decodeFile(file.getPath());
                    Drawable image = new BitmapDrawable(bitmap);
                    mAdapter.addItem(image,
                            file.getName(),                                       // 파일 이름
                            false);                                     // 파일일 경우에는 파일의 크기
                } else if (Et.equals("aac") || Et.equals("mp3") || Et.equals("wav") || Et.equals("wma")) { //음악파일
                    mAdapter.addItem(getResources().getDrawable(R.drawable.musicfile),
                            file.getName(),                                         // 파일 이름
                            false);                                    // 파일일 경우에는 파일의 크기
                } else if (Et.equals("avi") || Et.equals("flv") || Et.equals("mkv") || Et.equals("wmv") || Et.equals("mp4")) { //동영상
                    mAdapter.addItem(getResources().getDrawable(R.drawable.play),
                            file.getName(),                                         // 파일 이름
                            false);                                    // 파일일 경우에는 파일의 크기
                }else if (Et.equals("txt")) { //텍스트파일
                    mAdapter.addItem(getResources().getDrawable(R.drawable.txtfile),
                            file.getName(),                                         // 파일 이름
                            false);                                    // 파일일 경우에는 파일의 크기
                }
                else if (Et.equals("zip")) { //알집
                    mAdapter.addItem(getResources().getDrawable(R.drawable.zipfile),
                            file.getName(),                                         // 파일 이름
                            false);                                    // 파일일 경우에는 파일의 크기
                }
                else {
                    mAdapter.addItem(getResources().getDrawable(R.drawable.defaultfile),
                            file.getName(),                                         // 파일 이름
                            false);                                // 파일일 경우에는 파일의 크기
                }
            }

        }
    }

    private void getDirpath(String dirPath) {
        itemalert = new ArrayList<String>(); //폴더 or 파일 이름
        pathalert = new ArrayList<String>(); //경로
        alertcurrentroot = dirPath;
        Log.v("파일경로", alertcurrentroot);
        File f = new File(dirPath); // 인자로 받은 경로로 파일 지정
        File[] files = f.listFiles();

        if (!dirPath.equals("/storage/sdcard0")) { // 현재 파일이 루트가 아닐 때
            pathalert.add(f.getParent());
            itemalert.add(f.getParent());
            mAdapter3.addItem(getResources().getDrawable(R.drawable.folder),
                    "..",                                         // 파일 이름
                    false);                       // 디렉토리 내의 파일 or 디렉토리 수
        } else {
        }
        //root2 = "";

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            pathalert.add(file.getPath());
            if (file.isDirectory()) {                                           // 디렉토리일 경우
                itemalert.add(file.getName());
                mAdapter3.addItem(getResources().getDrawable(R.drawable.folder),
                        file.getName(),                                         // 파일 이름
                        false);                       // 디렉토리 내의 파일 or 디렉토리 수

            } else {                                                            // 디렉토리가 아닐 경우
                String Et = getExtension(file.getName());
                itemalert.add(file.getName());
                if (Et.equals("jpg") || Et.equals("png") || Et.equals("jpeg") || Et.equals("gif") || Et.equals("ai") || Et.equals("psd") || Et.equals("eps")) { //그림파일
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getPath(),options);
                    int imageHeight = options.outHeight;
                    int imageWidth = options.outWidth;
                    String imageType = options.outMimeType;
                    // Get the dimensions of the View
                    int targetW = holder.mIcon.getWidth();
                    int targetH = holder.mIcon.getHeight();
                    // Get the dimensions of the bitmap
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getPath(), bmOptions);
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;

// Determine how much to scale down the image
                    int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

// Decode the image file into a Bitmap sized to fill the View
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inPurgeable = true;

                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), bmOptions);
                    //mImageView.setImageBitmap(bitmap);
                    //Bitmap mybitmap = BitmapFactory.decodeFile(file.getPath());
                    Drawable image = new BitmapDrawable(bitmap);
                    mAdapter3.addItem(image,
                            file.getName(),                                       // 파일 이름
                            false);                                     // 파일일 경우에는 파일의 크기
                } else if (Et.equals("aac") || Et.equals("mp3") || Et.equals("wav") || Et.equals("wma")) { //음악파일
                    mAdapter3.addItem(getResources().getDrawable(R.drawable.musicfile),
                            file.getName(),                                         // 파일 이름
                            false);                                    // 파일일 경우에는 파일의 크기
                } else if (Et.equals("avi") || Et.equals("flv") || Et.equals("mkv") || Et.equals("wmv") || Et.equals("mp4")) { //동영상
                    mAdapter3.addItem(getResources().getDrawable(R.drawable.play),
                            file.getName(),                                         // 파일 이름
                            false);                                    // 파일일 경우에는 파일의 크기
                }else if (Et.equals("txt")) { //텍스트파일
                    mAdapter3.addItem(getResources().getDrawable(R.drawable.txtfile),
                            file.getName(),                                         // 파일 이름
                            false);                                    // 파일일 경우에는 파일의 크기
                }
                else if (Et.equals("zip")) { //알집
                    mAdapter3.addItem(getResources().getDrawable(R.drawable.zipfile),
                            file.getName(),                                         // 파일 이름
                            false);                                    // 파일일 경우에는 파일의 크기
                }
                else {
                    mAdapter3.addItem(getResources().getDrawable(R.drawable.defaultfile),
                            file.getName(),                                         // 파일 이름
                            false);                                // 파일일 경우에는 파일의 크기
                }
            }

        }
    }

    public class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ListData> mListData = new ArrayList<ListData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                holder = new ViewHolder2();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item2, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.mImage);
                holder.mText = (TextView) convertView.findViewById(R.id.mText);
                holder.mCheck = (CheckBox) convertView.findViewById(R.id.mCheck);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder2) convertView.getTag();
            }

            ListData mData = mListData.get(position);

            if (mData.mIcon != null) {
                holder.mIcon.setVisibility(View.VISIBLE);
                holder.mIcon.setImageDrawable(mData.mIcon);
            } else {
                holder.mIcon.setVisibility(View.GONE);
            }

            holder.mText.setText(mData.mTitle);
            holder.mCheck.setChecked(box.get(position));
            holder.mCheck.setFocusable(false);
            holder.mCheck.setClickable(false);
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


            return convertView;
        }

        public void addItem(Drawable icon, String mTitle, Boolean mcheck) {
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mIcon = icon;
            addInfo.mTitle = mTitle;
            addInfo.mCheck = mcheck;

            mListData.add(addInfo);
        }
        public void addItemposition(Drawable icon, String mTitle, Boolean mcheck,int position) {
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mIcon = icon;
            addInfo.mTitle = mTitle;
            addInfo.mCheck = mcheck;

            mListData.add(position,addInfo);
        }

        public void remove(int position) {
            mListData.remove(position);
            dataChange();
        }

        public void sort() {
            Collections.sort(mListData, ListData.ALPHA_COMPARATOR);
            dataChange();
        }

        public void dataChange() {
            mAdapter.notifyDataSetChanged();
        }
    }

    public class ListViewAdapter2 extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ListData> mListData = new ArrayList<ListData>();

        public ListViewAdapter2(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                holder = new ViewHolder2();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item2, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.mImage);
                holder.mText = (TextView) convertView.findViewById(R.id.mText);
                holder.mCheck = (CheckBox) convertView.findViewById(R.id.mCheck);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder2) convertView.getTag();
            }

            ListData mData = mListData.get(position);

            if (mData.mIcon != null) {
                holder.mIcon.setVisibility(View.VISIBLE);
                holder.mIcon.setImageDrawable(mData.mIcon);
            } else {
                holder.mIcon.setVisibility(View.GONE);
            }

            holder.mText.setText(mData.mTitle);
            holder.mCheck.setVisibility(View.GONE);
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


            return convertView;
        }

        public void addItem(Drawable icon, String mTitle, Boolean mcheck) {
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mIcon = icon;
            addInfo.mTitle = mTitle;
            addInfo.mCheck = mcheck;

            mListData.add(addInfo);
        }
        public void addItemposition(Drawable icon, String mTitle, Boolean mcheck,int position) {
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mIcon = icon;
            addInfo.mTitle = mTitle;
            addInfo.mCheck = mcheck;

            mListData.add(position,addInfo);
        }

        public void remove(int position) {
            mListData.remove(position);
            dataChange();
        }

        public void sort() {
            Collections.sort(mListData, ListData.ALPHA_COMPARATOR);
            dataChange();
        }

        public void dataChange() {
            mAdapter.notifyDataSetChanged();
        }
    }

    public static String getExtension(String fileStr) { // 확장자 뽑기
        return fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
    }

    public void onBackPressed() {
        if (thiscurrentroot.equals(root)) {
            super.onBackPressed();
        } else {
            File f = new File(thiscurrentroot);
            box.clear();
            while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                mAdapter.remove(0);
            getDir(f.getParent());
            mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용*/
        }
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
