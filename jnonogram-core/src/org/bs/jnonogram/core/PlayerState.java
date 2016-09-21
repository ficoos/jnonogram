package org.bs.jnonogram.core;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableListValue;
import org.bs.jnonogram.util.UndoRedoStack;
import org.bs.jnonogram.util.UndoableAction;

import java.util.*;

public class PlayerState {
    private final ObjectProperty<Nonogram> _nonogramProperty = new SimpleObjectProperty<>(this, "Nonogram");
    private final UndoRedoStack<UndoableAction> _undoRedoStack;
    private final PlayerInfo _playerInfo;
    private final NextTurnObserver _finishedComputerTurnObserver = new NextTurnObserver();
    private int _moveCount;
    private int _undoCount;
    private int _moveCountInCurrentTurn;
    private final int _maxTurns;
    private Object moveList;

    public PlayerState(Nonogram nonogram, PlayerInfo playerInfo,int maxTurns) {
        _moveCount = 0;
        _undoCount = 0;
        _moveCountInCurrentTurn = 0;
        _nonogramProperty.setValue(nonogram);
        _playerInfo = playerInfo;
        _undoRedoStack = new UndoRedoStack<>();
        _maxTurns = maxTurns;
    }

    public final void undoAction() {
        if (_undoRedoStack.canUndo()) {
            _undoRedoStack.undoAction();
            _undoCount++;
            _moveCount--;
            if(_moveCountInCurrentTurn > 0){
                _moveCountInCurrentTurn--;
            }
        }
    }

    public PlayerType getPlayerType() { return _playerInfo.getPlayerType(); }
    public final void redoAction() { _undoRedoStack.redoAction(); }

    public final ReadOnlyNonogram getNonogram() {
        return _nonogramProperty.getValue();
    }

    public PlayerInfo getPlayerInfo() {
        return _playerInfo;
    }

    public void applyMove(NonogramMove move) {
        _undoRedoStack.pushAction(new NonogramMoveAction(_nonogramProperty.getValue(), move));
        _moveCount++;
        _moveCountInCurrentTurn++;

    }

    public void applyMove(List<CellPosition> cellPositions, Nonogram.CellKind cellTypeTarget, String comment) {
        if(!isMaxTurnMovesReached()) {
            List<NonogramMoveAction>  moveactions = new LinkedList<>();
            for(CellPosition cellPosition : cellPositions){
                NonogramMove move = new NonogramMove();
                move.setOrigin(cellPosition);
                move.setTargetKind(cellTypeTarget);
                move.setSize(1);
                move.setOrientation(SliceOrientation.Row);
                moveactions.add(new NonogramMoveAction(_nonogramProperty.getValue(), move));
            }
            _undoRedoStack.pushAction(new NonogramMoveActionComposite(moveactions, comment, cellTypeTarget));
            _moveCount++;
            _moveCountInCurrentTurn++;
        }
    }

    public void finishedComputerMoveAddListerner (NextTurnListener listener) { _finishedComputerTurnObserver.addListener(listener);}

    public void finishedComputerMoveRemoveListerner (NextTurnListener listener) { _finishedComputerTurnObserver.removeListener(listener);}

    public boolean isMaxTurnMovesReached () {
        return _moveCountInCurrentTurn >= _maxTurns && _maxTurns > 0;
    }

    public int getMoveCount() {
        return _moveCount;
    }

    public int getUndoCount() {
        return _undoCount;
    }

    public int getScore() {
        return _nonogramProperty.getValue().getScore();
    }

    public List<UndoableAction> getActionList() {
        return _undoRedoStack.getActionStack();
    }

    public ObjectProperty<Nonogram> nonogramProperty() {
        return _nonogramProperty;
    }

    public ObservableListValue<UndoableAction> actionListProperty() {
        return _undoRedoStack.actionListProperty();
    }

    public void resetTurnMoveCount() {
        _moveCountInCurrentTurn = 0;
    }

    public void makeComputerTurn(){
        Random rand = new Random();
        int numberOfMove  =  rand.nextInt(_maxTurns) + 1;

        for (int i = 0; i < numberOfMove; i++) {
            makeComputerMove();
        }

        _finishedComputerTurnObserver.notifyAll(this);
    }

    public void makeComputerMove(){
        Random rand = new Random();
        int scoreBeforeMove = getScore();
        Set<CellPosition> movePositions = new HashSet<>();
        int columnsCount = _nonogramProperty.getValue().getColumnCount();
        int rowsCount = _nonogramProperty.getValue().getRowCount();
        int numberOfPositions = rand.nextInt(rowsCount * columnsCount)/10 + 1;

        for (int i = 0; i < numberOfPositions; i++)
        {
            int column = rand.nextInt(columnsCount);
            int row = rand.nextInt(rowsCount);
            movePositions.add(new CellPosition(row, column));
        }
        Nonogram.CellKind targetCellType = rand.nextBoolean() ? Nonogram.CellKind.Black : Nonogram.CellKind.White;
        applyMove(new LinkedList<>(movePositions),targetCellType, "Random Move");

        if(getScore() < scoreBeforeMove){
            undoAction();
        }
    }

        @Override
    public String toString() {
        return String.format("#%d : %s : %s ", _playerInfo.getId(), _playerInfo.getName(),_playerInfo.getPlayerType());
    }
}
