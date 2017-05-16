package com.example.yun.filesss;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;
import java.util.prefs.PreferenceChangeListener;

/**
 * Created by Yun on 2016-05-07.
 */
public class Settings extends PreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener{
    Preference change;
    Preference name,repath,scan;
    SQLiteDatabase namedb;
    String tablename="namechange";

    Cursor cursor,cursor2,cursor3;
    String sql = "select * from "+ tablename;
    int count,count2,count3;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);

        change = (Preference)findPreference("autoChange"); // 파일이름변경 설정
        name = (Preference)findPreference("fileName"); // 변경 이름 설정
        repath = (Preference)findPreference("autofile");
        scan = (Preference)findPreference("scan");

        scan.setOnPreferenceClickListener(this);
        change.setOnPreferenceChangeListener(this);
        name.setOnPreferenceChangeListener(this);
        repath.setOnPreferenceChangeListener(this);

        namedb = openOrCreateDatabase("FilenameChange.db", MODE_WORLD_WRITEABLE, null); // db생성, db 이미 있을시에 오픈
        try {
            namedb.execSQL("create table "+tablename+" (name text)"); // namechange 테이블 생성
        }catch (Exception e) {}

        try {
            namedb.execSQL("create table changeset (sett integer)");
        }catch (Exception e){}

        try {
            namedb.execSQL("create table classifyset (sett integer)");
        }catch (Exception e){}


        cursor = namedb.rawQuery(sql, null);
        count = cursor.getCount(); // db안의 데이터 총 갯수를 확인
        for(int i = 0; i<count; i++){
            cursor.moveToNext();//
            name.setSummary(cursor.getString(0));
        }


        cursor2 = namedb.rawQuery("select * from changeset",null);
        count2 = cursor2.getCount();
        for(int i = 0; i<count2; i++){
            cursor2.moveToNext();
            if(cursor2.getInt(0) == 1) {
                change.setDefaultValue(true);
                Log.v("change참","참");
            }
            else {
                change.setDefaultValue(false);
                Log.v("false", "false");
            }
        }

        cursor3 = namedb.rawQuery("select * from classifyset",null);
        count3 = cursor3.getCount();
        for(int i = 0; i<count3; i++){
            cursor3.moveToNext();
            if(cursor3.getInt(0) == 1) {
                repath.setDefaultValue(true);
                Log.v("repath참","참");
            }
            else {
                repath.setDefaultValue(false);;
                Log.v("false", "false");
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        switch(preference.getKey()) {
            case "scan":
                Toast.makeText(this, "스캔중..", Toast.LENGTH_SHORT).show();

                break;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
            Intent i = new Intent(this,NameChange.class);
        switch(preference.getKey()){
            case "autoChange":
                if(newValue.equals(true)){
                    //Toast.makeText(getApplicationContext(),"True",Toast.LENGTH_SHORT).show();
                    if(count2 < 1){
                        namedb.beginTransaction(); //sql문을 실행하는 일정구간을 트랜잭션으로 묶어주겠다라는 의미
                        //트랜잭션 시작을 나타내는 메소드
                        try{
                            namedb.execSQL("insert into changeset (sett) values (1)"); // tablename 테이블에 데이터저장
                            Log.v("되냐","된다");
                            namedb.setTransactionSuccessful(); //트랜잭션으로 묶어준 일정영역의 SQL문들이 모두 성공적으로 끝났을 지정
                        }catch(Exception e){
                            e.printStackTrace();
                        }finally{
                            namedb.endTransaction();//트랜잭션을 끝내는 메소드.

                        }
                    }
                    else
                    {
                        namedb.beginTransaction(); //sql문을 실행하는 일정구간을 트랜잭션으로 묶어주겠다라는 의미
                        //트랜잭션 시작을 나타내는 메소드
                        try{
                            namedb.execSQL("update changeset set sett = 1"); // tablename 테이블에 데이터저장
                            namedb.setTransactionSuccessful(); //트랜잭션으로 묶어준 일정영역의 SQL문들이 모두 성공적으로 끝났을 지정
                        }catch(Exception e){
                            e.printStackTrace();
                        }finally{
                            namedb.endTransaction();//트랜잭션을 끝내는 메소드.
                        }
                    }
                    stopService(i);
                    startService(i);
                }
                else if(newValue.equals(false)){
                    //Toast.makeText(getApplicationContext(),"False",Toast.LENGTH_SHORT).show();
                    if(count2 < 1){
                        namedb.beginTransaction(); //sql문을 실행하는 일정구간을 트랜잭션으로 묶어주겠다라는 의미
                        //트랜잭션 시작을 나타내는 메소드
                        try{
                            namedb.execSQL("insert into changeset (sett) values (2)"); // tablename 테이블에 데이터저장4
                            namedb.setTransactionSuccessful(); //트랜잭션으로 묶어준 일정영역의 SQL문들이 모두 성공적으로 끝났을 지정
                        }catch(Exception e){
                            e.printStackTrace();

                        }finally{
                            namedb.endTransaction();//트랜잭션을 끝내는 메소드.

                        }
                    }
                    else
                    {
                        namedb.beginTransaction(); //sql문을 실행하는 일정구간을 트랜잭션으로 묶어주겠다라는 의미
                        //트랜잭션 시작을 나타내는 메소드
                        try{
                            namedb.execSQL("update changeset set sett = 2"); // tablename 테이블에 데이터저장
                            namedb.setTransactionSuccessful(); //트랜잭션으로 묶어준 일정영역의 SQL문들이 모두 성공적으로 끝났을 지정
                        }catch(Exception e){
                            e.printStackTrace();
                        }finally{
                            namedb.endTransaction();//트랜잭션을 끝내는 메소드.
                        }
                    }
                    stopService(i);

                }
                break;
            case "fileName":
                name.setSummary((String)newValue);
                if(count < 1){
                    namedb.beginTransaction(); //sql문을 실행하는 일정구간을 트랜잭션으로 묶어주겠다라는 의미
                    //트랜잭션 시작을 나타내는 메소드
                    try{
                        namedb.execSQL("insert into "+tablename+" (name) values ('"+ newValue + "')"); // tablename 테이블에 데이터저장
                        namedb.setTransactionSuccessful(); //트랜잭션으로 묶어준 일정영역의 SQL문들이 모두 성공적으로 끝났을 지정
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                        namedb.endTransaction();//트랜잭션을 끝내는 메소드.

                    }
                }
                else
                {
                    namedb.beginTransaction(); //sql문을 실행하는 일정구간을 트랜잭션으로 묶어주겠다라는 의미
                    //트랜잭션 시작을 나타내는 메소드
                    try{
                        namedb.execSQL("update "+tablename+" set name = '"+ newValue + "'"); // tablename 테이블에 데이터저장
                        namedb.setTransactionSuccessful(); //트랜잭션으로 묶어준 일정영역의 SQL문들이 모두 성공적으로 끝났을 지정
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                        namedb.endTransaction();//트랜잭션을 끝내는 메소드.
                    }
                }

                break;
            case "autofile":
                if(newValue.equals(true)){
                    //Toast.makeText(getApplicationContext(),"True",Toast.LENGTH_SHORT).show();
                    if(count3 < 1){
                        namedb.beginTransaction(); //sql문을 실행하는 일정구간을 트랜잭션으로 묶어주겠다라는 의미
                        //트랜잭션 시작을 나타내는 메소드
                        try{
                            namedb.execSQL("insert into classifyset (sett) values (1)"); // tablename 테이블에 데이터저장
                            namedb.setTransactionSuccessful(); //트랜잭션으로 묶어준 일정영역의 SQL문들이 모두 성공적으로 끝났을 지정
                        }catch(Exception e){
                            e.printStackTrace();
                        }finally{
                            namedb.endTransaction();//트랜잭션을 끝내는 메소드.

                        }
                    }
                    else
                    {
                        namedb.beginTransaction(); //sql문을 실행하는 일정구간을 트랜잭션으로 묶어주겠다라는 의미
                        //트랜잭션 시작을 나타내는 메소드
                        try{
                            namedb.execSQL("update classifyset set sett = 1"); // tablename 테이블에 데이터저장
                            Log.v("되냐", "된다");
                            namedb.setTransactionSuccessful(); //트랜잭션으로 묶어준 일정영역의 SQL문들이 모두 성공적으로 끝났을 지정
                        }catch(Exception e){
                            e.printStackTrace();
                        }finally{
                            namedb.endTransaction();//트랜잭션을 끝내는 메소드.
                        }
                    }
                    stopService(i);
                    startService(i);
                }
                else if(newValue.equals(false)){
                    //Toast.makeText(getApplicationContext(),"False",Toast.LENGTH_SHORT).show();
                    if(count3 < 1){
                        namedb.beginTransaction(); //sql문을 실행하는 일정구간을 트랜잭션으로 묶어주겠다라는 의미
                        //트랜잭션 시작을 나타내는 메소드
                        try{
                            namedb.execSQL("insert into classifyset (sett) values (2)"); // tablename 테이블에 데이터저장
                            namedb.setTransactionSuccessful(); //트랜잭션으로 묶어준 일정영역의 SQL문들이 모두 성공적으로 끝났을 지정
                        }catch(Exception e){
                            e.printStackTrace();

                        }finally{
                            namedb.endTransaction();//트랜잭션을 끝내는 메소드.

                        }
                    }
                    else
                    {
                        namedb.beginTransaction(); //sql문을 실행하는 일정구간을 트랜잭션으로 묶어주겠다라는 의미
                        //트랜잭션 시작을 나타내는 메소드
                        try{
                            namedb.execSQL("update classifyset set sett = 2"); // tablename 테이블에 데이터저장
                            namedb.setTransactionSuccessful(); //트랜잭션으로 묶어준 일정영역의 SQL문들이 모두 성공적으로 끝났을 지정
                        }catch(Exception e){
                            e.printStackTrace();
                        }finally{
                            namedb.endTransaction();//트랜잭션을 끝내는 메소드.
                        }
                    }
                    stopService(i);
                }
                break;
        }
        return true;
    }




}
