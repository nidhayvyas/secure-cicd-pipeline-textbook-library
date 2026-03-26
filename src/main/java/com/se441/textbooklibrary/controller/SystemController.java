package com.se441.textbooklibrary.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class SystemController {

    @Value("${app.environment:development}")
    private String environment;

    @GetMapping("/os")
    public Map<String, String> getOsInfo() throws UnknownHostException {
        Map<String, String> response = new HashMap<>();
        response.put("os", InetAddress.getLocalHost().getHostName());
        response.put("env", environment);
        return response;
    }

    @GetMapping("/live")
    public Map<String, String> liveness() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "live");
        return response;
    }

    @GetMapping("/ready")
    public Map<String, String> readiness() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ready");
        return response;
    }
}
