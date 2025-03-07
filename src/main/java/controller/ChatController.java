package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;

import java.io.IOException;
import java.util.logging.Logger;

public class ChatController extends HttpServlet {
    final static Logger logger = Logger.getLogger(ChatController.class.getName());

    @Override
    public void init() throws ServletException {
        super.init();
        logger.info("ChatController initialized");
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
//        super.service(req, res);
        logger.info("service! service!");
        res.getWriter().println("받아라");
    }

    @Override
    public void destroy() {
        logger.info("잘가");
        super.destroy();
    }
}
