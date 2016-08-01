package org.bs.jnonogram.tui;

import org.bs.jnonogram.core.CellPosition;
import org.bs.jnonogram.core.NonogramMove;
import org.bs.jnonogram.core.SliceOrientation;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.MatchResult;

public class NonogramMoveParser {
    private static final String MoveFormat = "(([rc])\\s*(\\d+)\\s*,\\s*(\\d+)\\s*:\\s*(\\d+)\\s*>\\s*[bwu] (.*)|done)";
    private final Scanner _scanner;
    public NonogramMoveParser(InputStream stream) {
        _scanner = new Scanner(stream);
    }

    public NonogramMove parseMove() {
        try {
            _scanner.next(MoveFormat);
            MatchResult match = _scanner.match();
            if (match.group(1).equalsIgnoreCase("done")) {
                return null;
            }

            SliceOrientation orientation;
            String rawOrientation = match.group(2);
            if (rawOrientation.equalsIgnoreCase("r")) {
                orientation = SliceOrientation.Row;
            } else {
                orientation = SliceOrientation.Column;
            }

            int column = Integer.valueOf(match.group(3));
            int row = Integer.valueOf(match.group(4));
            int size = Integer.valueOf(match.group(5));
            NonogramMove move = new NonogramMove();
            move.setOrientation(orientation);
            move.setOrigin(new CellPosition(column, row));
            move.setSize(size);

            return move;
        }
        catch (Exception ex) {
            _scanner.nextLine();
            throw ex;
        }
    }
}
