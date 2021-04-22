package com.allegro.pattern.finder.api;

import com.allegro.pattern.finder.api.exceptions.AmbiguousImageException;
import com.allegro.pattern.finder.api.exceptions.FileEmptyException;
import com.allegro.pattern.finder.service.ImageProcessor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Rest controller for managing image upload.
 * Provides an endpoint for retrieving a response based on uploaded image:
 * I) if the uploaded image has a white-red stripe pattern, returns an image with pattern aligned vertically, white up
 * II) if the uploaded image has no pattern, returns 204 No Content response
 * III) if the uploaded image has more patterns conflicting with each other, returns 400 Bad Request response
 */
@RestController
public class ImageUploadController {

    /**
     *
     * @param multipartFile uploaded file
     * @return ByteArray containing byte data of the image; since this endpoint produces IMAGE_PNG_VALUE MediaType, browser handles the conversion
     * @throws FileEmptyException if provided MultipartFile is empty
     * @throws AmbiguousImageException if provided image has more than 1 pattern
     * @throws com.allegro.pattern.finder.api.exceptions.NoPatternException if provided image has no patterns
     */
    @PostMapping(value = "/rotate", consumes = "multipart/form-data", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] fileUploadAndServe(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) { // Can't be empty
            if (Objects.equals(multipartFile.getContentType(), "image/png")) {
                ImageProcessor processor = new ImageProcessor(ImageIO.read(multipartFile.getInputStream())); // Here's the magic
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                ImageIO.write(processor.processImage(), "png", bao); // writing image data to byte array output stream
                return bao.toByteArray(); // returning image in byte array format
            }
        }
        throw new FileEmptyException();
    }
}
