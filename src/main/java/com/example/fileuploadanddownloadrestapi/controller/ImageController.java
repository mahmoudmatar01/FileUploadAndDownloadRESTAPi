package com.example.fileuploadanddownloadrestapi.controller;

import com.example.fileuploadanddownloadrestapi.entity.ImageData;
import com.example.fileuploadanddownloadrestapi.service.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/image")
@AllArgsConstructor
public class ImageController {
    private final StorageService service;

    @PostMapping()
    public ResponseEntity<?> upload(@RequestParam MultipartFile file) {
        try {
            String response = service.uploadImage(file);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping(value = "/{fileName}", produces = MediaType.ALL_VALUE, consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> download(@PathVariable String fileName) {
        byte[] response = service.downloadImage(fileName);
        if (response != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/url/{fileName}")
    public ResponseEntity<?> getImageUrl(@PathVariable String fileName) {
        Optional<ImageData> imageData = service.getImageDataByName(fileName);
        return imageData.map(data -> ResponseEntity.status(HttpStatus.OK).body(data.getImageUrl()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found"));
    }
}
