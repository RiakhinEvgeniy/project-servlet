package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int index = getSelectIndex(req);
        resp.sendRedirect("/index.jsp");
    }

    private int getSelectIndex(HttpServletRequest req) {
        String click = req.getParameter("click");
        boolean isNumericChar = click.chars().allMatch(Character::isDigit);
        return isNumericChar ? Integer.parseInt(click) : 0;
    }
}
