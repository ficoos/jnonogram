package org.bs.jnonogram.core;

public class PlayerInfo {
    private final String name;
    private final PlayerType playerType;
    private final int id;

    public PlayerInfo(int id, String mName, PlayerType playerType) {
        this.name = mName;
        this.playerType = playerType;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public int getId() {
        return id;
    }
}
