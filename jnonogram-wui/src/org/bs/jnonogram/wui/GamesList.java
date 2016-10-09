package org.bs.jnonogram.wui;

import org.bs.jnonogram.core.GameInfo;
import org.bs.jnonogram.core.GameManager;
import org.bs.jnonogram.core.PlayerInfo;
import org.bs.jnonogram.core.PlayerType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamesList {
    public Entry get(String id) {
        return _entries.get(id);
    }

    public boolean contains(String requestedId) {
        return _entries.containsKey(requestedId);
    }

    public static class Entry
    {
        private final String _id;
        private final String _creator;
        private GameInfo _info;

        public GameInfo getInfo() {
            return _info;
        }

        private GameManager _manager;

        public boolean hasGameStarted() {
            return _manager != null;
        }

        public boolean containesPlayer(String username) {
            return _info.getPlayersInformation().stream().anyMatch(playerInfo -> playerInfo.getName().equals(username));
        }

        public void addPlayer(String username, PlayerType playerType)
        {
            if (containesPlayer(username)) {
                return;
            }

            if (_info.getPlayersInformation().size() == _info.getMaxPlayers()) {
                throw new RuntimeException("Players Maxed");
            }

            _info.getPlayersInformation().add(
                    new PlayerInfo(username.hashCode(), username, playerType));
        }

        public void removePlayer(String username) {
            _info.getPlayersInformation().removeIf((playerInfo) -> playerInfo.getName().equals(username));
        }

        public GameManager getManager() {
            return _manager;
        }

        public Entry(String id, GameInfo info, String creator) {
            _id = id;
            _creator = creator;
            _info = info;
            _manager = null;
        }

        public void startGame() {
            // TODO
        }

        public String getCreator() {
            return _creator;
        }

        public String getId() {
            return _id;
        }

        public boolean isFull() {
            return this.getInfo().getMaxPlayers() <= this.getInfo().getPlayersInformation().size();
        }
    }

    public Map<String, Entry> getEntries() {
        return _entries;
    }

    private final HashMap<String, Entry> _entries = new HashMap<>();

    public Entry addNew(GameInfo gameInfo, String creator) {
        String uuid = UUID.randomUUID().toString();
        Entry entry = new Entry(uuid, gameInfo, creator);
        _entries.put(uuid, entry);
        return entry;
    }

    public void reset() {
        _entries.clear();
    }
}
