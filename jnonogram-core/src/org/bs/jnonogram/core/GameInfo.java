package org.bs.jnonogram.core;

import java.util.Collections;
import java.util.List;

public class GameInfo {
    private final Nonogram _nonogram;
    private final GameTypeInfo _gameTypeInfo;

    public GameInfo(Nonogram nonogram, GameTypeInfo gameTypeInfo) {
        _nonogram = nonogram;
        _gameTypeInfo = gameTypeInfo;
    }

    public Nonogram getNonogram() {
        return _nonogram;
    }

    public int getMaxMoves() {
        return _gameTypeInfo.getMaxMoves();
    }

    public String getTitle() {
        return _gameTypeInfo.getTitle();
    }

    public List<PlayerInfo> getPlayersInformation() {
        return Collections.unmodifiableList(_gameTypeInfo.getPlayersInformation());
    }
}
