package org.bs.jnonogram.core;

import javafx.beans.property.*;

import java.util.Collections;
import java.util.List;

public class GameInfo {
    private final SimpleObjectProperty<Nonogram> _nonogramProperty;
    private final GameTypeInfo _gameTypeInfo;
    private final StringProperty _titleProperty = new SimpleStringProperty(this, "Title");

    public GameInfo(Nonogram nonogram, GameTypeInfo gameTypeInfo) {
        _nonogramProperty = new SimpleObjectProperty<>(nonogram);
        _gameTypeInfo = gameTypeInfo;
        _titleProperty.bind(gameTypeInfo.titleProperty());
    }

    public Nonogram getNonogram() {
        return _nonogramProperty.getValue();
    }

    public int getMaxMoves() {
        return _gameTypeInfo.getMaxMoves();
    }

    public String getTitle() {
        return _titleProperty.getValue();
    }

    public List<PlayerInfo> getPlayersInformation() {
        return Collections.unmodifiableList(_gameTypeInfo.getPlayersInformation());
    }

    public ReadOnlyStringProperty titleProperty() {
        return _titleProperty;
    }

    public ObjectProperty<Nonogram> nonogramProperty() {
        return _nonogramProperty;
    }
}
