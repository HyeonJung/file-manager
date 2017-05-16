package com.example.yun.filesss;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileMove extends AppCompatActivity {
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    private List<String> item = null;
    private List<String> path = null;
    private String root = "/storage/sdcard0";
    private String root2 = "";
    private String currentroot = "",Type;
    Button btOK,btCancle;
    private String MoveFile,MoveFileName;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd"); // 파일 최근 수정 날짜 형식

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_move);
        Intent intent = getIntent();
        MoveFile = intent.getExtras().getString("filepath");
        MoveFileName = intent.getExtras().getString("movefilename");
        Type = intent.getExtras().getString("type");
        mListView = (ListView) findViewById(R.id.mList);

        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);
        getDir(root);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                File file = new File(path.get(position));
                if (file.isDirectory()) //디렉토리이면
                    if (file.canRead()) { //읽을 수 있는 파일이면
                        // ListData mData = mAdapter.mListData.get(position);
                        // Toast.makeText(MainActivity.this, mAdapter.getCount() + "", Toast.LENGTH_SHORT).show();
                        while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                            mAdapter.remove(0);
                        getDir(path.get(position));
                        mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용
                    }

            }
        });
        btOK = (Button)findViewById(R.id.btOk);
        btOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileMove.this, FIleManager.class);
                if(Type.equals("move"))
                intent.putExtra("type","move");
                else if(Type.equals("copy"))
                intent.putExtra("type","copy");

                intent.putExtra("current",currentroot);
                intent.putExtra("filepath",MoveFile);
                intent.putExtra("movefilename",MoveFileName);
                startActivity(intent);
            }
        });

        btCancle = (Button)findViewById(R.id.btCancel);
        btCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getDir(String dirPath) {
        item = new ArrayList<String>(); //폴더 or 파일 이름
        path = new ArrayList<String>(); //경로

        currentroot = dirPath;
        File f = new File(dirPath); // 인자로 받은 경로로 파일 지정
        File[] files = f.listFiles();
        if (!dirPath.equals("/storage/sdcard0")) { // 현재 파일이 루트가 아닐 때
            item.add("..");
            path.add(f.getParent()); // 부모 디렉토리
            root2 = f.getParent();
            mAdapter.addItem(getResources().getDrawable(R.drawable.folder),
                    "..", "", "");
        } else
            root2 = "";

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            path.add(file.getPath());
            if (file.isDirectory()) {                                           // 디렉토리일 경우
                item.add(file.getName());
                mAdapter.addItem(getResources().getDrawable(R.drawable.folder),
                        file.getName(),                                         // 파일 이름
                        sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                        file.listFiles().length + "항목");                       // 디렉토리 내의 파일 or 디렉토리 수

            } else {                                                            // 디렉토리가 아닐 경우
                String Et = getExtension(file.getName());
                item.add(file.getName());
                if (Et.equals("jpg") || Et.equals("png") || Et.equals("jpeg") || Et.equals("gif") || Et.equals("ai") || Et.equals("psd") || Et.equals("eps")) { //그림파일
                    Bitmap mybitmap = BitmapFactory.decodeFile(file.getPath());

                    mAdapter.addItem(getResources().getDrawable(R.drawable.photo),
                            file.getName(),                                         // 파일 이름
                            sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                            getSize(file.length()));                                     // 파일일 경우에는 파일의 크기
                } else if (Et.equals("aac") || Et.equals("mp3") || Et.equals("wav") || Et.equals("wma")) { //음악파일
                    mAdapter.addItem(getResources().getDrawable(R.drawable.musicfile),
                            file.getName(),                                         // 파일 이름
                            sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                            getSize(file.length()));                                    // 파일일 경우에는 파일의 크기
                } else if (Et.equals("avi") || Et.equals("flv") || Et.equals("mkv") || Et.equals("wmv") || Et.equals("mp4")) { //동영상
                    mAdapter.addItem(getResources().getDrawable(R.drawable.play),
                            file.getName(),                                         // 파일 이름
                            sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                            getSize(file.length()));                                    // 파일일 경우에는 파일의 크기
                } else {
                    mAdapter.addItem(getResources().getDrawable(R.drawable.defaultfile),
                            file.getName(),                                         // 파일 이름
                            sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                            getSize(file.length()));                                // 파일일 경우에는 파일의 크기
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
            ViewHolder holder;
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
    public void onBackPressed() {
        if (root2.equals("")) {
            super.onBackPressed();
        } else {
            while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                mAdapter.remove(0);
            getDir(root2);
            mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용*/
        }
    }

    public static String getExtension(String fileStr) { // 확장자 뽑기
        return fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
    }

    public static String getSize(long size) {
        int count = 0;
        while (size > 1024) {
            if (size > 1024) {
                size = size / 1024;
                count++;
            }
        }
        if (count == 1)
            return size + "KB";
        else if (count == 2)
            return size + "MB";
        else if (count == 3)
            return size + "GB";
        else
            return size + "B";

    }
    public int deleteDir(String a_path){
        File file = new File(a_path);
        if(file.exists()){
            File[] childFileList = file.listFiles();
            for(File childFile : childFileList){
                if(childFile.isDirectory()){
                    deleteDir(childFile.getAbsolutePath());
                }
                else{
                    childFile.delete();
                }
            }
            file.delete();
            return 1;
        }else{
            return 0;
        }
    };
}


