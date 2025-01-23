package com.awsspringboot.controllers;

import com.awsspringboot.controllers.dtos.MessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class PublicController {
    @GetMapping("/public")
    public ResponseEntity<MessageDTO> publicEndpoint() {
        return ResponseEntity.ok(new MessageDTO("How are you?"));
    }
}
