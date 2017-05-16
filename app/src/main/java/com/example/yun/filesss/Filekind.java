package com.example.yun.filesss;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Filekind extends AppCompatActivity {

    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    private List<String> item = null;
    private List<String> path = null;
    private String root = "/storage/sdcard0";
    private String root2 = "";
    ViewHolder holder;

    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    ArrayList<String> list = new ArrayList<String>();

    private String cate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filekind);
        mListView = (ListView) findViewById(R.id.mList);
        item = new ArrayList<String>(); //폴더 or 파일 이름
        path = new ArrayList<String>(); //경로
        Intent intent = getIntent();
        cate = intent.getExtras().getString("category");

        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);
        if(cate.equals("music"))
            Musicmediascan();
        else if(cate.equals("image"))
            Imagemediascan();
        else if(cate.equals("video"))
            Videomediascan();
        else if(cate.equals("lately"))
            Latelymediascan();
       // getDir(root);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                File file = new File(path.get(position));
                if (file.isDirectory()) //디렉토리이면
                    if (file.canRead()) { //읽을 수 있는 파일이면
                        // ListData mData = mAdapter.mListData.get(position);
                        // Toast.makeText(MainActivity.this, mAdapter.getCount() + "", Toast.LENGTH_SHORT).show();
                        root2 = "뒤로가기"; // 뒤로가기 버튼 누를 때 바로 메인 액티비티로 넘어 가지 않게 하기 위해 설정
                        while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                            mAdapter.remove(0);
                        getDir(path.get(position));
                        mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용


                    }

            }
        });

    }

    private void getDir(String dirPath) {
        File f = new File(dirPath); // 인자로 받은 경로로 파일 지정
        File[] files = f.listFiles();
        item = new ArrayList<String>(); //폴더 or 파일 이름
        path = new ArrayList<String>(); //경로

        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            if (file.isDirectory()) {                                           // 디렉토리일 경우

            }else {                                                            // 디렉토리가 아닐 경우
                String Et = getExtension(file.getName());
                if(cate.equals("image")){
                if(Et.equals("jpg")|| Et.equals("png") || Et.equals("jpeg") || Et.equals("gif") || Et.equals("ai") || Et.equals("psd") || Et.equals("eps")) { //그림파일
                 //   Bitmap mybitmap = BitmapFactory.decodeFile(file.getPath());
                    item.add(file.getName());
                    path.add(file.getPath());
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
                            file.getName(),                                         // 파일 이름
                            sf.format(file.lastModified())+"",                      // 마지막 수정 날짜
                            getSize(file.length()));                                     // 파일일 경우에는 파일의 크기
                }}
                if(cate.equals("music")){
                if (Et.equals("aac") || Et.equals("mp3") || Et.equals("wav") || Et.equals("wma") || Et.equals("ogg")) { //음악파일
                    item.add(file.getName());
                    path.add(file.getPath());
                    mAdapter.addItem(getResources().getDrawable(R.drawable.musicfile),
                            file.getName(),                                         // 파일 이름
                            sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                            getSize(file.length()));                                    // 파일일 경우에는 파일의 크기
                }}
                if(cate.equals("video")){
                if(Et.equals("avi")|| Et.equals("flv") || Et.equals("mkv") || Et.equals("wmv") || Et.equals("mp4")) { //동영상
                    item.add(file.getName());
                    path.add(file.getPath());
                    mAdapter.addItem(getResources().getDrawable(R.drawable.play),
                            file.getName(),                                         // 파일 이름
                            sf.format(file.lastModified())+"",                      // 마지막 수정 날짜
                            getSize(file.length()));                                    // 파일일 경우에는 파일의 크기
                }}
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
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.mImage);
                holder.mText = (TextView) convertView.findViewById(R.id.mText);
                holder.mDate = (TextView) convertView.findViewById(R.id.mDate);
                holder.mSize = (TextView) convertView.findViewById(R.id.mSize);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ListData mData = mListData.get(position);

            if (mData.mIcon != null) {
                holder.mIcon.setVisibility(View.VISIBLE);
                holder.mIcon.setImageDrawable(mData.mIcon);
            } else {
                holder.mIcon.setVisibility(View.GONE);
            }

            holder.mText.setText(mData.mTitle);
            holder.mDate.setText(mData.mDate);
            holder.mSize.setText(mData.mSize);

            return convertView;
        }

        public void addItem(Drawable icon, String mTitle, String mDate, String mSize) {
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mIcon = icon;
            addInfo.mTitle = mTitle;
            addInfo.mDate = mDate;
            addInfo.mSize = mSize;
            // addInfo.m

            mListData.add(addInfo);
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

    @Override
    public void onBackPressed() { //onBackPressed() 오버라이딩
        if (root2.equals("")) {
            super.onBackPressed();
        } else {
            while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                mAdapter.remove(0);
            if(cate.equals("music"))
                Musicmediascan();
            else if(cate.equals("image"))
                Imagemediascan();
            else if(cate.equals("video"))
                Videomediascan();
            root2="";
            mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용*/
        }
    }

    public static String getExtension(String fileStr) { // 확장자 뽑기
        return fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
    }

    public static String getSize(long size){
        int count=0;
        while(size>1024){
            if(size > 1024){
                size = size/1024;
                count++;
            }}
        if(count==1)
            return size+"KB";
        else if(count==2)
            return size+"MB";
        else if(count==3)
            return size+"GB";
        else
            return size+"B";

    }

   public void Musicmediascan(){
        item = new ArrayList<String>(); //폴더 or 파일 이름
        path = new ArrayList<String>(); //경로
        ContentResolver cr = getContentResolver();
        Cursor mImageCursor;

        String sortOrder = MediaStore.Audio.AudioColumns.DATE_ADDED + " DESC";
        mImageCursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,sortOrder);

        if(mImageCursor.moveToFirst())
        {
            do
            {
               String Fullpath =  mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)); // 음악 파일의 path
                int directorypath = (mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)).length() - mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)).length())-1;
                //음악 파일의 상위 디렉터리를 얻기 위해서 씀.
                String pathcheck = Fullpath.substring(0,directorypath); // 음악 파일의 상위 디렉토리

                int check = 1; // 처음의 상위 디렉토리를 리스트 뷰에 포함 시키기 위해 1을 씀

                for(int i=0;i<list.size();i++){
                    if(list.get(i).equals(pathcheck)){ // 해당 경로가 있으면 list에 추가하지 않음
                        check = 0;
                        break;
                    }
                    else // 없으면
                        check = 1;
                }
                if(check == 1){
                   list.add(pathcheck); // 추가
                }

            }while(mImageCursor.moveToNext());
        }
        for(int i=0;i<list.size();i++){                                     //리스트에 추가 된 모든 경로의 디렉토리를 리스트뷰에 추가 ㄱㄱ
            File file = new File(list.get(i));
            path.add(file.getPath());
            mAdapter.addItem(getResources().getDrawable(R.drawable.folder),
                    file.getName(),                                         // 파일 이름
                    sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                    file.listFiles().length + "항목");                   // 디렉토리 내의 파일 or 디렉토리 수
        }
    }

    public void Imagemediascan(){
        item = new ArrayList<String>(); //폴더 or 파일 이름
        path = new ArrayList<String>(); //경로
        ContentResolver cr = getContentResolver();
        Cursor mImageCursor;

        String sortOrder = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC";
        mImageCursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,sortOrder);

        if(mImageCursor.moveToFirst())
        {
            do
            {
                String Fullpath =  mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)); // 음악 파일의 path
                int directorypath = (mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)).length() - mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)).length())-1;
                //음악 파일의 상위 디렉터리를 얻기 위해서 씀.
                String pathcheck = Fullpath.substring(0,directorypath); // 음악 파일의 상위 디렉토리

                int check = 1; // 처음의 상위 디렉토리를 리스트 뷰에 포함 시키기 위해 1을 씀

                for(int i=0;i<list.size();i++){
                    if(list.get(i).equals(pathcheck)){ // 해당 경로가 있으면 list에 추가하지 않음
                        check = 0;
                        break;
                    }
                    else // 없으면
                        check = 1;
                }
                if(check == 1){
                    list.add(pathcheck); // 추가
                }

            }while(mImageCursor.moveToNext());
        }
        for(int i=1;i<list.size();i++){                                     //리스트에 추가 된 모든 경로의 디렉토리를 리스트뷰에 추가 ㄱㄱ
            Log.i(""+list.size(), list.get(i));
            File file = new File(list.get(i));
            path.add(file.getPath());
            Log.i("" + list.size(), file.getName() + " " + sf.format(file.lastModified()) + " " + file.listFiles().length + "항목");
            mAdapter.addItem(getResources().getDrawable(R.drawable.folder),
                    file.getName(),                                         // 파일 이름
                    sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                    file.listFiles().length + "항목");                   // 디렉토리 내의 파일 or 디렉토리 수
        }
    }

    public void Videomediascan(){
        item = new ArrayList<String>(); //폴더 or 파일 이름
        path = new ArrayList<String>(); //경로
        ContentResolver cr = getContentResolver();
        Cursor mImageCursor;

        String sortOrder = MediaStore.Video.VideoColumns.DATE_ADDED + " DESC";
        mImageCursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,null,null,null,sortOrder);

        if(mImageCursor.moveToFirst())
        {
            do
            {
                String Fullpath =  mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)); // 음악 파일의 path
                int directorypath = (mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)).length() - mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME)).length())-1;
                //음악 파일의 상위 디렉터리를 얻기 위해서 씀.
                String pathcheck = Fullpath.substring(0,directorypath); // 음악 파일의 상위 디렉토리

                int check = 1; // 처음의 상위 디렉토리를 리스트 뷰에 포함 시키기 위해 1을 씀

                for(int i=0;i<list.size();i++){
                    if(list.get(i).equals(pathcheck)){ // 해당 경로가 있으면 list에 추가하지 않음
                        check = 0;
                        break;
                    }
                    else // 없으면
                        check = 1;
                }
                if(check == 1){
                    list.add(pathcheck); // 추가
                }

            }while(mImageCursor.moveToNext());
        }
        for(int i=0;i<list.size();i++){                                     //리스트에 추가 된 모든 경로의 디렉토리를 리스트뷰에 추가 ㄱㄱ
            File file = new File(list.get(i));
            path.add(file.getPath());
            mAdapter.addItem(getResources().getDrawable(R.drawable.folder),
                    file.getName(),                                         // 파일 이름
                    sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                    file.listFiles().length + "항목");                   // 디렉토리 내의 파일 or 디렉토리 수
        }
    }

    public void Latelymediascan(){
        item = new ArrayList<String>(); //폴더 or 파일 이름
        path = new ArrayList<String>(); //경로
        ContentResolver cr = getContentResolver();
        Cursor mImageCursor;

        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
        String selection = MediaStore.Files.FileColumns.DATE_ADDED +" > ?";

        String before7day = ((new Date().getTime() - (60 * 60 * 24 * 7000)) / 1000)+"";
        String[] selectionArgs = { before7day };
        mImageCursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,selection,selectionArgs,sortOrder);

        if(mImageCursor.moveToFirst())
        {
            do
            {
               String Fullpath =  mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)); // 음악 파일의 path
                int directorypath = (mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)).length() - mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)).length())-1;
                //음악 파일의 상위 디렉터리를 얻기 위해서 씀.
                String pathcheck = Fullpath.substring(0,directorypath); // 음악 파일의 상위 디렉토리
                String Et = getExtension(mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)));

                if (Et.equals("jpg") || Et.equals("png") || Et.equals("jpeg") || Et.equals("gif") || Et.equals("ai") || Et.equals("psd") || Et.equals("eps")) { //그림파일
                    mAdapter.addItem(getResources().getDrawable(R.drawable.image),
                            mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)),                                         // 파일 이름
                            sf.format(mImageCursor.getLong(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)))+"",                      // 마지막 수정 날짜
                            getSize(mImageCursor.getLong(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)))+"");         // 파일일 경우에는 파일의 크기
                } else if (Et.equals("aac") || Et.equals("mp3") || Et.equals("wav") || Et.equals("wma")) { //음악파일
                    mAdapter.addItem(getResources().getDrawable(R.drawable.musicfile),
                            mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)),                                         // 파일 이름
                            sf.format(mImageCursor.getLong(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)))+"",                      // 마지막 수정 날짜
                            getSize(mImageCursor.getLong(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)))+"");         // 파일일 경우에는 파일의 크기
                } else if (Et.equals("avi") || Et.equals("flv") || Et.equals("mkv") || Et.equals("wmv") || Et.equals("mp4")) { //동영상
                    mAdapter.addItem(getResources().getDrawable(R.drawable.play),
                            mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)),                                         // 파일 이름
                            sf.format(mImageCursor.getLong(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)))+"",                      // 마지막 수정 날짜
                            getSize(mImageCursor.getLong(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)))+"");         // 파일일 경우에는 파일의 크기
                } else if (Et.equals("txt")) { //텍스트파일
                    mAdapter.addItem(getResources().getDrawable(R.drawable.txtfile),
                            mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)),                                         // 파일 이름
                            sf.format(mImageCursor.getLong(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)))+"",                      // 마지막 수정 날짜
                            getSize(mImageCursor.getLong(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)))+"");                  // 파일일 경우에는 파일의 크기
                } else if (Et.equals("zip")) { //알집
                    mAdapter.addItem(getResources().getDrawable(R.drawable.zipfile),
                            mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)),                                         // 파일 이름
                            sf.format(mImageCursor.getLong(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)))+"",                      // 마지막 수정 날짜
                            getSize(mImageCursor.getLong(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)))+"");
                } else {
                    mAdapter.addItem(getResources().getDrawable(R.drawable.defaultfile),
                            mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)),                                         // 파일 이름
                            sf.format(mImageCursor.getLong(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)))+"",                      // 마지막 수정 날짜
                            getSize(mImageCursor.getLong(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)))+"");
                }
                path.add(mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
            }while(mImageCursor.moveToNext());
        }

    }




}