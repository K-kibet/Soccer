package com.mkopaitems.soccer;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixturesFragment extends Fragment {
    private RecyclerView recyclerView;
    private FixturesAdapter fixturesAdapter;
    private List<Match> matchList;
    private  String url;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fixtures, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        url = "https://api.football-data.org/v4/matches?status=SCHEDULED";
        recyclerView = view.findViewById(R.id.recyclerView);
        matchList = new ArrayList<>();
        loadMatches(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        fixturesAdapter = new FixturesAdapter(getContext(), matchList);

        builder = new AlertDialog.Builder(getContext());
        builder.setMessage("An error occurred while trying to fetch fixtures.Do you want to exit ?");
        builder.setTitle("Alert !");
        builder.setCancelable(false);
        builder.setPositiveButton("Try Again", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
            loadMatches(getContext());
        });
        builder.setNegativeButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });
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
                            if(jsonObject.getJSONObject("score").getJSONObject("fullTime").getString("home") == "null") {
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("X-Auth-Token", "6f290c7d3caf4bb69f07dacaf7273267");
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}