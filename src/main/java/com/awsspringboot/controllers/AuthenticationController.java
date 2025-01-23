package com.awsspringboot.controllers;

import com.awsspringboot.controllers.dtos.CognitoTokenResponseDto;
import com.awsspringboot.controllers.dtos.TokenDto;
import com.awsspringboot.controllers.dtos.UrlDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@RestController
public class AuthenticationController {
    @Value("${auth.cognitoUri}")
    private String cognitoUrl;
    @Value("${spring.security.oauth2.resourceserver.jwt.clientId}")
    private String clientId;
    private String redirectUri = "http%3A%2F%2Flocalhost%3A4200%2Fauth%2Fcallback";
    @Value("${spring.security.oauth2.resourceserver.jwt.clientSecret}")
    private String clientSecret;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @GetMapping("/auth/url")
    public ResponseEntity<UrlDTO> url() {
        String url = cognitoUrl +
                "/login?" +
                "response_type=code&" +
                "client_id=" + clientId + "&" +
                "redirect_uri=" + redirectUri + "&" +
                "scope=openid+profile+email&";
        return ResponseEntity.ok(new UrlDTO(url));
    }

    @GetMapping("/auth/callback")
    public ResponseEntity<TokenDto> callback(@RequestParam("code") String code) {
        String urlStr = cognitoUrl + "/oauth2/token?" +
                "grant_type=authorization_code&" +
                "client_id=" + clientId + "&" +
                "client_secret=" + clientSecret + "&" +
                "code=" + code + "&" +
                "redirect_uri=" + redirectUri;
        String authenticationInfo = clientId + ":" + clientSecret;
        String basicAuthentication = Base64.getEncoder().encodeToString(authenticationInfo.getBytes());
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(cognitoUrl + "/oauth2/token?"))
                .header("Content-type", "application/x-www-form-urlencoded")
                //.header("Authorization", "Basic " + basicAuthentication)
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials&" +
                        "client_id=4645odacscv77g0oln05mn9iad" + "&" +
                        "client_secret=dibqt2bc9t1dn7qqp4hgddcvl5v3hmbcl8f29tqpk3la6ens6nk" + "&" +
                        "code=" + code + "&" +
                        "scope=default-m2m-resource-server-islmmb/read&"+
                        "redirect_uri=" + redirectUri))
                .build();

        HttpResponse<String> response;
        try(HttpClient httpClient = HttpClient.newHttpClient()) {
            try {
                response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get token");
        }

        CognitoTokenResponseDto token;
        try {
            token = MAPPER.readValue(response.body(), CognitoTokenResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(new TokenDto(token.access_token()));
    }
}
