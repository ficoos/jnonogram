package org.bs.jnonogram.gui.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.bs.jnonogram.core.GameManager;
import org.bs.jnonogram.core.Nonogram;
import org.bs.jnonogram.core.PlayerState;
import org.bs.jnonogram.core.PlayerType;
import org.bs.jnonogram.util.UndoableAction;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerPane extends HBox implements Initializable {

    private PlayerState _state;

    @FXML
    private Button applyMoveButton;

    @FXML
    private Button passTurnButton;

    @FXML
    private Button undoButton;

    @FXML
    private Button quitGame;

    @FXML
    private ListView<UndoableAction> moveHistoryListView;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label turnCountLabel;

    @FXML
    private NonogramView nonogramView;

    @FXML
    private RadioButton blackRadioButton;

    @FXML
    private RadioButton whiteRadioButton;

    @FXML
    private RadioButton unknownRadioButton;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private CheckBox updateSatisfiedConstraintsCheckbox;


    public PlayerPane(PlayerState state, GameManager manager) {
        this._state = state;
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
        blackRadioButton.setToggleGroup(toggleGroup);
        unknownRadioButton.setToggleGroup(toggleGroup);
        whiteRadioButton.setToggleGroup(toggleGroup);

        if(state.getPlayerInfo().getPlayerType() == PlayerType.Computer)
        {
            disableGameControl();
        }
        
       addControlsEvents(manager);
    }

    private final void addControlsEvents(GameManager manager){
        boolean k_updateBoard = true;
        this.nonogramView.nonogramProperty().bindBidirectional(_state.nonogramProperty());
        this.moveHistoryListView.itemsProperty().bind(_state.actionListProperty());
        updateSatisfiedConstraintsCheckbox.selectedProperty().bindBidirectional((nonogramView.showSatisfiedBlockProperty()));
        updateSatisfiedConstraintsCheckbox.setOnAction(event -> updateControlsAfterAction(k_updateBoard));

        if(manager.isMultiPlayerGame())
        {
            turnCountLabel.textProperty().bind(manager.getCurrentTurnProperty().asString().concat("/"+manager.getMaxMoves()));
        }
        else {
            turnCountLabel.setText("Not Available");
        }

        this.applyMoveButton.setOnAction(event -> {
            _state.applyMove(nonogramView.selectedCells(), getSelectedCellTypeTarget(), getMoveDescription());
            updateControlsAfterAction(k_updateBoard);
            manager.checkIfPlayerFinishedBoard();
        });

        this.undoButton.setOnAction(event -> {
            _state.undoAction();
            updateControlsAfterAction(k_updateBoard);
        });

        this.passTurnButton.setOnAction(event -> {
            if(!manager.getGameOverProperty().getValue()) {
                _state.resetTurnMoveCount();
                updateControlsAfterAction(!k_updateBoard);
            }
            manager.NextTurn();
        });

        _state.finishedComputerMoveAddListerner(playerState -> {
            manager.checkIfPlayerFinishedBoard();
            if(!manager.getGameOverProperty().getValue()) {
                _state.resetTurnMoveCount();
                updateControlsAfterAction(k_updateBoard);
            }
            manager.NextTurn();
        });

        quitGame.setOnAction(event -> manager.getGameOverProperty().setValue(true));
    }

    private final void updateControlsAfterAction(boolean updateBoard)
    {
        if(updateBoard) {
            nonogramView.updateBoard(_state.nonogramProperty().getValue());
        }
        this.scoreLabel.setText(Integer.toString(_state.getScore()));
        descriptionTextField.clear();
        if(_state.isMaxTurnMovesReached()){
            applyMoveButton.setDisable(true);
        }
        else if(_state.getPlayerInfo().getPlayerType() != PlayerType.Computer){
            applyMoveButton.setDisable(false);
        }

    }

    private final String getMoveDescription()
    {
        return descriptionTextField.getText();
    }

    private final Nonogram.CellKind getSelectedCellTypeTarget()
    {
        if(whiteRadioButton.isSelected()){
            return Nonogram.CellKind.White;
        }
        else if (blackRadioButton.isSelected()) {
            return Nonogram.CellKind.Black;
        } else{
            return Nonogram.CellKind.Unknown;
        }
    }

    public void disablePassTurnButton()
    {
        passTurnButton.setDisable(true);
    }

    public void disableGameControl(){
        disablePassTurnButton();
        applyMoveButton.setDisable(true);
        nonogramView.setDisable(true);
        undoButton.setDisable(true);
        descriptionTextField.setDisable(true);
        blackRadioButton.setDisable(true);
        whiteRadioButton.setDisable(true);
        unknownRadioButton.setDisable(true);
        quitGame.setDisable(true);
        updateSatisfiedConstraintsCheckbox.setDisable(true);

    }


    public PlayerState getPlayerState() { return _state;}

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
