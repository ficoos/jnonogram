package org.bs.jnonogram.wui;

import org.bs.jnonogram.core.GameInfo;
import org.bs.jnonogram.core.GameInfoFactory;
import org.bs.jnonogram.core.GameInfoFactoryException;
import org.bs.jnonogram.core.PlayerType;
import org.bs.jnonogram.wui.api.Game;
import org.bs.jnonogram.wui.api.ResponseUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/games"})
@MultipartConfig
public class GamesServlet extends HttpServlet {
    private ApplicationContext appContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        appContext = ApplicationContext.instanceFor(this);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestedId = req.getParameter("id");
        String action = req.getParameter("action");
        if (action == null) {
            action = "show";
        }

        SessionWrapper session = new SessionWrapper(req.getSession());
        GamesList gamesManager = appContext.getGamesManager();
        if (requestedId != null) {
            if (!gamesManager.contains(requestedId)) {
                ResponseUtils.errorResponse(resp, 404, "Game not found");
                return;
            }

            GamesList.Entry gameEntry = gamesManager.get(requestedId);
            if (action.equals("join")) {
                if (gameEntry.hasGameStarted()) {
                    ResponseUtils.errorResponse(resp, 403, "Game already started");
                    return;
                }

                if (gameEntry.isFull()) {
                    ResponseUtils.errorResponse(resp, 403, "Game is full");
                    return;
                }

                gameEntry.addPlayer(session.getUserInformation().username, PlayerType.Human);
            } else if (action.equals("part")) {
                gameEntry.removePlayer(session.getUserInformation().username);
            } else {
                show(requestedId, resp);
            }

            resp.getWriter().write("{}");
            resp.getWriter().close();
        }
        else
        {
            list(resp);
        }
    }

    private void show(String id, HttpServletResponse resp) throws IOException {
        ResponseUtils.writeResponse(
                resp.getWriter(),
                Game.class,
                Game.fromGameEntry(appContext.getGamesManager().get(id))
        );
    }

    private void list(HttpServletResponse resp) throws IOException {
        ResponseUtils.writeResponse(
                resp.getWriter(),
                Game.class,
                appContext.getGamesManager()
                        .getEntries()
                        .values()
                        .stream()
                        .map(Game::fromGameEntry)
        );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionWrapper session = new SessionWrapper(req.getSession());
        Part filePart = req.getPart("file");
        GameInfo gameInfo;
        try {
            gameInfo = GameInfoFactory.loadFromXml(filePart.getInputStream());
            // Not dynamic multi player
            if (gameInfo.getPlayersInformation().size() != 0) {
                throw new GameInfoFactoryException("Only dynamic multi player games are supported");
            }
            GamesList.Entry entry = appContext.getGamesManager().addNew(gameInfo, session.getUserInformation().username);
            ResponseUtils.writeResponse(
                    resp.getWriter(),
                    Game.class,
                    Game.fromGameEntry(entry)
            );
        } catch (GameInfoFactoryException e) {
            ResponseUtils.errorResponse(resp, 403, "Could not load game: " + e.getMessage());
        }
    }
}
