package com.awsspringboot.controllers;

import com.awsspringboot.controllers.dtos.MessageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
public class PrivateController {
    @Value("${auth.cognitoUri}")
    private String cognitoUrl;

    @GetMapping("/private")
    public ResponseEntity<MessageDTO> privateMessage(@RequestHeader(name="Authorization") String token) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(cognitoUrl + "/oauth2/userInfo?"))
                .header("Authorization", token)
                .GET()
                .build();
        HttpResponse<String> response;
        try(HttpClient httpClient = HttpClient.newHttpClient()) {
            try {
                response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.ok(new MessageDTO("Hello, " + response.body() + "!"));
    }
}
