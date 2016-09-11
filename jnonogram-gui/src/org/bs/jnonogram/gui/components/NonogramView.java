package org.bs.jnonogram.gui.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.TextAlignment;
import org.bs.jnonogram.core.Nonogram;
import org.bs.jnonogram.core.NonogramConstraint;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class NonogramView extends GridPane implements Initializable {
    @FXML
    private GridPane rowConstraintsGrid;

    @FXML
    private GridPane columnConstraintsGrid;

    @FXML
    private GridPane nonogramGrid;

    private final SimpleObjectProperty<Nonogram> _nonogramProperty;

    public final ObjectProperty<Nonogram> nonogramProperty() {
        return _nonogramProperty;
    }

    public Nonogram getNonogram() {
        return _nonogramProperty.getValue();
    }

    public void setNonogram(Nonogram nonogram) {
        _nonogramProperty.setValue(nonogram);
    }

    private void onNonogramChanged(Object sender, Nonogram oldValue, Nonogram newValue ) {
        int maxSlicesInRowConstraint = Arrays.stream(newValue.getRowConstraints())
                .mapToInt(NonogramConstraint::count)
                .max()
                .orElse(0);

        int maxSlicesInColumnConstraint = Arrays.stream(newValue.getColumnConstraints())
                .mapToInt(NonogramConstraint::count)
                .max()
                .orElse(0);

        populateGrids(
                newValue.getRowCount(),
                maxSlicesInRowConstraint,
                newValue.getColumnCount(),
                maxSlicesInColumnConstraint);
        populateColumnConstraints(newValue, maxSlicesInColumnConstraint);
        populateRowConstraints(newValue, maxSlicesInRowConstraint);
        populateNonogram(newValue);
    }

    private void populateNonogram(Nonogram nonogram) {
        nonogram.cellsProperty().forEach((position, cellKind) -> {
            CellButton cellButton = new CellButton();
            cellButton.cellProperty().setValue(cellKind);
            nonogram.cellsProperty().addListener((observable, oldValue, newValue) -> {
                cellButton.cellProperty().setValue(newValue.get(position));
            });

            cellButton.cellProperty().addListener((observable, oldValue, newValue) -> {
                nonogram.cellsProperty().replace(position, newValue);
            });
            nonogramGrid.add(cellButton, position.getColumn(), position.getRow());
        });
    }

    private void populateColumnConstraints(Nonogram nonogram, int maxSlicesInColumnConstraint) {
        for (int columnIdx = 0; columnIdx < nonogram.getColumnCount(); columnIdx++)
        {
            NonogramConstraint constraint = nonogram.getColumnConstraints()[columnIdx];
            for (int rowIdx = 0; rowIdx < constraint.count(); rowIdx++) {
                int index = rowIdx + maxSlicesInColumnConstraint - constraint.count();
                NonogramConstraint.Slice slice = constraint.getSlice(rowIdx);
                Label lbl = new Label(Integer.toString(slice.getSize()));
                lbl.setMaxHeight(Double.MAX_VALUE);
                lbl.setMaxWidth(Double.MAX_VALUE);
                lbl.setAlignment(Pos.CENTER);
                lbl.setTextAlignment(TextAlignment.CENTER);
                columnConstraintsGrid.add(
                        lbl,
                        columnIdx,
                        index);
            }
        }
    }

    private void populateRowConstraints(Nonogram nonogram, int maxSlicesInRowConstraint) {
        for (int rowIdx = 0; rowIdx < nonogram.getRowCount(); rowIdx++)
        {
            NonogramConstraint constraint = nonogram.getRowConstraints()[rowIdx];
            for (int columnsIdx = 0; columnsIdx < constraint.count(); columnsIdx++) {
                int index = columnsIdx + maxSlicesInRowConstraint - constraint.count();
                NonogramConstraint.Slice slice = constraint.getSlice(columnsIdx);
                Label lbl = new Label(Integer.toString(slice.getSize()));
                lbl.setMaxHeight(Double.MAX_VALUE);
                lbl.setMaxWidth(Double.MAX_VALUE);
                lbl.setAlignment(Pos.CENTER);
                lbl.setTextAlignment(TextAlignment.CENTER);
                rowConstraintsGrid.add(
                        lbl,
                        index,
                        rowIdx);
            }
        }
    }

    private void populateGrids(int rowConstraintsCount, int rowConstraintSliceCount, int columnConstraintCount, int columnConstraintSliceCount) {
        RowConstraints gridRowConstraint = columnConstraintsGrid.getRowConstraints().get(0);
        ColumnConstraints gridColumnConstraint = columnConstraintsGrid.getColumnConstraints().get(0);
        columnConstraintsGrid.getRowConstraints().clear();
        columnConstraintsGrid.getColumnConstraints().clear();
        columnConstraintsGrid.getChildren().clear();
        rowConstraintsGrid.getColumnConstraints().clear();
        rowConstraintsGrid.getRowConstraints().clear();
        rowConstraintsGrid.getChildren().clear();
        nonogramGrid.getColumnConstraints().clear();
        nonogramGrid.getRowConstraints().clear();
        nonogramGrid.getChildren().clear();

        for (int i = 0; i < columnConstraintSliceCount; i++) {
            columnConstraintsGrid.getRowConstraints().add(gridRowConstraint);
        }

        for (int i = 0; i < columnConstraintCount; i++) {
            columnConstraintsGrid.getColumnConstraints().add(gridColumnConstraint);
            nonogramGrid.getColumnConstraints().add(gridColumnConstraint);
        }

        for (int i = 0; i < rowConstraintsCount; i++) {
            rowConstraintsGrid.getRowConstraints().add(gridRowConstraint);
            nonogramGrid.getRowConstraints().add(gridRowConstraint);
        }

        for (int i = 0; i < rowConstraintSliceCount; i++) {
            rowConstraintsGrid.getColumnConstraints().add(gridColumnConstraint);
        }
    }

    public NonogramView() {
        _nonogramProperty = new SimpleObjectProperty<>();
        _nonogramProperty.addListener(this::onNonogramChanged);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "NonogramView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
