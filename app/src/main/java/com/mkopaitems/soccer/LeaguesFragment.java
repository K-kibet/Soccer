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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaguesFragment extends Fragment {
    private RecyclerView recyclerView;
    LeaguesAdapter leaguesAdapter;
    private List<League> leagueList;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leagues, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        loadCompetitions(getContext());
        leagueList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        leaguesAdapter = new LeaguesAdapter(getContext(), leagueList);

        builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("An error occurred while trying to fetch leagues. Do you want to exit ?");
        builder.setTitle("Error!");
        builder.setCancelable(false);
        builder.setPositiveButton("Try Again", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
            loadCompetitions(getContext());
        });
        builder.setNegativeButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> dialog.cancel());
        alertDialog = builder.create();
    }

    private void loadCompetitions (Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = "https://api.football-data.org/v4/competitions";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("competitions");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            League item = new League(jsonArray.getJSONObject(i).getJSONObject("area").getString("name"), jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getJSONObject("area").getString("id"), jsonArray.getJSONObject(i).getString("emblem"));
                            leagueList.add(item);
                        }
                        recyclerView.setAdapter(leaguesAdapter);
                    } catch (JSONException e) {
                        alertDialog.show();
                    }
                }, error -> alertDialog.show()){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("X-Auth-Token", "6f290c7d3caf4bb69f07dacaf7273267");
                params.put("Accept", "application/json");
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}