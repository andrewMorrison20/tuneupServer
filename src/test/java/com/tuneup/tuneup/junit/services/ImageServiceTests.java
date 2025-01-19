package com.tuneup.tuneup.junit.services;

import com.google.cloud.storage.*;
import com.tuneup.tuneup.images.Image;
import com.tuneup.tuneup.images.ImageRepository;
import com.tuneup.tuneup.images.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private Storage storage;

    @Mock
    private Bucket bucket;

    @Mock
    private Blob blob;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        when(bucket.create(anyString(), (byte[]) any(), anyString())).thenReturn(blob);
        when(storage.get(anyString())).thenReturn(bucket);
        when(blob.getMediaLink()).thenReturn("https://test-url.com/image.jpg");
    }


    @Test
    void testUploadFile() throws IOException {
        String filename = "test.jpg";
        Image image = new Image();
        image.setUrl("https://test-url.com/image.jpg");
        image.setDescription(filename);

        when(file.getOriginalFilename()).thenReturn(filename);
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(file.getContentType()).thenReturn("image/jpeg");
        when(imageRepository.save(any(Image.class))).thenReturn(image);


        Image result = imageService.uploadFile(file);

        assertNotNull(result);
        assertEquals("https://test-url.com/image.jpg", result.getUrl());
        assertEquals(filename, result.getDescription());
    }

    @Test
    void testDownloadFileReturnsFile() {
        String fileName = UUID.randomUUID().toString();
        byte[] content = new byte[]{1, 2, 3};

        when(bucket.get(fileName)).thenReturn(blob);
        when(blob.getContent()).thenReturn(content);

        byte[] result = imageService.downloadFile(fileName);

        assertNotNull(result);
        assertArrayEquals(content, result);
    }

    @Test
    void testDownloadFileExpectsException() {
        String fileName = "non-existent.jpg";

        when(bucket.get(fileName)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> imageService.downloadFile(fileName));
        assertEquals("File not found: " + fileName, exception.getMessage());
    }
}

