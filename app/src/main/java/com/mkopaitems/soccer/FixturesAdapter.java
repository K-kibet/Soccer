package com.mkopaitems.soccer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper;
import com.google.android.gms.ads.nativead.NativeAd;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FixturesAdapter extends RecyclerView.Adapter<FixturesAdapter.ViewHolder> {
    private  boolean bool = true;
    private Context context;
    private List<Match> matchList;
    private InterstitialAd mInterstitialAd;
    public FixturesAdapter(Context context, List<Match> list) {
        this.context = context;
        this.matchList = list;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fixture, parent, false);
        if(this.bool) {
            MobileAds.initialize(this.context);
            AdRequest adRequest = new AdRequest.Builder().build();
            Context context = this.context;
            InterstitialAd.load(context, context.getString(R.string.Interstitial_Ad_Unit), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            mInterstitialAd = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            super.onAdLoaded(interstitialAd);
                            FixturesAdapter.this.mInterstitialAd = interstitialAd;
                        }
                    });
            this.bool = false;
        }
        return new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final Match match = this.matchList.get(position);
        holder.textHome.setText(match.getHomeTeam());
        holder.textAway.setText(match.getAwayTeam());
        holder.textResults.setText(match.getScore());
        if(match.getScore() == "?-?") {
            holder.duration.setText("Upcoming");
        }
        Picasso.get().load(match.getCupImage()).placeholder(R.drawable.ic_football).error(R.drawable.ic_football).into(holder.imageCup);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FixturesAdapter.this.mInterstitialAd != null) {
                    FixturesAdapter.this.mInterstitialAd.show((Activity) FixturesAdapter.this.context);
                }
                Intent fixturesIntent = new Intent(FixturesAdapter.this.context, LivestreamActivity.class);
                FixturesAdapter.this.context.startActivity(fixturesIntent);
            }
        });

    }

    public int getItemCount() {
        return this.matchList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textHome;
        private TextView textAway;
        private TextView textResults;
        private TextView duration;
        private ImageView imageCup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textHome = itemView.findViewById(R.id.textHome);
            this.textAway = itemView.findViewById(R.id.textAway);
            this.textResults = itemView.findViewById(R.id.textResults);
            this.imageCup = itemView.findViewById(R.id.imageCup);
            this.duration = itemView.findViewById(R.id.duration);
        }
    }
}
