package com.nexus.enrollment.admin.handler;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;

public class AdminHandler implements HttpHandler {
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "{\"message\": \"Admin Service\", \"status\": \"success\"}";
        
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
