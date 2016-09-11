package org.bs.jnonogram.core;

import javafx.beans.property.*;

public class PlayerInfo {
    private final StringProperty _nameProperty = new SimpleStringProperty(this, "Name");
    private final ObjectProperty<PlayerType> _playerTypeProperty = new SimpleObjectProperty<>(this, "Player Type");
    private final IntegerProperty _idProperty = new SimpleIntegerProperty(this, "Id");

    public PlayerInfo(int id, String mName, PlayerType playerType) {
        this._nameProperty.setValue(mName);
        this._playerTypeProperty.setValue(playerType);
        this._idProperty.setValue(id);
    }

    public ReadOnlyStringProperty nameProperty() {
        return _nameProperty;
    }

    public ReadOnlyIntegerProperty idProperty() {
        return _idProperty;
    }

    public ReadOnlyObjectProperty<PlayerType> playerProperty() {
        return _playerTypeProperty;
    }

    public String getName() {
        return _nameProperty.getValue();
    }

    public PlayerType getPlayerType() {
        return _playerTypeProperty.getValue();
    }

    public int getId() {
        return _idProperty.getValue();
    }
}
