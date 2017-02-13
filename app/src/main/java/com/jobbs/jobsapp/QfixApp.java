package com.jobbs.jobsapp;

import android.app.Application;

import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Inzimam on 2/6/2017.
 */
public class QfixApp extends Application {

    private static final String TWITTER_KEY = "NRd7fXlE8ZJBRC2Y0CpYjNzFy";
    private static final String TWITTER_SECRET = "kfsPnMCO0SI1AmgRCNnmNhuIF8XaNtDSWv1Lkz5SuhnMZZbQPc";

    @Override
    public void onCreate() {
        super.onCreate();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
    }
}
