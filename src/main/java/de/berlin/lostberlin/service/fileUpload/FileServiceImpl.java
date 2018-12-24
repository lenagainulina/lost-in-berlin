package de.berlin.lostberlin.service.fileUpload;

import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.exception.ResourceNotSavedException;
import de.berlin.lostberlin.service.fileUpload.config.FileStorageConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService {

    private final Path rootLocation;

    @Autowired
    public FileServiceImpl(FileStorageConfiguration properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void initFileStorage() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new ResourceNotSavedException("Could not initialize storage");
        }
    }

    @Override
    public Path loadFile(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public void storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        validateFile(file, fileName);
        try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException ex) {
            throw new ResourceNotSavedException("Could not store file " + fileName + ". Please try again!");
        }
    }

    @Override
    public Resource loadFileAsResource(String filename) {
        try {
            Path file = loadFile(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("Could not read: file " + filename);
        }
    }

    @Override
    public void deleteAllFiles() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }


    private void validateFile(MultipartFile file, String fileName){
        if (file.isEmpty()) {
            throw new ResourceNotSavedException("Failed to store empty file " + fileName);
        }
        if (fileName.contains("..")) {
            throw new ResourceNotSavedException("Sorry! Filename contains invalid path sequence " + fileName);
        }
    }

}

