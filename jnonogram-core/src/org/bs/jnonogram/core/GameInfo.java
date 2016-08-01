package org.bs.jnonogram.core;

import java.util.ArrayList;

public class GameInfo {
    private final Nonogram _nonogram;
    private final int _maxMoves;
    private final String _title;
    private final ArrayList<PlayerInfo> _playerInfos;

    public GameInfo(Nonogram nonogram, int maxMoves, String title, ArrayList<PlayerInfo> playerInfos) {
        _nonogram = nonogram;
        _maxMoves = maxMoves;
        _title = title;
        _playerInfos = playerInfos;
    }

    public Nonogram getNonogram() {
        return _nonogram;
    }

    public int getMaxMoves() {
        return _maxMoves;
    }

    public String getTitle() {
        return _title;
    }

    public Iterable<PlayerInfo> getPlayerInfos() {
        return _playerInfos;
    }
}
