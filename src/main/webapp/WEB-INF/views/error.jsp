<%@ page isErrorPage="true" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<html>
<head>
    <title>Error ${statusCode}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #ffe6e6;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        .container {
            text-align: center;
            border: 2px solid #ff4d4d;
            padding: 20px;
            border-radius: 10px;
            background-color: #fff0f0;
            max-width: 600px;
        }
        h1 {
            color: #cc0000;
        }
        p {
            font-size: 18px;
            color: #666;
        }
        a {
            text-decoration: none;
            color: #008CBA;
            font-size: 16px;
            padding: 8px 16px;
            border: 2px solid #008CBA;
            border-radius: 5px;
            transition: 0.3s;
        }
        a:hover {
            background-color: #008CBA;
            color: white;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Error ${statusCode}</h1>
    <p>${fn:escapeXml(message)}</p>
    <p><a href="/">Return to Home</a></p>
</div>
</body>
</html>