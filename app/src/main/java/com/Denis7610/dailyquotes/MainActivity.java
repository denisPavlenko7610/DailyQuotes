package com.Denis7610.dailyquotes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;

    private String[] mQuotes;
    private ImageButton mNextButton;
    private ImageButton mPreviewButton;
    private TextView mQuoteCount;
    private TextView mTextQuote;
    private ImageButton mShare;
    private String textToShare;
    private int mNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // add advertisement
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7173647303121367~7772462205");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //show advertisement when user close an app
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });

        //find resources
        mNextButton = (ImageButton) findViewById(R.id.nextButton);
        mPreviewButton = (ImageButton) findViewById(R.id.previewButton);
        mTextQuote = (TextView) findViewById(R.id.textQuote);
        mQuoteCount = (TextView) findViewById(R.id.quoteCount);
        mShare = (ImageButton) findViewById(R.id.share);

        //shuffle quotes
        mQuotes = getResources().getStringArray(R.array.quotes);
        List<String> list = Arrays.asList(mQuotes);
        Collections.shuffle(list);
        mQuotes = list.toArray(new String[list.size()]);

        //set first quote
        mTextQuote.setText(mQuotes[mNumber]);
        textToShare = mQuotes[mNumber];

        //set count of quotes
        mQuoteCount.setText(getString(R.string.quote_count) + mQuotes.length);
    }

    public void nextQuote(View view) {
        if (mNumber != mQuotes.length) {
            mNumber++;
            mTextQuote.setText(mQuotes[mNumber]);
            textToShare = mQuotes[mNumber];
        } else {
            mNumber = 0;
        }
    }

    public void previewQuote(View view) {
        if (mNumber != 0) {
            mNumber--;
            mTextQuote.setText(mQuotes[mNumber]);
            textToShare = mQuotes[mNumber];
        }
    }

    public void shareText(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        sendIntent.setType("text/plain");

        String title = "Поделиться";

        Intent chooser = Intent.createChooser(sendIntent, title);

        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }
}