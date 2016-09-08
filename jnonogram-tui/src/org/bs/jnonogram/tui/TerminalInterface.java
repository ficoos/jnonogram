package org.bs.jnonogram.tui;

import org.bs.jnonogram.core.*;
import org.bs.jnonogram.util.UndoableAction;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ListIterator;

public class TerminalInterface {
    private GameInfo _gameInfo = null;
    private GameManager _currentGame = null;
    private PlayerState _playerState = null;

    public void run() {
        new Menu()
                .setTitle("JNonogram 1.0")
                .addMenuItem(new MenuAction() {
                    @Override
                    public String getTitle() {
                        return "Load XML file";
                    }

                    @Override
                    public void doAction() {
                        loadXmlFile();
                    }
                })
                .addMenuItem(new MenuAction() {
                    @Override
                    public String getTitle() {
                        return "Start new Game";
                    }

                    @Override
                    public void doAction() {
                        startGame();
                    }
                })
                .addMenuItem(new MenuAction() {
                    @Override
                    public String getTitle() {
                        return "Show board state";
                    }

                    @Override
                    public void doAction() {
                        showBoardState();
                    }
                }).addMenuItem(new MenuAction() {
                    @Override
                    public String getTitle() {
                        return "Enter move";
                    }

                    @Override
                    public void doAction() {
                        enterMove();
                    }
                }).addMenuItem(new MenuAction() {
                    @Override
                    public String getTitle() {
                return "Show move history";
            }

                    @Override
                    public void doAction() {
                showMoveHistory();
            }
                }).addMenuItem(new MenuAction() {
                    @Override
                    public String getTitle() {
                        return "Undo move";
                    }

                    @Override
                    public void doAction() {
                        undoMove();
                    }
                })
                .addMenuItem(new MenuAction() {
                    @Override
                    public String getTitle() {
                        return "Show stats";
                    }

                    @Override
                    public void doAction() {
                        showStats();
                    }
                })
                .addMenuItem(new MenuAction() {
                    @Override
                    public String getTitle() {
                        return "End current game";
                    }

                    @Override
                    public void doAction() {
                        endCurrentGame();
                    }
                })
                .run();
    }

    private void endCurrentGame() {
        if (_playerState == null) {
            System.out.println("Game has not started yet");
            return;
        }

        _printEndOfGameSummary();
        _playerState = null;
        _currentGame = null;
        System.out.println("Game has ended");
    }

    private void _printEndOfGameSummary() {
        if (_playerState == null) {
            return;
        }

        showBoardState();
        showStats();
    }

    private void showMoveHistory() {
        if (_playerState == null) {
            System.out.println("Game has not started yet");
            return;
        }

        System.out.println("Move History:");
        List<UndoableAction> actions = _playerState.getActionList();
        int i = actions.size();
        for (ListIterator<UndoableAction> it = actions.listIterator(i); it.hasPrevious();)
        {
            System.out.print(Integer.toString(i));
            System.out.print(": ");
            System.out.println(it.previous());
            i--;
        }
    }

    private void showStats() {
        if (_playerState == null) {
            System.out.println("Game has not started yet");
            return;
        }

        System.out.printf("Move count: %d", _playerState.getMoveCount());
        System.out.println();
        System.out.printf("Undo count: %d", _playerState.getUndoCount());
        System.out.println();
        LocalDateTime now = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(_currentGame.getStartTime(), now);
        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        System.out.printf("Time since game started: %d:%d:%d",
                hours,
                minutes,
                seconds);
        System.out.println();
        System.out.printf("Current score: %d", _playerState.getScore());
        System.out.println();
    }

    private void undoMove() {
        if (_playerState == null) {
            System.out.println("Game has not started yet");
            return;
        }

        _playerState.undoAction();
    }

    private void enterMove() {
        if (_playerState == null) {
            System.out.println("Game has not started yet");
            return;
        }

        NonogramMove move;
        for (;;) {
            System.out.print("Please enter a move in for the format `[rc]<origin-row>,<origin-column>:<size>` :");
            try {
                SliceOrientation orientation = TerminalUtils.expectEnum(
                        "Please select orientation",
                        SliceOrientation.Column);

                int row = TerminalUtils.expectInteger("Enter origin row", 1, _playerState.getNonogram().getRowCount()) - 1;
                int column = TerminalUtils.expectInteger("Enter origin column", 1, _playerState.getNonogram().getColumnCount()) - 1;
                int maxSize;
                if (orientation.equals(SliceOrientation.Column)) {
                    maxSize = _playerState.getNonogram().getRowCount() - row;
                } else {
                    maxSize = _playerState.getNonogram().getColumnCount() - column;
                }

                int size = TerminalUtils.expectInteger("Enter slice size", 1, maxSize);
                Nonogram.CellKind targetKind = TerminalUtils.expectEnum("What to change this slice to", Nonogram.CellKind.Black);
                final boolean allowEmpty = true;
                String comment = TerminalUtils.ExpectString("Describe this move", allowEmpty);
                move = new NonogramMove();
                move.setOrigin(new CellPosition(column, row));
                move.setSize(size);
                move.setOrientation(orientation);
                move.setTargetKind(targetKind);
                move.setComment(comment);
                break;
            } catch (Exception e) {
                System.out.println("Could not parse move, please try again.");
            }
        }

        _playerState.applyMove(move);
        if (_playerState.getScore() == 100) {
            System.out.println("Congratulations!");
            endCurrentGame();
        }
    }

    private void showBoardState() {
        if (_playerState == null) {
            System.out.println("No game in progress.");
            return;
        }

        OutputStreamWriter writer = new OutputStreamWriter(System.out);
        try {
            NonogramWriter.writeNonogram(_playerState.getNonogram(), writer);
            writer.flush();
        } catch (IOException e) {
            // This shouldn't happen
            throw new RuntimeException("Someone close stdout");
        }
    }

    private void startGame() {
        if (_gameInfo == null) {
            System.out.println("Can't start game. No game file loaded!");
            return;
        }

        if (_currentGame != null) {
            if (!TerminalUtils.AskYesNoQuestion("Game already in progress. Are you sure you want start a new game?")) {
                return;
            }
        }

        _currentGame = new GameManager(_gameInfo);
        // We know there is only one
        _playerState = _currentGame.getPlayerStates().get(0);
        showBoardState();
    }

    private void loadXmlFile() {
        if (_gameInfo != null) {
            if (!TerminalUtils.AskYesNoQuestion("Game already loaded. Are you sure you want to load a game file?")) {
                return;
            }
        }

        File file = TerminalUtils.expectPath("Enter XML file path to load", true, false);
        try {
            _gameInfo = GameInfoFactory.loadFromXml(file);
        } catch (Exception e) {
            System.out.print("Could not load file: ");
            System.out.println(e.getMessage());
            return;
        }

        if (_gameInfo == null) {
            System.out.println("Invalid Nonogram definition");
            return;
        }

        List<PlayerInfo> playersInformation = _gameInfo.getPlayersInformation();
        if (playersInformation.size() != 1) {
            System.out.println("Only Single player games are supported in TUI");
            _gameInfo = null;
            return;
        }

        System.out.println("Game file loaded successfully");
    }
}
