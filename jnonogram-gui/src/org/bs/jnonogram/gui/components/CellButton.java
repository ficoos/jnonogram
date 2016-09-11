package org.bs.jnonogram.gui.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.bs.jnonogram.core.Nonogram;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CellButton extends GridPane implements Initializable {

    private final ObjectProperty<Nonogram.CellKind> _cellProperty;

    @FXML
    private Label contentLabel;

    public ObjectProperty<Nonogram.CellKind> cellProperty() {
        return _cellProperty;
    }
    
    private void onCellKindChanged(Object sender, Nonogram.CellKind oldValue, Nonogram.CellKind newValue) {
        contentLabel.getStyleClass().clear();
        if (newValue == null || newValue.equals(Nonogram.CellKind.Unknown)) {
            contentLabel.getStyleClass().add("grid-cell-unknown");
        } else if (newValue.equals(Nonogram.CellKind.Black)) {
            contentLabel.getStyleClass().add("grid-cell-black");
        } else {
            contentLabel.getStyleClass().add("grid-cell-white");
        }
    }

    public CellButton() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "CellButton.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        _cellProperty = new SimpleObjectProperty<>(null);
        _cellProperty.addListener(this::onCellKindChanged);
        contentLabel.setOnMousePressed(this::onMousePressed);
    }

    private void onMousePressed(MouseEvent event) {
        switch (event.getButton()) {
            case PRIMARY:
                _cellProperty.setValue(Nonogram.CellKind.Black);
                break;
            case MIDDLE:
                _cellProperty.setValue(Nonogram.CellKind.Unknown);
                break;
            case SECONDARY:
                _cellProperty.setValue(Nonogram.CellKind.White);
                break;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
