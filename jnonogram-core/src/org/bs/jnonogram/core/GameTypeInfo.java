package org.bs.jnonogram.core;

import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;

import java.util.ArrayList;
import java.util.List;

public class GameTypeInfo {
    private int maxMoves;
    private int _maxPlayers;
    private String _gameType;

    private final StringProperty _title = new StringPropertyBase() {
        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "Title";
        }
    };

    public StringProperty titleProperty() {
        return _title;
    }

    private ArrayList<PlayerInfo> playersInformation;

    public GameTypeInfo() {
        maxMoves = -1;
        this._title.setValue("");
        playersInformation = new ArrayList<>();
    }

    public int getMaxMoves() {
        return maxMoves;
    }

    public void setMaxMoves(int maxMoves) {
        this.maxMoves = maxMoves;
    }

    public String getGameType() { return _gameType; }

    public void setGameType(String gameType) {  _gameType = gameType; }

    public String getTitle() {
        return _title.getValue();
    }

    public void setTitle(String title) {
        this._title.setValue(title);
    }

    public List<PlayerInfo> getPlayersInformation() {
        return playersInformation;
    }

    public void setMaxPlayers(int maxPlayers) {
        this._maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return _maxPlayers;
    }
}
