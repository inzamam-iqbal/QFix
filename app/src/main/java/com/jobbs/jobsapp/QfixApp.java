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

    private static final String TWITTER_KEY = "ed2648ca3525157ca9d3c4c1526de1f467769e6d";
    private static final String TWITTER_SECRET = "48df61d5139abaae774bea795e0327d7de22bd9a6251878be58851e7dd02f51c";

    @Override
    public void onCreate() {
        super.onCreate();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
    }
}
