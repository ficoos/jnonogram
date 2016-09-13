package org.bs.jnonogram.gui.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.bs.jnonogram.core.Nonogram;
import org.bs.jnonogram.core.PlayerState;
import org.bs.jnonogram.util.UndoableAction;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerPane extends HBox implements Initializable {

    private PlayerState state;

    @FXML
    private Button finishTurnButton;

    @FXML
    private Button undoButton;

    @FXML
    private ListView<UndoableAction> moveHistoryListView;

    @FXML
    private Label scoreLabel;

    @FXML
    private NonogramView nonogramView;

    @FXML
    private RadioButton radioBlack;

    @FXML
    private RadioButton radioWhite;

    @FXML
    private RadioButton radioUnknown;

    @FXML
    private TextField textFieldDescription;

    public PlayerPane(PlayerState state) {
        this.state = state;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "PlayerPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ToggleGroup toggleGroup = new ToggleGroup();
        radioBlack.setToggleGroup(toggleGroup);
        radioUnknown.setToggleGroup(toggleGroup);
        radioWhite.setToggleGroup(toggleGroup);

        this.nonogramView.nonogramProperty().bindBidirectional(state.nonogramProperty());
        this.moveHistoryListView.itemsProperty().bind(state.actionListProperty());
        this.finishTurnButton.setOnAction(event -> {
            state.applyMove(nonogramView.selectedCells(), getCellTypeTarget(), getComment());
            updateFieldsAfterAction();
        });

        this.undoButton.setOnAction(event -> {
            state.undoAction();
            updateFieldsAfterAction();
        });
    }

    private final void updateFieldsAfterAction()
    {
        nonogramView.setNonogram(state.nonogramProperty().getValue());
        this.scoreLabel.setText(Integer.toString(state.getScore()));
        textFieldDescription.clear();
    }

    private final String getComment()
    {
        return textFieldDescription.getText();
    }

    private final Nonogram.CellKind getCellTypeTarget()
    {
        if(radioWhite.isSelected()){
            return Nonogram.CellKind.White;
        }
        else if (radioBlack.isSelected()) {
            return Nonogram.CellKind.Black;
        } else{
            return Nonogram.CellKind.Unknown;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
