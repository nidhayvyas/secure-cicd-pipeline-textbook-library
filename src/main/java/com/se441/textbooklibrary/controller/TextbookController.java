package com.se441.textbooklibrary.controller;

import com.se441.textbooklibrary.dto.TextbookRequest;
import com.se441.textbooklibrary.model.Textbook;
import com.se441.textbooklibrary.repository.TextbookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class TextbookController {

    @Autowired
    private TextbookRepository textbookRepository;

    @PostMapping("/textbook")
    public ResponseEntity<Textbook> getTextbook(@RequestBody TextbookRequest request) {
        return textbookRepository.findById(request.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
