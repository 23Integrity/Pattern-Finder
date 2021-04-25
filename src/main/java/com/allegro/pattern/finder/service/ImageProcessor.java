package com.allegro.pattern.finder.service;

import com.allegro.pattern.finder.api.exceptions.AmbiguousImageException;
import com.allegro.pattern.finder.api.exceptions.NoPatternException;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Class serving a purpose of:
 * - scanning the image for pattern presence
 * - choosing correct image rotation
 * - image rotation and returning new image
 */
public class ImageProcessor {
    private final BufferedImage image;      // Actual image
    private final String[][] imageAsStringMatrix;     // representation of the image as matrix

    /**
     * Pattern structure for providing correct rotation of the image
     */
    private static class Pattern {
        private final int alignment; // vertical or horizontal
        private final boolean needsRotation; // white starts or red starts the pattern

        Pattern(int alignment, boolean needsRotation) {
            this.alignment = alignment;
            this.needsRotation = needsRotation;
        }

        /**
         * Value informing whether the pattern is aligned vertically or horizontally.
         *
         * If the alignment is 0 (vertical), image needs 0 or 180 degrees rotation.
         * If it's 1 (horizontal), image needs 90 or 270 degrees rotation
         * @return integer value representing pattern alignment - 0 for vertical, 1 for horizontal
         */
        protected int getAlignment() {
            return alignment;
        }

        /**
         * Value informing whether the patterns starts with white or red color - so if it needs to be rotated
         * by additional 180 degrees.
         *
         * If it starts with white, it doesn't need additional rotation (false)
         * If it starts with red, it starts with additional +180 degree rotation (true)
         * @return returns a boolean representing whether the image needs additional rotation.
         */
        protected boolean getNeedsRotation() {
            return needsRotation;
        }
    }

    /**
     * Constructor providing image and imageAsStringMatrix assignment
     * @param image Takes BufferedImage and assigns to fields: BufferedImage image and imageAsStringMatrix - uses imageToStringArray() to convert it
     */
    public ImageProcessor(BufferedImage image) {
        this.image = image;
        this.imageAsStringMatrix = imageToStringArray(image);
    }

    /**
     * Core of the ImageProcessor - provides pattern to rotate or throws an error if there's != 1 pattern
     * @return Rotated BufferedImage based on provided pattern or throw AmbiguousImageException if there's more than 1 pattern or NoPatternException if none
     * @throws AmbiguousImageException if there's more than 1 pattern provided
     * @throws NoPatternException if there's no pattern provided (ArrayList is empty)
     */
    public BufferedImage processImage() throws NoPatternException, AmbiguousImageException {
        ArrayList<Pattern> patternArrayList = findPatternList();
        if (patternArrayList.size() > 1) {
            throw new AmbiguousImageException();
        }
        else if (patternArrayList.isEmpty()) {
            throw new NoPatternException();
        }
        return rotate(patternArrayList.get(0));
    }

    /**
     * Scans the String matrix for 3 pixels white and 3 pixels red stripe pattern left - right and up - down.
     * @return Pattern ArrayList with patterns found in the image
     */
    private ArrayList<Pattern> findPatternList() {
        ArrayList<Pattern> patterns = new ArrayList<>();
        final String WHITE = String.valueOf(new Color(255, 255, 255).getRGB());
        final String RED = String.valueOf(new Color(255, 0, 0).getRGB());

        final String whitePattern = WHITE + WHITE + WHITE + RED + RED + RED;
        final String redPattern = RED + RED + RED + WHITE + WHITE + WHITE;

        // Search array left - right
        for (String[] colors : imageAsStringMatrix) {
            for (int column = 0; column < colors.length; column++) {
                if ((colors[column].equals(WHITE) || colors[column].equals(RED)) && column + 5 < colors.length) {
                    String probablePattern = colors[column] +
                            colors[column + 1] +
                            colors[column + 2] +
                            colors[column + 3] +
                            colors[column + 4] +
                            colors[column + 5];
                    if (probablePattern.equals(whitePattern)) {
                        patterns.add(new Pattern(0, false));
                    }
                    if (probablePattern.equals(redPattern)) {
                        patterns.add(new Pattern(1, false));
                    }
                }
            }
        }

        // Search the array up - down
        for (int row = 0; row < imageAsStringMatrix.length; row++) {
            for (int column = 0; column < imageAsStringMatrix[row].length; column++) {
                if ((imageAsStringMatrix[row][column].equals(WHITE) || imageAsStringMatrix[row][column].equals(RED))
                        && row + 5 < imageAsStringMatrix.length) {
                    String probablePattern = imageAsStringMatrix[row][column] +
                            imageAsStringMatrix[row + 1][column] +
                            imageAsStringMatrix[row + 2][column] +
                            imageAsStringMatrix[row + 3][column] +
                            imageAsStringMatrix[row + 4][column] +
                            imageAsStringMatrix[row + 5][column];

                    if (probablePattern.equals(whitePattern)) {
                        patterns.add(new Pattern(0, true));
                    }
                    if (probablePattern.equals(redPattern)) {
                        patterns.add(new Pattern(1, true));
                    }
                }
            }
        }
        return patterns;
    }

    /**
     * Image to String converter; converts image's pixels' RGB values to String and puts them in corresponding
     * place in the array.
     * @param image BufferedImage to be turned into RGB value String matrix
     * @return String matrix filled with RGB value of each pixel
     */
    private String[][] imageToStringArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        String[][] imageStringArray = new String[height][width];

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                imageStringArray[row][column] += String.valueOf(image.getRGB(column, row));
                imageStringArray[row][column] = imageStringArray[row][column].substring(4);
            }
        }
        return imageStringArray;
    }

    /**
     * Rotates the image by *Theta* degrees basing on it's alignment and whether it needs additional rotation
     * - Vertical - 0 or 180 degrees
     * - Horizontal - 90 or 270 degrees
     * @return new BufferedImage containing processed image basing on the pattern provided.
     */
    private BufferedImage rotate(Pattern pattern) {
        AffineTransform transform = new AffineTransform();
        BufferedImage out;
        // Vertical
        if (pattern.getNeedsRotation()) {
            double offset = 0;
            out = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

            // no rotation
            if (pattern.getAlignment() == 0) {
                transform.translate(offset, offset);
                transform.rotate(0, image.getWidth() / 2.0, image.getHeight() / 2.0);

                AffineTransformOp atop = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
                atop.filter(image, out);
            }

            // + 180 degrees rotation
            else {
                transform.translate(offset, offset);
                transform.rotate(Math.PI, image.getWidth() / 2.0, image.getHeight() / 2.0);

                AffineTransformOp atop = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
                atop.filter(image, out);
            }
        }
        // Horizontal
        else {
            double offset = (image.getHeight() - image.getWidth()) / 2.0;
            out = new BufferedImage(image.getHeight(), image.getWidth(), image.getType());

            // no rotation
            if (pattern.getAlignment() == 0) {
                transform.rotate(Math.PI / 2, image.getWidth(), image.getHeight() / 2.0);

                AffineTransformOp atop = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
                atop.filter(image, out);
            }

            // + 180 degrees rotation
            else {
                transform.rotate(3 * Math.PI / 2, image.getWidth() / 2.0, image.getHeight() / 2.0);
                transform.translate(offset, offset);

                AffineTransformOp atop = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
                atop.filter(image, out);
            }
        }
        return out;
    }
}
