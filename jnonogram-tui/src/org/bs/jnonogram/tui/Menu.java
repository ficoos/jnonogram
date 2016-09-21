package org.bs.jnonogram.tui;

import java.util.ArrayList;

public final class Menu {
    private String _title;
    private final ArrayList<MenuAction> _actions;

    public Menu() {
        _actions = new ArrayList<>();
        _title = "";
    }

    public final Menu addMenuItem(MenuAction action) {
        _actions.add(action);
        return this;
    }

    public final Menu setTitle(String title) {
        _title = title;
        return this;
    }

    private final void show() {
        System.out.println(_title);
        TerminalUtils.repeatString("=", _title.length());
        System.out.println();
        for (int i = 0; i < _actions.size(); i++) {
            final MenuAction action = _actions.get(i);
            System.out.printf("%d) %s", i + 1, action.getTitle());
            System.out.println();
        }

        System.out.printf("%d) %s", _actions.size() + 1, "Quit");
        System.out.println();
    }

    public final void run() {
        boolean isRunning = true;
        while (isRunning) {
            show();
            int selection = TerminalUtils.expectInteger("Enter an item number", 1, _actions.size() + 1);
            if (selection > _actions.size()) {
                isRunning = false;
                System.out.println("Thank you for playing!");
            } else {
                _actions.get(selection - 1).doAction();
            }
        }
    }
}
