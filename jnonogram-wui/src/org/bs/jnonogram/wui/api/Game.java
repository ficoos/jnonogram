package org.bs.jnonogram.wui.api;

import com.google.gson.annotations.SerializedName;
import org.bs.jnonogram.core.*;
import org.bs.jnonogram.core.Nonogram;
import org.bs.jnonogram.wui.GamesList;

@ApiEntityName(singular = "game", plural = "games")
public class Game {
    @SerializedName("title")
    public String title;

    @SerializedName("max_players")
    public int maxPlayers;

    @SerializedName("player_count")
    public int playerCount;

    @SerializedName("creator")
    public String creator;

    @SerializedName("max_moves")
    public int maxMoves;

    @SerializedName("board_width")
    public int boardWidth;

    @SerializedName("board_height")
    public int boardHeight;

    @SerializedName("id")
    public String id;

    @SerializedName("has_game_started")
    private boolean hasGameStarted;

    @SerializedName("nonogram")
    private NonogramApi nonogram;

    public static Game fromGameEntry(GamesList.Entry entry) {
        GameInfo info = entry.getInfo();
        Game details = new Game();
        details.id = entry.getId();
        details.title = info.getTitle();
        details.playerCount = info.getPlayersInformation().size();
        details.maxPlayers = info.getMaxPlayers();
        details.creator = entry.getCreator();
        details.maxMoves = entry.getInfo().getMaxMoves();
        details.boardHeight = entry.getInfo().getNonogram().getRowCount();
        details.boardWidth = entry.getInfo().getNonogram().getColumnCount();
        details.hasGameStarted = entry.hasGameStarted();
        details.nonogram = convertNonogram(entry.getInfo().getNonogram());
        return details;
    }

    private static NonogramApi convertNonogram(Nonogram nonogram) {
        NonogramApi nonogramApi = new NonogramApi();
        nonogramApi.cells = new String[nonogram.getRowCount() * nonogram.getColumnCount()];
        nonogramApi.rowCount = nonogram.getRowCount();
        nonogramApi.columnCount = nonogram.getColumnCount();
        for (int row = 0; row < nonogram.getRowCount(); row++) {
            for (int column = 0; column < nonogram.getColumnCount(); column++) {
                nonogramApi.cells[row * nonogram.getColumnCount() + column] = nonogram.getCellAt(column, row).toString();
            }
        }

        return nonogramApi;
    }
}
