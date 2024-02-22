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
import java.util.*;

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
    private int velocityY = -9; //move bird up/down speed.
    private int gravity = 1;
    private ArrayList<Pipe> pipes = new ArrayList<>();
    private Random random = new Random();
    private boolean gameOver = false;
    private double score = 0;

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
            case "move":
                move(req, resp);
                break;
            case "restart":
                restart(req, resp);
                break;
        }
    }

    private void restart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        gameOver = false;
        score = 0;
        pipes.clear();
        bird = new Bird(birdX, birdY, birdWidth, birdHeight, birdImg); // Create a new bird object
//        System.out.println("bird restart: " + bird.toString());
        // Reset additional variables affecting bird movement
        velocityX = -4;
        velocityY = -9;

        Map<String, Object> data = new HashMap<>();
        data.put("background", background);
        data.put("bird", bird);
        data.put("topPipeImg", topPipeImg);
        data.put("bottomPipeImg", bottomPipeImg);
        data.put("pipeWidth", pipeWidth);
        data.put("pipeHeight", pipeHeight);

        Gson gson = new Gson();
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(data));
    }


    private void jump(HttpServletRequest req, HttpServletResponse resp) {
        if (bird != null) {
            velocityY = -12;
        }
    }

    private void move(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String jump = req.getParameter("jump");
        if (jump != null && jump.equals("true")) {
            jump(req, resp);
        }

        if (bird != null) {
            velocityY += gravity;
            bird.setY(bird.getY() + velocityY);
            bird.setY(Math.max(bird.getY(), 0)); //apply gravity to current bird.y, limit the bird.y to top of the canvas

            movePipes();

            Map<String, Object> response = new HashMap<>();
            response.put("bird", bird);
            response.put("score", score);
            response.put("gameOver", gameOver);
            response.put("pipes", pipes);

            Gson gson = new Gson();
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(response));
        } else System.out.println("bird is null");

    }

    private void movePipes() {
        Iterator<Pipe> iterator = pipes.iterator();
        while (iterator.hasNext()) {
            Pipe pipe = iterator.next();
            pipe.setX(pipe.getX() + velocityX);

            if (!pipe.isPassed() && bird.getX() > pipe.getX() + pipe.getWidth()) {
                score += 0.5; //0.5 because there are 2 pipes! so 0.5*2 = 1, 1 for each set of pipes
                pipe.setPassed(true);
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }

            if (pipe.getX() < -pipeWidth) {
                iterator.remove();
            }
        }
        if (bird.getY() > boardHeight) {
            gameOver = true;
        }
    }

    private boolean collision(Bird a, Pipe b) {
        return a.getX() < b.getX() + b.getWidth() &&   //a's top left corner doesn't reach b's top right corner
                a.getX() + a.getWidth() > b.getX() &&   //a's top right corner passes b's top left corner
                a.getY() < b.getY() + b.getHeight() &&  //a's top left corner doesn't reach b's bottom left corner
                a.getY() + a.getHeight() > b.getY();    //a's bottom left corner passes b's top left corner
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
//            System.out.println("servlet da chay vao day");
//            System.out.println(bird.toString());

            // Create a map to hold data
            Map<String, Object> data = new HashMap<>();
            data.put("background", background);
            data.put("bird", bird);
            data.put("topPipeImg", topPipeImg);
            data.put("bottomPipeImg", bottomPipeImg);
            data.put("pipeWidth", pipeWidth);
            data.put("pipeHeight", pipeHeight);
            data.put("score",score);
            data.put("gameOver",gameOver);

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
