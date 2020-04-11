package com.Denis7610.dailyquotes;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Denis7610.dailyquotes.BroadcastReceiver.AlarmReceiver;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //ES policy
    private ConsentForm form;
    //ADMob
    private InterstitialAd mInterstitialAd;

    private String[] mQuotes;
    private ImageButton mNextButton;
    private ImageButton mPreviewButton;
    private TextView mQuoteCount;
    private TextView mTextQuote;
    private ImageButton mShare;
    private String textToShare;
    private int mNumber = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ESPolicy();

        AddAdvertisement();

        registerAlarm();

        //find resources
        mNextButton = findViewById(R.id.nextButton);
        mPreviewButton = findViewById(R.id.previewButton);
        mTextQuote = findViewById(R.id.textQuote);
        mQuoteCount = findViewById(R.id.quoteCount);
        mShare = findViewById(R.id.share);

        //shuffle quotes
        shuffleQuotes();
        setQuote();

        //set count of quotes
        mQuoteCount.setText(getString(R.string.quote_count) + mQuotes.length);
    }

    private void registerAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 10); // hour
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void setQuote() {
        //set first quote
        mTextQuote.setText(mQuotes[mNumber]);
        textToShare = mQuotes[mNumber];
    }

    private void AddAdvertisement() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7173647303121367~7772462205");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //show advertisement when user close an app
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });
    }

    private void ESPolicy() {
        ConsentInformation consentInformation = ConsentInformation.getInstance(getApplicationContext());
        String[] publisherIds = {"pub-7173647303121367"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                boolean inEEA = ConsentInformation.getInstance(getApplicationContext()).isRequestLocationInEeaOrUnknown();

                if (inEEA) {
                    if (consentStatus == consentStatus.PERSONALIZED) {
                        //no code
                    } else if (consentStatus == consentStatus.NON_PERSONALIZED) {
                        Bundle extras = new Bundle();
                        extras.putString("npa", "1");

                        AdRequest request = new AdRequest.Builder()
                                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                .build();
                    } else { //start code form

                        URL privacyUrl = null;
                        try {
                            privacyUrl = new URL("https://wolfprogrammer.000webhostapp.com/");
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            // Handle error.
                        }
                        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
                                .withListener(new ConsentFormListener() {
                                    @Override
                                    public void onConsentFormLoaded() {
                                        // Consent form loaded successfully.
                                        form.show();
                                    }

                                    @Override
                                    public void onConsentFormOpened() {
                                        // Consent form was displayed.
                                    }

                                    @Override
                                    public void onConsentFormClosed(
                                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                                        // Consent form was closed.
                                        if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
                                            Bundle extras = new Bundle();
                                            extras.putString("npa", "1");

                                            AdRequest request = new AdRequest.Builder()
                                                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                                    .build();
                                        }
                                    }

                                    @Override
                                    public void onConsentFormError(String errorDescription) {
                                        // Consent form error.
                                    }
                                })
                                .withPersonalizedAdsOption()
                                .withNonPersonalizedAdsOption()
                                .build();
                        form.load();
                    } //end code form
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });
    }

    private void shuffleQuotes() {
        mQuotes = getResources().getStringArray(R.array.quotes);
        List<String> list = Arrays.asList(mQuotes);
        Collections.shuffle(list);
        mQuotes = list.toArray(new String[list.size()]);
    }

    public void nextQuote(View view) {
        if (mNumber != mQuotes.length) {
            mNumber++;
            setQuote();

            //set text color
            if ((mNumber > 5) && (mNumber <= 10)) {
                mTextQuote.setTextColor(Color.parseColor("#7942bc")); //violet
            } else if ((mNumber > 10) && (mNumber <= 15)) {
                mTextQuote.setTextColor(Color.parseColor("#6dcc07")); //green
            } else if ((mNumber > 15) && (mNumber <= 30)) {
                mTextQuote.setTextColor(Color.parseColor("#ae283f")); //red
            } else {
                mTextQuote.setTextColor(Color.parseColor("#00BCD4")); //blue
            }

        } else {
            mNumber = 0;
        }
    }

    public void previewQuote(View view) {
        if (mNumber != 0) {
            mNumber--;
            setQuote();
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