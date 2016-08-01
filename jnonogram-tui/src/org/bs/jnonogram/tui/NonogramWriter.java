package org.bs.jnonogram.tui;

import org.bs.jnonogram.core.NonogramConstraint;
import org.bs.jnonogram.core.ReadOnlyNonogram;

import java.io.IOException;
import java.io.Writer;

public final class NonogramWriter {
    private static final String k_BlackCell = " ";
    private static final String k_BlankSpace = " ";
    private static final String k_WhiteCell = "\u2588";
    private static final String k_UnknownCell = "?";
    private static final String k_FrameTopRightCorner = "\u250c";
    private static final String k_FrameTopLeftCorner = "\u2510";
    private static final String k_FrameBottomRightCorner = "\u2514";
    private static final String k_FrameBottomLeftCorner = "\u2518";
    private static final String k_FrameVertical = "\u2502";
    private static final String k_FrameHorizontal = "\u2500";

    private static void drawFrame(ReadOnlyNonogram nonogram, Writer writer) throws IOException {
        drawColumnConstraints(nonogram, writer);
        drawFrameHeader(nonogram, writer);
        drawFrameContent(nonogram, writer);
        drawFrameFooter(nonogram, writer);
    }

    private static void drawColumnConstraints(ReadOnlyNonogram nonogram, Writer writer) throws IOException {
        int maxLength = 0;
        for (NonogramConstraint constraint: nonogram.getColumnConstraints()) {
            maxLength = Math.max(constraint.count(), maxLength);
        }

        for (int i = maxLength; i > 0; i--) {
            drawPadding(nonogram, writer);
            writer.write(k_BlankSpace);
            for (NonogramConstraint constraint : nonogram.getColumnConstraints()) {
                if (constraint.count() >= i ) {
                    writer.write(String.valueOf(constraint.getSlice(constraint.count() - i).getSize()));
                }
                else
                {
                    writer.write(k_BlankSpace);
                }
            }
            writer.write(System.lineSeparator());
        }
    }

    private static void drawFrameContent(ReadOnlyNonogram nonogram, Writer writer) throws IOException {
        int maxLength = 0;
        for (NonogramConstraint constraint: nonogram.getRowConstraints()) {
            maxLength = Math.max(constraint.count(), maxLength);
        }

        final NonogramConstraint[] rowConstraints = nonogram.getRowConstraints();

        for (int y = 0; y < nonogram.getRowCount(); y++) {
            final NonogramConstraint constraint = rowConstraints[y];
            for (int i = constraint.count() - maxLength; i < constraint.count(); i++) {
                if (i < 0) {
                    writer.write(k_BlankSpace);
                }
                else
                {
                    writer.write(String.valueOf(constraint.getSlice(i).getSize()));
                }
            }

            writer.write(k_FrameVertical);
            for (int x = 0; x < nonogram.getColumnCount(); x++) {
                switch (nonogram.getCellAt(x, y)) {
                    case Black:
                        writer.write(k_BlackCell);
                        break;
                    case White:
                        writer.write(k_WhiteCell);
                        break;
                    case Unknown:
                        writer.write(k_UnknownCell);
                        break;
                }
            }
            writer.write(k_FrameVertical);
            writer.write(System.lineSeparator());
        }
    }

    private static void drawPadding(ReadOnlyNonogram nonogram, Writer writer) throws IOException {
        int padding = 0;
        for (NonogramConstraint constraint: nonogram.getRowConstraints()) {
            padding = Math.max(constraint.count(), padding);
        }

        for (int i = 0; i < padding; i++) {
            writer.write(k_BlankSpace);
        }
    }

    private static void drawFrameFooter(ReadOnlyNonogram nonogram, Writer writer) throws IOException {
        drawPadding(nonogram, writer);
        writer.write(k_FrameBottomRightCorner);
        for (int i = 0; i < nonogram.getColumnCount(); i++) {
            writer.write(k_FrameHorizontal);
        }

        writer.write(k_FrameBottomLeftCorner);
        writer.write(System.lineSeparator());
    }

    private static void drawFrameHeader(ReadOnlyNonogram nonogram, Writer writer) throws IOException {
        drawPadding(nonogram, writer);
        writer.write(k_FrameTopRightCorner);
        for (int i = 0; i < nonogram.getColumnCount(); i++) {
            writer.write(k_FrameHorizontal);
        }

        writer.write(k_FrameTopLeftCorner);
        writer.write(System.lineSeparator());
    }

    public static void writeNonogram(ReadOnlyNonogram nonogram, Writer writer) throws IOException {
        drawFrame(nonogram, writer);
    }
}
