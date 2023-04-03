package com.mkopaitems.soccer;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
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
        matchList = new ArrayList<>();
        loadMatches(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        fixturesAdapter = new FixturesAdapter(getContext(), matchList);

        builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("An error occurred while trying to fetch live scores. Do you want to exit ?");
        builder.setTitle("Error!");
        builder.setCancelable(false);
        builder.setPositiveButton("Try Again", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
            loadMatches(getContext());
        });
        builder.setNegativeButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> dialog.cancel());
        alertDialog = builder.create();

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
                                    jsonObject.getJSONObject("score").getJSONObject("fullTime").getString("home") + "-"+ jsonObject.getJSONObject("score").getJSONObject("fullTime").getString("away"));
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
    }
    private String getDate () {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
}