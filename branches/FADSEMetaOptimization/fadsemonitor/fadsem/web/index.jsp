<%-- 
    Document   : index
    Created on : Jul 1, 2010, 9:49:55 AM
    Author     : Horia Calborean
--%>

<%@page import="net.sf.json.JSONObject"%>
<%@page import="java.net.InetAddress"%>
<%@page import="io.FadseConnector"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
            JSONObject simualtionStatus = null;
            String port = request.getParameter("port");
            String address = request.getParameter("address");
            FadseConnector connector = null;
            boolean connected = false;
            if (port != null && !port.equals("") && address != null && !address.equals("")) {
                System.out.println("Building the FadseConnector");
                int p = Integer.parseInt(port);
                connector = FadseConnector.getInstance(p, InetAddress.getByName(address));
                connected = true;
                System.out.println("Trying to read the object from the stream");
                simualtionStatus = (JSONObject.fromObject(connector.getNumberOfActiveSimulations()));
            }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>FADSE Monitor</title>
    </head>
    <body>
        <%=simualtionStatus%>
    </body>
</html>
