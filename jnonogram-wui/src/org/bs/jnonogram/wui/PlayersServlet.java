package org.bs.jnonogram.wui;

import org.bs.jnonogram.wui.api.Player;
import org.bs.jnonogram.wui.api.ResponseUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/players"})
public class PlayersServlet extends HttpServlet{
    private ApplicationContext appContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        appContext = ApplicationContext.instanceFor(this);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        appContext.getUserManager().getList();
        ResponseUtils.writeResponse(
                resp.getWriter(),
                Player.class,
                appContext.getUserManager().getList().stream().map((name) -> {
                    Player player = new Player();
                    player.username = name;
                    return player;
                })
        );
    }
}
