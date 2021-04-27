package com.michalwesoly.pattern.finder.api;

import com.michalwesoly.pattern.finder.api.exceptions.AmbiguousImageException;
import com.michalwesoly.pattern.finder.api.exceptions.NoPatternException;
import com.michalwesoly.pattern.finder.service.ImageProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
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
     * Endpoint providing rotated image
     *
     * @param multipartFile uploaded file
     * @return ResponseEntity containing byte data of the image; since this endpoint produces IMAGE_PNG_VALUE MediaType, browser handles the conversion
     */
    @PostMapping(value = "/rotate", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> fileUploadAndServe(@RequestParam("file") MultipartFile multipartFile, HttpServletResponse response) throws IOException {
        if (!multipartFile.isEmpty()) { // Can't be empty
            if (Objects.equals(multipartFile.getContentType(), "image/png")) {
                try {
                    ImageProcessor processor = new ImageProcessor(ImageIO.read(multipartFile.getInputStream())); // Here's the magic
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    ImageIO.write(processor.processImage(), "png", bao); // converting image to binary
                    return ResponseEntity.ok(bao.toByteArray()); // and returning it
                }
                catch(AmbiguousImageException e) {
                    return ResponseEntity.badRequest().body(null);
                }
                catch(NoPatternException e) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
                }
            }
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
