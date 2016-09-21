package org.bs.jnonogram.core;
import java.util.ArrayList;
import java.util.List;

public class NextTurnObserver {

    private List<NextTurnListener> handlers;

    public NextTurnObserver() {
        handlers = new ArrayList<>();
    }

    public void addListener(NextTurnListener handler) {
        if (handler != null && !handlers.contains(handler)) {
            handlers.add(handler);
        }
    }

    public void removeListener(NextTurnListener handler) {
        handlers.remove(handler);
    }

    public void notifyAll(PlayerState playerState) {
        List<NextTurnListener> handlersToInvoke = new ArrayList<>(handlers);
        for (NextTurnListener handler : handlersToInvoke) {
            handler.nextTurnHappened(playerState);
        }
    }

}
