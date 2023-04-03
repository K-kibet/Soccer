package com.mkopaitems.soccer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {
    InterstitialAd mInterstitialAd;
    CardView cardLivestream;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView cardLivestream = view.findViewById(R.id.cardLivestream);

        cardLivestream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent livestreamIntent = new Intent(getContext(), LivestreamActivity.class);
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(getActivity());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            mInterstitialAd = null;
                            startActivity(livestreamIntent);
                        }
                    });
                } else {
                    startActivity(livestreamIntent);
                }
            }
        });
    }
}