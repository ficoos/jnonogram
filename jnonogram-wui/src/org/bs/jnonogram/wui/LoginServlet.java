package org.bs.jnonogram.wui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bs.jnonogram.wui.api.ApiError;
import org.bs.jnonogram.wui.api.Player;
import org.bs.jnonogram.wui.api.ResponseUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/login", "/api/logout"})
public class LoginServlet extends HttpServlet implements HttpSessionListener {
    private ApplicationContext appContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        appContext = ApplicationContext.instanceFor(this);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().equals("/api/logout"))
        {
            logout(req, resp);
        }
        else
        {
            login(req, resp);
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson gson = new GsonBuilder().create();
        Player player = gson.fromJson(req.getReader(), Player.class);
        player.username = player.username.trim();
        SessionWrapper session = new SessionWrapper(req.getSession());
        if (player == null || player.username == null || player.username.isEmpty())
        {
            resp.setStatus(400);
            ResponseUtils.writeResponse(
                    resp.getWriter(),
                    ApiError.class,
                    new ApiError(400, "Invalid request"));
        }

        if (session.isLoggedIn()) {
            resp.setStatus(400);
            ResponseUtils.writeResponse(
                    resp.getWriter(),
                    ApiError.class,
                    new ApiError(400, "You are already logged in"));
            return;
        }

        if (!appContext.getUserManager().tryAddUser(player.username))
        {
            resp.setStatus(403);
            ResponseUtils.writeResponse(
                    resp.getWriter(),
                    ApiError.class,
                    new ApiError(403, "User with that title already exists. Please select a different title"));
            return;
        }

        session.setUserInformation(player);
        resp.setStatus(200);
        ResponseUtils.writeResponse(
                resp.getWriter(),
                Player.class,
                player);
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SessionWrapper session = new SessionWrapper(req.getSession());
        Player player = session.getUserInformation();
        if (player == null)
        {
            return;
        }

        appContext.getUserManager().removeUser(player.username);
        session.setUserInformation(null);
        resp.sendRedirect("/");
    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        // The user should poll all the time
        httpSessionEvent.getSession().setMaxInactiveInterval(30);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        ApplicationContext appContext = ApplicationContext.instanceFor(httpSessionEvent.getSession());
        SessionWrapper session = new SessionWrapper(httpSessionEvent.getSession());
        if (session.isLoggedIn()) {
            appContext.getUserManager()
                    .removeUser(session.getUserInformation().username);
        }
    }
}
