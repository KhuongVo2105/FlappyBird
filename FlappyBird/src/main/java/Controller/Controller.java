package Controller;

import Model.Couple;
import Model.Pipe;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/controller")
public class Controller extends HttpServlet {
    private int boardWith, boardHeight;
    private int birdX, birdY, birdWidth, birdHeight,
            pipeX, pipeY, pipeWidth, pipeHeight;
    private static final int GRAVITY = 0, SPACING = 100;
    boolean gameover = false;
    double score = 0;

    private ArrayList<Couple> coupleList;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        switch (action) {
            case "boardgame":
                boardGame(req, resp);
                break;
            case "current-bird":
                currentBird(req, resp);
                break;
            case "other-size":
                setSizeOther(req, resp);
                break;
            case "add-couple":
                addCouple(req, resp);
                break;
            case "load-pipes":
                getCoupleList(req, resp);
                break;
        }
    }

    private void getCoupleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson gson = new Gson();
        coupleList.forEach(couple->{
            couple.next();
        });
        String json = gson.toJson(coupleList);
        resp.getWriter().write(json);
    }

    private void addCouple(HttpServletRequest req, HttpServletResponse resp) {
            Pipe top = new Pipe(), bottom = new Pipe();

            top.setX(boardWith);
            bottom.setX(boardWith);

            top.setWidth(pipeWidth);
            bottom.setWidth(pipeWidth);

            int totalHeight = boardHeight - SPACING;
            int topHeight = (int) (Math.random() * totalHeight);
            topHeight = topHeight >= totalHeight ? topHeight / 2 : topHeight;
            int bottomHeight = totalHeight - topHeight;
            top.setHeight(topHeight);
            bottom.setHeight(bottomHeight);

            top.setY(0);
            bottom.setY(0);

            top.setImage("toppipe.png");
            bottom.setImage("bottompipe.png");

            coupleList.add(new Couple(top, bottom));
    }

    private void setSizeOther(HttpServletRequest req, HttpServletResponse resp) {
        int birdWidth = Integer.parseInt(req.getParameter("birdWidth")),
                birdHeight = Integer.parseInt(req.getParameter("birdHeight")),
                pipeWidth = Integer.parseInt(req.getParameter("pipeWidth")),
                pipeHeight = Integer.parseInt(req.getParameter("pipeHeight"));
        System.out.println("birdWidth: " + birdWidth + " birdHeight: " + birdHeight);
        System.out.println("pipeWidth: " + pipeWidth + " pipeHeight: " + pipeHeight);
        this.birdWidth = birdWidth;
        this.birdHeight = birdHeight;
        this.pipeWidth = pipeWidth;
        this.pipeHeight = pipeHeight;
    }

    private void currentBird(HttpServletRequest req, HttpServletResponse resp) {
        int currentX = Integer.parseInt(req.getParameter("currentX")),
                currentY = Integer.parseInt(req.getParameter("currentY"));
        System.out.println("currentX: " + currentX + " currentY: " + currentY);
        birdX = currentX;
        birdY = currentY;
    }

    // set resolution
    private void boardGame(HttpServletRequest req, HttpServletResponse resp) {
        int width = Integer.parseInt(req.getParameter("boardWith")),
                height = Integer.parseInt(req.getParameter("boardHeight"));
        System.out.println("width: " + width + " height: " + height);
        boardWith = width;
        boardHeight = height;
    }
}
