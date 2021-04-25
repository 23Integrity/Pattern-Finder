package com.allegro.pattern.finder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ImageTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private BufferedImage noPattern = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
    private BufferedImage doublePattern = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
    private BufferedImage onePattern = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
    private BufferedImage emptyImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);

    public ImageTests() {
        generateImages();
    }

    @Test
    public void imageWithNoPatternShouldReturn204() throws Exception {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(noPattern, "png", bao);

        MockMultipartFile file = new MockMultipartFile("file", "none.png", MediaType.IMAGE_PNG_VALUE,
                bao.toByteArray());

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/rotate").file(file)).andExpect(status().isNoContent());
    }

    @Test
    public void imageWithTooManyPatternsShouldReturn400() throws Exception {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(doublePattern, "png", bao);

        MockMultipartFile file = new MockMultipartFile("file", "double.png", MediaType.IMAGE_PNG_VALUE,
                bao.toByteArray());

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/rotate").file(file)).andExpect(status().isBadRequest());
    }

    @Test
    public void imageWithOnePatternShouldReturnOk() throws Exception {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(onePattern, "png", bao);

        MockMultipartFile file = new MockMultipartFile("file", "one.png", MediaType.IMAGE_PNG_VALUE,
                bao.toByteArray());

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/rotate").file(file)).andExpect(status().isOk());
    }

    @Test
    public void imageShouldNotBeEmpty() throws Exception {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(emptyImage, "png", bao);

        MockMultipartFile file = new MockMultipartFile("file", "empty.png", MediaType.IMAGE_PNG_VALUE,
                bao.toByteArray());

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/rotate").file(file)).andExpect(status().isNoContent());
    }

    @Test
    public void fileHasToBeImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "text.txt", MediaType.TEXT_PLAIN_VALUE,
               "Testing".getBytes());

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/rotate").file(file)).andExpect(status().isBadRequest());
    }


    private void generateImages() {
        int RED = new Color(255, 0, 0).getRGB();
        int WHITE = new Color(255, 255, 255).getRGB();

        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                noPattern.setRGB(i, j, 0);
                onePattern.setRGB(i, j, 0);
                doublePattern.setRGB(i, j, 0);

                if (i == 0) {
                    if (j < 3) {
                        onePattern.setRGB(i, j, RED);
                        doublePattern.setRGB(i, j, RED);
                    }
                    else {
                        onePattern.setRGB(i, j, WHITE);
                        doublePattern.setRGB(i, j, WHITE);
                    }
                }
                if (i == 1) {
                    if (j < 3) {
                        doublePattern.setRGB(i, j, RED);
                    }
                    else {
                        doublePattern.setRGB(i, j, WHITE);
                    }
                }
            }
        }
    }
}

