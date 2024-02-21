package Controller;

import Model.Bird;
import Model.Pipe;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@WebServlet("/controller")
public class Controller extends HttpServlet {
    private int boardWidth, boardHeight,
            birdX, birdY, birdWidth, birdHeight,
            pipeX, pipeY, pipeWidth, pipeHeight;

    private String background = "Resources/background.png",
            birdImg = "Resources/bird.png",
            topPipeImg = "Resources/toppipe.png",
            bottomPipeImg = "Resources/bottompipe.png";

    // game logic
    private Bird bird;
    private int velocityX = -4; //move pipes to the left speed (simulates bird moving right)
    private int velocityY = 0; //move bird up/down speed.
    private int gravity = 1;
    private ArrayList<Pipe> pipes = new ArrayList<>();
    private Random random = new Random();
    private boolean gameOver = false;
    private int score = 0;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        switch (action) {
            case "start":
                start(req, resp);
                break;
            case "place-pipes":
                placePipes(req, resp);
                break;
            case "render-pipers":
                renderPipes(req, resp);
                break;
        }
    }

    private void renderPipes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Convert map to JSON using Gson
        Gson gson = new Gson();
        String json = gson.toJson(pipes);

        // Write JSON as response
        resp.setContentType("application/json");
        resp.getWriter().write(json);
    }

    private void placePipes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //(0-1) * pipeHeight/2.
        // 0 -> -128 (pipeHeight/4)
        // 1 -> -128 - 256 (pipeHeight/4 - pipeHeight/2) = -3/4 pipeHeight
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(pipeX, pipeY, pipeWidth, pipeHeight, topPipeImg, false);
        topPipe.setY(randomPipeY);
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(pipeX, pipeY, pipeWidth, pipeHeight, bottomPipeImg, false);
        bottomPipe.setY(topPipe.getY() + pipeHeight + openingSpace);
        pipes.add(bottomPipe);

        Gson gson = new Gson();
        String json = gson.toJson(pipes);
        resp.getWriter().println(json);
    }

    private void start(HttpServletRequest req, HttpServletResponse resp) {
        try {
            boardWidth = Integer.parseInt(req.getParameter("boardWidth"));
            boardHeight = Integer.parseInt(req.getParameter("boardHeight"));
//            System.out.println("boardWidth: " + boardWidth + " boardHeight: " + boardHeight);

            birdX = boardWidth / 4;
            birdY = boardHeight / 2;
            birdWidth = 70;
            birdHeight = 47;

            pipeX = boardWidth;
            pipeY = 0;
            pipeWidth = 70;
            pipeHeight = boardHeight;

            bird = new Bird(birdX, birdY, birdWidth, birdHeight, birdImg);
            System.out.println("servlet da chay vao day");

            // Create a map to hold data
            Map<String, Object> data = new HashMap<>();
            data.put("background", background);
            data.put("bird", bird);
            data.put("topPipeImg", topPipeImg);
            data.put("bottomPipeImg", bottomPipeImg);
            data.put("pipeWidth", pipeWidth);
            data.put("pipeHeight", pipeHeight);

            // Convert map to JSON using Gson
            Gson gson = new Gson();
            String json = gson.toJson(data);

            // Write JSON as response
            resp.setContentType("application/json");
            resp.getWriter().write(json);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
