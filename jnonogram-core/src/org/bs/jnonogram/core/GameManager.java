package org.bs.jnonogram.core;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameManager {
    private final ArrayList<PlayerState> _playerStates;
    private final Nonogram _nonogram;
    private final int _maxMoves;
    private final String _title;
    private LocalDateTime _startTime;

    public GameManager(GameInfo info) {
        _startTime = LocalDateTime.now();
        _nonogram = info.getNonogram();
        _title = info.getTitle();
        _playerStates = new ArrayList<>();
        info.getPlayersInformation()
                .forEach(playerInfo -> _playerStates.add(new PlayerState(_nonogram, playerInfo)));
        _maxMoves = info.getMaxMoves();
    }

    public final List<PlayerState> getPlayerStates() {
        return Collections.unmodifiableList(_playerStates);
    }

    public final String getTitle() {
        return _title;
    }

    public final int getMaxMoves() {
        return _maxMoves;
    }

    public LocalDateTime getStartTime() {
        return _startTime;
    }
}
