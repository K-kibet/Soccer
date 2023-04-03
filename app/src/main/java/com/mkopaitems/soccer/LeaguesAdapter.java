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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LeaguesAdapter extends RecyclerView.Adapter<LeaguesAdapter.ViewHolder> {
    private  boolean bool = true;
    private Context context;
    private List<League> competitionList;
    private InterstitialAd mInterstitialAd;

    public LeaguesAdapter(Context context, List<League> list) {
        this.context = context;
        this.competitionList = list;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_competition, parent, false);
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
                            LeaguesAdapter.this.mInterstitialAd = interstitialAd;
                        }
                    });
            this.bool = false;
        }
        return new ViewHolder (view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final League league = this.competitionList.get(position);
        holder.leagueName.setText(league.getLeagueName());
        holder.leagueArena.setText(league.getLeagueArena());
        Picasso.get().load(league.getLeagueImage()).placeholder(R.drawable.ic_epl_banner).error(R.drawable.ic_epl_banner).into(holder.competitionImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LeaguesAdapter.this.mInterstitialAd != null) {
                    LeaguesAdapter.this.mInterstitialAd.show((Activity) LeaguesAdapter.this.context);
                }
                Intent fixturesIntent = new Intent(LeaguesAdapter.this.context, LivestreamActivity.class);
                LeaguesAdapter.this.context.startActivity(fixturesIntent);
            }
        });

    }

    public int getItemCount() {
        return this.competitionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView leagueName;
        private TextView leagueArena;
        private ImageView competitionImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.leagueName = itemView.findViewById(R.id.competitionName);
            this.leagueArena = itemView.findViewById(R.id.competitionArena);
            this.competitionImage = itemView.findViewById(R.id.competitionImage);
        }
    }
}