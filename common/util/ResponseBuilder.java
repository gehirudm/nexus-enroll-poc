package common.util;

import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder {
    
    public static class Response {
        private boolean success;
        private String message;
        private Object data;
        private Map<String, Object> metadata;
        
        public Response() {
            this.metadata = new HashMap<>();
        }
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    public static Response success(Object data) {
        Response response = new Response();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }
    
    public static Response success(String message, Object data) {
        Response response = new Response();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
    
    public static Response error(String message) {
        Response response = new Response();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
    
    public static Response error(String message, Object data) {
        Response response = new Response();
        response.setSuccess(false);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}
