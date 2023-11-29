package com.example.fileuploadanddownloadrestapi.service;
import com.example.fileuploadanddownloadrestapi.entity.ImageData;
import com.example.fileuploadanddownloadrestapi.repository.StorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;
import static com.example.fileuploadanddownloadrestapi.util.ImageUtils.compressImage;
import static com.example.fileuploadanddownloadrestapi.util.ImageUtils.decompressImage;

@Service
public class StorageService {

    private final StorageRepository repository;

    @Autowired
    public StorageService(StorageRepository repository) {
        this.repository = repository;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        ImageData image = repository.save(
                ImageData.builder()
                        .name(file.getOriginalFilename())
                        .type(file.getContentType())
                        .imageData(compressImage(file.getBytes()))
                        .imageUrl(generateImageUrl(file.getOriginalFilename()))
                        .build()
        );

        if (image != null) {
            return "File uploaded successfully: "+file.getOriginalFilename();
        }
        return "File can't upload";
    }

    public byte[] downloadImage(String imageName) {
        Optional<ImageData> dbImage = repository.findByName(imageName);
        byte[]image=decompressImage(dbImage.get().getImageData());
        return image;
    }

    public Optional<ImageData> getImageDataByName(String imageName) {
        return repository.findByName(imageName);
    }

    private String generateImageUrl(String imageName) {
        return "http://localhost:5920/image/" + imageName;
    }

}
