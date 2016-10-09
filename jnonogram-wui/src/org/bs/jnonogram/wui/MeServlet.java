package org.bs.jnonogram.wui;

import org.bs.jnonogram.wui.api.ApiError;
import org.bs.jnonogram.wui.api.ResponseUtils;
import org.bs.jnonogram.wui.api.Player;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/me"})
public class MeServlet extends HttpServlet {
    private ApplicationContext appContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        appContext = ApplicationContext.instanceFor(this);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SessionWrapper session = new SessionWrapper(req.getSession());
        if (!session.isLoggedIn())
        {
            resp.setStatus(403);
            ResponseUtils.writeResponse(
                    resp.getWriter(),
                    ApiError.class,
                    new ApiError(403, "User is not logged in")
                    );
            return;
        }

        ResponseUtils.writeResponse(
                resp.getWriter(),
                Player.class,
                session.getUserInformation()
                );
    }
}
