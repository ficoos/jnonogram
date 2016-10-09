package org.bs.jnonogram.wui;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/index.html"})
public class IndexServlet extends HttpServlet {
    private ApplicationContext appContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        appContext = ApplicationContext.instanceFor(this);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionWrapper wrapper = new SessionWrapper(req.getSession());
        if (wrapper.isLoggedIn())
        {
            resp.sendRedirect("/lobby.html");
        }
        else
        {
            resp.sendRedirect("/login.html");
        }
    }
}
