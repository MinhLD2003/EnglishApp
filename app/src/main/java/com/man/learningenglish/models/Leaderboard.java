package com.man.learningenglish.models;

public class Leaderboard {
    private String name;
    private long time;
    private int rank;
    private String uid;

    public Leaderboard() {
        // Required empty constructor for Firestore
    }

    public Leaderboard(String name, long time) {
        this.name = name;
        this.time = time;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
}