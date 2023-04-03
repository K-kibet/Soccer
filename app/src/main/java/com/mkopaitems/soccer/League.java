package com.mkopaitems.soccer;

public class League {
    private String leagueName;
    private String leagueArena;
    private String leagueId;
    private String leagueImage;

    public League(String name, String arena, String id, String leagueImage) {
        this.leagueName = name;
        this.leagueArena = arena;
        this.leagueId = id;
        this.leagueImage = leagueImage;
    }
    public String getLeagueName() {
        return leagueName;
    }

    public String getLeagueArena() {
        return leagueArena;
    }

    public String getLeagueId() {
        return leagueId;
    }
    public String getLeagueImage() {
        return leagueImage;
    }
}
