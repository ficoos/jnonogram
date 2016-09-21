package org.bs.jnonogram.gui;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import java.io.IOException;

import static javafx.scene.control.ButtonType.CLOSE;

public class ProgressDialog<V> extends Dialog<V>{
    public ProgressDialog(Task<V> task, String styleSheetPath) {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProgressDialog.fxml"));
        loader.setControllerFactory(aClass -> {
            try {
                return aClass.getConstructor(Task.class).newInstance(task);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent root;
        try {
            root = loader.load();
            root.getStylesheets().add(styleSheetPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.setResizable(false);
        this.setResultConverter(buttonType -> task.getValue());

        this.setDialogPane((DialogPane) root);
        this.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> {
            if (this.getDialogPane().lookupButton(ButtonType.CLOSE).isDisabled()) {
                event.consume();
            }
        });
        this.getDialogPane().lookupButton(CLOSE).setDisable(true);
        this.titleProperty().bind(task.titleProperty());
    }
}
