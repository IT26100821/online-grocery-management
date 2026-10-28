
package com.grocery.onlinegrocerymanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ProductImageService {

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024; // 5 MB

    private final Path imageFolder =
            Paths.get("data", "product-images")
                    .toAbsolutePath()
                    .normalize();

    // SAVE PRODUCT IMAGE
    public String saveProductImage(MultipartFile file) {

        // Photo is optional
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Image must be smaller than 5 MB"
            );
        }

        // Accept JPG and PNG only
        String contentType = file.getContentType();
        String format;

        if ("image/jpeg".equalsIgnoreCase(contentType)) {
            format = "jpg";

        } else if ("image/png".equalsIgnoreCase(contentType)) {
            format = "png";

        } else {
            throw new IllegalArgumentException(
                    "Only JPG and PNG images are allowed"
            );
        }

        try {

            byte[] bytes = file.getBytes();

            if (bytes.length > MAX_FILE_SIZE) {
                throw new IllegalArgumentException(
                        "Image must be smaller than 5 MB"
                );
            }

            // Check actual image file signature
            boolean validSignature;

            if ("jpg".equals(format)) {
                validSignature =
                        bytes.length >= 3 &&
                                (bytes[0] & 0xFF) == 0xFF &&
                                (bytes[1] & 0xFF) == 0xD8 &&
                                (bytes[2] & 0xFF) == 0xFF;
            } else {
                byte[] pngSignature = {
                        (byte) 137, 80, 78, 71,
                        13, 10, 26, 10
                };

                validSignature =
                        bytes.length >= pngSignature.length;

                for (int i = 0;
                     validSignature && i < pngSignature.length;
                     i++) {

                    if (bytes[i] != pngSignature[i]) {
                        validSignature = false;
                    }
                }
            }

            if (!validSignature) {
                throw new IllegalArgumentException(
                        "Invalid image format"
                );
            }

            // Decode and validate image
            BufferedImage image = ImageIO.read(
                    new ByteArrayInputStream(bytes)
            );

            if (image == null ||
                    image.getWidth() > 5000 ||
                    image.getHeight() > 5000) {

                throw new IllegalArgumentException(
                        "Invalid image or image dimensions too large"
                );
            }

            // Create folder
            Files.createDirectories(imageFolder);

            // Generate unique filename
            String fileName =
                    UUID.randomUUID() + "." + format;

            Path destination = imageFolder.resolve(fileName);

            // Re-encode image before saving
            boolean saved = ImageIO.write(
                    image,
                    format,
                    destination.toFile()
            );

            if (!saved) {
                throw new IllegalArgumentException(
                        "Unable to save image format"
                );
            }

            return fileName;

        } catch (IOException e) {

            throw new UncheckedIOException(
                    "Error saving product image", e
            );
        }
    }

    // GET SAFE IMAGE PATH
    public Path getImagePath(String fileName) {

        if (fileName == null ||
                !fileName.matches(
                        "[0-9a-fA-F-]{36}\\.(jpg|png)"
                )) {

            throw new IllegalArgumentException(
                    "Invalid image filename"
            );
        }

        Path imagePath =
                imageFolder.resolve(fileName).normalize();

        if (!imagePath.startsWith(imageFolder)) {
            throw new IllegalArgumentException(
                    "Invalid image path"
            );
        }

        return imagePath;
    }
}
