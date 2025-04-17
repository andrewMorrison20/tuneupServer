package com.tuneup.tuneup.junit.services;



import com.google.cloud.storage.*;
import com.tuneup.tuneup.images.entities.Image;
import com.tuneup.tuneup.images.services.ImageService;
import com.tuneup.tuneup.images.repositories.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTests {

    @Mock
    private ImageRepository imageRepository;
    @Mock
    private Storage storage;
    @Mock
    private Bucket bucket;
    @Mock
    private Blob blob;

    private ImageService imageService;

    @BeforeEach
    void setUp() throws Exception {

        imageService = new ImageService(imageRepository);

        ReflectionTestUtils.setField(imageService, "storage", storage);
        ReflectionTestUtils.setField(imageService, "bucketName", "test-bucket");
        when(storage.get("test-bucket")).thenReturn(bucket);
    }

    @Test
    void uploadFile_Success() throws IOException {
        byte[] content = "hello".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file", "hello.txt", "text/plain", content);

        when(bucket.create(startsWith(""), eq(content), eq("text/plain")))
                .thenReturn(blob);
        when(blob.getMediaLink()).thenReturn("http://media-link");
        // simulate save
        Image saved = new Image();
        saved.setUrl("http://media-link");
        saved.setDescription("hello.txt");
        when(imageRepository.save(any())).thenReturn(saved);

        Image result = imageService.uploadFile(file);

        assertNotNull(result);
        assertEquals("http://media-link", result.getUrl());
        assertEquals("hello.txt", result.getDescription());
        verify(bucket).create(anyString(), eq(content), eq("text/plain"));
        verify(imageRepository).save(any());
    }

    @Test
    void downloadFile_Success() {
        byte[] data = {1, 2, 3};
        when(bucket.get("myfile")).thenReturn(blob);
        when(blob.getContent()).thenReturn(data);

        byte[] result = imageService.downloadFile("myfile");

        assertArrayEquals(data, result);
        verify(bucket).get("myfile");
    }

    @Test
    void downloadFile_NotFound() {
        when(bucket.get("nofile")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> imageService.downloadFile("nofile"));
        assertEquals("File not found: nofile", ex.getMessage());
    }
}

