package com.example.fileuploadanddownloadrestapi.service;

import com.example.fileuploadanddownloadrestapi.entity.ImageData;
import com.example.fileuploadanddownloadrestapi.repository.StorageRepository;
import com.example.fileuploadanddownloadrestapi.util.ImageUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StorageService {
    private final StorageRepository repository;

    public String uploadImage(MultipartFile file) throws IOException {
        String imagePath = generateUniqueImagePath(file.getOriginalFilename());
        ImageData image = repository.save(
                ImageData.builder()
                        .name(file.getOriginalFilename())
                        .type(file.getContentType())
                        .imagePath(imagePath)
                        .imageData(ImageUtils.compressImage(file.getBytes()))
                        .build()
        );

        if (image != null) {
            String imageUrl = getImageUrl(imagePath); // Generate the full URL for the image path
            return "File uploaded successfully: " + imageUrl;
        }
        return "File can't upload";
    }

    public byte[] downloadImage(String imageName) {
        Optional<ImageData> dbImage = repository.findImageByName(imageName);
        if (dbImage.isPresent()) {
            return ImageUtils.decompressImage(dbImage.get().getImageData());
        }
        return null;
    }

    private String getImageUrl(String imagePath) {
        return "http://localhost:5920/" + imagePath;
    }

    private String generateUniqueImagePath(String originalFilename) {
        return "images/" + System.currentTimeMillis() + "_" + originalFilename;
    }
}
