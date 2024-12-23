package com.tuneup.tuneup.images;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${gcs.bucket.name}")
    private String bucketName;
    private final Storage storage;
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) throws IOException {
        this.storage  = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream("tuneup/src/main/resources/tuneup-cloud-key.json")))
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
