package com.mkopaitems.soccer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class LivestreamActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;
    private TemplateView nativeAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livestream);
        CardView cardLivestream = findViewById(R.id.cardLivestream);
        nativeAdView = findViewById(R.id.nativeAdView);

        cardLivestream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent errorIntent = new Intent(LivestreamActivity.this, ErrorActivity.class);
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(LivestreamActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            mInterstitialAd = null;
                            startActivity(errorIntent);
                        }
                    });
                } else {
                    startActivity(errorIntent);
                }
            }
        });

        MobileAds.initialize(this);
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,getString(R.string.Interstitial_Ad_Unit), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
        AdLoader adLoader = new AdLoader.Builder(this, getString(R.string.Native_Ad_Unit))
                .forNativeAd(nativeAdView::setNativeAd).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }
}