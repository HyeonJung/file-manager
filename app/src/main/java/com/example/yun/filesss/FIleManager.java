package com.example.yun.filesss;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FIleManager extends AppCompatActivity {
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    private ListViewAdapter2 mAdapter3 = null;
    private List<String> item = null;
    private List<String> path = null;
    private List<Boolean> box = new ArrayList<Boolean>();
    private List<String> pathcopy = new ArrayList<String>();
    private List<String> itemcopy = new ArrayList<String>();
    private List<Boolean> boxcopy = new ArrayList<Boolean>();
    private String root = "/storage/sdcard0";
    private String root2 = "";
    private String currentroot = "";
    private String thiscurrentroot = root;
    private String MoveFile, MoveFileName;
    private List<String> itemalert = null;
    private List<String> pathalert = null;
    private String alertcurrentroot = "";
    AlertDialog.Builder alert2;
    FileThread ft;
    FileCopyHandler fh;
    FileProgressThread fpt;
    ProgressDialog filebar;

    int check = 0, click = 0, arraysize = 0;
    boolean checkcancle = false; //체크박스가 나타나 있을 때 백스페이스
    boolean del = false;
    int filecontrol = 0;
    int per = 0,size1=0,size2=0;
    ImageView menuimage;
    TextView tv;
    String type;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd"); // 파일 최근 수정 날짜 형식
    String rename;
    boolean filecopysuccess = false;
    Button menu, btNewDir, btMoveFile, btCancle, btFileMove2, btFileCopy, btFileCopy2, FileDelete;
    boolean checkboxon = false, Ckable = true;
    ViewHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        check = 0;
        tv = (TextView) findViewById(R.id.wheretv);
        menu = (Button) findViewById(R.id.filemenu);
        btNewDir = (Button) findViewById(R.id.btnewdirectory);
        btMoveFile = (Button) findViewById(R.id.btfilemove);
        btFileMove2 = (Button) findViewById(R.id.btfilemove2);
        btFileCopy2 = (Button) findViewById(R.id.btfilecopy2);
        btCancle = (Button) findViewById(R.id.filecancle);
        menuimage = (ImageView) findViewById(R.id.menuimage);
        btFileCopy = (Button) findViewById(R.id.btfilecopy);
        FileDelete = (Button) findViewById(R.id.btfilesdelete);
        mAdapter3 = new ListViewAdapter2(this);
        fh = new FileCopyHandler();
        Intent intent = getIntent();
        type = intent.getExtras().getString("type");
        if (type.equals("download")) // 메인에서 다운로드 버튼을 눌렀을 경우에.
            root = "/storage/sdcard0/Download";
        else if (type.equals("sd"))
            root = "/storage/external_SD";
        mListView = (ListView) findViewById(R.id.mList);
        filebar = new ProgressDialog(FIleManager.this);
        filebar.setCancelable(false);
        filebar.setMessage("파일을 복사하는 중입니다.");
        filebar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        filebar.setProgress(0);
        filebar.setMax(100);
        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);
        getDir(root);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                             @Override
                                             public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                                 if (Ckable) {
                                                     File file = new File(path.get(position));
                                                     if (file.isDirectory()) { //디렉토리이면
                                                         if (file.canRead()) { //읽을 수 있는 파일이면
                                                             // ListData mData = mAdapter.mListData.get(position);
                                                             // Toast.makeText(MainActivity.this, mAdapter.getCount() + "", Toast.LENGTH_SHORT).show();
                                                             while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                                                 mAdapter.remove(0);
                                                             getDir(path.get(position));
                                                             mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용
                                                         }
                                                     } else {
                                                         String ext = getExtension(file.getName());
                                                         if (ext.equals("Mp3") || ext.equals("mp3")) {
                                                             Intent i = new Intent();
                                                             i.setAction(android.content.Intent.ACTION_VIEW);
                                                             i.setDataAndType(Uri.fromFile(file), "audio/*");
                                                             startActivity(i);
                                                         } else if (ext.equals("avi") || ext.equals("flv") || ext.equals("mkv") || ext.equals("wmv") || ext.equals("mp4")) {
                                                             Intent i = new Intent();
                                                             i.setAction(android.content.Intent.ACTION_VIEW);
                                                             i.setDataAndType(Uri.fromFile(file), "video/*");
                                                             startActivity(i);
                                                         } else if (ext.equals("jpg") || ext.equals("png") || ext.equals("jpeg") || ext.equals("gif") || ext.equals("ai") || ext.equals("psd") || ext.equals("eps")) {
                                                             Intent i = new Intent();
                                                             i.setAction(android.content.Intent.ACTION_VIEW);
                                                             i.setDataAndType(Uri.fromFile(file), "image/*");
                                                             startActivity(i);
                                                         }

                                                     }


                                                 } else

                                                 {

                                                     if (box.get(position)) { //체크되어있으면
                                                         box.set(position, false); //체크 해제
                                                     } else {                   //체크되어있지 않으면
                                                         box.set(position, true); // 체크
                                                     }
                                                     Log.v(box.get(position) + "", position + "");
                                                     Log.v(box.size() + "", "왜이래이거");

                                                     if (click == 0) {
                                                         click++;
                                                         arraysize = box.size();
                                                     }
                                                     Log.v(arraysize + "", "왜이래이거");
                                                     mAdapter.remove(position);
                                                     File file = new File(path.get(position));
                                                     String Et = getExtension(path.get(position));
                                                     if (file.isDirectory()) {
                                                         mAdapter.addItemposition(getResources().getDrawable(R.drawable.folder),
                                                                 file.getName(),                                         // 파일 이름
                                                                 sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                                                                 file.listFiles().length + "항목",
                                                                 false, position);                       // 디렉토리 내의 파일 or 디렉토리 수

                                                     } else if (Et.equals("jpg") || Et.equals("png") || Et.equals("jpeg") || Et.equals("gif") || Et.equals("ai") || Et.equals("psd") || Et.equals("eps")) { //그림파일
                                                         BitmapFactory.Options options = new BitmapFactory.Options();
                                                         options.inJustDecodeBounds = true;
                                                         BitmapFactory.decodeFile(path.get(position), options);
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
                                                                 item.get(position),
                                                                 sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                                                                 getSize(file.length()),// 파일 이름
                                                                 false, position);                                     // 파일일 경우에는 파일의 크기
                                                     } else if (Et.equals("aac") || Et.equals("mp3") || Et.equals("wav") || Et.equals("wma")) { //음악파일
                                                         mAdapter.addItemposition(getResources().getDrawable(R.drawable.musicfile),
                                                                 item.get(position),
                                                                 sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                                                                 getSize(file.length()),// 파일 이름
                                                                 false, position);                                    // 파일일 경우에는 파일의 크기
                                                     } else if (Et.equals("avi") || Et.equals("flv") || Et.equals("mkv") || Et.equals("wmv") || Et.equals("mp4")) { //동영상
                                                         mAdapter.addItemposition(getResources().getDrawable(R.drawable.play),
                                                                 item.get(position),
                                                                 sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                                                                 getSize(file.length()),// 파일 이름
                                                                 false, position);                                    // 파일일 경우에는 파일의 크기
                                                     } else if (Et.equals("txt")) { //텍스트파일
                                                         mAdapter.addItemposition(getResources().getDrawable(R.drawable.txtfile),
                                                                 item.get(position),
                                                                 sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                                                                 getSize(file.length()),// 파일 이름
                                                                 false, position);                                    // 파일일 경우에는 파일의 크기
                                                     } else if (Et.equals("zip")) { //알집
                                                         mAdapter.addItemposition(getResources().getDrawable(R.drawable.zipfile),
                                                                 item.get(position),
                                                                 sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                                                                 getSize(file.length()),// 파일 이름
                                                                 false, position);                                    // 파일일 경우에는 파일의 크기
                                                     } else if (Et.equals("pptx")) { //알집
                                                         mAdapter.addItemposition(getResources().getDrawable(R.drawable.pptx),
                                                                 item.get(position),
                                                                 sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                                                                 getSize(file.length()),// 파일 이름
                                                                 false, position);                                    // 파일일 경우에는 파일의 크기
                                                     }else {
                                                         mAdapter.addItemposition((getResources().getDrawable(R.drawable.defaultfile)),
                                                                 item.get(position),
                                                                 sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                                                                 getSize(file.length()),// 파일 이름
                                                                 false, position);                                // 파일일 경우에는 파일의 크기
                                                     }
                                                 }
                                             }
                                         }

        );
        menu.setOnClickListener(new View.OnClickListener()

                                { //메뉴를 클릭하면 메뉴 바가 나오게
                                    @Override
                                    public void onClick(View v) { // 메뉴 버튼 눌렀을 때
                                        if (check == 0) {
                                            menuimage.setVisibility(View.VISIBLE); // 메뉴 바 아이콘 보여줌
                                            btFileCopy.setClickable(true);
                                            btNewDir.setClickable(true); // 새 디렉토리 만들기 버튼 활성화
                                            btMoveFile.setClickable(true); // 파일 이동 버튼 활성화
                                            FileDelete.setClickable(true);
                                            check = 1;
                                        } else if (check == 1) {
                                            menuimage.setVisibility(View.INVISIBLE); // 메뉴 바 아이콘 가림
                                            btFileCopy.setClickable(false);
                                            btNewDir.setClickable(false); // 새 디렉토리 만들기 버튼 비활성화
                                            btMoveFile.setClickable(false); // 파일 이동 버튼 비활성화
                                            FileDelete.setClickable(false);
                                            check = 0;
                                        }
                                    }
                                }

        );
        btNewDir.setOnClickListener(new View.OnClickListener()

                                    { //메뉴를 클릭하면 메뉴 바가 나오게
                                        @Override
                                        public void onClick(View v) {
                                            realert();
                                            Log.v("새폴더생성", thiscurrentroot);
                                            alert2.show();

                                        }
                                    }

        );
        FileDelete.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View v) {
                                              check = 0;
                                              menuimage.setVisibility(View.INVISIBLE); // 메뉴 바 아이콘 가림
                                              btFileCopy.setClickable(false);
                                              btNewDir.setClickable(false); // 새 디렉토리 만들기 버튼 비활성화
                                              btMoveFile.setClickable(false); // 파일 이동 버튼 비활성화
                                              FileDelete.setClickable(false);
                                              filecontrol = 3;
                                              if (!checkboxon) {    //체크 박스가 켜져있지 않으면
                                                  box.clear();    //box ArrayList 초기화
                                                  checkboxon = true;    //체크박스 온
                                                  checkcancle = true;   //backpressed 이벤트 발생 시 체크박스 off하기 위해 사용
                                                  Ckable = false;
                                                  click = 0;
                                                  btFileMove2.setAlpha(1.0f);
                                                  btFileMove2.setClickable(true);
                                                  btFileMove2.setText("파일 삭제");
                                                  btCancle.setAlpha(1.0f);
                                                  btCancle.setClickable(true);
                                                  for (int i = 0; i < path.size(); i++) {
                                                      pathcopy.add(i, path.get(i));
                                                  }
                                                  for (int i = 0; i < item.size(); i++) {
                                                      itemcopy.add(i, item.get(i));
                                                  }
                                                  while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                                      mAdapter.remove(0);
                                                  getDir(thiscurrentroot);
                                              } else {   //체크 박스가 켜져 있으면
                                                  box.clear();    //box ArrayList 초기화
                                                  checkboxon = false;   //체크박스 오프
                                                  checkcancle = false;
                                                  Ckable = true;
                                                  click = 0;
                                                  btFileMove2.setAlpha(0.0f);
                                                  btFileMove2.setClickable(false);
                                                  btCancle.setAlpha(0.0f);
                                                  btCancle.setClickable(false);
                                                  while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                                      mAdapter.remove(0);
                                                  getDir(thiscurrentroot);

                                              }
                                          }
                                      }

        );
            /*FIleOk2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (filecontrol == 1) {
                        for (int i = 0; i < arraysize; i++) {
                            if (box.get(i)) {
                                Log.v("파일경로", pathcopy.get(i));
                                Log.v("이름", itemcopy.get(i));
                                Log.v("옮길경로", thiscurrentroot);
                                moveFile(itemcopy.get(i), pathcopy.get(i), thiscurrentroot);
                            }

                        }
                        pathcopy.clear();
                        itemcopy.clear();
                    } else if (filecontrol == 2) {
                        for (int i = 0; i < arraysize; i++) {
                            if (box.get(i)) {
                                MoveFileName = thiscurrentroot + "/" + itemcopy.get(i);
                                File sourceFile = new File(pathcopy.get(i));
                                boolean check1 = copyFile(sourceFile, MoveFileName);
                                Log.v(MoveFileName, "확인" + check1);
                            }

                        }
                        pathcopy.clear();
                        itemcopy.clear();
                    } else if (filecontrol == 3) {
                        checkboxon = false;    //체크박스 오프
                        checkcancle = true;   //backpressed 이벤트 발생 시 체크박스 off하기 위해 사용
                        Ckable = true;
                        for (int j = 0; j < arraysize; j++) {
                            boxcopy.add(j, box.get(j));
                        }
                        AlertDialog.Builder alert = new AlertDialog.Builder(FIleManager.this);
                        alert.setTitle("정말 삭제하시겠습니까?");
                        alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.v(arraysize + "" + boxcopy.get(1), pathcopy.get(1) + "asdasdasd12312312sa");
                                for (int i = 0; i < arraysize; i++) {
                                    if (boxcopy.get(i)) {
                                        Log.v("asdfasd", pathcopy.get(i));
                                        Log.v("sadfasdf", itemcopy.get(i));
                                        Log.v("asadsasd", thiscurrentroot);
                                        File delfile = new File(pathcopy.get(i));
                                        if (delfile.isDirectory()) {
                                            deleteDir(pathcopy.get(i));
                                        } else {
                                            delfile.delete();
                                        }
                                    }

                                }
                                Toast.makeText(FIleManager.this, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                                pathcopy.clear();
                                itemcopy.clear();
                                boxcopy.clear();
                                while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                    mAdapter.remove(0);
                                getDir(thiscurrentroot);
                                mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용
                            }
                        });
                        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                        alert.show();
                    }

                    while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                        mAdapter.remove(0);
                    getDir(thiscurrentroot);
                    mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용
                    FIleOk2.setClickable(false);
                    FIleOk2.setAlpha(0.0f);
                    btCancle.setClickable(false);
                    btCancle.setAlpha(0.0f);
                    tv.setText("현재 위치 : " + thiscurrentroot);

                    Ckable = true;
                    checkcancle = false;
                }
            });
        }*/
        btCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                box.clear();
                checkboxon = false;
                checkcancle = false;
                Ckable = true;
                btFileMove2.setAlpha(0.0f);
                btFileMove2.setClickable(false);
                btCancle.setAlpha(0.0f);
                btCancle.setClickable(false);
                btFileMove2.setAlpha(0.0f);
                btFileMove2.setClickable(false);
                //FIleOk2.setAlpha(0.0f);
                //FIleOk2.setClickable(false);
                click = 0;
                filecontrol = 0;
                while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                    mAdapter.remove(0);
                getDir(thiscurrentroot);
                mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용*/

            }
        });
        btFileCopy.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View v) {
                                              check = 0;
                                              menuimage.setVisibility(View.INVISIBLE); // 메뉴 바 아이콘 가림
                                              btFileCopy.setClickable(false);
                                              btNewDir.setClickable(false); // 새 디렉토리 만들기 버튼 비활성화
                                              btMoveFile.setClickable(false); // 파일 이동 버튼 비활성화
                                              FileDelete.setClickable(false);
                                              filecontrol = 2;
                                              if (!checkboxon) {    //체크 박스가 켜져있지 않으면
                                                  box.clear();    //box ArrayList 초기화
                                                  checkboxon = true;    //체크박스 온
                                                  checkcancle = true;   //backpressed 이벤트 발생 시 체크박스 off하기 위해 사용
                                                  Ckable = false;
                                                  click = 0;
                                                  btFileMove2.setAlpha(1.0f);
                                                  btFileMove2.setClickable(true);
                                                  btFileMove2.setText("파일 복사");
                                                  btCancle.setAlpha(1.0f);
                                                  btCancle.setClickable(true);

                                                  while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                                      mAdapter.remove(0);
                                                  getDir(thiscurrentroot);
                                              } else {   //체크 박스가 켜져 있으면
                                                  box.clear();    //box ArrayList 초기화
                                                  checkboxon = false;   //체크박스 오프
                                                  checkcancle = false;
                                                  Ckable = true;
                                                  click = 0;
                                                  btFileMove2.setAlpha(0.0f);
                                                  btFileMove2.setClickable(false);
                                                  btCancle.setAlpha(0.0f);
                                                  btCancle.setClickable(false);
                                                  while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                                      mAdapter.remove(0);
                                                  getDir(thiscurrentroot);

                                              }
                                          }
                                      }

        );
       /*btFileCopy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btFileCopy2.setAlpha(0.0f);
                btFileCopy2.setClickable(false);

                checkcancle = true;   //backpressed 이벤트 발생 시 체크박스 off하기 위해 사용

                Ckable = true;
                checkboxon = false;

                for (int i = 0; i < path.size(); i++) {
                    pathcopy.add(i, path.get(i));
                }
                for (int i = 0; i < item.size(); i++) {
                    itemcopy.add(i, item.get(i));
                }
                FIleOk3.setAlpha(1.0f);
                FIleOk3.setClickable(true);
                while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                    mAdapter.remove(0);
                getDir(root);
                mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용
                tv.setText("파일을 복사할 위치를 선택 해주세요.");
            }
        });*/
        btFileMove2.setOnClickListener(new View.OnClickListener() { //파일 이동 체크박스 선택하고 확인
            @Override
            public void onClick(View v) {
                btFileMove2.setAlpha(0.0f);
                btFileMove2.setClickable(false);
                btCancle.setAlpha(0.0f);
                btCancle.setClickable(false);
                checkboxon = false;    //체크박스 오프
                checkcancle = true;   //backpressed 이벤트 발생 시 체크박스 off하기 위해 사용
                Ckable = true;
                for (int i = 0; i < path.size(); i++) {
                    pathcopy.add(i, path.get(i));
                }
                for (int i = 0; i < item.size(); i++) {
                    itemcopy.add(i, item.get(i));
                }
                //FIleOk2.setAlpha(1.0f);
                //FIleOk2.setClickable(true);

                while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                    mAdapter.remove(0);
                getDir(root);
                mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용*/
                Log.v("대체", "이게 왜?");
                if (filecontrol == 1) {
                    while (mAdapter3.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                        mAdapter3.remove(0);
                    getDirpath(root);
                    AlertDialog.Builder alt_bld;
                    final AlertDialog alert3;
                    alt_bld = new AlertDialog.Builder(FIleManager.this);
                    LayoutInflater inflater = FIleManager.this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.alert_listview, null);
                    alt_bld.setView(dialogView);
                    alert3 = alt_bld.create();
                    alert3.setTitle("파일 이동 경로 선택");
                    alert3.setMessage("파일을 이동 할 경로를 선택해 주세요.");
                    ListView list = new ListView(FIleManager.this);


                    ListView listv = (ListView) dialogView.findViewById(R.id.alertlistView);
                    Button btok = (Button) dialogView.findViewById(R.id.alertbutton);
                    Button btcancel = (Button) dialogView.findViewById(R.id.alertbutton2);
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
                            int count = 0;
                            for (int i = 0; i < arraysize; i++) {
                                if (box.get(i)) {
                                    Log.v("파일경로", pathcopy.get(i));
                                    Log.v("이름", itemcopy.get(i));
                                    Log.v("옮길경로", thiscurrentroot);
                                    moveFile(itemcopy.get(i), pathcopy.get(i), alertcurrentroot);
                                    count++;
                                }
                            }
                            while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                mAdapter.remove(0);
                            getDir(alertcurrentroot);
                            mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용
                            tv.setText("현재 위치 : " + alertcurrentroot);
                            Ckable = true;
                            checkcancle = false;
                            Toast.makeText(getApplicationContext(), count + "개의 파일을 " + alertcurrentroot + "로 이동 했습니다.", Toast.LENGTH_SHORT).show();
                            pathcopy.clear();
                            itemcopy.clear();
                            alert3.dismiss();
                        }
                    });
                    btcancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert3.dismiss();
                        }
                    });
                    alert3.show();
                } else if (filecontrol == 2) {
                    while (mAdapter3.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                        mAdapter3.remove(0);
                    getDirpath(root);
                    AlertDialog.Builder alt_bld;
                    final AlertDialog alert3;
                    alt_bld = new AlertDialog.Builder(FIleManager.this);
                    LayoutInflater inflater = FIleManager.this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.alert_listview, null);
                    alt_bld.setView(dialogView);
                    alert3 = alt_bld.create();
                    alert3.setTitle("파일 복사 경로 선택");
                    alert3.setMessage("파일을 복사 할 경로를 선택해 주세요.");
                    ListView list = new ListView(FIleManager.this);


                    ListView listv = (ListView) dialogView.findViewById(R.id.alertlistView);
                    Button btok = (Button) dialogView.findViewById(R.id.alertbutton);
                    Button btcancel = (Button) dialogView.findViewById(R.id.alertbutton2);
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
                            int count = 0;

                            for (int i = 0; i < arraysize; i++) {
                                if (box.get(i)) {
                                    MoveFileName = alertcurrentroot + "/" + itemcopy.get(i);
                                    File sourceFile = new File(pathcopy.get(i));
                                    boolean check1 = copyFile(sourceFile, MoveFileName);
                                    Log.v(MoveFileName, "확인" + check1);
                                    count++;
                                }
                            }
                            while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                mAdapter.remove(0);
                            getDir(alertcurrentroot);
                            mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용
                            tv.setText("현재 위치 : " + alertcurrentroot);
                            Ckable = true;
                            checkcancle = false;
                            Toast.makeText(getApplicationContext(), count + "개의 파일을 " + alertcurrentroot + "로 복사 했습니다.", Toast.LENGTH_SHORT).show();
                            pathcopy.clear();
                            itemcopy.clear();
                            alert3.dismiss();
                        }
                    });
                    btcancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert3.dismiss();
                        }
                    });
                    alert3.show();
                } else if (filecontrol == 3) {
                    {
                        checkboxon = false;    //체크박스 오프
                        checkcancle = true;   //backpressed 이벤트 발생 시 체크박스 off하기 위해 사용
                        Ckable = true;
                        for (int j = 0; j < arraysize; j++) {
                            boxcopy.add(j, box.get(j));
                        }
                        AlertDialog.Builder alert = new AlertDialog.Builder(FIleManager.this);
                        alert.setTitle("정말 삭제하시겠습니까?");
                        alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.v(arraysize + "" + boxcopy.get(1), pathcopy.get(1) + "asdasdasd12312312sa");
                                for (int i = 0; i < arraysize; i++) {
                                    if (boxcopy.get(i)) {
                                        Log.v("asdfasd", pathcopy.get(i));
                                        Log.v("sadfasdf", itemcopy.get(i));
                                        Log.v("asadsasd", thiscurrentroot);
                                        File delfile = new File(pathcopy.get(i));
                                        if (delfile.isDirectory()) {
                                            deleteDir(pathcopy.get(i));
                                        } else {
                                            delfile.delete();
                                        }
                                    }

                                }
                                Toast.makeText(FIleManager.this, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                                pathcopy.clear();
                                itemcopy.clear();
                                boxcopy.clear();
                                while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                    mAdapter.remove(0);
                                getDir(thiscurrentroot);
                                mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용
                            }
                        });
                        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                        alert.show();
                    }

                    while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                        mAdapter.remove(0);
                    getDir(thiscurrentroot);
                    mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용
                    btFileMove2.setClickable(false);
                    btFileMove2.setAlpha(0.0f);
                    btCancle.setClickable(false);
                    btCancle.setAlpha(0.0f);
                    tv.setText("현재 위치 : " + thiscurrentroot);

                    Ckable = true;
                    checkcancle = false;
                }
            }
        });

        btMoveFile.setOnClickListener(new View.OnClickListener()

                                      { // 여러개 파일 이동
                                          @Override
                                          public void onClick(View v) {
                                              check = 0;
                                              menuimage.setVisibility(View.INVISIBLE); // 메뉴 바 아이콘 가림
                                              btFileCopy.setClickable(false);
                                              btNewDir.setClickable(false); // 새 디렉토리 만들기 버튼 비활성화
                                              btMoveFile.setClickable(false); // 파일 이동 버튼 비활성화
                                              FileDelete.setClickable(false);
                                              filecontrol = 1;
                                              if (!checkboxon) {    //체크 박스가 켜져있지 않으면
                                                  box.clear();    //box ArrayList 초기화
                                                  checkboxon = true;    //체크박스 온
                                                  checkcancle = true;   //backpressed 이벤트 발생 시 체크박스 off하기 위해 사용
                                                  Ckable = false;
                                                  click = 0;
                                                  btFileMove2.setAlpha(1.0f);
                                                  btFileMove2.setClickable(true);
                                                  btFileMove2.setText("파일 이동");
                                                  btCancle.setAlpha(1.0f);
                                                  btCancle.setClickable(true);

                                                  while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                                      mAdapter.remove(0);
                                                  getDir(thiscurrentroot);
                                              } else {   //체크 박스가 켜져 있으면
                                                  box.clear();    //box ArrayList 초기화
                                                  checkboxon = false;   //체크박스 오프
                                                  checkcancle = false;
                                                  Ckable = true;
                                                  click = 0;
                                                  btFileMove2.setAlpha(0.0f);
                                                  btFileMove2.setClickable(false);
                                                  btCancle.setAlpha(0.0f);
                                                  btCancle.setClickable(false);
                                                  while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                                      mAdapter.remove(0);
                                                  getDir(thiscurrentroot);

                                              }
                                          }
                                      }

        );

    }

    private void getDir(String dirPath) {
        item = new ArrayList<String>(); //폴더 or 파일 이름
        path = new ArrayList<String>(); //경로
        thiscurrentroot = dirPath;
        Log.v("파일경로", thiscurrentroot);
        if (filecontrol == 0)
            tv.setText("현재 위치 : " + thiscurrentroot);

        File f = new File(dirPath); // 인자로 받은 경로로 파일 지정
        File[] files = f.listFiles();

        if (!dirPath.equals(root)) { // 현재 파일이 루트가 아닐 때
            root2 = f.getParent();
        } else
            root2 = "";

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            path.add(file.getPath());
            if (file.isDirectory()) {                                           // 디렉토리일 경우
                item.add(file.getName());
                box.add(false);
                mAdapter.addItem(getResources().getDrawable(R.drawable.folder),
                        file.getName(),                                         // 파일 이름
                        sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                        file.listFiles().length + "항목",
                        false);                       // 디렉토리 내의 파일 or 디렉토리 수

            } else {                                                            // 디렉토리가 아닐 경우
                String Et = getExtension(file.getName());
                item.add(file.getName());
                box.add(false);
                if (Et.equals("jpg") || Et.equals("png") || Et.equals("jpeg") || Et.equals("gif") || Et.equals("ai") || Et.equals("psd") || Et.equals("eps")) { //그림파일
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getPath(), options);
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
                            sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                            getSize(file.length()),
                            false);                                     // 파일일 경우에는 파일의 크기
                } else if (Et.equals("aac") || Et.equals("mp3") || Et.equals("wav") || Et.equals("wma")) { //음악파일
                    mAdapter.addItem(getResources().getDrawable(R.drawable.musicfile),
                            file.getName(),                                         // 파일 이름
                            sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                            getSize(file.length()),
                            false);                                    // 파일일 경우에는 파일의 크기
                } else if (Et.equals("avi") || Et.equals("flv") || Et.equals("mkv") || Et.equals("wmv") || Et.equals("mp4")) { //동영상
                    mAdapter.addItem(getResources().getDrawable(R.drawable.play),
                            file.getName(),                                         // 파일 이름
                            sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                            getSize(file.length()),
                            false);                                    // 파일일 경우에는 파일의 크기
                } else if (Et.equals("txt")) { //텍스트파일
                    mAdapter.addItem(getResources().getDrawable(R.drawable.txtfile),
                            file.getName(),                                         // 파일 이름
                            sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                            getSize(file.length()),
                            false);                                    // 파일일 경우에는 파일의 크기
                } else if (Et.equals("zip")) { //알집
                    mAdapter.addItem(getResources().getDrawable(R.drawable.zipfile),
                            file.getName(),                                         // 파일 이름
                            sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                            getSize(file.length()),
                            false);                                    // 파일일 경우에는 파일의 크기
                } else if (Et.equals("pptx")) { //알집
                    mAdapter.addItem(getResources().getDrawable(R.drawable.pptx),
                            file.getName(),                                         // 파일 이름
                            sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                            getSize(file.length()),
                            false);                                    // 파일일 경우에는 파일의 크기
                }else {
                    mAdapter.addItem(getResources().getDrawable(R.drawable.defaultfile),
                            file.getName(),                                         // 파일 이름
                            sf.format(file.lastModified()) + "",                      // 마지막 수정 날짜
                            getSize(file.length()),
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
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.mImage);
                holder.mText = (TextView) convertView.findViewById(R.id.mText);
                holder.mDate = (TextView) convertView.findViewById(R.id.mDate);
                holder.mSize = (TextView) convertView.findViewById(R.id.mSize);
                holder.mCheck = (CheckBox) convertView.findViewById(R.id.mCheck);

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

            if (checkboxon == true)
                holder.mCheck.setVisibility(View.VISIBLE);
            else {
                holder.mCheck.setVisibility(View.GONE);
            }


            holder.mCheck.setChecked(box.get(position));
            holder.mCheck.setFocusable(false);
            holder.mCheck.setClickable(false);
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


            return convertView;
        }

        public void addItem(Drawable icon, String mTitle, String mDate, String mSize, Boolean mcheck) {
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mIcon = icon;
            addInfo.mTitle = mTitle;
            addInfo.mDate = mDate;
            addInfo.mSize = mSize;
            addInfo.mCheck = mcheck;

            mListData.add(addInfo);
        }

        public void addItemposition(Drawable icon, String mTitle, String mDate, String mSize, Boolean mcheck, int position) {
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mIcon = icon;
            addInfo.mTitle = mTitle;
            addInfo.mDate = mDate;
            addInfo.mSize = mSize;
            addInfo.mCheck = mcheck;

            mListData.add(position, addInfo);
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
        if (checkcancle == true) {
            box.clear();
            checkboxon = false;
            checkcancle = false;
            Ckable = true;
            btFileMove2.setAlpha(0.0f);
            btFileMove2.setClickable(false);
            btCancle.setAlpha(0.0f);
            btCancle.setClickable(false);
            btFileMove2.setAlpha(0.0f);
            btFileMove2.setClickable(false);
            //FIleOk2.setAlpha(0.0f);
            //FIleOk2.setClickable(false);
            click = 0;
            filecontrol = 0;
            while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                mAdapter.remove(0);
            getDir(thiscurrentroot);
            mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용*/

        } else {
            if (root2.equals("")) {
                super.onBackPressed();
            } else {
                while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                    mAdapter.remove(0);
                getDir(root2);
                mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용*/
            }
        }
    }

    public static String getExtension(String fileStr) { // 확장자 뽑기
        return fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
    }

    public static String getSize(long size) {  //파일의 용량 얻기
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

    @Override

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        //res폴더의 menu플더안에 xml로 MenuItem추가하기.
        //mainmenu.xml 파일을 java 객체로 인플레이트(inflate)해서 menu객체에 추가
        getMenuInflater().inflate(R.menu.menu_listview, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position; //AdapterView안에서 ContextMenu를 보여즈는 항목의 위치
        final File file = new File(path.get(index));
        switch (item.getItemId()) {
            case R.id.delete: // 파일 삭제 클릭
                if (file.isDirectory()) {
                    deleteDir(path.get(index));
                    while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                        mAdapter.remove(0);
                    getDir(file.getParent());
                    mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용
                } else {
                    file.delete();
                    while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                        mAdapter.remove(0);
                    getDir(file.getParent());
                    mAdapter.notifyDataSetChanged(); // 어댑터 갱신위해 사용
                    Toast.makeText(this, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.move: // 파일 이동 클릭
            {
                while (mAdapter3.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                    mAdapter3.remove(0);
                getDirpath(root);
                AlertDialog.Builder alt_bld;
                final AlertDialog alert3;
                alt_bld = new AlertDialog.Builder(FIleManager.this);
                LayoutInflater inflater = FIleManager.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_listview, null);
                alt_bld.setView(dialogView);
                alert3 = alt_bld.create();
                alert3.setTitle("파일 이동 경로 선택");
                alert3.setMessage("파일을 이동 할 경로를 선택해 주세요.");
                ListView list = new ListView(FIleManager.this);


                ListView listv = (ListView) dialogView.findViewById(R.id.alertlistView);
                Button btok = (Button) dialogView.findViewById(R.id.alertbutton);
                Button btcancel = (Button) dialogView.findViewById(R.id.alertbutton2);
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
                        String name = file.getName();
                        String fpath = file.getPath();
                        moveFile(name, fpath, alertcurrentroot);
                        while (mAdapter.getCount() != 0)
                            mAdapter.remove(0);
                        getDir(alertcurrentroot);
                        Toast.makeText(getApplicationContext(), name + "파일이 " + alertcurrentroot + "로 이동 되었습니다.", Toast.LENGTH_SHORT).show();
                        alert3.dismiss();
                    }
                });
                btcancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert3.dismiss();
                    }
                });
                alert3.show();
            }
            break;
            case R.id.info: // 세부정보보기 클릭
                new AlertDialog.Builder(this)
                        .setTitle("[세부정보]")
                        .setMessage("이름 : " + file.getName() + "\n위치 : " + file.getPath() + "\n크기 : " + getSize(file.length()) + "\n수정 날짜 : " + sf.format(file.lastModified()))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                break;
            case R.id.copy: // 파일 복사 클릭
            {
                while (mAdapter3.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                    mAdapter3.remove(0);
                getDirpath(root);
                AlertDialog.Builder alt_bld;
                final AlertDialog alert3;
                alt_bld = new AlertDialog.Builder(FIleManager.this);
                LayoutInflater inflater = FIleManager.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_listview, null);
                alt_bld.setView(dialogView);
                alert3 = alt_bld.create();
                alert3.setTitle("파일 복사 경로 선택");
                alert3.setMessage("파일을 복사 할 경로를 선택해 주세요.");
                ListView list = new ListView(FIleManager.this);


                ListView listv = (ListView) dialogView.findViewById(R.id.alertlistView);
                Button btok = (Button) dialogView.findViewById(R.id.alertbutton);
                Button btcancel = (Button) dialogView.findViewById(R.id.alertbutton2);
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
                        String name = file.getName();
                        String fpath = file.getPath();
                        //copyFile(file, alertcurrentroot + "/" + name);
                        ft = new FileThread(file, alertcurrentroot + "/" + name);
                        fpt = new FileProgressThread(file, alertcurrentroot + "/" + name);
                        ft.start();
                        fpt.start();
                        alert3.dismiss();
                       /* while (mAdapter.getCount() != 0)
                            mAdapter.remove(0);
                        getDir(alertcurrentroot);*/

                        // Toast.makeText(getApplicationContext(), name + "파일이 " + alertcurrentroot + "로 복사 되었습니다.", Toast.LENGTH_SHORT).show();
                        filebar.show();


                    }
                });
                btcancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert3.dismiss();
                    }
                });
                alert3.show();
            }
            break;
            case R.id.rename: // 이름바꾸기 클릭
                MoveFile = path.get(index);
                MoveFileName = getExtension(file.getName());
                if (file.isDirectory()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("폴더 이름 바꾸기");
                    alert.setMessage("바꿀 이름을 입력해 주세요");
                    final EditText name = new EditText(this);
                    alert.setView(name);
                    alert.setPositiveButton("바꾸기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            rename = name.getText().toString();
                            moveFile(rename, MoveFile, thiscurrentroot);
                            while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                mAdapter.remove(0);
                            getDir(thiscurrentroot);
                        }
                    });
                    alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    alert.show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("이름 바꾸기");
                    alert.setMessage("바꿀 이름을 입력해 주세요(확장자 입력X)");
                    final EditText name = new EditText(this);
                    alert.setView(name);
                    alert.setPositiveButton("바꾸기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            rename = name.getText().toString() + "." + MoveFileName;
                            moveFile(rename, MoveFile, thiscurrentroot);
                            while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                                mAdapter.remove(0);
                            getDir(thiscurrentroot);
                            Toast.makeText(getApplicationContext(), name.getText() + "으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    alert.show();
                }
        }
        return true;
    }

    public int deleteDir(String a_path) {
        File file = new File(a_path);
        if (file.exists()) {
            File[] childFileList = file.listFiles();
            for (File childFile : childFileList) {
                if (childFile.isDirectory()) {
                    deleteDir(childFile.getAbsolutePath());
                } else {
                    childFile.delete();
                }
            }
            file.delete();
            return 1;
        } else {
            return 0;
        }
    }

    ;

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

    private File makeDirectory(String dir_path) {
        File dir = new File(dir_path);
        if (!dir.exists()) {
            dir.mkdirs();
            Log.i("디렉토리 생성", "!dir.exists");
            Toast.makeText(this, "디렉토리가 생성 되었습니다..", Toast.LENGTH_SHORT).show();
            while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                mAdapter.remove(0);
        } else {
            Log.i("디렉토리가 존재", "dir.exists");
            Toast.makeText(this, "이미 같은 디렉토리 명이 있습니다.", Toast.LENGTH_SHORT).show();
            while (mAdapter.getCount() != 0) // 어댑터의 크기가 0이 아닐 때 까지 어댑터의 내용 삭제
                mAdapter.remove(0);
        }

        return dir;
    }

    public void realert() {
        alert2 = new AlertDialog.Builder(this);
        alert2.setTitle("새 폴더 생성");
        alert2.setMessage("새로 만들 폴더의 이름을 입력해주세요.");
        final EditText name = new EditText(this);
        alert2.setView(name);
        alert2.setPositiveButton("생성", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                rename = name.getText().toString();
                makeDirectory(thiscurrentroot + "/" + rename);
                getDir(thiscurrentroot);
            }
        });
        alert2.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
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
            ViewHolder2 holder;
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

        public void addItemposition(Drawable icon, String mTitle, Boolean mcheck, int position) {
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mIcon = icon;
            addInfo.mTitle = mTitle;
            addInfo.mCheck = mcheck;

            mListData.add(position, addInfo);
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
                    BitmapFactory.decodeFile(file.getPath(), options);
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
                } else if (Et.equals("txt")) { //텍스트파일
                    mAdapter3.addItem(getResources().getDrawable(R.drawable.txtfile),
                            file.getName(),                                         // 파일 이름
                            false);                                    // 파일일 경우에는 파일의 크기
                } else if (Et.equals("zip")) { //알집
                    mAdapter3.addItem(getResources().getDrawable(R.drawable.zipfile),
                            file.getName(),                                         // 파일 이름
                            false);                                    // 파일일 경우에는 파일의 크기
                } else {
                    mAdapter3.addItem(getResources().getDrawable(R.drawable.defaultfile),
                            file.getName(),                                         // 파일 이름
                            false);                                // 파일일 경우에는 파일의 크기
                }
            }

        }
    }

    public class FileProgressThread extends Thread {
        File sfile;
        String cfilepath;
        float sfilesize, cfilesize;

        public FileProgressThread(File sfile, String cfilepath) {
            sfilesize = (float) sfile.length();
            this.cfilepath = cfilepath;
        }

        public void run() {
            while (per < 100) {
                File cfile = new File(cfilepath);
                cfilesize = (float) cfile.length();
                per = (int)((cfilesize/sfilesize)*100);

                fh.sendEmptyMessage(2);
            }
            fh.sendEmptyMessage(3);
            per =0;


        }


    }

    public class FileThread extends Thread {
        File f;
        String s;
        Message message;

        public FileThread(File file, String save_file) {
            f = file;
            s = save_file;
        }

        public void run() {
            copyFile(f, s);
            filecopysuccess = true;
            fh.sendEmptyMessage(1);


        }

    }

    class FileCopyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    while (mAdapter.getCount() != 0)
                        mAdapter.remove(0);
                    getDir(alertcurrentroot);
                    Toast.makeText(getApplicationContext(), "복사가 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                    filecopysuccess = false;
                    break;
                case 2:
                    filebar.setProgress(per);
                    break;
                case 3:
                    filebar.dismiss();
                    break;
                default:
                    break;
            }
        }

    }


}
