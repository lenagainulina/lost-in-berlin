package de.berlin.lostberlin.service.fileUpload;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileService {
    /**
     * Initializes a file storage directory
     */
    void initFileStorage();

    /**
     * Loads a file path by a given file name
     * @param filename string with the name of the file
     * @return (@Path) a path of the file
     */
    Path loadFile(String filename);

    /**
     * Saves a file in the file directory
     * @param file Multipart representation of the file
     */
    void storeFile(MultipartFile file);

    /**
     * Load a file stored in the file storage
     * @param filename string with the name of the file
     * @return (@Resource) file resource
     */
    Resource loadFileAsResource(String filename);

    /**
     * Deletes all files from the storage
     */
    void deleteAllFiles();
}
