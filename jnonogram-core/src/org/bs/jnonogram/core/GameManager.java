package org.bs.jnonogram.core;

import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameManager {
    private final ArrayList<PlayerState> _playerStates;
    private int _NextTurnPlayerIndex = -1;
    private final int _multiPlayerMaxMovesPerTurn = 2;
    private SimpleIntegerProperty _numberOfTotalTurns = new SimpleIntegerProperty(0);
    private final Nonogram _nonogram;
    private final int _maxTurns;
    private final StringProperty _titleProperty = new SimpleStringProperty(this, "Title");
    private LocalDateTime _startTime;
    private NextTurnObserver _observer= new NextTurnObserver();
    private SimpleBooleanProperty _gameOverProperty = new SimpleBooleanProperty(false);
    private boolean _isMultiPlayerGame;

    public SimpleBooleanProperty getGameOverProperty() {return _gameOverProperty; }

    public SimpleIntegerProperty getCurrentTurnProperty() {return _numberOfTotalTurns; }


    public GameManager(GameInfo info) {
        _startTime = LocalDateTime.now();
        _nonogram = info.getNonogram();
        _titleProperty.bind(info.titleProperty());
        _playerStates = new ArrayList<>();
        _isMultiPlayerGame = info.getGameType().equalsIgnoreCase("MultiPlayer");
        int maxTurnMoves = _isMultiPlayerGame ? _multiPlayerMaxMovesPerTurn : 0;
        info.getPlayersInformation()
                .forEach(playerInfo -> _playerStates.add(new PlayerState(_nonogram.clone(), playerInfo, maxTurnMoves)));
        _playerStates.sort((a, b) -> a.getPlayerInfo().getId() - b.getPlayerInfo().getId());
        _maxTurns = info.getMaxMoves();
    }

    public final boolean isMultiPlayerGame() { return _isMultiPlayerGame; }

    public final void addNextTurnListener(NextTurnListener listener) { _observer.addListener(listener);}

    public final void removeNextTurnListener(NextTurnListener listener) { _observer.removeListener(listener);}

    public final void fireNextTurn() { _observer.notifyAll(_playerStates.get(_NextTurnPlayerIndex));}

    public final void NextTurn() {
        _NextTurnPlayerIndex = ( _NextTurnPlayerIndex + 1 ) % _playerStates.size();
        if (_NextTurnPlayerIndex == 0) {
            if(_numberOfTotalTurns.getValue() + 1 > _maxTurns && _maxTurns > 0){
                _gameOverProperty.set(true);
            }
            else {
                _numberOfTotalTurns.setValue(_numberOfTotalTurns.getValue() + 1);
            }
        }

        if(!_gameOverProperty.getValue()) {
            fireNextTurn();

            PlayerState currentPlayer = _playerStates.get(_NextTurnPlayerIndex);
            if(currentPlayer.getPlayerInfo().getPlayerType() == PlayerType.Computer)
            {
               currentPlayer.makeComputerTurn();
            }
        }

        checkIfPlayerFinishedBoard();
    }

    public String getSummaryString() {
        String summary = "";
        String winner = "";
        for (PlayerState playerState : _playerStates)
        {

            summary += String.format("Name :%s\nMove count: %d\nUndo count: %d\nCurrent score: %d\nType: %s\n\n",
                    playerState.getPlayerInfo().getName(),
                    playerState.getMoveCount(),
                    playerState.getUndoCount(),
                    playerState.getScore(),
                    playerState.getPlayerInfo().getPlayerType());

            if(playerState.getScore() == 100)
            {
                winner = playerState.getPlayerInfo().getName() + " Is The Winner!!";
            }
        }

        LocalDateTime now = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(this.getStartTime(), now);
        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;

        summary += String.format("Time since game started: %d:%d:%d\n",
                hours,
                minutes,
                seconds);

        return summary + winner;
    }

    public final void checkIfPlayerFinishedBoard(){
        for (PlayerState playerState : _playerStates) {
            if (playerState.getScore() == 100) {
                _gameOverProperty.setValue(true);
            }
        }
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
        return _maxTurns;
    }

    public final Nonogram getNonogram() {
        return _nonogram;
    }

    public LocalDateTime getStartTime() {
        return _startTime;
    }
}
