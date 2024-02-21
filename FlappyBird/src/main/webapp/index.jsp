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
            border: 1px solid black;
        }

        .pipe {
            position: absolute;
            width: 64px;
            height: 100vh;
        }
    </style>
    <img id="bird">
    <img class="pipe" style="top: -400px; left: 90%" src="<%=webUrl+"/"+topPipeImg%>">
    <img class="pipe" style="top: 840px; left: 90%" src="<%=webUrl+"/"+bottomPipeImg%>">
<script>
    let boardWidth, boardHeight

    $(document).ready(function () {
        start();
    })

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
            success: function (data){
                console.log(data)
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
        const bird = data.bird;
        const topPipeImg = data.topPipeImg;
        const bottomPipeImg = data.bottomPipeImg;

        // Update styles and image sources
        $("#bird").css({
            top: bird.y,
            left: bird.x,
            width: bird.width,
            height: bird.height
        });
        $("#bird").attr("src","<%=webUrl%>/" + bird.image);
        // Update pipe styles and image sources based on data
    }

</script>
</body>
</html>
