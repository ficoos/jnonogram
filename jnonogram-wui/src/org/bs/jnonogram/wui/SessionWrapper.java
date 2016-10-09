package org.bs.jnonogram.wui;

import org.bs.jnonogram.wui.api.Player;

import javax.servlet.http.HttpSession;

public class SessionWrapper {
    private static final String SESSION_ATTR_USER_INFO = "user_info";

    private final HttpSession _session;
    private final UserManager _userManager;

    public SessionWrapper(HttpSession session)
    {
        this._session = session;
        this._userManager = ApplicationContext.instanceFor(_session).getUserManager();
    }

    public boolean isLoggedIn() {
        return getUserInformation() != null;
    }

    public void setUserInformation(Player player) {
        _session.setAttribute(SESSION_ATTR_USER_INFO, player);
    }

    public Player getUserInformation() {
        return (Player) _session.getAttribute(SESSION_ATTR_USER_INFO);
    }
}
