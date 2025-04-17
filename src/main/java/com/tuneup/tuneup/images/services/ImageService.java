package com.tuneup.tuneup.images.services;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.StorageOptions;
import com.tuneup.tuneup.images.entities.Image;
import com.tuneup.tuneup.images.repositories.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${gcs.bucket.name}")
    private String bucketName;
    private final Storage storage;
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) throws IOException {
        // Load the file from the classpath
        InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream("tuneup-cloud-key.json");
        if (credentialsStream == null) {
            throw new FileNotFoundException("Service account key file not found in classpath: tuneup-cloud-key.json");
        }

        this.storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(credentialsStream))
                .build()
                .getService();
        this.imageRepository = imageRepository;
    }

    /**
     * Uploads a file to Google Cloud Storage and persists the image metadata.
     *
     * @param file the file to upload
     * @return the saved Image entity
     * @throws IOException if an error occurs during file upload
     */
    public Image uploadFile(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Bucket bucket = storage.get(bucketName);
        Blob blob = bucket.create(filename, file.getBytes(), file.getContentType());
        Image image = new Image();

        image.setUrl(blob.getMediaLink());
        image.setDescription(file.getOriginalFilename());
        return imageRepository.save(image);
    }

        public byte[] downloadFile(String fileName) {
        Bucket bucket = storage.get(bucketName);
        Blob blob = bucket.get(fileName);
        if (blob == null) {
            throw new RuntimeException("File not found: " + fileName);
        }
        return blob.getContent();
    }
}
