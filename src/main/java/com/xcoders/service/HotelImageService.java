package com.xcoders.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import com.xcoders.dao.HotelImageDAO;
import com.xcoders.model.HotelImage;

/**
 * Service layer for hotel image operations.
 * Handles image uploads and database operations.
 */
public class HotelImageService {

    private final HotelImageDAO imageDAO = new HotelImageDAO();
    
    // Directory where images are stored
    private static final String IMAGE_DIR = "src/main/resources/hotel-images";

    /**
     * Upload a hotel image and save to database
     * @param hotelId Hotel ID
     * @param sourceFile Source image file to upload
     * @param imageType 'MAIN' or 'REFERENCE'
     * @return true if upload successful
     */
    public boolean uploadImage(int hotelId, File sourceFile, String imageType) {
        if (sourceFile == null || !sourceFile.exists()) {
            System.err.println("Source file does not exist: " + sourceFile);
            return false;
        }

        try {
            // Create directory if it doesn't exist
            Path imageDir = Paths.get(IMAGE_DIR);
            if (!Files.exists(imageDir)) {
                Files.createDirectories(imageDir);
            }

            // Generate unique filename
            String extension = getFileExtension(sourceFile.getName());
            String uniqueFilename = String.format("hotel_%d_%s_%s.%s", 
                    hotelId, 
                    imageType.toLowerCase(),
                    UUID.randomUUID().toString().substring(0, 8),
                    extension);
            
            // Destination path
            Path destPath = imageDir.resolve(uniqueFilename);

            // Copy file
            Files.copy(sourceFile.toPath(), destPath);
            System.out.println("[ImageService] Image uploaded: " + destPath);

            // Save to database
            String relativePath = "hotel-images/" + uniqueFilename;
            HotelImage image = new HotelImage(hotelId, relativePath, imageType);
            boolean saved = imageDAO.addImage(image);

            if (!saved) {
                System.err.println("[ImageService] Failed to save image record to database");
                // Delete the file if DB save fails
                Files.delete(destPath);
                return false;
            }

            return true;

        } catch (IOException e) {
            System.err.println("[ImageService] Error uploading image: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get main image for a hotel
     */
    public HotelImage getMainImage(int hotelId) {
        return imageDAO.getMainImage(hotelId);
    }

    /**
     * Get all reference images for a hotel
     */
    public List<HotelImage> getReferenceImages(int hotelId) {
        return imageDAO.getReferenceImages(hotelId);
    }

    /**
     * Get all images for a hotel
     */
    public List<HotelImage> getHotelImages(int hotelId) {
        return imageDAO.getHotelImages(hotelId);
    }

    /**
     * Delete an image
     */
    public boolean deleteImage(int imageId) {
        return imageDAO.deleteImage(imageId);
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "jpg";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot + 1).toLowerCase();
        }
        return "jpg";
    }

    /**
     * Validate image file
     */
    public static boolean isValidImageFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }

        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
               name.endsWith(".png") || name.endsWith(".gif") ||
               name.endsWith(".bmp");
    }
}
