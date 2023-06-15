package com.mkopaitems.soccer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LivescoresFragment extends Fragment {
    private RecyclerView recyclerView;
    private FixturesAdapter fixturesAdapter;
    private List<Match> matchList;
    private  String url;
    private AlertDialog alertDialog;
    private FrameLayout adViewContainer;
    private TextView textEmpty;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_livescores, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String date = getDate();
        url = "https://api.football-data.org/v4/matches?date=" + date;
        recyclerView = view.findViewById(R.id.recyclerView);
        adViewContainer =view.findViewById(R.id.adViewContainer);
        textEmpty = view.findViewById(R.id.textEmpty);

        matchList = new ArrayList<>();
        loadMatches(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        fixturesAdapter = new FixturesAdapter(getContext(), matchList);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("An error occurred while trying to fetch live scores. Do you want to exit ?");
        builder.setTitle("Error!");
        builder.setCancelable(false);
        builder.setPositiveButton("Try Again", (dialog, which) -> {
            dialog.cancel();
            loadMatches(getContext());
        });
        builder.setNegativeButton("Ok", (dialog, which) -> {
            dialog.cancel();
            textEmpty.setVisibility(View.VISIBLE);
        });
        alertDialog = builder.create();
        adViewContainer.post(this::LoadBanner);
    }

    private void loadMatches (Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("matches");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Match match = new Match(jsonObject.getJSONObject("homeTeam").getString("shortName"),
                                    jsonObject.getJSONObject("awayTeam").getString("shortName"),
                                    jsonObject.getJSONObject("competition").getString("emblem"),
                                    jsonObject.getJSONObject("score").getJSONObject("fullTime").getString("home") + "-"+ jsonObject.getJSONObject("score").getJSONObject("fullTime").getString("away"),
                                    jsonObject.getString("utcDate"));
                            if(jsonObject.getJSONObject("score").getJSONObject("fullTime").getString("home").equals("null")) {
                                match.setScore("?-?");
                            }
                            matchList.add(match);
                        }
                        recyclerView.setAdapter(fixturesAdapter);

                    } catch (JSONException e) {
                        alertDialog.show();
                    }
                }, error -> alertDialog.show()){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("X-Auth-Token", "6f290c7d3caf4bb69f07dacaf7273267");
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
        new Handler().postDelayed(() -> {
            if(matchList.isEmpty()) {
                textEmpty.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }
    private String getDate () {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
    private void LoadBanner() {
        AdView adView = new AdView(requireContext());
        adView.setAdUnitId(getString(R.string.Banner_Ad_Unit));
        adViewContainer.removeAllViews();
        adViewContainer.addView(adView);

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getContext(), adWidth);
    }
}