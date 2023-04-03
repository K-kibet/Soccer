package com.mkopaitems.soccer;

public class League {
    private final String leagueName;
    private final String leagueArena;
    private final String leagueId;
    private final String leagueImage;

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
    public String getLeagueImage() {
        return leagueImage;
    }
}
