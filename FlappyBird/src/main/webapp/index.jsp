<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="Model.Bird" %><%--
  Created by IntelliJ IDEA.
  User: khuongvo
  Date: 21/02/2024
  Time: 21:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" isELIgnored="false" %>
<%
    String webUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" href="<%=webUrl%>/Resources/bird.png">
    <title>Flappy bird</title>
    <!--jQuery-->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
</head>
<%
    String background = request.getAttribute("background") == null ? "Resources/background.png" : request.getAttribute("background").toString(),
            topPipeImg = request.getAttribute("topPipeImg") == null ? "Resources/toppipe.png" : request.getAttribute("topPipeImg").toString(),
            bottomPipeImg = request.getAttribute("bottomPipeImg") == null ? "Resources/bottompipe.png" : request.getAttribute("bottomPipeImg").toString();
    Bird bird = (Bird) request.getAttribute("bird");
%>
<body>
<style>
    html, body {
        width: 100vw;
        height: 100vh;
    }

    body {
        margin: 0;
        background-image: url(<%=webUrl+"/"+background%>);
        background-size: 100% 100%;
        overflow: hidden;

        position: relative;
    }

    #bird {
        position: absolute;
    }

    .pipe {
        position: absolute;
        width: 64px;
        height: 100vh;
    }
    #score {
        position: absolute;
        top: 10px;
        left: 20px;
        z-index: 1;
    }
</style>
<img id="bird">
<h1 id="score"></h1>
<script>
    let boardWidth, boardHeight,
        pipeWidth, pipeHeight,
        bird, score, gameOver, pipes
    let countPipe = 0
    let jump = false
    let gameLoopInterval, placeTimerInterval

    $(document).ready(function () {
        start();
       placeTimerInterval = setInterval(placePipeTimer, 2000)
       gameLoopInterval = setInterval(gameLoop, 1000 / 30)

        $(document).on('keydown', jumpEvent)
    })

    function gameLoop() {
        // console.log('gameLoop is running')
        $.ajax({
            url: "controller",
            type: "get",
            data: {
                action: "move",
                jump: jump
            },
            success: function (data) {
                // console.log(data)
                if (data != null) {
                    bird = data.bird,
                        score = data.score,
                        gameOver = data.gameOver,
                        pipes = data.pipes;

                    //
                    renderBird(bird)
                    renderPipes(pipes)
                    renderScore()

                    if (gameOver) {
                        clearInterval(gameLoopInterval);
                        clearInterval(placeTimerInterval);
                        let confirmed = confirm("Your score: " + score + "\nRestart game?");
                        if (confirmed) {
                            // Send an AJAX request to the servlet to restart the game
                            $.ajax({
                                url: "controller",
                                type: "GET",
                                data: {
                                    action: "restart"
                                },
                                success: function (data) {
                                    // Update the game state and DOM based on the response
                                    updateDOM(data);
                                    window.location.reload()
                                },
                                error: function (xhr) {
                                    console.error(xhr);
                                }
                            });
                        }
                    }
                }

                jump = false
            },
            error: function (xhr) {
                console.error(xhr)
            }
        })
    }

    function placePipeTimer() {
        $.ajax({
            url: 'controller',
            method: 'get',
            data: {
                action: 'place-pipes'
            },
            error: function (xhr) {
                console.error(xhr);
            }
        });
    }

    function start() {
        boardWidth = window.innerWidth
        boardHeight = window.innerHeight
        $.ajax({
            url: "controller",
            type: "GET",
            data: {
                action: "start",
                boardWidth: boardWidth,
                boardHeight: boardHeight
            },
            success: function (data) {
                // console.log(data)
                updateDOM(data)
            },
            error: function (xhr) {
                console.error(xhr)
            }
        })
    }

    function updateDOM(data) {
        // Access attributes from data object
        const background = data.background;
        bird = data.bird;
        const topPipeImg = data.topPipeImg;
        const bottomPipeImg = data.bottomPipeImg;
        pipeWidth = data.pipeWidth;
        pipeHeight = data.pipeHeight;
        score = data.score;
        gameOver = data.gameOver

        renderBird(bird)
        renderScore()
    }

    function renderBird(bird) {
        $('#bird').remove()

        const nBird = $('<img>').attr("id", "bird").css({
            top: bird.y,
            left: bird.x,
            width: bird.width,
            height: bird.height
        }).attr("src", "<%=webUrl%>/" + bird.image)

        $('body').append(nBird)
    }

    function renderPipes(pipes) {
        $('img.pipe').remove()
        let body = $('body')
        pipes.forEach((pipe, index) => {
            let temp
            if (index % 2 != 0) {
                temp = $('<img>').addClass('pipe').css({
                    top: pipe.y,
                    left: pipe.x
                }).attr('src', '<%=webUrl+"/"+bottomPipeImg%>').attr('id', countPipe)
            } else {
                temp = $('<img>').addClass('pipe').css({
                    top: pipe.y,
                    left: pipe.x
                }).attr('src', '<%=webUrl+"/"+topPipeImg%>').attr('id', countPipe)
            }
            body.append(temp)
            countPipe++
        })
    }

    function jumpEvent(event) {
        if (event.which == 32 || event.which == 38) { // 32 là Space, 38 là ArrowUp
            jump = true
        }
    }

    function renderScore(){
        $('#score').remove()
        $('body').append(
            $('<h1></h1>').attr('id','score').text(score)
        )
    }
</script>
</body>
</html>
