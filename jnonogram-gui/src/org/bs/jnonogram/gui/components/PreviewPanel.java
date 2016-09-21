package org.bs.jnonogram.gui.components;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import org.bs.jnonogram.core.GameManager;
import org.bs.jnonogram.core.PlayerState;
import org.bs.jnonogram.gui.MainWindow;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PreviewPanel extends HBox implements Initializable {

    @FXML
    private Button startGameButton;

    @FXML
    private ListView<PlayerState> playerStateListView;

    @FXML
    private NonogramView nonogramPreviewView;

    public PreviewPanel(GameManager gamemanager, MainWindow mainWindow) {
        GameManager _gameManager = gamemanager;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "PreviewPanel.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.nonogramPreviewView.nonogramProperty().setValue(gamemanager.getNonogram());
        this.nonogramPreviewView.setDisable(true);
        playerStateListView.setItems(FXCollections.observableArrayList(gamemanager.getPlayerStates()));
        this.startGameButton.setOnAction(event -> mainWindow.startGame());

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
