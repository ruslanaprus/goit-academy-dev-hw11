<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<html>
<head>
  <title>Current Time</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      background-color: #f4f4f9;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      margin: 0;
    }
    .container {
      text-align: center;
    }
    h1 {
      color: #333;
    }
    p {
      font-size: 20px;
    }
    a {
      text-decoration: none;
      color: #008CBA;
      font-size: 18px;
      padding: 10px 20px;
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
  <h1>Current Time in ${fn:escapeXml(zoneId)}</h1>
  <p>The current time is: <strong>${fn:escapeXml(formattedTime)}</strong></p>
  <p><a href="/">Back to Home</a></p>
</div>
</body>
</html>