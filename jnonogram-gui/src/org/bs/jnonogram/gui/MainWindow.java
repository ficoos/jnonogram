package org.bs.jnonogram.gui;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bs.jnonogram.core.GameInfo;
import org.bs.jnonogram.core.GameInfoFactory;
import org.bs.jnonogram.core.GameManager;
import org.bs.jnonogram.core.PlayerState;
import org.bs.jnonogram.gui.components.PlayerPane;
import org.bs.jnonogram.util.ObservableValueProxy;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class MainWindow implements Initializable {
    @FXML
    private Node root;

    @FXML
    private Menu lruMenu;

    @FXML
    private TabPane gameTabPane;

    private static final int MaxRecentDocumentsNum = 10;
    private LeastRecentlyUsedModel<File> recentDocuments;

    public void quit(ActionEvent actionEvent) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    public void openXml(ActionEvent actionEvent) {
        final FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JNonogram game files (*.xml)", "*.xml"));
        final File file = chooser.showOpenDialog(root.getScene().getWindow());
        if (file == null) {
            return;
        }

        openFile(file);
    }

    private void openFile(File file) {
        Task<GameInfo> task = GameInfoFactory.loadFromXmlAsync(file);
        JnonogramGui.getInstance().scheduleTask(task);
        ProgressDialog<GameInfo> dialog = new ProgressDialog<>(task);
        dialog.showAndWait().ifPresent(result -> {
            recentDocuments.addItem(file);
            GameManager gameManager = new GameManager(result);
            gameTabPane.getTabs().clear();
            for (PlayerState playerState: gameManager.getPlayerStates()) {
                PlayerPane playerPane = new PlayerPane(playerState);
                Tab tab = new Tab(String.format("%s (ID: %d)", playerState.getPlayerInfo().getName(), playerState.getPlayerInfo().getId()));
                tab.setContent(playerPane);
                gameTabPane.getTabs().add(tab);
            }

            ((Stage) root.getScene().getWindow()).titleProperty().bind(
                    new ObservableValueProxy<String, String>(gameManager.titleProperty()) {
                        @Override
                        protected String convertValue(String value) {
                            return "Jnonogram - " + value;
                        }
                    });
        });
    }

    private void handleRecentDocumentOpen(ActionEvent actionEvent) {
        openFile((File)((MenuItem)actionEvent.getSource()).getUserData());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Preferences preferences = JnonogramGui.getInstance().getPreferences();
        try {
            preferences.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }

        recentDocuments = new LeastRecentlyUsedModel<>(
                MaxRecentDocumentsNum,
                Arrays.stream(preferences
                        .get("recent_documents", "")
                        .split(";"))
                        .map(File::new)
                        .collect(Collectors.toList())
        );

        recentDocuments.itemsProperty().addListener(this::onRecentDocumentsUpdated);
        this.onRecentDocumentsUpdated(this, null, recentDocuments.items());
    }

    private void onRecentDocumentsUpdated(Object sender, ObservableList<File> oldValue, ObservableList<File> newValue) {
        Preferences preferences = JnonogramGui.getInstance().getPreferences();

        preferences.put("recent_documents",
                String.join(";",
                        recentDocuments
                                .items()
                                .stream()
                                .map(File::getAbsolutePath)
                                .collect(Collectors.toList())
                )
        );
        try {
            preferences.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
        ObservableList<MenuItem> items = lruMenu.getItems();
        items.clear();
        if (newValue.size() == 0) {
            MenuItem item = new MenuItem("No recent XML files");
            item.setDisable(true);
            items.add(item);
            return;
        }

        for(File file: newValue) {
            MenuItem item = new MenuItem(file.getAbsolutePath());
            item.setUserData(file);
            item.setOnAction(this::handleRecentDocumentOpen);
            items.add(item);
        }

        items.add(new SeparatorMenuItem());

        MenuItem clearMenuItem = new MenuItem("Clear");
        clearMenuItem.setOnAction(event -> recentDocuments.clear());
        items.add(clearMenuItem);
    }
}
