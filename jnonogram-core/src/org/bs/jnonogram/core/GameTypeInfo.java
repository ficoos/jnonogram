package org.bs.jnonogram.core;

import java.util.ArrayList;
import java.util.List;

public class GameTypeInfo {
    private int maxMoves;
    private String title;
    private ArrayList<PlayerInfo> playersInformation;

    public GameTypeInfo() {
        maxMoves = -1;
        title = "";
        playersInformation = new ArrayList<>();
    }

    public int getMaxMoves() {
        return maxMoves;
    }

    public void setMaxMoves(int maxMoves) {
        this.maxMoves = maxMoves;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PlayerInfo> getPlayersInformation() {
        return playersInformation;
    }
}
