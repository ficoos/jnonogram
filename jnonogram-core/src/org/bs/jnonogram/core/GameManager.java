package org.bs.jnonogram.core;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameManager {
    private final ArrayList<PlayerState> _playerStates;
    private final Nonogram _nonogram;
    private final int _maxMoves;
    private final StringProperty _titleProperty = new SimpleStringProperty(this, "Title");
    private LocalDateTime _startTime;

    public GameManager(GameInfo info) {
        _startTime = LocalDateTime.now();
        _nonogram = info.getNonogram();
        _titleProperty.bind(info.titleProperty());
        _playerStates = new ArrayList<>();
        info.getPlayersInformation()
                .forEach(playerInfo -> _playerStates.add(new PlayerState(_nonogram.clone(), playerInfo)));
        _maxMoves = info.getMaxMoves();
    }

    public final List<PlayerState> getPlayerStates() {
        return Collections.unmodifiableList(_playerStates);
    }

    public final ReadOnlyStringProperty titleProperty() {
        return _titleProperty;
    }

    public final String getTitle() {
        return _titleProperty.getValue();
    }

    public final int getMaxMoves() {
        return _maxMoves;
    }

    public LocalDateTime getStartTime() {
        return _startTime;
    }
}
