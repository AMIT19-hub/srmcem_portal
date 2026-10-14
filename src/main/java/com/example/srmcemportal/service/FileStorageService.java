
package com.example.srmcemportal.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDirectory;

    public FileStorageService(
            @Value("${app.upload.dir}") String uploadDir) {
        this.uploadDirectory = Paths.get(uploadDir)
                .toAbsolutePath().normalize();
    }

    public String saveResume(MultipartFile file) throws IOException {
        validateFile(file, 5);

        String contentType = file.getContentType();
        if (contentType == null
                || !contentType.equalsIgnoreCase("application/pdf")) {
            throw new RuntimeException("Only PDF resumes are allowed");
        }

        return saveFile(file, "resumes", ".pdf");
    }

    public String saveProfileImage(MultipartFile file)
            throws IOException {
        validateFile(file, 2);

        String contentType = file.getContentType();
        if (contentType == null
                || !(contentType.equalsIgnoreCase("image/jpeg")
                || contentType.equalsIgnoreCase("image/png"))) {
            throw new RuntimeException(
                    "Only JPG and PNG images are allowed");
        }

        String extension = contentType.equalsIgnoreCase("image/png")
                ? ".png" : ".jpg";

        return saveFile(file, "profiles", extension);
    }

    private void validateFile(MultipartFile file, int maxSizeMb) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please select a file");
        }

        if (file.getSize() > maxSizeMb * 1024L * 1024L) {
            throw new RuntimeException(
                    "File size must not exceed " + maxSizeMb + " MB");
        }
    }

    private String saveFile(
            MultipartFile file, String folder, String extension)
            throws IOException {

        Path directory = uploadDirectory.resolve(folder).normalize();
        Files.createDirectories(directory);

        String fileName = UUID.randomUUID() + extension;
        Path destination = directory.resolve(fileName).normalize();

        if (!destination.startsWith(directory)) {
            throw new IOException("Invalid file path");
        }

        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination);
        }

        return folder + "/" + fileName;
    }

    public void deleteFile(String relativePath) throws IOException {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }

        Path filePath = uploadDirectory.resolve(relativePath).normalize();

        if (!filePath.startsWith(uploadDirectory)) {
            throw new IOException("Invalid file path");
        }

        Files.deleteIfExists(filePath);
    }

    public Resource loadResume(String relativePath) throws IOException {

        if (relativePath == null || relativePath.isBlank()) {
            throw new RuntimeException("Resume not found");
        }

        Path filePath = uploadDirectory.resolve(relativePath).normalize();

        if (!filePath.startsWith(uploadDirectory)
                || !relativePath.startsWith("resumes/")) {
            throw new RuntimeException("Invalid resume path");
        }

        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Resume file not found");
        }

        return resource;
    }

}
