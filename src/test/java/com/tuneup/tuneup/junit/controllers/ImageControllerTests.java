package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.images.controllers.ImageController;
import com.tuneup.tuneup.images.dtos.ImageDto;
import com.tuneup.tuneup.images.entities.Image;
import com.tuneup.tuneup.images.mappers.ImageMapper;
import com.tuneup.tuneup.images.services.ImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageControllerTests {

    @Mock
    private ImageService imageService;

    @Mock
    private ImageMapper imageMapper;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ImageController imageController;

    @Test
    void uploadProfilePicture_ShouldReturnImageDto() throws IOException {
        Image image = new Image();
        image.setFilename("test.jpg");
        ImageDto imageDto = new ImageDto();
        imageDto.setFilename("test.jpg");

        when(imageService.uploadFile(file)).thenReturn(image);
        when(imageMapper.toImageDto(image)).thenReturn(imageDto);

        ResponseEntity<?> response = imageController.uploadProfilePicture(file);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(imageDto, response.getBody());
    }

    @Test
    void uploadProfilePicture_ShouldReturnBadRequestForInvalidFile() throws IOException {
        when(imageService.uploadFile(file)).thenThrow(new IllegalArgumentException("Invalid file"));

        ResponseEntity<?> response = imageController.uploadProfilePicture(file);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invalid file"));
    }

    @Test
    void uploadProfilePicture_ShouldReturnServiceUnavailableOnIOException() throws IOException {
        when(imageService.uploadFile(file)).thenThrow(new IOException("File upload failed"));

        ResponseEntity<?> response = imageController.uploadProfilePicture(file);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("File upload failed"));
    }

    @Test
    void getProfilePicture_ShouldReturnImageContent() {
        String fileName = "test.jpg";
        byte[] content = new byte[]{1, 2, 3};
        when(imageService.downloadFile(fileName)).thenReturn(content);

        ResponseEntity<byte[]> response = imageController.getProfilePicture(fileName);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(content, response.getBody());
    }

    @Test
    void getProfilePicture_ShouldReturnNotFoundForInvalidFile() {
        String fileName = "invalid.jpg";
        when(imageService.downloadFile(fileName)).thenThrow(new RuntimeException("File not found"));

        ResponseEntity<byte[]> response = imageController.getProfilePicture(fileName);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
