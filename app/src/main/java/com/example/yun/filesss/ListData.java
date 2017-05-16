package com.example.yun.filesss;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by Yun on 2016-03-22.
 */
public class ListData {
        /**
         * 리스트 정보를 담고 있을 객체 생성
         */

        // 아이콘
        public Drawable mIcon;

        // 제목
        public String mTitle;

        // 날짜
        public String mDate;

        // 용량
        public String mSize;

        //체크박스
        public Boolean mCheck;

        /**
         * 알파벳 이름으로 정렬
         */
        public static final Comparator<ListData> ALPHA_COMPARATOR = new Comparator<ListData>() {
            private final Collator sCollator = Collator.getInstance();

            @Override
            public int compare(ListData mListDate_1, ListData mListDate_2) {
                return sCollator.compare(mListDate_1.mTitle, mListDate_2.mTitle);
            }
        };
    }
