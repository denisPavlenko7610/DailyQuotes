package com.Denis7610.dailyquotes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String[] mQuotes;
    private ImageButton mNextButton;
    private ImageButton mPreviewButton;
    private TextView mQuoteCount;
    private TextView mTextQuote;
    private int mNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNextButton = (ImageButton) findViewById(R.id.nextButton);
        mPreviewButton = (ImageButton) findViewById(R.id.previewButton);
        mTextQuote = (TextView) findViewById(R.id.textQuote);
        mQuoteCount = (TextView) findViewById(R.id.quoteCount);

        mQuotes = getResources().getStringArray(R.array.quotes);
        List<String> list = Arrays.asList(mQuotes);
        Collections.shuffle(list);
        mQuotes = list.toArray(new String[list.size()]);

        mTextQuote.setText(mQuotes[mNumber]);
        mQuoteCount.setText(getString(R.string.quoteCount) + mQuotes.length);
    }

    public void nextQuote(View view) {
        if (mNumber != mQuotes.length) {
            mNumber++;
            mTextQuote.setText(mQuotes[mNumber]);
        } else {
            mNumber = 0;
        }
    }

    public void previewQuote(View view) {
        if (mNumber != 0) {
            mNumber--;
            mTextQuote.setText(mQuotes[mNumber]);
        }
    }
}