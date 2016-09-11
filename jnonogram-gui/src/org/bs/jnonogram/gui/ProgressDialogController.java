package org.bs.jnonogram.gui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.bs.jnonogram.util.ObservableValueProxy;

import java.net.URL;
import java.util.ResourceBundle;

public class ProgressDialogController implements Initializable {
    private final Task task;

    @FXML
    private DialogPane pane;

    @FXML
    private Label messageLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private ProgressBar progressBar;

    public ProgressDialogController(Task model) {
        this.task = model;
        model.setOnRunning(event -> {
            messageLabel.textProperty().bind(model.messageProperty());
            progressBar.progressProperty().bind(model.progressProperty());
            pane.lookupButton(ButtonType.CLOSE).disableProperty().bind(task.runningProperty());
            errorLabel.textProperty().bind(
                    new ObservableValueProxy<String, Throwable>(task.exceptionProperty()) {
                        @Override
                        public String convertValue(Throwable exception) {
                            if (exception == null) {
                                return null;
                            } else {
                                return exception.getMessage();
                            }
                        }
                    });
            errorLabel.visibleProperty().bind(task.exceptionProperty().isNotNull());
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
