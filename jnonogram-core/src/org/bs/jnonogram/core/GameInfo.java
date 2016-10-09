package org.bs.jnonogram.core;

import javafx.beans.property.*;

import java.util.List;

public class GameInfo {
    private final SimpleObjectProperty<Nonogram> _nonogramProperty;
    private final GameTypeInfo _gameTypeInfo;
    private final StringProperty _titleProperty = new SimpleStringProperty(this, "Title");
    private final int _maxPlayers;

    public GameInfo(Nonogram nonogram, GameTypeInfo gameTypeInfo) {
        _maxPlayers = gameTypeInfo.getMaxPlayers();
        _nonogramProperty = new SimpleObjectProperty<>(nonogram);
        _gameTypeInfo = gameTypeInfo;
        _titleProperty.bind(gameTypeInfo.titleProperty());
    }

    public Nonogram getNonogram() {
        return _nonogramProperty.getValue();
    }

    public String getGameType() {return _gameTypeInfo.getGameType();}

    public int getMaxMoves() {
        return _gameTypeInfo.getMaxMoves();
    }

    public String getTitle() {
        return _titleProperty.getValue();
    }

    public List<PlayerInfo> getPlayersInformation() {
        return _gameTypeInfo.getPlayersInformation();
    }

    public ReadOnlyStringProperty titleProperty() {
        return _titleProperty;
    }

    public ObjectProperty<Nonogram> nonogramProperty() {
        return _nonogramProperty;
    }

    public int getMaxPlayers() {
        return _maxPlayers;
    }
}
