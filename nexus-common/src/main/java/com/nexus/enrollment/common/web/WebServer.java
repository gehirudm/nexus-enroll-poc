package com.nexus.enrollment.common.web;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.json.JavalinJackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nexus.enrollment.common.exceptions.HttpException;
import com.nexus.enrollment.common.exceptions.InvalidFormatException;
import com.nexus.enrollment.common.util.ResponseBuilder;

public class WebServer {
    
    public static Javalin createServer() {
        return Javalin.create(config -> {
            // Configure Jackson to handle Java 8 date/time types
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            config.jsonMapper(new JavalinJackson(objectMapper));
        });
    }
    
    public static void configureExceptionHandlers(Javalin app) {
        // Handle all custom HttpExceptions with a single handler
        app.exception(HttpException.class, (e, ctx) -> e.handleResponse(ctx));
        
        // Handle Javalin built-in exceptions
        app.exception(BadRequestResponse.class, (e, ctx) -> {
            ctx.status(400).json(ResponseBuilder.error("Bad request: " + e.getMessage()));
        });
        
        app.exception(NotFoundResponse.class, (e, ctx) -> {
            ctx.status(404).json(ResponseBuilder.error("Resource not found: " + e.getMessage()));
        });
        
        app.exception(InternalServerErrorResponse.class, (e, ctx) -> {
            ctx.status(500).json(ResponseBuilder.error("Internal server error: " + e.getMessage()));
        });
        
        // Handle NumberFormatException (common for invalid ID formats)
        app.exception(NumberFormatException.class, (e, ctx) -> {
            InvalidFormatException formatException = new InvalidFormatException("Invalid number format", e);
            formatException.handleResponse(ctx);
        });
        
        // Generic exception handler (catch-all)
        app.exception(Exception.class, (e, ctx) -> {
            System.err.println("Unhandled exception: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).json(ResponseBuilder.error("An unexpected error occurred"));
        });
    }
    
    public static Javalin createAndConfigureServer() {
        Javalin app = createServer();
        configureExceptionHandlers(app);
        return app;
    }
}
