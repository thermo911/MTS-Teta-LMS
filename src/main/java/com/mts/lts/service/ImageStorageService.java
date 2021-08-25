package com.mts.lts.service;

import com.mts.lts.dao.ImageRepository;
import com.mts.lts.domain.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.*;

@Service
public class ImageStorageService {

    private final ImageRepository imageRepository;

    @Value("${image.file.storage.path}")
    private String path;

    public ImageStorageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public void save(Image prevImage, String contentType, InputStream is) {
        String filename;
        Image newImage;

        if (prevImage == null) {
            filename = UUID.randomUUID().toString();
            newImage = new Image(null, contentType, filename);
        } else {
            filename = prevImage.getFilename();
            newImage = prevImage;
            newImage.setContentType(contentType);
        }
        imageRepository.save(newImage);

        try (OutputStream os = Files.newOutputStream(
                Path.of(path, filename), CREATE, WRITE, TRUNCATE_EXISTING
        )) {
            is.transferTo(os);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void deleteImage(Image image) {
        if (image == null)
            return;
        try {
            Files.delete(Path.of(path, image.getFilename()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageRepository.delete(image);
    }

    public Optional<byte[]> getImageData(Image image) {
        if (image == null)
            return Optional.empty();

        try {
            return Optional.of(Files.readAllBytes(Path.of(path, image.getFilename())));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
