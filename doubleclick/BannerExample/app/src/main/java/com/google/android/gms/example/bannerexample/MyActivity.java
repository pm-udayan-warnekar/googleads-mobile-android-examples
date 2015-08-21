/*
 * Copyright (C) 2013 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.example.bannerexample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

/**
 * Main Activity. Inflates main activity xml and child fragments.
 */
public class MyActivity extends ActionBarActivity {

    private PublisherAdView mAdView;

    // Flags to check and handle PubMatic passback
    private boolean mIsAppEventPubmaticPbk = false;
    private boolean mIsAdLoadSuccessful = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        mAdView = (PublisherAdView) findViewById(R.id.ad_view);

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();

        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.i("BannerAdExample", "onAdLoaded()");
                mIsAdLoadSuccessful = true;
                loadNewAd();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);

                // Reset both flags
                mIsAdLoadSuccessful = false;
                mIsAppEventPubmaticPbk = false;
            }
        });

        mAdView.setAppEventListener(new AppEventListener() {
            @Override
            public void onAppEvent(String key, String value) {
                Log.i("BannerAdExample", "onAppEvent() Key:" + key + " Value:" + value);
                if (TextUtils.equals(key, "pmpbk") && TextUtils.equals(value, "1")) {
                    // App event pmpbk with value 1 indicates PubMatic passed back
                    // Set a pass back flag
                    mIsAppEventPubmaticPbk = true;
                    loadNewAd();
                }
            }
        });

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }

    /**
     * Create and send new ad request adding custom targetting with PubMatic passback information
     */
    private synchronized void loadNewAd() {
        // Send new request only when both flags are true
        if (mIsAdLoadSuccessful && mIsAppEventPubmaticPbk) {

            // First Reset both flags
            mIsAdLoadSuccessful = false;
            mIsAppEventPubmaticPbk = false;

            // Create new ad request with custom targeting
            PublisherAdRequest publisherAdRequest = new PublisherAdRequest.Builder().addCustomTargeting("pmpbk", "1").build();
            Log.i("BannerAdExample", "Sending new ad request with pmpbk=1");

            // Send new ad request
            mAdView.loadAd(publisherAdRequest);
        }
    }

    public void loadAdButtonClicked(View view) {
        // Send new ad request
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }


}
