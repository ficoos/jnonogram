package org.bs.jnonogram.gui.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
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

        this.nonogramView.nonogramProperty().bindBidirectional(state.nonogramProperty());
        this.moveHistoryListView.itemsProperty().bind(state.actionListProperty());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
