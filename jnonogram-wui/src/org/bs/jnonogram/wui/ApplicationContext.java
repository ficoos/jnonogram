package org.bs.jnonogram.wui;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;

public class ApplicationContext implements ServletContextListener {
    private static final String CONTEXT_KEY = "__application_context";
    private final UserManager _userManager = new UserManager();
    private final GamesList _gamesManager = new GamesList();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        final ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.setAttribute(CONTEXT_KEY, this);
        _userManager.reset();
        _gamesManager.reset();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    public UserManager getUserManager() {
        return _userManager;
    }

    public GamesList getGamesManager() { return _gamesManager; }

    public static ApplicationContext instanceFor(HttpSession session)
    {
        return instanceFor(session.getServletContext());
    }

    public static ApplicationContext instanceFor(Servlet servlet)
    {
        return instanceFor(servlet.getServletConfig().getServletContext());
    }

    public static ApplicationContext instanceFor(ServletContext ctx)
    {
        return (ApplicationContext) ctx.getAttribute(CONTEXT_KEY);
    }
}
