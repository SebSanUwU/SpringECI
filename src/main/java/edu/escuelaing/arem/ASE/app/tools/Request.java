package edu.escuelaing.arem.ASE.app.tools;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private String resource;

    public Request(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Map<String, String> parseQuery() {
        Map<String, String> queryParams = new HashMap<>();
        if (resource == null || resource.isEmpty()) {
            return queryParams;
        }
        String[] pairs = resource.split("\\?");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                queryParams.put(keyValue[0], keyValue[1]);
            } else if (keyValue.length == 1) {
                queryParams.put(keyValue[0], "");
            }
        }
        return queryParams;
    }
}
