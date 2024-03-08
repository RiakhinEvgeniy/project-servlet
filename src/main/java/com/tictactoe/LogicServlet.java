package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession currentSession = req.getSession();

        Field field = extractField(currentSession);

        int index = getSelectIndex(req);
        if (getIndexOnclickCell(req, resp, field, index)) return;

        field.getField().put(index, Sign.CROSS);

        if (checkWinner(resp, currentSession, field)) return;

        boolean emptyField = getEmptyField(field);

        if (emptyField) {
            getDraw(field, currentSession, resp);
            return;
        }

        if (checkWinner(resp, currentSession, field)) return;

        List<Sign> data = field.getFieldData();

        currentSession.setAttribute("data", data);
        currentSession.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }

    private boolean checkWinner(HttpServletResponse resp, HttpSession session, Field field) {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            session.setAttribute("winner", winner);

            List<Sign> data = field.getFieldData();
            session.setAttribute("data", data);

            try {
                resp.sendRedirect("/index.jsp");
            } catch (IOException e) {
                throw new RuntimeException("Fail redirect from checkWinner method", e);
            }
            return true;
        }
        return false;
    }

    private boolean getEmptyField(Field field) {
        int emptyFieldIndex = field.getEmptyFieldIndex();
        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            return false;
        }
        return true;
    }

    private void getDraw(Field field, HttpSession session, HttpServletResponse resp) {
        session.setAttribute("draw", true);
        List<Sign> data = field.getFieldData();
        session.setAttribute("data", data);
        try {
            resp.sendRedirect("/index.jsp");
        } catch (IOException e) {
            throw new RuntimeException("Fail redirect from getEmptyField method" + e);
        }
    }

    private boolean getIndexOnclickCell(HttpServletRequest req, HttpServletResponse resp, Field field, int index) throws ServletException, IOException {
        Sign currentSign = field.getField().get(index);
        if (Sign.EMPTY != currentSign) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, resp);
            return true;
        }
        return false;
    }

    private Field extractField(HttpSession session) {
        Object fieldAttribute = session.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            session.invalidate();
            throw new RuntimeException("Session is broken, try again!");
        }
        return (Field) fieldAttribute;
    }

    private int getSelectIndex(HttpServletRequest req) {
        String click = req.getParameter("click");
        boolean isNumericChar = click.chars().allMatch(Character::isDigit);
        return isNumericChar ? Integer.parseInt(click) : 0;
    }
}
